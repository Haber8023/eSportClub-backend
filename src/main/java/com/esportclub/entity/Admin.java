package com.esportclub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("admin")
public class Admin {
    private Long id;
    private String username;
    private String password;
    private String role;       // super_admin / shop_owner / cs / assessor / finance
    private String nickname;
    private Integer status;    // 1启用 0禁用
    private Integer isSuper;  // 1=超级管理员（仅admin）0=普通管理员

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
