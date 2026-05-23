package com.esportclub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esportclub.entity.Order;
import com.esportclub.entity.Response;
import com.esportclub.mapper.OrderMapper;
import com.esportclub.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boss/orders")
public class BossOrdersController {

    @Autowired private OrderMapper orderMapper;
    @Autowired private JwtUtil jwtUtil;

    private Long getCurrentBossId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtUtil.getUserId(token);
    }

    @GetMapping
    public Response<?> list(@RequestHeader("Authorization") String authHeader,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int pageSize) {
        Long bossId = getCurrentBossId(authHeader);
        Page<Order> p = new Page<>(page, pageSize);
        orderMapper.selectPage(p, new LambdaQueryWrapper<Order>()
                .eq(Order::getBossId, bossId)
                .orderByDesc(Order::getCreatedAt));
        return Response.ok(p);
    }

    @GetMapping("/{id}")
    public Response<?> get(@PathVariable Long id,
                         @RequestHeader("Authorization") String authHeader) {
        Long bossId = getCurrentBossId(authHeader);
        Order order = orderMapper.selectById(id);
        if (order == null) return Response.fail(404, "订单不存在");
        if (!order.getBossId().equals(bossId)) return Response.fail(403, "无权查看此订单");
        return Response.ok(order);
    }
}
