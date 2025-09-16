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
@Schema(name ="ActionInformation",description = "活动信息实体类")
public class ActionInformation {

    @Schema(name = "id",description = "id主键")
    private String id;

    @Schema(name = "actionName",description = "活动名称")
    private String actionName;

    @Schema(name = "activityContent",description = "活动内容详情")
    private String activityContent;

    @Schema(name = "userId",description = "组织者")
    private String userId;

    @Schema(name = "organizerPhone",description = "组织者电话")
    private String organizerPhone;

    @Schema(name = "signUpStartTime",description = "活动报名开始时间")
    private LocalDateTime signUpStartTime;

    @Schema(name = "signUpEndTime",description = "活动报名结束时间")
    private LocalDateTime signUpEndTime;

    @Schema(name = "actionStartTime",description = "活动开始时间")
    private LocalDateTime actionStartTime;

    @Schema(name = "actionEndTime",description = "活动结束时间")
    private LocalDateTime actionEndTime;

    @Schema(name = "eventAddress",description = "活动地点")
    private String eventAddress;

    @Schema(name = "createBy",description = "发布人")
    private String createBy;

    @Schema(name = "createTime",description = "发布时间")
    private LocalDateTime createTime;

    @Schema(name = "updateBy",description = "修改人")
    private String updateBy;

    @Schema(name = "updateTime",description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(name = "isDelete",description = "删除标识")
    private Integer isDelete;
}
