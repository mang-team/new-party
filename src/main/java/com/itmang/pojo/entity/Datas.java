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
@Schema(name = "Datas", description = "资料表实体类")
public class Datas {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "number", description = "资料编码，唯一键")
    private String number;

    @Schema(name = "title", description = "标题名")
    private String title;

    @Schema(name = "icon", description = "图标url")
    private String icon;

    @Schema(name = "content", description = "文档url")
    private String content;

    @Schema(name = "points", description = "学习积分（默认：0）")
    private Integer points;

    @Schema(name = "releaseBy", description = "发布者，外键（用户id）")
    private String releaseBy;

    @Schema(name = "releaseTime", description = "发布时间")
    private LocalDateTime releaseTime;

    @Schema(name = "status", description = "资料状态（1为可阅读，2为不可阅读）（默认为1）")
    private Integer status;

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