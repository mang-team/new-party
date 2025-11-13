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
@Schema(name = "ChoiceVO", description = "选项VO")
public class ChoiceVO {

    @Schema(name = "id", description = "题目id")
    private String id;
    @Schema(name = "question", description = "问题")
    private String question;
    @Schema(name = "questionOption", description = "选择题选项")
    private List<String> questionOption;
    @Schema(name = "answer", description = "答案")
    private String answer;
    @Schema(name = "note", description = "解析")
    private String note;

}