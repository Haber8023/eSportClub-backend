package com.esportclub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("recharge_record")
public class RechargeRecord {
    private Long id;
    private Long bossId;
    private Long accountId;
    private String type;        // new_card / old_recharge / old_card
    private String accountType;
    private Long companionId;
    private BigDecimal discount;
    private BigDecimal rechargeAmount;
    private BigDecimal giftAmount;
    private String paymentMethod;
    private String screenshot;
    private Long csId;
    private String status;      // pending / confirmed / cancelled

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
