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
@Schema(name = "PaperVO", description = "考卷VO")
public class PaperVO {

    @Schema(name = "number", description = "考卷编号，唯一键")
    private String number;
    @Schema(name = "examinationInformationName", description = "考试名称")
    private String examinationInformationName;
    @Schema(name = "userName", description = "考生名字")
    private String userName;
    @Schema(name = "isSubmit", description = "考卷状态")
    private Integer isSubmit;
    @Schema(name = "questionQuantity", description = "题目数量")
    private Integer questionQuantity;
    @Schema(name = "answeredAlreadyQuantity", description = "已作答题目数量")
    private Integer answeredAlreadyQuantity;
    @Schema(name = "singleChoiceAnswers", description = "单选题答案集合")
    private String singleChoiceAnswers;
    @Schema(name = "multipleChoiceAnswers", description = "多选题答案集合")
    private String multipleChoiceAnswers;
    @Schema(name = "judgeAnswers", description = "判断题答案集合")
    private String judgeAnswers;
    @Schema(name = "fillBlankAnswers", description = "填空题答案集合")
    private String fillBlankAnswers;
    @Schema(name = "score", description = "得分（默认值为0）")
    private Integer score;
    @Schema(name = "notes", description = "考卷备注")
    private String notes;
    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;
    @Schema(name = "updateName", description = "修改人名字")
    private String updateName;

}