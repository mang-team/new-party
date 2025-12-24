package com.itmang.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 签到信息查询/新增 DTO
 */
@Data
public class FindRegisterSignDTO {

    @Schema(description = "id")
    private String id;
    @Schema(description = "签到信息id")
    private String signInInformationId;
    @Schema(description = "用户id")
    private String userId;
    @Schema(description = "签到状态")
    private Integer state;
    @Schema(description = "签到发布人id")
    private String createBy;
    @Schema(description = "签到发布人名字")
    private String createName;
    @Schema(description = "签到发布时间开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;
    @Schema(description = "签到发布时间结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;
    @Schema(description = "页码")
    private Integer pageNum;
    @Schema(description = "每页显示条数")
    private Integer pageSize;
}