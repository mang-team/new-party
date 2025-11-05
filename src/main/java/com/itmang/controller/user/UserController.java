package com.itmang.controller.user;


import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itmang.annotation.GlobalInterceptor;
import com.itmang.constant.JwtClaimsConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.context.BaseContext;
import com.itmang.controller.BaseController;
import com.itmang.exception.BaseException;
import com.itmang.pojo.dto.LoginDTO;
import com.itmang.pojo.dto.UserDto;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.entity.User;
import com.itmang.pojo.vo.LoginVO;
import com.itmang.pojo.vo.UserQueryVo;
import com.itmang.properties.JwtProperties;
import com.itmang.service.user.UserService;
import com.itmang.config.CosConfig;
import com.itmang.utils.CosUtil;
import com.itmang.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("user/user")
@Tag(name = "用户相关接口")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;//导入jwt配置类

    @Autowired
    private CosUtil cosUtil;

    @Operation(summary = "登录接口")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        log.info("用户登录,登录的信息:{}", loginDTO);
        //先查询数据库是否有这个用户的信息
        User user = userService.login(loginDTO);
        //查询到了，直接生成jwt令牌，用户之后验证
        //1、使用键值对的方式存储信息
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());//将用户的id存储进声明中
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
        //修改： 将token持久化保存在cookie
//        saveToken2cookie(response, token);
        log.info("用户登录成功,返回数据:{}", loginVO);
        return Result.success(loginVO);
    }


    /**
     * 注册用户接口
     *
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
    @Operation(summary = "自动登录接口")
    @GetMapping("/autoLogin")
    public Result<LoginVO> autoLogin() {
        //检验token是否有效
        if (!isTokenValid()) {
            throw new BaseException(MessageConstant.USER_NOT_LOGIN);
        }
        //检验登录用户是否被禁用
        String userId = getUserIdFromToken();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userId);
        queryWrapper.eq(User::getStatus, 1);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            throw new BaseException(MessageConstant.USER_NOT_LOGIN);
        }
        //重置token
        String token = resetTokenTime();
//        saveToken2cookie(response,token);
        LoginVO loginVO = new LoginVO();
        loginVO.setId(userId);
        loginVO.setName(user.getUserName());
        loginVO.setToken(token);
        return Result.success(loginVO);
    }

    @Operation(summary = "退出登录接口")
    @PostMapping("/logout")
    public Result<Object> logout() {
        //将token加入黑名单中
        blacklistToken();
        //清除cookie持久化保存的token
//        cleanTokenFromCookie(response);
        return Result.success();
    }

    @Operation(summary = "编辑用户接口")
    @PostMapping("/editUser")
    @GlobalInterceptor(checkLogin = true)
    public Result<Object> editUser(@RequestBody UserDto userDto) {
        String userId = getUserIdFromToken();
        User user = new User();
        //修改： 为了与管理端的修改用户信息接口合并
        user.setId(userDto.getUserId());
        user.setUserName(userDto.getUserName());
        user.setImage(userDto.getImage());
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(userId);
        boolean isSuccess = userService.updateById(user);
        if (!isSuccess) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }
        return Result.success();
    }
//
//    @Operation(summary = "修改密码接口")
//    @PostMapping("/changePassword")
//    @GlobalInterceptor(checkLogin = true)
//    public Result<Object> changePassword(@RequestParam String password,@RequestParam String userId) {
//        String updateUserId = getUserIdFromToken();
//        User user = new User();
//        user.setId(userId);
//        user.setPassword(password);
//        user.setUpdateBy(updateUserId);
//        user.setUpdateTime(LocalDateTime.now());
//        boolean isSuccess = userService.updateById(user);
//        if (!isSuccess) {
//            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
//        }
//        return Result.success();
//    }

    @Operation(summary = "获取用户信息")
    @PostMapping("/getUserInfo")
    public Result<Object> getUserInfo() {
        String userId = BaseContext.getCurrentId();
        UserQueryVo userQueryVo = userService.getUserInfo(userId);
        BaseContext.removeCurrentId();
        return Result.success(userQueryVo);
    }

    /**
     * 上传头像接口
     *
     * @param avatarFile 头像文件
     * @return 包含头像URL的结果
     */
    @Operation(summary = "上传头像接口")
    @PostMapping("/uploadAvatar")
    @GlobalInterceptor(checkLogin = true)
    public Result<String> uploadAvatar(@RequestParam("avatarFile") MultipartFile avatarFile) {
        // 检查文件是否为空
        if (avatarFile.isEmpty()) {
            throw new BaseException("上传的文件不能为空");
        }

        try {
            // 获取用户ID
            String userId = BaseContext.getCurrentId();

            // 创建临时文件
            String originalFilename = avatarFile.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            String tmpPath = System.getProperty("user.dir") + File.separator + "tmp" + File.separator;

            // 将MultipartFile保存为临时文件
            if(!new File(tmpPath).exists()) {
                FileUtil.mkdir(tmpPath);
            }
            avatarFile.transferTo(new File(tmpPath + fileName));

            // 构造COS对象键（路径）
            String cosKey = "/avatar/" + userId + "/" + fileName;

            // 上传到COS
            String avatarUrl = cosUtil.uploadFile(cosKey, new File(tmpPath + fileName));

            // 删除临时文件
            FileUtil.del(new File(tmpPath + fileName));

            // 检查上传是否成功
            if (avatarUrl == null) {
                throw new BaseException("头像上传失败");
            }

            // 更新用户头像URL
            User user = new User();
            user.setId(userId);
            user.setImage(avatarUrl);
            user.setUpdateTime(LocalDateTime.now());
            user.setUpdateBy(userId);
            boolean isSuccess = userService.updateById(user);
            if (!isSuccess) {
                throw new BaseException("用户信息更新失败");
            }

            return Result.success(avatarUrl);
        } catch (IOException e) {
            log.error("上传头像失败: {}", e.getMessage());
            throw new BaseException("上传头像失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("上传头像异常: {}", e.getMessage());
            throw new BaseException("上传头像异常: " + e.getMessage());
        }
    }
}
