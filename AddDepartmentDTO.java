package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

@Schema(description = "部门创建请求")
public class AddDepartmentDTO {


        @Schema(description = "部门名称", required = true)
        @NotBlank(message = "部门名称不能为空")
        @Size(max = 20, message = "部门名称长度不能超过20个字符")
        private String departmentName;

        @Schema(description = "上级部门id")
        private String fatherDepartmentId;

    }

