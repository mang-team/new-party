package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Schema(name = "PaperUpdateDTO",description = "编辑考卷DTO")
@Data
public class PaperUpdateDTO implements Serializable {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "answeredAlreadyQuantity", description = "已作答题目数量")
    private Integer answeredAlreadyQuantity;
    @Schema(name = "singleChoiceAnswers", description = "单选题答案集合")
    private String singleChoiceAnswers;
    @Schema(name = "multipleChoiceAnswers", description = "多选题答案集合")
    private String multipleChoiceAnswers;
    @Schema(name = "judgeAnswers", description = "判断题答案集合")
    private String judgeAnswers;
    @Schema(name = "fillBlankAnswers", description = "填空题答案集合（字符串）")
    private String fillBlankAnswers;
}
