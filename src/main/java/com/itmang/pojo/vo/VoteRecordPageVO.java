package com.itmang.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "VoteRecordPageVO", description = "投票记录表分页VO")
public class VoteRecordPageVO {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "userId", description = "用户id")
    private String userId;
    @Schema(name = "userName", description = "用户名字")
    private String userName;
}