package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "PermissionUpdateDTO", description = "更新权限DTO")
public class PermissionUpdateDTO implements Serializable {

    @Schema(name = "id", description = "权限ID")
    private String id;

    @Schema(name = "description", description = "权限描述")
    private String description;

    @Schema(name = "url", description = "权限路径")
    private String url;

}