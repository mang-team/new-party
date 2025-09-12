package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Schema(name = "BankUpdateDTO",description = "题目DTO")
@Data
public class BankUpdateDTO implements Serializable {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "type", description = "题目类型")
    private String type;
    @Schema(name = "question", description = "问题")
    private String question;
    @Schema(name = "questionOption", description = "选择题选项")
    private String questionOption;
    @Schema(name = "answerType", description = "答案类型")
    private String answerType;
}
