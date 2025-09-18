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
@Schema(name = "User", description = "用户表实体类")
public class User {

    @Schema(name = "id", description = "id，主键自增")
    private String id;

    @Schema(name = "openid", description = "小程序openid（保留字段）")
    private String openid;

    @Schema(name = "number", description = "学号/工号，唯一键")
    private String number;

    @Schema(name = "userName", description = "用户名")
    private String userName;

    @Schema(name = "roleId", description = "角色id，外键（默认：0）")
    private String roleId;

    @Schema(name = "memberId", description = "成员id，外键")
    private String memberId;

    @Schema(name = "image", description = "头像（默认一个头像url）")
    private String image;

    @Schema(name = "countNumber", description = "账号（先设置着）")
    private String countNumber;

    @Schema(name = "password", description = "密码（默认密码：123456，建议首次登录时强制修改密码）")
    private String password;

    @Schema(name = "status", description = "状态（1为正常，2为禁用）（默认为1）")
    private Integer status;

    @Schema(name = "isFirst", description = "是否第一次登录（默认为:Y）")
    private String isFirst;

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