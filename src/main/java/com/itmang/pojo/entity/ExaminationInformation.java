package com.itmang.pojo.entity;

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
@Schema(name = "ExaminationInformation", description = "考试信息表实体类")
public class ExaminationInformation {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "type", description = "类型")
    private Integer type;

    @Schema(name = "examinationName", description = "考试名称")
    private String examinationName;

    @Schema(name = "userIds", description = "用户id集合，外键集合")
    private String userIds;

    @Schema(name = "scoreValue", description = "各类题型分值集合（单选题分值，多选题分值，判断题分值，填空题分值）[集合，按顺序传]")
    private String scoreValue;

    @Schema(name = "singleChoiceIds", description = "单选题id集合，外键集合")
    private String singleChoiceIds;

    @Schema(name = "multipleChoiceIds", description = "多选题id集合，外键集合")
    private String multipleChoiceIds;

    @Schema(name = "judgeIds", description = "判断题id集合，外键集合")
    private String judgeIds;

    @Schema(name = "fillBlankIds", description = "填空题id集合，外键集合")
    private String fillBlankIds;

    @Schema(name = "questionQuantity", description = "题目数量（冗余字段）（默认值：0）")
    private Integer questionQuantity;

    @Schema(name = "examinationInstruction", description = "考试说明")
    private String examinationInstruction;

    @Schema(name = "startTime", description = "考试开始时间")
    private LocalDateTime startTime;

    @Schema(name = "endTime", description = "考试结束时间")
    private LocalDateTime endTime;

    @Schema(name = "createBy", description = "创建人")
    private String createBy;

    @Schema(name = "createTime", description = "创建时间")
    private LocalDateTime createTime;

    @Schema(name = "updateBy", description = "修改人")
    private String updateBy;

    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(name = "note", description = "备注")
    private String note;

    @Schema(name = "isDelete", description = "是否删除（1为删除，2为未删除）（默认为2）")
    private Integer isDelete;
}