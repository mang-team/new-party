package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "UpdateExaminationPaperDTO", description = "编辑考试试题DTO")
public class UpdateExaminationPaperDTO {

    @Schema(name = "id", description = "id")
    private String id;
    @Schema(name = "paperName", description = "试题名称")
    private String paperName;
    @Schema(name = "userId", description = "用户id")
    private String userId;
    @Schema(name = "scoreValue", description = "各类题型分值集合")
    private String scoreValue;
    @Schema(name = "singleChoiceIds", description = "单选题id集合")
    private String singleChoiceIds;
    @Schema(name = "multipleChoiceIds", description = "多选题id集合")
    private String multipleChoiceIds;
    @Schema(name = "judgeIds", description = "判断题id集合")
    private String judgeIds;
    @Schema(name = "fillBlankIds", description = "填空题id集合")
    private String fillBlankIds;
    @Schema(name = "note", description = "试题备注")
    private String note;
}