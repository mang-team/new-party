package com.itmang.service.user.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmang.constant.MessageConstant;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;
import com.itmang.mapper.user.UserRoleMapper;
import com.itmang.pojo.entity.Role;
import com.itmang.mapper.user.RoleMapper;
import com.itmang.pojo.entity.UserRole;
import com.itmang.service.user.UserRoleService;
import com.itmang.utils.IdGenerate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private IdGenerate idGenerate;

    /**
     * 给用户分配角色
     *
     * @param userId
     * @param roleIds
     */
    @Override
    @Transactional
    public void assignRoles(String userId, String[] roleIds) {
        // 1. 检查用户是否已分配角色
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoleList = userRoleMapper.selectList(queryWrapper);
        
        // 如果用户已有角色，先删除旧的角色分配
        if (!userRoleList.isEmpty()) {
            for (UserRole userRole : userRoleList) {
                userRoleMapper.deleteById(userRole.getId());
            }
        }

        // 2. 添加新的用户角色关联
        for (String roleId : roleIds) {
            // 3. 判断角色是否存在
            Role role = roleMapper.selectById(roleId);
            if (role == null || role.getIsDelete() == 1) {
                throw new BaseException("角色不存在");
            }

            UserRole userRole = new UserRole();
            userRole.setId(idGenerate.nextUUID(userRole));
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setIsDelete(2);
            userRole.setCreateBy(BaseContext.getCurrentId());
            userRole.setCreateTime(LocalDateTime.now());
            userRole.setUpdateBy(BaseContext.getCurrentId());
            userRole.setUpdateTime(LocalDateTime.now());
            userRoleMapper.insert(userRole);
        }
    }
}