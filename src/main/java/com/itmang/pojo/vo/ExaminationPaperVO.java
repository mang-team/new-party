package com.itmang.pojo.vo;

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
@Schema(name = "ExaminationPaperVO", description = "考试试题VO")
public class ExaminationPaperVO {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "userId", description = "用户id")
    private String userId;
    @Schema(name = "userName", description = "用户名字")
    private String userName;
    @Schema(name = "paperName", description = "试题名称")
    private String paperName;
    @Schema(name = "questionQuantity", description = "题目数量")
    private Integer questionQuantity;
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

}