package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "RolePageDTO", description = "角色分页查询DTO")
public class RolePageDTO implements Serializable {

    @Schema(name = "page", description = "页码")
    private Integer page;

    @Schema(name = "pageSize", description = "每页显示记录数")
    private Integer pageSize;

    @Schema(name = "roleName", description = "角色名称")
    private String roleName;

}