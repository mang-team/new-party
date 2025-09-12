package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Schema(name = "DatasDTO",description = "资料DTO")
@Data
public class DatasDTO implements Serializable {

    @Schema(name = "id",description = "资料id")
    private String id;
    @Schema(name = "title",description = "标题名")
    private String title;
    @Schema(name = "icon",description = "图标url")
    private String icon;
    @Schema(name = "content",description = "文档url")
    private String content;
    @Schema(name = "points",description = "学习积分")
    private String points;
}
