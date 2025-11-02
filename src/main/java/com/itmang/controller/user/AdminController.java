package com.itmang.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itmang.annotation.ActionLog;
import com.itmang.annotation.GlobalInterceptor;
import com.itmang.constant.JwtClaimsConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;
import com.itmang.pojo.dto.AddUserDto;
import com.itmang.pojo.dto.LoginDTO;
import com.itmang.pojo.dto.PageUserDto;
import com.itmang.pojo.dto.UserDto;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.entity.User;
import com.itmang.pojo.vo.LoginVO;
import com.itmang.pojo.vo.UserQueryVo;
import com.itmang.properties.JwtProperties;
import com.itmang.service.user.UserService;
import com.itmang.service.user.UserRoleService;
import com.itmang.utils.IdGenerate;
import com.itmang.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:AdminController
 * Package:com.itmang.controller.user
 * Description:
 *
 * @Author: 绯雾sama
 * @Create:2025/10/28 11:17
 * Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("admin/user")
@Tag(name = "管理员相关接口")
public class AdminController {

    @Resource
    private UserService userService;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private IdGenerate idGenerate;

    @Resource
    private JwtProperties jwtProperties;

    @Operation(summary = "登录接口")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        log.info("用户登录,登录的信息:{}", loginDTO);
        //先查询数据库是否有这个用户的信息
        User user = userService.adminLogin(loginDTO,request.getRequestURI());
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


    @Operation(summary = "编辑用户接口")
    @PostMapping("/editUser")
    @GlobalInterceptor(checkLogin = true, checkPermission = true)
    @ActionLog(description = "编辑用户", operationType = 2)
    public Result<Object> editUser(@RequestBody UserDto userDto) {
        String userId = BaseContext.getCurrentId();
        User user = new User();
        //修改： 为了与管理端的修改用户信息接口合并
        user.setId(userDto.getUserId());
        user.setUserName(userDto.getUserName());
        user.setImage(userDto.getImage());
        user.setPassword(userDto.getPassword() == null ? null : DigestUtils.md5DigestAsHex(userDto.getPassword().getBytes()));
        user.setStatus(userDto.getStatus());
        user.setIsFirst(userDto.getIsFirst());
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(userId);
        boolean isSuccess = userService.updateById(user);
        if (!isSuccess) {
            throw new BaseException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        return Result.success();
    }

    @Operation(summary = "改变用户状态")
    @PostMapping("/changeUserStatus/{userId}")
    @GlobalInterceptor(checkLogin = true, checkPermission = true)
    @ActionLog(description = "改变用户状态", operationType = 2)
    public Result<Object> changeUserStatus(@PathVariable String userId) {
        String updateUserId = BaseContext.getCurrentId();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BaseException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        user.setStatus(user.getStatus() == 0 ? 1 : 0);
        user.setUpdateBy(updateUserId);
        user.setUpdateTime(LocalDateTime.now());
        userService.updateById(user);
        return Result.success();
    }

    @Operation(summary = "分页获取用户列表")
    @PostMapping("/getUserPage")
    @GlobalInterceptor(checkLogin = true, checkPermission = true)
    @ActionLog(description = "分页获取用户列表", operationType = 1)
    public Result<Object> getUserPage(@RequestBody PageUserDto pageUserDto) {
        PageResult pageResult = userService.getUserPage(pageUserDto);
        return Result.success(pageResult);
    }

    @Operation(summary = "批量新增用户")
    @PostMapping("/addUserBatch")
    @GlobalInterceptor(checkLogin = true, checkPermission = true)
    @ActionLog(description = "分页获取用户列表", operationType = 3)
    public Result<Object> addUser(@RequestBody List<AddUserDto> addUserDtoList) {
        List<User> userList = new ArrayList<>();
        for (AddUserDto addUserDto :
                addUserDtoList) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getNumber, addUserDto.getNumber());
            long count = userService.count(queryWrapper);
            if (count != 0) {
                throw new BaseException(MessageConstant.USER_PART_ADD_FAILED);
            }
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserName, addUserDto.getUserName());
            count = userService.count(queryWrapper);
            if (count != 0) {
                throw new BaseException(MessageConstant.USER_PART_ADD_FAILED);
            }
            User user = new User();
            user.setNumber(addUserDto.getNumber());
            user.setUserName(addUserDto.getUserName());
            user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
            user.setCreateBy(BaseContext.getCurrentId());
            user.setCreateTime(LocalDateTime.now());
            user.setId(idGenerate.nextId(user).toString());
            userList.add(user);
        }
        userService.saveBatch(userList);
        return Result.success();
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/getUserInfo")
    @GlobalInterceptor(checkLogin = true, checkPermission = true)
    @ActionLog(description = "获取用户信息", operationType = 1)
    public Result<Object> getUserInfo(@RequestParam String userId) {
        UserQueryVo userQueryVo = userService.getUserInfo(userId);
        BaseContext.removeCurrentId();
        return Result.success(userQueryVo);
    }

    /**
     * 给用户分配角色
     *
     * @param userId
     * @param roleIds
     * @return
     */
    @Operation(summary = "给用户分配角色")
    @PostMapping("/assignRoles")
    @GlobalInterceptor(checkLogin = true, checkPermission = true)
    @ActionLog(description = "给用户分配角色", operationType = 3)
    public Result assignRoles(@RequestParam String userId, @RequestParam String roleIds) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        if (roleIds == null || roleIds.trim().isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        log.info("给用户分配角色: userId={}, roleIds={}", userId, roleIds);
        //将roleIds的参数分割成数组
        String[] roleIdArray = roleIds.split(",");
        userRoleService.assignRoles(userId, roleIdArray);
        return Result.success();
    }
}
