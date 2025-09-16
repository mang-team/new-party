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
@Schema(name = "ExaminationPaper", description = "考卷表实体类")
public class ExaminationPaper {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "number", description = "考卷编号，唯一键")
    private String number;

    @Schema(name = "examinationInformationId", description = "考试信息id，外键")
    private String examinationInformationId;

    @Schema(name = "userId", description = "考试用户id，外键")
    private String userId;

    @Schema(name = "isSubmit", description = "考卷状态（已提交为1，未提交为2）（默认为2）")
    private Integer isSubmit;

    @Schema(name = "questionQuantity", description = "题目数量（冗余字段）（默认值：0）")
    private Integer questionQuantity;

    @Schema(name = "answeredAlreadyQuantity", description = "已作答题目数量（冗余字段）（默认值：0）")
    private Integer answeredAlreadyQuantity;

    @Schema(name = "singleChoiceAnswers", description = "单选题答案集合(选A 1，选B 2，选C 3，选D 4)")
    private String singleChoiceAnswers;

    @Schema(name = "multipleChoiceAnswers", description = "多选题答案集合（选A 1，选B 2，选C 3，选D 4，选E 5，选F 6)（集合套集合，里面集合表示一个多选题答案）")
    private String multipleChoiceAnswers;

    @Schema(name = "judgeAnswers", description = "判断题答案集合(√ 1，× 2）")
    private String judgeAnswers;

    @Schema(name = "fillBlankAnswers", description = "填空题答案集合（字符串）")
    private String fillBlankAnswers;

    @Schema(name = "score", description = "得分（默认值为0）")
    private Integer score;

    @Schema(name = "notes", description = "考卷备注")
    private String notes;

    @Schema(name = "createTime", description = "创建时间")
    private LocalDateTime createTime;

    @Schema(name = "createBy", description = "创建人")
    private String createBy;

    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(name = "updateBy", description = "修改人")
    private String updateBy;

    @Schema(name = "isDelete", description = "是否删除（1为删除，2为未删除）（默认为2）")
    private Integer isDelete;
}