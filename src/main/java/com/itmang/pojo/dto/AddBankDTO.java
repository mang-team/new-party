package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Schema(name = "AddBankDTO",description = "题目DTO")
@Data
public class AddBankDTO implements Serializable {

    @Schema(name = "type", description = "题目类型（单选题 1，多选题 2，判断题 3，填空题 4）")
    private String type;
    @Schema(name = "question", description = "问题")
    private String question;
    @Schema(name = "questionOption", description = "选择题选项")
    private String questionOption;
    @Schema(name = "answerType", description = "答案类型")
    private String answerType;
}
