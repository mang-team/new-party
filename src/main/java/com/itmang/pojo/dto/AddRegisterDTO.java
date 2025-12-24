package com.itmang.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author luo
 */

@Data
public class AddRegisterDTO implements Serializable {

    @Schema(description = "用户id集合")
    private String userIds;
    @Schema(description = "签到标题")
    private String signInTitle;      // 签到标题
    @Schema(description = "签到内容")
    private String signInContent;    // 签到内容
    @Schema(description = "签到开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;        // 开始时间，格式: yyyy-MM-dd HH:mm:ss
    @Schema(description = "签到结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;          // 结束时间，格式: yyyy-MM-dd HH:mm:ss
}