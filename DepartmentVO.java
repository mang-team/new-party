package com.itmang.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "部门详细信息VO")
public class DepartmentVO {
        @Schema(description = "部门ID")
        private String id;

        @Schema(description = "部门名称")
        private String departmentName;

        @Schema(description = "上级部门ID")
        private String fatherDepartmentId;

        @Schema(description = "上级部门名称")
        private String fatherDepartmentName;

        @Schema(description = "创建人")
        private String createBy;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;

        @Schema(description = "修改人")
        private String updateBy;

        @Schema(description = "修改时间")
        private LocalDateTime updateTime;

        @Schema(description = "是否删除（1为删除，2为未删除）")
        private Integer isDelete;
        @Schema(description = "状态显示")
        private String statusDisplay;
//不知道要不要衍生出来
//        @Schema(description = "子部门数量")
//        private Integer childrenCount;
//
//        @Schema(description = "部门成员数量")
//        private Integer memberCount;



}
