package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "RoleAddDTO", description = "新增角色DTO")
public class RoleAddDTO implements Serializable {

    @Schema(name = "roleName", description = "角色名称")
    private String roleName;

}