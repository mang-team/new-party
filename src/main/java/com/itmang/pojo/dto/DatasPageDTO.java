package com.itmang.pojo.dto;



import io.swagger.models.auth.In;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Schema(name = "DatasPageDTO",description = "资料DTO")
@Data
public class DatasPageDTO implements Serializable {

    @Schema(name = "number", description = "资料编码")
    private String number;
    @Schema(name = "title", description = "标题名")
    private String title;
    @Schema(name = "points", description = "学习积分")
    private Integer points;
    @Schema(name = "status", description = "资料状态")
    private Integer status;
    @Schema(name = "releaseTimeStart", description = "发布开始时间")
    private LocalDateTime releaseTimeStart;
    @Schema(name = "releaseTimeEnd", description = "发布结束时间")
    private LocalDateTime releaseTimeEnd;
    @Schema(name = "page", description = "当前页")
    private Integer page;
    @Schema(name = "pageSize", description = "每页大小")
    private Integer pageSize;


}
