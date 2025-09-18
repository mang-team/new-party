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
@Schema(name = "Permission", description = "权限表实体类")
public class Permission {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "describe", description = "权限描述")
    private String describe; // 注意：Java中建议避免使用关键字，可改为description

    @Schema(name = "url", description = "权限路径")
    private String url;

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