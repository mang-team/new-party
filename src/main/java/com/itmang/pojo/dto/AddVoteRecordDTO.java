package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class AddVoteRecordDTO implements Serializable {
    @Schema(description = "投票信息id")
    private String voteInformationId;
    @Schema(description = "投票人id集合")
    private String userIds;
}