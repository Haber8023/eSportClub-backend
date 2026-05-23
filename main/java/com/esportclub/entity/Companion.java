package com.esportclub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("companion")
public class Companion {
    private Long id;
    private String companionNo;
    private String nickname;
    private String phone;
    private String password;
    private String gender;      // male / female
    private String wechat;
    private String alipayAccount;
    private String alipayName;
    private String idCard;
    private String status;     // active / leave / vacation / blacklisted
    private BigDecimal commissionRate;
    private BigDecimal depositSetting;
    private BigDecimal depositSelf;
    private BigDecimal depositLocked;
    private BigDecimal orderIncome;
    private BigDecimal companionIncome;
    private BigDecimal paidSalary;
    private LocalDate hireDate;
    private String games;       // JSON
    private String experience; // none / under1year / over1year
    private String statusAudit; // pending / approved / rejected

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
