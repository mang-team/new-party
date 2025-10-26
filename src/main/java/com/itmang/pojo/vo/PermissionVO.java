package com.itmang.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "PermissionVO", description = "权限信息VO")
public class PermissionVO {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "description", description = "权限描述")
    private String description;

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

}