package com.itmang.pojo.vo;

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
@Schema(name = "SignInRecordVO", description = "签到记录VO")
public class SignInRecordVO {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "signInInformationId", description = "签到信息id")
    private String signInInformationId;
    @Schema(name = "signInTitle", description = "签到标题")
    private String signInTitle;
    @Schema(name = "userId", description = "用户id")
    private String userId;
    @Schema(name = "userName", description = "用户名字")
    private String userName;
    @Schema(name = "state", description = "状态（未签到 2，已签到 1）（默认为2）")
    private Integer state;
    @Schema(name = "notes", description = "备注")
    private String notes;
    @Schema(name = "signInTime", description = "签到时间")
    private LocalDateTime signInTime;
    @Schema(name = "createName", description = "签到发起人名字")
    private String createName;
    @Schema(name = "releaseTime", description = "发布签到时间")
    private LocalDateTime releaseTime;
}