package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FindRegisterDTO implements Serializable {

    @Schema(description = "发布签到人id")
    private String userId;
    @Schema(description = "发布签到人名称")
    private String userName;
    @Schema(description = "签到人id")
    private String signId;
    @Schema(description = "签到标题")
    private String signInTitle;
    @Schema(description = "签到开始时间开始时间")
    private LocalDateTime startTimeStart;
    @Schema(description = "签到开始时间结束时间")
    private LocalDateTime startTimeEnd;
    @Schema(description = "签到结束时间开始时间")
    private LocalDateTime endTimeStart;
    @Schema(description = "签到结束时间结束时间")
    private LocalDateTime endTimeEnd;
    @Schema(description = "分页页码")
    private Integer pageNum;
    @Schema(description = "分页大小")
    private Integer pageSize;
}