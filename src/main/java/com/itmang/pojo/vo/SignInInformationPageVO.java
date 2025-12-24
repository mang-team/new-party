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
@Schema(name = "SignInInformationPageVO", description = "签到信息分页VO")
public class SignInInformationPageVO {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "signInTitle", description = "签到标题")
    private String signInTitle;
    @Schema(name = "signInContent", description = "签到说明")
    private String signInContent;
    @Schema(name = "startTime", description = "签到开始时间")
    private LocalDateTime startTime;
    @Schema(name = "endTime", description = "签到结束时间")
    private LocalDateTime endTime;
}