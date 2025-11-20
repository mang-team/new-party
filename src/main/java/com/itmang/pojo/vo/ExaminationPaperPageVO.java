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
@Schema(name = "ExaminationPaperPageVO", description = "考试信息分页VO")
public class ExaminationPaperPageVO {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "paperName", description = "试题名称")
    private String paperName;
    @Schema(name = "questionQuantity", description = "题目数量")
    private String questionQuantity;
}