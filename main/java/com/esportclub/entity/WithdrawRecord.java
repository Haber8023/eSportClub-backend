package com.esportclub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("withdraw_record")
public class WithdrawRecord {
    private Long id;
    private Long companionId;
    private BigDecimal amount;
    private String alipayAccount;
    private String alipayName;
    private String status;     // pending / approved / rejected / paid
    private Long csId;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
