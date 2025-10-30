package com.itmang.pojo.vo;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ClassName:UserPageVo
 * Package:com.itmang.pojo.vo
 * Description:
 *
 * @Author: 绯雾sama
 * @Create:2025/9/19 9:02
 * Version: 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserPageVo {

    private Integer id;

    private String userName;

    private String image;

    private String isFirst;

    private Integer status;

    private String password;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createUserName;

    private String updateUserName;
}
