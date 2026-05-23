package com.esportclub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("complaint")
public class Complaint {
    private Long id;
    private String orderNo;
    private Long companionId;
    private Long bossId;
    private String type;      // complaint / suggestion
    private String content;
    private String screenshot;
    private String status;     // pending / processed
    private String fineStatus;
    private BigDecimal fineAmount;
    private String csRemark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
