package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(description = "部门分页查询请求")
public class DepartmentPageQueryDTO {

@Schema(description = "部门名称")
                private String departmentName;

@Schema(description = "上级部门id")
                private String fatherDepartmentId;

@Schema(description = "创建人名称")
                private String createBy;

@Schema (description = "分页查询创建的开始时间")
@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                private LocalDateTime createTimeBegin;

@Schema (description = "分页查询创建的结束时间")
@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                private LocalDateTime createTimeEnd;
@Schema (description = "修改人名称")
                private String updateBy;
@Schema (description = "分页查询的修改开始时间")
@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                private LocalDateTime updateTimeBegin;
@Schema (description = "分页查询的修改结束时间")
@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                private LocalDateTime updateTimeEnd;

    // 修改为接口文档要求的字段名
    @Schema(description = "分页查询的页码，如果未指定，默认1")
    private Integer currentpage;

    @Schema(description = "分页查询的每页记录数")
    private Integer size;

}
