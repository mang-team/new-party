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
@Schema(name = "ExaminationVO", description = "考试信息VO")
public class ExaminationVO {

    @Schema(name = "examinationName", description = "考试名称")
    private String examinationName;
    @Schema(name = "userNames", description = "用户名字集合")
    private String userNames;
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
    @Schema(name = "updateName", description = "更新人名字")
    private String updateName;
    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;
}