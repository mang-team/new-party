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
@Schema(name = "PaperPageVO", description = "考卷分页VO")
public class PaperPageVO {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "number", description = "考卷编号")
    private String number;
    @Schema(name = "examinationName", description = "考试名称")
    private String examinationName;
    @Schema(name = "userName", description = "考生名字")
    private String userName;
    @Schema(name = "isSubmit", description = "考卷状态")
    private Integer isSubmit;
    @Schema(name = "questionQuantity", description = "题目数量")
    private Integer questionQuantity;
    @Schema(name = "answeredAlreadyQuantity", description = "已作答题目数量")
    private Integer answeredAlreadyQuantity;
    @Schema(name = "score", description = "得分")
    private Integer score;
    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;
}