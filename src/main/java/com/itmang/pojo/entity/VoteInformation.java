package com.itmang.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
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
@Schema(name = "VoteInformation", description = "投票信息表实体类")
@TableName(autoResultMap = true)
public class VoteInformation {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "voteTitle", description = "投票标题")
    private String voteTitle;

    @Schema(name = "voteContent", description = "投票说明")
    private String voteContent;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> options;

    @Schema(name = "startTime", description = "投票开始时间")
    private LocalDateTime startTime;

    @Schema(name = "endTime", description = "投票结束时间（默认值：null）")
    private LocalDateTime endTime;

    @Schema(name = "createBy", description = "发布人")
    private String createBy;

    @Schema(name = "createTime", description = "发布时间")
    private LocalDateTime createTime;

    @Schema(name = "updateBy", description = "修改人")
    private String updateBy;

    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(name = "isDelete", description = "是否删除（1为删除，2为未删除）（默认为2）")
    private Integer isDelete;
}