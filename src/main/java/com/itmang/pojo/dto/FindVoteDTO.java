package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FindVoteDTO implements Serializable {
    @Schema(description = "发起用户id")
    private String userId;
    @Schema(description = "发起用户名字")
    private String userName;
    @Schema(description = "参与投票用户id")
    private String voterId;
    @Schema(description = "投票标题")
    private String voteTitle;
    @Schema(description = "投票开始时间开始时间")
    private LocalDateTime startTimeStart;
    @Schema(description = "投票开始时间结束时间")
    private LocalDateTime startTimeEnd;
    @Schema(description = "投票结束时间开始时间")
    private LocalDateTime endTimeStart;
    @Schema(description = "投票结束时间结束时间")
    private LocalDateTime endTimeEnd;
    @Schema(description = "分页页码")
    private Integer pageNum;
    @Schema(description = "分页大小")
    private Integer pageSize;
}