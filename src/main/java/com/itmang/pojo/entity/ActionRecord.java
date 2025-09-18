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
@Schema(name = "ActionRecord", description = "活动记录表实体类")
public class ActionRecord {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "actionInformationId", description = "活动信息id，外键")
    private String actionInformationId;

    @Schema(name = "userId", description = "用户id，外键")
    private String userId;

    @Schema(name = "state", description = "状态（未开始 1，进行中 2，已结束 3）")
    private Integer state;

    @Schema(name = "notes", description = "备注")
    private String notes;

    @Schema(name = "createBy", description = "创建人")
    private String createBy;

    @Schema(name = "createTime", description = "创建时间")
    private LocalDateTime createTime;

    @Schema(name = "updateBy", description = "修改人")
    private String updateBy;

    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(name = "isDelete", description = "是否删除（1为删除，2为未删除）（默认为2）")
    private Integer isDelete;
}