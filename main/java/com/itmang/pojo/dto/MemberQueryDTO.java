package com.itmang.pojo.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "成员查询条件DTO")
public class MemberQueryDTO {

        @Schema(description = "成员ID")
        private String id;

        @Schema(description = "用户ID")
        private String userId;

        @Schema(description = "姓名")
        private String name;

        @Schema(description = "部门ID")
        private String departmentId;

        @Schema(description = "政治面貌 (1群众, 2共青团员, 3入党积极分子, 4发展对象, 5预备党员, 6正式党员)")
        private Integer politicalStatus;

        @Schema(description = "是否在校 (1在校, 2不在校)")
        private Integer isAtSchool;

        @Schema(description = "是否删除标记")
        private Integer isDelete;

        @Schema(name = "page", description = "当前页")
        private Integer page;
        @Schema(name = "pageSize", description = "每页大小")
        private Integer pageSize;


    }
