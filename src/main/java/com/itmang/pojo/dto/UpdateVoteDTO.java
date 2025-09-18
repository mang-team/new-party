package com.itmang.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author luo
 */

@Data
public class UpdateVoteDTO implements Serializable {
    private String id;               // 签到 ID
    private String voteInTitle;    // 签到标题
    private String voteInContent;  // 签到内容说明
    private String options;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime; // 签到开始时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;   // 签到结束时间
}