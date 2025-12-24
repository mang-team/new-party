package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class AddRegisterRecordDTO implements Serializable {

    @Schema(description = "签到信息id")
    private String signInInformationId;
    @Schema(description = "用户id集合")
    private String userIds;
}