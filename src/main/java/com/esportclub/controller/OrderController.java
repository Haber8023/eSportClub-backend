package com.esportclub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esportclub.entity.Order;
import com.esportclub.entity.Response;
import com.esportclub.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired private OrderMapper orderMapper;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public Response<?> list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "20") int size,
                           @RequestParam(required = false) Long bossId,
                           @RequestParam(required = false) Long companionId,
                           @RequestParam(required = false) String status) {
        Page<Order> p = new Page<>(page, size);
        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>();
        if (bossId != null) qw.eq(Order::getBossId, bossId);
        if (companionId != null) qw.eq(Order::getCompanionId, companionId);
        if (status != null && !status.isEmpty()) qw.eq(Order::getStatus, status);
        qw.orderByDesc(Order::getId);
        orderMapper.selectPage(p, qw);
        return Response.ok(p);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Response<?> get(@PathVariable Long id) {
        return Response.ok(orderMapper.selectById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SHOP_OWNER','CS')")
    public Response<?> create(@RequestBody Order order) {
        // 生成订单编号
        String dateStr = LocalDate.now().toString().replace("-", "");
        long count = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().ge(Order::getOrderDate, LocalDate.now()));
        order.setOrderNo(dateStr + String.format("%04d", count + 1));

        // 自动计算
        BigDecimal orderTotal = order.getUnitPrice()
                .multiply(order.getDuration());
        BigDecimal discountedTotal = orderTotal.multiply(order.getDiscount());
        BigDecimal companionIncome = discountedTotal.multiply(order.getCommissionRate());
        BigDecimal shopIncome = discountedTotal.subtract(companionIncome);

        order.setOrderTotal(orderTotal);
        order.setDiscountedTotal(discountedTotal);
        order.setCompanionIncome(companionIncome);
        order.setShopIncome(shopIncome);
        order.setOrderDate(LocalDate.now());
        order.setStatus("dispatched");

        orderMapper.insert(order);
        return Response.ok(order);
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SHOP_OWNER','CS')")
    public Response<?> confirm(@PathVariable Long id, @RequestBody Order order) {
        Order existing = orderMapper.selectById(id);
        if (existing == null) return Response.fail(404, "订单不存在");
        existing.setStatus("confirmed");
        existing.setSpecificTime(order.getSpecificTime());
        existing.setReportScreenshot(order.getReportScreenshot());
        orderMapper.updateById(existing);
        return Response.ok("验收确认成功");
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SHOP_OWNER','CS')")
    public Response<?> reject(@PathVariable Long id, @RequestBody Order order) {
        Order existing = orderMapper.selectById(id);
        if (existing == null) return Response.fail(404, "订单不存在");
        existing.setStatus("confirmed_error");
        existing.setRejectReason(order.getRejectReason());
        orderMapper.updateById(existing);
        return Response.ok("订单已驳回");
    }
}
