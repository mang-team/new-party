package com.itmang.service.user;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.LoginDTO;
import com.itmang.pojo.dto.PageUserDto;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.User;
import com.itmang.pojo.vo.UserQueryVo;
import org.springframework.stereotype.Service;


public interface UserService extends IService<User> {


    /**
     * 登录
     * @param loginDTO
     * @return
     */
    User login(LoginDTO loginDTO);

    PageResult getUserPage(PageUserDto pageUserDto);

    UserQueryVo getUserInfo(String userId);

    User adminLogin(LoginDTO loginDTO,String uri);
}
