package com.itmang.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "LoginVO",description = "用户登录VO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginVO {

    @Schema(name = "id",description = "用户id")
    private String id;

    @Schema(name = "name",description = "用户名")
    private String name;

    @Schema(name = "token",description = "用户token")
    private String token;

    @Schema(name = "isFirst",description = "是否第一次登录")
    private String isFirst;
}
