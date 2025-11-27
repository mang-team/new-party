package com.itmang.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "VoteInformationPageVO", description = "投票信息分页VO")
public class VoteInformationPageVO {


    @Schema(name = "id", description = "id")
    private String id;

    @Schema(name = "createName", description = "发布人名字")
    private String createName;

    @Schema(name = "voteTitle", description = "投票标题")
    private String voteTitle;

    @Schema(name = "voteContent", description = "投票说明")
    private String voteContent;

    @Schema(name = "startTime", description = "投票开始时间")
    private LocalDateTime startTime;

    @Schema(name = "endTime", description = "投票结束时间（默认值：null）")
    private LocalDateTime endTime;

}