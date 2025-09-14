package com.itmang.service.user;


import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.LoginDTO;
import com.itmang.pojo.entity.User;


public interface UserService extends IService<User> {


    User login(LoginDTO loginDTO);
}
