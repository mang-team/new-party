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
@Schema(name = "SignInRecordPageVO", description = "签到记录分页VO")
public class SignInRecordPageVO {

    @Schema(name = "id", description = "id，主键自增")
    private String id;
    @Schema(name = "signInInformationId", description = "签到信息id，外键")
    private String signInInformationId;
    @Schema(name = "signInTitle", description = "签到信息标题")
    private String signInTitle;
    @Schema(name = "userId", description = "用户id，外键")
    private String userId;
    @Schema(name = "userName", description = "用户名字")
    private String userName;
    @Schema(name = "state", description = "状态（未签到 2，已签到 1）（默认为2）")
    private Integer state;
    @Schema(name = "notes", description = "备注")
    private String notes;
    @Schema(name = "signInTime", description = "签到时间")
    private LocalDateTime signInTime;
}