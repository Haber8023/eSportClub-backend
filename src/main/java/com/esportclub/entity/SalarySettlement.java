package com.esportclub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("salary_settlement")
public class SalarySettlement {
    private Long id;
    private Long companionId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Integer totalOrders;
    private BigDecimal totalIncome;
    private BigDecimal commissionRateAvg;
    private BigDecimal grossSalary;
    private BigDecimal fines;
    private BigDecimal rewards;
    private BigDecimal netSalary;
    private String status;     // pending / settled
    private LocalDateTime settledAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
