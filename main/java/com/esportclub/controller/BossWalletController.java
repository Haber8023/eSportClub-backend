package com.esportclub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esportclub.entity.Boss;
import com.esportclub.entity.Response;
import com.esportclub.mapper.BossMapper;
import com.esportclub.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/boss")
public class BossWalletController {

    @Autowired private BossMapper bossMapper;
    @Autowired private JwtUtil jwtUtil;

    private Long getCurrentBossId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtUtil.getUserId(token);
    }

    @GetMapping("/balance")
    public Response<?> balance(@RequestHeader("Authorization") String authHeader) {
        Long bossId = getCurrentBossId(authHeader);
        Boss boss = bossMapper.selectById(bossId);
        if (boss == null) return Response.fail(401, "未登录");
        Map<String, Object> data = new HashMap<>();
        data.put("balance", boss.getBalance() != null ? boss.getBalance() : BigDecimal.ZERO);
        data.put("totalRecharge", boss.getTotalRecharge() != null ? boss.getTotalRecharge() : BigDecimal.ZERO);
        data.put("totalConsume", boss.getTotalConsume() != null ? boss.getTotalConsume() : BigDecimal.ZERO);
        data.put("totalLocked", boss.getTotalLocked() != null ? boss.getTotalLocked() : BigDecimal.ZERO);
        return Response.ok(data);
    }

    @PostMapping("/recharges")
    public Response<?> createRecharge(@RequestHeader("Authorization") String authHeader,
                                     @RequestBody Map<String, Object> body) {
        Long bossId = getCurrentBossId(authHeader);
        Object amountObj = body.get("amount");
        BigDecimal amount = null;
        if (amountObj instanceof Number) {
            amount = new BigDecimal(amountObj.toString());
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Response.fail(400, "充值金额必须大于0");
        }
        Boss boss = bossMapper.selectById(bossId);
        if (boss == null) return Response.fail(401, "未登录");
        // 简单记录，余额直接加（实际项目应该走支付回调）
        boss.setBalance(boss.getBalance().add(amount));
        boss.setTotalRecharge(boss.getTotalRecharge().add(amount));
        bossMapper.updateById(boss);
        Map<String, Object> data = new HashMap<>();
        data.put("balance", boss.getBalance());
        data.put("amount", amount);
        data.put("status", "success");
        return Response.ok(data);
    }

    @GetMapping("/recharges")
    public Response<?> rechargeRecords(@RequestHeader("Authorization") String authHeader,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        // 简化版：暂无充值记录表，直接返回空列表
        Map<String, Object> data = new HashMap<>();
        data.put("list", java.util.Collections.emptyList());
        data.put("total", 0L);
        data.put("page", page);
        data.put("pageSize", size);
        return Response.ok(data);
    }
}
