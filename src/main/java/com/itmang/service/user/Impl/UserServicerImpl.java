package com.itmang.service.user.Impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.AdminConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.StatusConstant;
import com.itmang.exception.PasswordErrorException;
import com.itmang.exception.UserNotLoginException;
import com.itmang.mapper.user.PermissionMapper;
import com.itmang.mapper.user.UserMapper;
import com.itmang.pojo.dto.LoginDTO;
import com.itmang.pojo.dto.PageUserDto;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.User;
import com.itmang.pojo.vo.UserQueryVo;
import com.itmang.service.user.PermissionService;
import com.itmang.service.user.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import com.itmang.exception.AccountNotFoundException;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class UserServicerImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private UserMapper userMapper;

    @Resource
    private PermissionService permissionService;

    @Resource
    private RedisTemplate redisTemplate;


    /**
     * 登录
     *
     * @param loginDTO
     * @return
     */
    public User login(LoginDTO loginDTO) {
        //查询用户的信息是否存在
        String number = loginDTO.getNumber();
        String password = loginDTO.getPassword();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("number", number));
        //判断是否查询到了
        if (user == null) {
            //没有查询到，则返回信息
            log.info("用户登录失败，用户名不存在");
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_OR_PASSWORD_ERROR);
        }
        //将明文密码转成md5的加密数组
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) { //将密码进行比对
            //密码错误
            log.info("用户登录失败，密码错误");
            throw new PasswordErrorException(MessageConstant.ACCOUNT_OR_PASSWORD_ERROR);
        }
        //判断账号是否被锁定
        if (user.getStatus().equals(StatusConstant.DISABLE)) {
            //账号被锁定
            log.info("用户登录失败，账号被冻结");
            throw new UserNotLoginException(MessageConstant.ACCOUNT_LOCKED);
        }
        //将用户权限存储到redis中
        List<String> permissionUrlList = permissionService.getUserPermission(user.getId());
        redisTemplate.opsForValue().set(AdminConstant.USER_PERMISSION_KEY + user.getId(), JSON.toJSONString(permissionUrlList), 1, TimeUnit.HOURS);
        //3、返回实体对象
        return user;
    }

    @Override
    public PageResult getUserPage(PageUserDto pageUserDto) {
        PageHelper.startPage(pageUserDto.getCurrent(), pageUserDto.getSize());
        List<User> userList = userMapper.getUserPage(pageUserDto);
        PageInfo<User> userPage = new PageInfo<>(userList);
        return new PageResult(userPage.getTotal(), userPage.getList());
    }

    @Override
    public UserQueryVo getUserInfo(String userId) {
        return userMapper.getUserInfo(userId);
    }


}
