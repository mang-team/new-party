package com.itmang.pojo.vo.action;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class DetailActionRecordMessageVO {

    @Schema(description = "活动id")
    private String id;               // 活动id
    @Schema(description = "活动名称")
    private String actionName;        // 活动名称
    @Schema(description = "活动内容详情")
    private String activityContent;   // 活动内容详情
    @Schema(description = "活动参与人数")
    private Integer participantQuantity;
    @Schema(description = "组织者id")
    private String organizerId;         // 组织者
    @Schema(description = "组织者名字")
    private String organizerName;    // 组织者名字
    @Schema(description = "组织者电话")
    private String organizerPhone;    // 组织者电话


    @Schema(description = "活动报名开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signUpStartTime;        // 活动报名开始时间
    @Schema(description = "活动报名结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signUpEndTime;          // 活动报名结束时间
    @Schema(description = "活动开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actionStartTime;        // 活动开始时间
    @Schema(description = "活动结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actionEndTime;          // 活动结束时间

    @Schema(description = "活动地点")
    private String eventAddress;
    @Schema(description = "发布人id")
    private String createId;
    @Schema(description = "发布人名字")
    private String createName;

    @Schema(description = "报名时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signInTime;
    @Schema(description = "活动状态")
    private Integer state;
    @Schema(description = "备注")
    private String notes;


}
