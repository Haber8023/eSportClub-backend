package com.esportclub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esportclub.entity.*;
import com.esportclub.mapper.*;
import com.esportclub.entity.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dict")
public class DictController {

    @Autowired private DictGameCategoryMapper categoryMapper;
    @Autowired private DictDiscountMapper discountMapper;

    @GetMapping("/game-categories")
    public Response<?> gameCategories() {
        List<DictGameCategory> list = categoryMapper.selectList(
                new LambdaQueryWrapper<DictGameCategory>().eq(DictGameCategory::getStatus, 1)
                        .orderByAsc(DictGameCategory::getSort));
        return Response.ok(list);
    }

    @GetMapping("/discounts")
    public Response<?> discounts() {
        List<DictDiscount> list = discountMapper.selectList(
                new LambdaQueryWrapper<DictDiscount>().eq(DictDiscount::getStatus, 1)
                        .orderByAsc(DictDiscount::getSort));
        return Response.ok(list);
    }
}
