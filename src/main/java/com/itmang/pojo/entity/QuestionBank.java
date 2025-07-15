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
@Schema(name = "QuestionBank", description = "题库表实体类")
public class QuestionBank {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "type", description = "题目类型（单选题 1，多选题 2，判断题 3，填空题 4）")
    private String type;

    @Schema(name = "question", description = "问题")
    private String question;

    @Schema(name = "answerType", description = "答案类型（选A 1，选B 2，选C 3，选D 4，选E 5，选F 6，√ 7，× 8，填空答案直接字符串）")
    private String answerType;

    @Schema(name = "isChoose", description = "是否选用（1为被选用，2为未被选用）")
    private Integer isChoose;

    @Schema(name = "createBy", description = "创建人")
    private String createBy;

    @Schema(name = "createTime", description = "创建时间")
    private LocalDateTime createTime;

    @Schema(name = "updateBy", description = "修改人")
    private String updateBy;

    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(name = "isDelete", description = "是否删除（1为删除，2为未删除）（默认为2）")
    private Integer isDelete;
}