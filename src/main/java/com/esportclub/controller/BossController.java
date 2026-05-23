package com.esportclub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esportclub.entity.Boss;
import com.esportclub.entity.Response;
import com.esportclub.mapper.BossMapper;
import com.esportclub.util.BcryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/boss")
public class BossController {

    @Autowired private BossMapper bossMapper;
    @Autowired private BcryptUtil bcryptUtil;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public Response<?> list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "20") int size) {
        Page<Boss> p = new Page<>(page, size);
        bossMapper.selectPage(p, new LambdaQueryWrapper<Boss>()
                .orderByDesc(Boss::getId));
        return Response.ok(p);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Response<?> get(@PathVariable Long id) {
        return Response.ok(bossMapper.selectById(id));
    }

    /** 老板端：我的首页数据 */
    @GetMapping("/me")
    @PreAuthorize("hasRole('BOSS')")
    public Response<?> me(@RequestHeader("Authorization") String authHeader) {
        // boss uses companion endpoint for now, placeholder
        return Response.ok(null);
    }

    /** 导入老板账号（管理后台，客服角色） */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SHOP_OWNER','CS')")
    public Response<?> create(@RequestBody Boss boss) {
        if (boss.getPhone() == null) return Response.fail(400, "手机号不能为空");
        Boss exist = bossMapper.selectOne(new LambdaQueryWrapper<Boss>()
                .eq(Boss::getPhone, boss.getPhone()));
        if (exist != null) return Response.fail(400, "手机号已存在");

        // 生成编号
        long count = bossMapper.selectCount(null);
        boss.setBossNo("BOSS" + String.format("%04d", count + 1));
        boss.setPassword(bcryptUtil.encode(boss.getPassword() != null ? boss.getPassword() : "123456"));
        boss.setStatus(1);
        boss.setBalance(BigDecimal.ZERO);
        boss.setTotalRecharge(BigDecimal.ZERO);
        boss.setTotalGift(BigDecimal.ZERO);
        boss.setTotalConsume(BigDecimal.ZERO);
        boss.setTotalLocked(BigDecimal.ZERO);
        boss.setFirstRechargeDate(LocalDate.now());
        bossMapper.insert(boss);
        return Response.ok(boss);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SHOP_OWNER','CS')")
    public Response<?> update(@PathVariable Long id, @RequestBody Boss boss) {
        Boss existing = bossMapper.selectById(id);
        if (existing == null) return Response.fail(404, "老板不存在");
        if (boss.getNickname() != null) existing.setNickname(boss.getNickname());
        if (boss.getGender() != null) existing.setGender(boss.getGender());
        if (boss.getWechat() != null) existing.setWechat(boss.getWechat());
        if (boss.getStatus() != null) existing.setStatus(boss.getStatus());
        bossMapper.updateById(existing);
        return Response.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Response<?> delete(@PathVariable Long id) {
        bossMapper.deleteById(id);
        return Response.ok("删除成功");
    }
}
