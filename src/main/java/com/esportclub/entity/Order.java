package com.esportclub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {
    private Long id;
    private String orderNo;
    private LocalDate orderDate;
    private Long csId;
    private Long bossId;
    private Long accountId;
    private Long companionId;
    private String gameCategory;
    private String gameItem;
    private BigDecimal unitPrice;
    private String extraFees;   // JSON
    private BigDecimal duration;
    private BigDecimal discount;
    private BigDecimal orderTotal;
    private BigDecimal discountedTotal;
    private BigDecimal commissionRate;
    private BigDecimal companionIncome;
    private BigDecimal shopIncome;
    private String referrerType;
    private Long referrerId;
    private String specificTime;
    private String reportScreenshot;
    private String status;     // dispatched / pending_confirm / confirmed / confirmed_error / settled / cancelled
    private String rejectReason;
    private String csRemark;
    private String source;     // miniapp / cs_manual / renewal

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
