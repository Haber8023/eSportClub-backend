package com.esportclub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("boss")
public class Boss {
    private Long id;
    private String bossNo;
    private String nickname;
    private String gender;
    private String wechat;
    private String phone;
    private String password;
    private String referrerType; // companion / boss / none
    private Long referrerId;
    private LocalDate firstRechargeDate;
    private BigDecimal totalRecharge;
    private BigDecimal totalGift;
    private BigDecimal totalConsume;
    private BigDecimal totalLocked;
    private BigDecimal balance;
    private Integer status;     // 1启用 0禁用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
