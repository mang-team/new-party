package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户日志分页查询DTO
 */
@Data
@Schema(name = "PageUserLogDto", description = "用户日志分页查询DTO")
public class PageUserLogDto {

    @Schema(name = "page", description = "页码")
    private Integer page;

    @Schema(name = "pageSize", description = "每页显示记录数")
    private Integer pageSize;

    @Schema(name = "userName", description = "操作人姓名")
    private String userName;

    @Schema(name = "type", description = "操作类型（1为查找操作，2为修改操作，3为增加操作，4为删除操作）")
    private Integer type;

    @Schema(name = "createStartTime", description = "操作开始时间")
    private LocalDateTime createStartTime;

    @Schema(name = "createEndTime", description = "操作结束时间")
    private LocalDateTime createEndTime;
}