package com.esportclub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("system_config")
public class SystemConfig {
    private Long id;
    private String siteName;
    private String siteTitle;
    private String siteSubtitle;
    private String siteLogo;
    private String customTags;  // JSON
    private String welcomeMessage;
    private String copyright;
    private String icpInfo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
