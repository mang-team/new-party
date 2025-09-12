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
@Schema(name = "VoteRecord", description = "投票记录表实体类")
public class VoteRecord {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "voteInformationId", description = "投票信息id，外键")
    private String voteInformationId;

    @Schema(name = "userId", description = "用户id，外键")
    private String userId;

    @Schema(name = "choose", description = "选择（用集合记录选择选项的序号）")
    private String choose; // 存储JSON格式的选项序号集合

    @Schema(name = "notes", description = "备注")
    private String notes;

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