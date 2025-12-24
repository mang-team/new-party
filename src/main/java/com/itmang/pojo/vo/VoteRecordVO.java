package com.itmang.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "VoteRecordVO", description = "投票记录VO")
public class VoteRecordVO {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "voteInformationId", description = "投票信息id")
    private String voteInformationId;
    @Schema(name = "userId", description = "用户id")
    private String userId;
    @Schema(name = "userName", description = "用户名字")
    private String userName;
    @Schema(name = "voteTime", description = "投票时间")
    private LocalDateTime voteTime;
    @Schema(name = "voteChoose", description = "投票选项")
    private String voteChoose;
    @Schema(name = "voteList", description = "投票各个选项信息")
    private List<ChoiceVO> voteList;

    @Data
    @Builder
    @Schema(name = "ChoiceVO", description = "单个选项VO")
    public static class ChoiceVO {
        @Schema(name = "names", description = "选项名字集合")
        private Map<String, String> names;
        @Schema(name = "count", description = "选项数量")
        private Integer count;
    }
}