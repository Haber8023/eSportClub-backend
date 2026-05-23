package com.esportclub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esportclub.entity.Companion;
import com.esportclub.entity.Response;
import com.esportclub.mapper.CompanionMapper;
import com.esportclub.util.BcryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/companion")
public class CompanionController {

    @Autowired private CompanionMapper companionMapper;
    @Autowired private BcryptUtil bcryptUtil;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public Response<?> list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "20") int size) {
        Page<Companion> p = new Page<>(page, size);
        companionMapper.selectPage(p, new LambdaQueryWrapper<Companion>()
                .orderByDesc(Companion::getId));
        return Response.ok(p);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Response<?> get(@PathVariable Long id) {
        return Response.ok(companionMapper.selectById(id));
    }

    /** 陪玩入驻申请 */
    @PostMapping("/register")
    public Response<?> register(@RequestBody Companion companion) {
        if (companion.getNickname() == null || companion.getPhone() == null) {
            return Response.fail(400, "昵称和手机号不能为空");
        }
        // 检查昵称/手机号唯一
        long nickCount = companionMapper.selectCount(
                new LambdaQueryWrapper<Companion>().eq(Companion::getNickname, companion.getNickname()));
        if (nickCount > 0) return Response.fail(400, "昵称已存在");

        long phoneCount = companionMapper.selectCount(
                new LambdaQueryWrapper<Companion>().eq(Companion::getPhone, companion.getPhone()));
        if (phoneCount > 0) return Response.fail(400, "手机号已存在");

        long count = companionMapper.selectCount(null);
        companion.setCompanionNo("COMP" + String.format("%04d", count + 1));
        companion.setPassword(bcryptUtil.encode(companion.getPassword() != null ? companion.getPassword() : "123456"));
        companion.setStatusAudit("pending");
        companion.setHireDate(LocalDate.now());
        companionMapper.insert(companion);
        return Response.ok("申请已提交，请等待审核");
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SHOP_OWNER','ASSESSOR')")
    public Response<?> approve(@PathVariable Long id) {
        Companion c = companionMapper.selectById(id);
        if (c == null) return Response.fail(404, "陪玩不存在");
        c.setStatusAudit("approved");
        companionMapper.updateById(c);
        return Response.ok("审核通过");
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SHOP_OWNER','ASSESSOR')")
    public Response<?> reject(@PathVariable Long id, @RequestBody Companion companion) {
        Companion c = companionMapper.selectById(id);
        if (c == null) return Response.fail(404, "陪玩不存在");
        c.setStatusAudit("rejected");
        companionMapper.updateById(c);
        return Response.ok("已驳回");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SHOP_OWNER','ASSESSOR')")
    public Response<?> update(@PathVariable Long id, @RequestBody Companion companion) {
        Companion existing = companionMapper.selectById(id);
        if (existing == null) return Response.fail(404, "陪玩不存在");
        if (companion.getNickname() != null) existing.setNickname(companion.getNickname());
        if (companion.getStatus() != null) existing.setStatus(companion.getStatus());
        if (companion.getCommissionRate() != null) existing.setCommissionRate(companion.getCommissionRate());
        if (companion.getWechat() != null) existing.setWechat(companion.getWechat());
        companionMapper.updateById(existing);
        return Response.ok("更新成功");
    }
}
