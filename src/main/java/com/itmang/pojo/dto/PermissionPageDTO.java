package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "PermissionPageDTO", description = "权限分页查询DTO")
public class PermissionPageDTO implements Serializable {

    @Schema(name = "page", description = "页码")
    private Integer page;

    @Schema(name = "pageSize", description = "每页显示记录数")
    private Integer pageSize;

    @Schema(name = "url", description = "权限路径")
    private String url;

}