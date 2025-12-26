package com.itmang.pojo.dto.action;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class PageDetailActionMessageDTO {

    @Schema(description = "活动名称")
    private String actionName;

    @Schema(description = "发布者id")
    private String releaseId;
    @Schema(description = "发布者名字")
    private String releaseName;
    @Schema(description = "组织者id")
    private String organizerId;
    @Schema(description = "组织者名字")
    private String organizerName;

    @Schema(description = "活动报名开始时间开始时间")
    private LocalDateTime signUpStartTimeStart;
    @Schema(description = "活动报名开始时间结束时间")
    private LocalDateTime signUpStartTimeEnd;
    @Schema(description = "活动报名结束时间开始时间")
    private LocalDateTime signUpEndTimeStart;
    @Schema(description = "活动报名结束时间结束时间")
    private LocalDateTime signUpEndTimeEnd;

    @Schema(description = "活动开始时间开始时间")
    private LocalDateTime actionStartTimeStart;
    @Schema(description = "活动开始时间结束时间")
    private LocalDateTime actionStartTimeEnd;
    @Schema(description = "活动结束时间开始时间")
    private LocalDateTime actionEndTimeStart;
    @Schema(description = "活动结束时间结束时间")
    private LocalDateTime actionEndTimeEnd;


    @Schema(description = "活动地址")
    private String eventAddress;

    @Schema(description = "当前页码")
    private Integer page;     // 当前页码
    @Schema(description = "每页展示数")
    private Integer pageSize; //每页展示数
}
