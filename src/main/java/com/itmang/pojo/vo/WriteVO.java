package com.itmang.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "WriteVO", description = "单项VO")
public class WriteVO {

    @Schema(name = "id", description = "题目id")
    private String id;
    @Schema(name = "question", description = "问题")
    private String question;
}