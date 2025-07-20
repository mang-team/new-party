package com.itmang.controller.user;


import com.itmang.constant.JwtClaimsConstant;
import com.itmang.pojo.dto.LoginDTO;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.entity.User;
import com.itmang.pojo.vo.LoginVO;
import com.itmang.properties.JwtProperties;
import com.itmang.service.user.UserService;
import com.itmang.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("user/user")
@Tag(name ="用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;//导入jwt配置类

    @Operation(summary = "登录接口")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO){
        log.info("用户登录,登录的信息:{}", loginDTO);
        //先查询数据库是否有这个用户的信息
        User user = userService.login(loginDTO);
        //查询到了，直接生成jwt令牌，用户之后验证
        //1、使用键值对的方式存储信息
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());//将用户的id存储进声明中
        //生成token令牌
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),//设置关键字
                jwtProperties.getAdminTtl(),//设置token有效时间
                claims);//设置声明信息（键值对）
        LoginVO loginVO = LoginVO.builder()
                .id(user.getId())
                .name(user.getUserName())
                .token(token)
                .build();
        log.info("用户登录成功,返回数据:{}", loginVO);
        return Result.success(loginVO);
    }


    /**
     * 注册用户接口
     * @param registerUserDTO
     * @return
     */
//    @Operation(summary = "注册接口")
//    @PostMapping("/register")
//    public Result save(@RequestBody RegisterUserDTO registerUserDTO){
//        log.info("注册用户信息:{}", registerUserDTO);
//        userService.register(registerUserDTO);
//        return Result.success();
//    }


}
