package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateVoteRecordDTO implements Serializable {
    @Schema(description = "投票信息id")
    private String voteId;
    @Schema(description = "投票选项")
    private String choose;
    @Schema(description = "备注")
    private String notes;
}