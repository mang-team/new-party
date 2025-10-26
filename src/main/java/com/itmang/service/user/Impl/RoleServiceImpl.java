package com.itmang.service.user.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.PageConstant;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;
import com.itmang.mapper.user.PermissionMapper;
import com.itmang.mapper.user.RoleMapper;
import com.itmang.mapper.user.RolePermissionMapper;
import com.itmang.mapper.user.UserRoleMapper;
import com.itmang.pojo.dto.RoleAddDTO;
import com.itmang.pojo.dto.RolePageDTO;
import com.itmang.pojo.dto.RoleUpdateDTO;
import com.itmang.pojo.entity.*;
import com.itmang.pojo.vo.RoleVO;
import com.itmang.service.user.RoleService;
import com.itmang.utils.IdGenerate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private IdGenerate idGenerate;

    /**
     * 新增角色
     *
     * @param roleAddDTO
     */
    @Override
    public void addRole(RoleAddDTO roleAddDTO) {
        // 1. 校验参数
        if (roleAddDTO.getRoleName() == null || roleAddDTO.getRoleName().trim().isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }

        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleName, roleAddDTO.getRoleName());
        queryWrapper.eq(Role::getIsDelete, 2);
        Long count = roleMapper.selectCount(queryWrapper);
        // 2. 检查角色名称是否已存在
        if (count != 0) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }

        // 3. 转换DTO为Entity并设置创建信息
        Role role = new Role();
        IdGenerate idGenerate = new IdGenerate();
        role.setId(idGenerate.nextUUID(Role.class));
        role.setRoleName(roleAddDTO.getRoleName());
        role.setCreateBy(BaseContext.getCurrentId());
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateBy(BaseContext.getCurrentId());
        role.setUpdateTime(LocalDateTime.now());
        role.setIsDelete(2); // 未删除

        // 4. 保存角色
        roleMapper.insert(role);
    }

    /**
     * 根据ID查询角色
     *
     * @param id
     * @return
     */
    @Override
    public RoleVO getRoleById(String id) {
        Role role = roleMapper.selectById(id);
        if (role == null || role.getIsDelete() == 1) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }

        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(role, roleVO);
        return roleVO;
    }

    /**
     * 更新角色
     *
     * @param roleUpdateDTO
     */
    @Override
    @Transactional
    public void updateRole(RoleUpdateDTO roleUpdateDTO) {
        // 1. 检查角色是否存在
        Role originalRole = roleMapper.selectById(roleUpdateDTO.getId());
        if (originalRole == null || originalRole.getIsDelete() == 1) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }

        // 2. 检查角色名称是否已存在（排除自己）
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleName, roleUpdateDTO.getRoleName());
        queryWrapper.eq(Role::getIsDelete, 2);
        Role existingRole = roleMapper.selectOne(queryWrapper);
        if (existingRole != null && !existingRole.getId().equals(roleUpdateDTO.getId())) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }

        // 3. 转换DTO为Entity并更新
        Role role = new Role();
        role.setId(roleUpdateDTO.getId());
        role.setRoleName(roleUpdateDTO.getRoleName());
        role.setUpdateBy(BaseContext.getCurrentId());
        role.setUpdateTime(LocalDateTime.now());

        roleMapper.updateById(role);
    }

    /**
     * 删除角色（可批量）
     *
     * @param ids
     */
    @Override
    public void deleteRole(String[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }

        for (String id : ids) {
            // 1. 检查角色是否存在
            Role role = roleMapper.selectById(id);
            if (role == null || role.getIsDelete() == 1) {
                throw new BaseException(MessageConstant.PARAMETER_ERROR);
            }

            // 2. 判断角色是否正在被用户使用
            LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserRole::getRoleId, id);
            Long count = userRoleMapper.selectCount(queryWrapper);
            if(count != 0){
                throw new BaseException("此角色正在被使用");
            }

            // 3. 删除角色
            Role newRole = new Role();
            newRole.setId(id);
            newRole.setIsDelete(2);
            roleMapper.updateById(newRole);

            // 4. 删除角色权限关联
            LambdaQueryWrapper<RolePermission> queryWrapper2 = new LambdaQueryWrapper<>();
            RolePermission rolePermission = new RolePermission();
            queryWrapper2.eq(RolePermission::getRoleId, role.getId());
            rolePermission.setIsDelete(1);
            rolePermissionMapper.update(rolePermission, queryWrapper2);
        }
    }

    /**
     * 分页查询角色
     *
     * @param rolePageDTO
     * @return
     */
    @Override
    public PageResult pageRole(RolePageDTO rolePageDTO) {
        // 设置默认分页参数
        if (rolePageDTO.getPage() == null || rolePageDTO.getPage() <= 0) {
            rolePageDTO.setPage(PageConstant.PAGE_NUM);
        }
        if (rolePageDTO.getPageSize() == null || rolePageDTO.getPageSize() <= 0) {
            rolePageDTO.setPageSize(PageConstant.PAGE_SIZE);
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(rolePageDTO.getPage(), rolePageDTO.getPageSize());
        // 执行分页查询
        List<RoleVO> roleList = roleMapper.pageQuery(rolePageDTO);
        PageInfo<RoleVO> roleVOPageInfo = new PageInfo<>(roleList);

        return new PageResult(roleVOPageInfo.getTotal(), roleVOPageInfo.getList());
    }

    /**
     * 给角色分配权限
     *
     * @param roleId
     * @param permissionIds
     */
    @Override
    @Transactional
    public void assignPermissions(String roleId, String[] permissionIds) {
        // 1. 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null || role.getIsDelete() == 1) {
            throw new BaseException("角色不存在");
        }

        // 2. 查询角色现有的权限
        LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RolePermission::getRoleId, role.getId());
        List<RolePermission> rolePermissionList = rolePermissionMapper.selectList(queryWrapper);
        Map<String, RolePermission> rolePermissionMap =
                rolePermissionList.stream().collect(Collectors.toMap(RolePermission::getPermissionId, p -> p));

        // 3. 筛选出哪些是新增的权限
        List<String> newPermissionIds = new ArrayList<>();
        for (String permissionId : permissionIds) {
            if (!rolePermissionMap.containsKey(permissionId)) {
                newPermissionIds.add(permissionId);
            }
        }

        // 4. 添加新的角色权限关联
        for (String permissionId : newPermissionIds) {
            // 5. 判断权限是否存在
            Permission permission = permissionMapper.getPermissionById(permissionId);
            if (permission == null) {
                throw new BaseException("权限不存在");
            }

            RolePermission rolePermission = new RolePermission();
            rolePermission.setId(idGenerate.nextUUID(rolePermission));
            rolePermission.setRoleId(role.getId());
            rolePermission.setPermissionId(permissionId);
            rolePermission.setIsDelete(2);
            rolePermission.setCreateBy(BaseContext.getCurrentId());
            rolePermission.setCreateTime(LocalDateTime.now());
            rolePermission.setUpdateBy(BaseContext.getCurrentId());
            rolePermission.setUpdateTime(LocalDateTime.now());
            rolePermissionMapper.insert(rolePermission);
        }
    }
}
