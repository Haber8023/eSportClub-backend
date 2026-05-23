package com.esportclub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esportclub.entity.Boss;
import com.esportclub.entity.Response;
import com.esportclub.mapper.BossMapper;
import com.esportclub.util.BcryptUtil;
import com.esportclub.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/boss/auth")
public class BossAuthController {

    @Autowired private BossMapper bossMapper;
    @Autowired private BcryptUtil bcryptUtil;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Response<?> login(@RequestBody Map<String, String> body) {
        String phone = body.get("username");
        String password = body.get("password");
        if (phone == null || password == null) {
            return Response.fail(400, "手机号和密码不能为空");
        }

        Boss boss = bossMapper.selectOne(
                new LambdaQueryWrapper<Boss>()
                        .eq(Boss::getPhone, phone)
                        .eq(Boss::getStatus, 1));
        if (boss == null) {
            return Response.fail(401, "账号不存在或已禁用");
        }

        if (!bcryptUtil.matches(password, boss.getPassword())) {
            return Response.fail(401, "密码错误");
        }

        String token = jwtUtil.generateToken(boss.getId(), boss.getPhone(), "BOSS");

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("bossId", boss.getId());
        data.put("nickname", boss.getNickname());
        data.put("phone", boss.getPhone());
        data.put("bossNo", boss.getBossNo());
        data.put("balance", boss.getBalance());
        return Response.ok(data);
    }

    @GetMapping("/me")
    public Response<?> me(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long bossId = jwtUtil.getUserId(token);
        Boss boss = bossMapper.selectById(bossId);
        if (boss == null) return Response.fail(401, "未登录");
        boss.setPassword(null);
        return Response.ok(boss);
    }
}
