package com.esportclub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esportclub.entity.Order;
import com.esportclub.entity.Response;
import com.esportclub.mapper.OrderMapper;
import com.esportclub.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/companion/orders")
public class CompanionOrdersController {

    @Autowired private OrderMapper orderMapper;
    @Autowired private JwtUtil jwtUtil;

    private Long getCurrentCompanionId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtUtil.getUserId(token);
    }

    @GetMapping("/my")
    public Response<?> myOrders(@RequestHeader("Authorization") String authHeader,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int pageSize) {
        Long companionId = getCurrentCompanionId(authHeader);
        Page<Order> p = new Page<>(page, pageSize);
        orderMapper.selectPage(p, new LambdaQueryWrapper<Order>()
                .eq(Order::getCompanionId, companionId)
                .orderByDesc(Order::getCreatedAt));
        return Response.ok(p);
    }

    @GetMapping("/{id}")
    public Response<?> getOrder(@PathVariable Long id,
                                @RequestHeader("Authorization") String authHeader) {
        Long companionId = getCurrentCompanionId(authHeader);
        Order order = orderMapper.selectById(id);
        if (order == null) return Response.fail(404, "订单不存在");
        if (!order.getCompanionId().equals(companionId)) return Response.fail(403, "无权查看此订单");
        return Response.ok(order);
    }

    @PutMapping("/{id}/accept")
    public Response<?> acceptOrder(@PathVariable Long id,
                                  @RequestHeader("Authorization") String authHeader) {
        Long companionId = getCurrentCompanionId(authHeader);
        Order order = orderMapper.selectById(id);
        if (order == null) return Response.fail(404, "订单不存在");
        if (!"pending".equals(order.getStatus()) && !"dispatched".equals(order.getStatus()))
            return Response.fail(400, "当前状态无法接单");
        order.setStatus("accepted");
        order.setCompanionId(companionId);
        orderMapper.updateById(order);
        return Response.ok("接单成功");
    }

    @PutMapping("/{id}/submit")
    public Response<?> submitOrder(@PathVariable Long id,
                                  @RequestHeader("Authorization") String authHeader,
                                  @RequestBody(required = false) Map<String, String> body) {
        Long companionId = getCurrentCompanionId(authHeader);
        Order order = orderMapper.selectById(id);
        if (order == null) return Response.fail(404, "订单不存在");
        if (!order.getCompanionId().equals(companionId))
            return Response.fail(403, "无权操作此订单");
        if (!"accepted".equals(order.getStatus()) && !"working".equals(order.getStatus()))
            return Response.fail(400, "当前状态无法提交");
        order.setStatus("submitted");
        if (body != null && body.get("reportScreenshot") != null) {
            order.setReportScreenshot(body.get("reportScreenshot"));
        }
        orderMapper.updateById(order);
        return Response.ok("提交验收成功");
    }

    @GetMapping("/stats")
    public Response<?> stats(@RequestHeader("Authorization") String authHeader) {
        Long companionId = getCurrentCompanionId(authHeader);

        long totalOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().eq(Order::getCompanionId, companionId));
        long pendingOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getCompanionId, companionId)
                        .in(Order::getStatus, "pending", "dispatched", "accepted"));
        long completedOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getCompanionId, companionId)
                        .in(Order::getStatus, "completed", "settled"));

        BigDecimal totalEarnings = BigDecimal.ZERO;
        BigDecimal pendingEarnings = BigDecimal.ZERO;

        try {
            QueryWrapper<Order> totalQw = new QueryWrapper<>();
            totalQw.eq("companion_id", companionId);
            totalQw.in("status", "completed", "settled");
            totalQw.select("SUM(companion_income) as total");
            Map<String, Object> totalMap = orderMapper.selectMaps(totalQw).stream().findFirst().orElse(null);
            if (totalMap != null && totalMap.get("total") != null) {
                totalEarnings = new BigDecimal(totalMap.get("total").toString());
            }

            QueryWrapper<Order> pendingQw = new QueryWrapper<>();
            pendingQw.eq("companion_id", companionId);
            pendingQw.in("status", "pending", "dispatched", "accepted", "submitted");
            pendingQw.select("SUM(companion_income) as total");
            Map<String, Object> pendingMap = orderMapper.selectMaps(pendingQw).stream().findFirst().orElse(null);
            if (pendingMap != null && pendingMap.get("total") != null) {
                pendingEarnings = new BigDecimal(pendingMap.get("total").toString());
            }
        } catch (Exception e) {
            // fallback to zero
        }

        Map<String, Object> data = new HashMap<>();
        data.put("totalOrders", totalOrders);
        data.put("pendingOrders", pendingOrders);
        data.put("completedOrders", completedOrders);
        data.put("totalEarnings", totalEarnings);
        data.put("pendingEarnings", pendingEarnings);
        data.put("settledEarnings", totalEarnings); // 已结算 = 已完成
        return Response.ok(data);
    }
}
