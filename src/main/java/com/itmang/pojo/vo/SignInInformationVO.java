package com.itmang.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SignInInformationVO", description = "签到信息VO")
public class SignInInformationVO {

    @Schema(name = "id", description = "id，主键")
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