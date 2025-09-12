package com.itmang.service.user;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.LoginDTO;
import com.itmang.pojo.entity.User;
import org.springframework.stereotype.Service;


public interface UserService extends IService<User> {


    /**
     * 登录
     * @param loginDTO
     * @return
     */
    User login(LoginDTO loginDTO);
}
