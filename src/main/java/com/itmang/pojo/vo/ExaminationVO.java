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
@Schema(name = "ExaminationVO", description = "考试信息VO")
public class ExaminationVO {

    @Schema(name = "examinationName", description = "考试名称")
    private String examinationName;
    @Schema(name = "userNames", description = "用户名字集合")
    private String userNames;
    @Schema(name = "scoreValue", description = "各类题型分值集合")
    private String scoreValue;
    @Schema(name = "singleChoiceIds", description = "单选题题目集合")
    private List<ChoiceVO> singleChoice;
    @Schema(name = "multipleChoiceIds", description = "多选题题目集合")
    private List<ChoiceVO> multipleChoice;
    @Schema(name = "judgeIds", description = "判断题题目集合")
    private List<WriteVO> judge;
    @Schema(name = "fillBlankIds", description = "填空题题目集合")
    private List<WriteVO> fillBlank;
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