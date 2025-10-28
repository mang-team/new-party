package com.itmang.service.user;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.PermissionAddDTO;
import com.itmang.pojo.dto.PermissionPageDTO;
import com.itmang.pojo.dto.PermissionUpdateDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Permission;
import com.itmang.pojo.vo.PermissionVO;

import java.util.List;

public interface PermissionService extends IService<Permission> {

    /**
     * 新增权限
     * @param permissionAddDTO
     */
    void addPermission(PermissionAddDTO permissionAddDTO);

    /**
     * 根据ID查询权限
     * @param id
     * @return
     */
    PermissionVO getPermissionById(String id);

    /**
     * 更新权限
     * @param permissionUpdateDTO
     */
    void updatePermission(PermissionUpdateDTO permissionUpdateDTO);

    /**
     * 删除权限（可批量）
     * @param ids
     */
    void deletePermission(String[] ids);

    /**
     * 分页查询权限
     * @param permissionPageDTO
     * @return
     */
    PageResult pagePermission(PermissionPageDTO permissionPageDTO);

    List<String> getUserPermission(String id);
}
