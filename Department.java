package com.itmang.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "Department", description = "部门表实体类")
public class Department {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "departmentName", description = "部门名称")
    private String departmentName;

    @Schema(name = "fatherDepartmentId", description = "上级部门id，外键（若为-1，则为最大部门）（默认值：-1）")
    private String fatherDepartmentId;

    @Schema(name = "createBy", description = "创建人")
    private String createBy;

    @Schema(name = "createTime", description = "创建时间")
    private LocalDateTime createTime;

    @Schema(name = "updateBy", description = "修改人")
    private String updateBy;

    @Schema(name = "updateTime", description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(name = "isDelete", description = "是否删除（1为删除，2为未删除）（默认为2）")
    private Integer isDelete;
}