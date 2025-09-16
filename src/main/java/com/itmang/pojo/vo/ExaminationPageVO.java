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
@Schema(name = "ExaminationPageVO", description = "考试信息分页VO")
public class ExaminationPageVO {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "examinationName", description = "考试名称")
    private String examinationName;
    @Schema(name = "questionQuantity", description = "题目数量")
    private String questionQuantity;
    @Schema(name = "startTime", description = "考试开始时间")
    private LocalDateTime startTime;
    @Schema(name = "endTime", description = "考试结束时间")
    private LocalDateTime endTime;
    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;
}