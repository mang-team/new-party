package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "PermissionAddDTO", description = "新增权限DTO")
public class PermissionAddDTO implements Serializable {

    @Schema(name = "description", description = "权限描述")
    private String description;

    @Schema(name = "url", description = "权限路径")
    private String url;

}