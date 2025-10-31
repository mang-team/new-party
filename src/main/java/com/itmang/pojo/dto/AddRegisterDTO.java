package com.itmang.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author luo
 */

@Data
public class AddRegisterDTO implements Serializable {
    private String signInTitle;      // 签到标题
    private String signInContent;    // 签到内容

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;        // 开始时间，格式: yyyy-MM-dd HH:mm:ss

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;          // 结束时间，格式: yyyy-MM-dd HH:mm:ss
}