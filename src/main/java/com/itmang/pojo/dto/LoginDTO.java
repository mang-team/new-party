package com.itmang.pojo.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Schema(name = "LoginDTO",description = "用户登录DTO")
@Data
public class LoginDTO implements Serializable {

    @Schema(name = "number",description = "账号")
    private String number;
    @Schema(name = "password",description = "密码")
    private String password;

}
