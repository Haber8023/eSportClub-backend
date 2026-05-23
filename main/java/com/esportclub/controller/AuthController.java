package com.esportclub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esportclub.entity.Admin;
import com.esportclub.entity.Response;
import com.esportclub.mapper.AdminMapper;
import com.esportclub.util.BcryptUtil;
import com.esportclub.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AdminMapper adminMapper;
    @Autowired private BcryptUtil bcryptUtil;
    @Autowired private JwtUtil jwtUtil;

    /** 统一登录接口 */
    @PostMapping("/login")
    public Response<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return Response.fail(400, "用户名和密码不能为空");
        }

        Admin admin = adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, username)
                .eq(Admin::getStatus, 1));
        if (admin == null) {
            return Response.fail(401, "账号不存在或已禁用");
        }

        if (!bcryptUtil.matches(password, admin.getPassword())) {
            return Response.fail(401, "密码错误");
        }

        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), admin.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", admin.getUsername());
        data.put("nickname", admin.getNickname());
        data.put("role", admin.getRole());
        data.put("isSuper", admin.getIsSuper());
        return Response.ok(data);
    }

    /** 获取当前登录用户信息 */
    @GetMapping("/me")
    public Response<?> me(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserId(token);
        Admin admin = adminMapper.selectById(userId);
        if (admin == null) return Response.fail(401, "未登录");
        Map<String, Object> data = new HashMap<>();
        data.put("id", admin.getId());
        data.put("username", admin.getUsername());
        data.put("nickname", admin.getNickname());
        data.put("role", admin.getRole());
        data.put("isSuper", admin.getIsSuper());
        return Response.ok(data);
    }
}
