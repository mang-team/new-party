package com.itmang.pojo.vo;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Schema(name = "DatasVO",description = "资料VO")
@Data
public class DatasVO implements Serializable {

    @Schema(name = "number", description = "资料编码")
    private String number;
    @Schema(name = "title", description = "标题名")
    private String title;
    @Schema(name = "icon", description = "图标url")
    private String icon;
    @Schema(name = "content", description = "文档url")
    private String content;
    @Schema(name = "points", description = "学习积分")
    private Integer points;
    @Schema(name = "releaseName", description = "发布者名称")
    private String releaseName;
    @Schema(name = "releaseTime", description = "发布时间")
    private LocalDateTime releaseTime;
    @Schema(name = "status", description = "资料状态")
    private Integer status;
    @Schema(name = "updateName", description = "修改人名称")
    private String updateName;
    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;
}
