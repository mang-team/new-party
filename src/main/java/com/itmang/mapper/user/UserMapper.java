package com.itmang.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.dto.PageUserDto;
import com.itmang.pojo.entity.Datas;
import com.itmang.pojo.entity.User;
import com.itmang.pojo.vo.UserQueryVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {


    /**
     * 根据用户id查询用户名称
     * @param userIds
     * @return
     */
    List<String> queryUserNames(String[] userIds);

    List<User> getUserPage(PageUserDto pageUserDto);

    UserQueryVo getUserInfo(String userId);
}
