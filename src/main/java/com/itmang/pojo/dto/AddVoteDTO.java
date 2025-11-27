package com.itmang.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author luo
 */

@Data
public class AddVoteDTO implements Serializable {

    @Schema(description = "投票标题")
    private String voteInTitle;
    @Schema(description = "投票内容")
    private String voteInContent;
    @Schema(description = "投票选项")
    private List<String> options;
    @Schema(description = "参与投票人id集合")
    private String userIds;
    @Schema(description = "投票开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;        // 开始时间，格式: yyyy-MM-dd HH:mm:ss
    @Schema(description = "投票结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;          // 结束时间，格式: yyyy-MM-dd HH:mm:ss
}