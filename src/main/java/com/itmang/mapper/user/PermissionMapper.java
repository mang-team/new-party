package com.itmang.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.dto.PermissionPageDTO;
import com.itmang.pojo.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 新增权限
     * @param permission
     */
    void insertPermission(Permission permission);

    /**
     * 根据ID查询权限
     * @param id
     * @return
     */
    Permission getPermissionById(String id);

    /**
     * 分页查询权限
     * @param permissionPageDTO
     * @return
     */
    List<Permission> pagePermission(PermissionPageDTO permissionPageDTO);

    List<String> getUserPermission(String id);
}