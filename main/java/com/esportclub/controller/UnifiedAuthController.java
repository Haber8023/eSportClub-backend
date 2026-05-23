package com.esportclub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esportclub.entity.*;
import com.esportclub.mapper.*;
import com.esportclub.util.BcryptUtil;
import com.esportclub.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/unified")
public class UnifiedAuthController {

    @Autowired private AdminMapper adminMapper;
    @Autowired private CompanionMapper companionMapper;
    @Autowired private BossMapper bossMapper;
    @Autowired private BcryptUtil bcryptUtil;
    @Autowired private JwtUtil jwtUtil;

    /** 统一登录接口 - 尝试所有角色 */
    @PostMapping("/login")
    public Response<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return Response.fail(400, "用户名和密码不能为空");
        }

        // 1. 尝试管理员
        Admin admin = adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, username)
                .eq(Admin::getStatus, 1));
        if (admin != null && bcryptUtil.matches(password, admin.getPassword())) {
            String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), admin.getRole());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("username", admin.getUsername());
            data.put("nickname", admin.getNickname());
            data.put("role", admin.getRole());
            data.put("isSuper", admin.getIsSuper());
            return Response.ok(data);
        }

        // 2. 尝试陪玩师（用手机号）
        Companion companion = companionMapper.selectOne(new LambdaQueryWrapper<Companion>()
                .eq(Companion::getPhone, username)
                .eq(Companion::getStatusAudit, "approved"));
        if (companion != null && bcryptUtil.matches(password, companion.getPassword())) {
            String token = jwtUtil.generateToken(companion.getId(), companion.getPhone(), "COMPANION");
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("role", "companion");
            data.put("companionId", companion.getId());
            data.put("nickname", companion.getNickname());
            data.put("phone", companion.getPhone());
            data.put("companionNo", companion.getCompanionNo());
            return Response.ok(data);
        }

        // 3. 尝试老板（用手机号）
        Boss boss = bossMapper.selectOne(new LambdaQueryWrapper<Boss>()
                .eq(Boss::getPhone, username)
                .eq(Boss::getStatus, 1));
        if (boss != null && bcryptUtil.matches(password, boss.getPassword())) {
            String token = jwtUtil.generateToken(boss.getId(), boss.getPhone(), "BOSS");
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("role", "boss");
            data.put("bossId", boss.getId());
            data.put("nickname", boss.getNickname());
            data.put("phone", boss.getPhone());
            return Response.ok(data);
        }

        return Response.fail(401, "账号或密码错误");
    }
}
