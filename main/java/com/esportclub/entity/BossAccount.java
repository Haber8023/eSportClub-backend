package com.esportclub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("boss_account")
public class BossAccount {
    private Long id;
    private Long bossId;
    private String accountType; // retail / silver / gold / diamond
    private Long companionId;
    private BigDecimal discount;
    private BigDecimal rechargeTotal;
    private BigDecimal giftTotal;
    private BigDecimal consumeTotal;
    private BigDecimal lockedTotal;
    private BigDecimal balance;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
