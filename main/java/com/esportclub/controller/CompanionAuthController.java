package com.esportclub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esportclub.entity.Companion;
import com.esportclub.entity.Response;
import com.esportclub.mapper.CompanionMapper;
import com.esportclub.util.BcryptUtil;
import com.esportclub.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/companion/auth")
public class CompanionAuthController {

    @Autowired private CompanionMapper companionMapper;
    @Autowired private BcryptUtil bcryptUtil;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Response<?> login(@RequestBody Map<String, String> body) {
        String phone = body.get("username"); // 前端username实际传的是手机号
        String password = body.get("password");
        if (phone == null || password == null) {
            return Response.fail(400, "手机号和密码不能为空");
        }

        Companion companion = companionMapper.selectOne(
                new LambdaQueryWrapper<Companion>()
                        .eq(Companion::getPhone, phone)
                        .eq(Companion::getStatusAudit, "approved"));
        if (companion == null) {
            return Response.fail(401, "账号不存在或未通过审核");
        }

        if (!bcryptUtil.matches(password, companion.getPassword())) {
            return Response.fail(401, "密码错误");
        }

        String token = jwtUtil.generateToken(companion.getId(), companion.getPhone(), "COMPANION");

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("companionId", companion.getId());
        data.put("nickname", companion.getNickname());
        data.put("phone", companion.getPhone());
        data.put("companionNo", companion.getCompanionNo());
        data.put("commissionRate", companion.getCommissionRate());
        return Response.ok(data);
    }

    @GetMapping("/me")
    public Response<?> me(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long companionId = jwtUtil.getUserId(token);
        Companion companion = companionMapper.selectById(companionId);
        if (companion == null) return Response.fail(401, "未登录");
        companion.setPassword(null);
        return Response.ok(companion);
    }
}
