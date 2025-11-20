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
@Schema(name = "ExaminationTemplateVO", description = "考试模版VO")
public class ExaminationTemplateVO {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "userId", description = "用户id")
    private String userId;
    @Schema(name = "userName", description = "用户名称")
    private String userName;
    @Schema(name = "examinationName", description = "考试名称")
    private String examinationName;
    @Schema(name = "scoreValue", description = "各类题型分值集合")
    private String scoreValue;
    @Schema(name = "examinationInstruction", description = "考试说明")
    private String examinationInstruction;
    @Schema(name = "note", description = "备注")
    private String note;
    @Schema(name = "updateName", description = "更新人名字")
    private String updateName;
    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;

}