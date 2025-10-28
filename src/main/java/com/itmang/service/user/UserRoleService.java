package com.itmang.service.user;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.entity.UserRole;

public interface UserRoleService extends IService<UserRole> {

    /**
     * 给用户分配角色
     * @param userId
     * @param roleIds
     */
    void assignRoles(String userId, String[] roleIds);
}