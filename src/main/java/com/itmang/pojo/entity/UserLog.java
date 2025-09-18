package com.itmang.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "UserLog", description = "用户日志表实体类")
public class UserLog {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "type", description = "操作类型（1为查找操作，2为修改操作，3为增加操作，4为删除操作）")
    private Integer type;

    @Schema(name = "content", description = "操作内容")
    private String content; // 原字段名varch可能为笔误，此处修正为String类型

    @Schema(name = "createBy", description = "操作人，外键")
    private String createBy;

    @Schema(name = "createTime", description = "操作时间")
    private LocalDateTime createTime;
}