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
@Schema(name = "SignInRecord", description = "签到记录表实体类")
public class SignInRecord {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "signInInformationId", description = "签到信息id，外键")
    private String signInInformationId;

    @Schema(name = "userId", description = "用户id，外键")
    private String userId;

    @Schema(name = "state", description = "状态（未签到 2，已签到 1）（默认为2）")
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