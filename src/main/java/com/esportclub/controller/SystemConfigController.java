package com.esportclub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esportclub.entity.Response;
import com.esportclub.entity.SystemConfig;
import com.esportclub.mapper.SystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system")
public class SystemConfigController {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    /** 获取系统配置（所有人可读） */
    @GetMapping("/config")
    public Response<?> getConfig() {
        SystemConfig cfg = systemConfigMapper.selectOne(new LambdaQueryWrapper<>());
        if (cfg == null) {
            cfg = new SystemConfig();
            cfg.setSiteName("电竞陪玩管理系统");
            systemConfigMapper.insert(cfg);
        }
        return Response.ok(cfg);
    }

    /** 更新系统配置（仅超级管理员） */
    @PutMapping("/config")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Response<?> updateConfig(@RequestBody SystemConfig config) {
        SystemConfig existing = systemConfigMapper.selectOne(new LambdaQueryWrapper<>());
        if (existing == null) {
            systemConfigMapper.insert(config);
        } else {
            config.setId(existing.getId());
            systemConfigMapper.updateById(config);
        }
        return Response.ok("保存成功");
    }
}
