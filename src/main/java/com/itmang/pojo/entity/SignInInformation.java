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
@Schema(name = "SignInInformation", description = "签到信息表实体类")
public class SignInInformation {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "userIds", description = "用户id集合")
    private String userIds;

    @Schema(name = "signInTitle", description = "签到标题")
    private String signInTitle;

    @Schema(name = "signInContent", description = "签到说明")
    private String signInContent;

    @Schema(name = "startTime", description = "签到开始时间（设定到了才能开始签）")
    private LocalDateTime startTime;

    @Schema(name = "endTime", description = "签到结束时间（默认值：null）")
    private LocalDateTime endTime;

    @Schema(name = "createBy", description = "发布人")
    private String createBy;

    @Schema(name = "createTime", description = "发布时间")
    private LocalDateTime createTime;

    @Schema(name = "updateBy", description = "修改人")
    private String updateBy;

    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(name = "isDelete", description = "是否删除（1为删除，2为未删除）（默认为2）")
    private Integer isDelete;
}