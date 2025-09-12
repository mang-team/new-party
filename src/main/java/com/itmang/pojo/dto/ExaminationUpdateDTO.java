package com.itmang.pojo.dto;

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
@Schema(name = "ExaminationUpdateDTO", description = "考试信息编辑DTO")
public class ExaminationUpdateDTO {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "examinationName", description = "考试名称")
    private String examinationName;
    @Schema(name = "userIds", description = "用户id集合")
    private String userIds;
    @Schema(name = "scoreValue", description = "各类题型分值集合")
    private String scoreValue;
    @Schema(name = "singleChoiceIds", description = "单选题id集合")
    private String singleChoiceIds;
    @Schema(name = "multipleChoiceIds", description = "多选题id集合")
    private String multipleChoiceIds;
    @Schema(name = "judgeIds", description = "判断题id集合")
    private String judgeIds;
    @Schema(name = "fillBlankIds", description = "填空题id集合")
    private String fillBlankIds;
    @Schema(name = "examinationInstruction", description = "考试说明")
    private String examinationInstruction;
    @Schema(name = "startTime", description = "考试开始时间")
    private LocalDateTime startTime;
    @Schema(name = "endTime", description = "考试结束时间")
    private LocalDateTime endTime;
}