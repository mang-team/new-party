package com.itmang.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author luo
 */

@Data
public class UpdateVoteDTO implements Serializable {
    @Schema(description = "id")
    private String id;
    @Schema(description = "投票标题")
    private String voteInTitle;
    @Schema(description = "投票内容")
    private String voteInContent;
    @Schema(description = "投票选项")
    private List<String> options;
    @Schema(description = "参与人员ID")
    private String userIds;
    @Schema(description = "投票开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime; // 签到开始时间
    @Schema(description = "投票结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;   // 签到结束时间
}