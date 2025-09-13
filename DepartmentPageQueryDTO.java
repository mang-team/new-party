package com.itmang.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "部门分页查询请求")
public class DepartmentPageQueryDTO {


                private String departmentName;


                private String fatherDepartmentId;


                private String createBy;


                private LocalDateTime createTimeBegin;


                private LocalDateTime createTimeEnd;
                private String updateBy;

                private LocalDateTime updateTimeBegin;

                private LocalDateTime updateTimeEnd;

    // 修改为接口文档要求的字段名
    @Schema(description = "分页查询的页码，如果未指定，默认1")
    private Integer currentpage;

    @Schema(description = "分页查询的每页记录数")
    private Integer size;

}
