package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "AddExaminationTemplateDTO", description = "考试模版DTO")
public class AddExaminationTemplateDTO {

    @Schema(name = "examinationName", description = "考试信息名称")
    private String examinationName;
    @Schema(name = "userId", description = "用户id")
    private String userId;
    @Schema(name = "scoreValue", description = "各类题型分值集合")
    private String scoreValue;
    @Schema(name = "examinationInstruction", description = "考试说明")
    private String examinationInstruction;
    @Schema(name = "note", description = "模版备注")
    private String note;
}