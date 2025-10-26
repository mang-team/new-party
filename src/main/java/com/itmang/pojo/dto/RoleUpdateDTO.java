package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "RoleUpdateDTO", description = "更新角色DTO")
public class RoleUpdateDTO implements Serializable {

    @Schema(name = "id", description = "角色ID")
    private String id;

    @Schema(name = "roleName", description = "角色名称")
    private String roleName;

}