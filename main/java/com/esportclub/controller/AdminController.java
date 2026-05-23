package com.esportclub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esportclub.entity.Admin;
import com.esportclub.entity.Response;
import com.esportclub.mapper.AdminMapper;
import com.esportclub.util.BcryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private AdminMapper adminMapper;
    @Autowired private BcryptUtil bcryptUtil;

    /** 管理员列表（分页） */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SHOP_OWNER')")
    public Response<?> list(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "20") int size) {
        Page<Admin> p = new Page<>(page, size);
        adminMapper.selectPage(p, new LambdaQueryWrapper<Admin>()
                .eq(Admin::getIsSuper, 0)  // 不显示超级管理员
                .orderByAsc(Admin::getId));
        return Response.ok(p);
    }

    /** 所有管理员（下拉框用） */
    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public Response<?> all() {
        List<Admin> list = adminMapper.selectList(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getStatus, 1)
                .orderByAsc(Admin::getId));
        return Response.ok(list);
    }

    /** 创建管理员（仅超级管理员） */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Response<?> create(@RequestBody Admin admin) {
        if (admin.getUsername() == null || admin.getPassword() == null) {
            return Response.fail(400, "用户名和密码不能为空");
        }
        if (adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, admin.getUsername())).getId() != null) {
            return Response.fail(400, "用户名已存在");
        }
        admin.setPassword(bcryptUtil.encode(admin.getPassword()));
        admin.setIsSuper(0);
        adminMapper.insert(admin);
        return Response.ok("创建成功");
    }

    /** 更新管理员 */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SHOP_OWNER')")
    public Response<?> update(@PathVariable Long id, @RequestBody Admin admin) {
        Admin existing = adminMapper.selectById(id);
        if (existing == null) return Response.fail(404, "管理员不存在");
        // 超级管理员不可被修改
        if (existing.getIsSuper() == 1) return Response.fail(403, "超级管理员不可修改");

        if (admin.getNickname() != null) existing.setNickname(admin.getNickname());
        if (admin.getRole() != null) existing.setRole(admin.getRole());
        if (admin.getStatus() != null) existing.setStatus(admin.getStatus());
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            existing.setPassword(bcryptUtil.encode(admin.getPassword()));
        }
        adminMapper.updateById(existing);
        return Response.ok("更新成功");
    }

    /** 删除管理员（仅超级管理员，且不能删自己） */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Response<?> delete(@PathVariable Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) return Response.fail(404, "管理员不存在");
        if (admin.getIsSuper() == 1) return Response.fail(403, "超级管理员不可删除");
        adminMapper.deleteById(id);
        return Response.ok("删除成功");
    }
}
