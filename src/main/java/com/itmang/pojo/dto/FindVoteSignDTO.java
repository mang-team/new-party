package com.itmang.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 投票信息查询/新增 DTO
 */
@Data
public class FindVoteSignDTO {

    @Schema(description = "投票人id")
    private String voterId;
    @Schema(description = "投票人名字")
    private String voterName;
    @Schema(description = "投票信息id")
    private String voteInformationId;
    @Schema(description = "投票信息名称")
    private String voteInformationName;
    @Schema(description = "投票选项")
    private String choose;
    @Schema(description = "当前页")
    private Integer pageNum;
    @Schema(description = "每页条数")
    private Integer pageSize;
}