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
@Schema(name = "QuestionBank", description = "题目VO")
public class  BankVO {

    @Schema(name = "type", description = "题目类型")
    private String type;
    @Schema(name = "question", description = "问题")
    private String question;
    @Schema(name = "questionOption", description = "选择题选项")
    private String questionOption;
    @Schema(name = "answerType", description = "答案类型")
    private String answerType;
    @Schema(name = "isChoose", description = "是否选用（1为被选用，2为未被选用）")
    private Integer isChoose;
    @Schema(name = "updateName", description = "修改人名称")
    private String updateName;
    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;

}