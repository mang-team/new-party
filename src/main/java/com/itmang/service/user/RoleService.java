package com.itmang.service.user;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.RoleAddDTO;
import com.itmang.pojo.dto.RolePageDTO;
import com.itmang.pojo.dto.RoleUpdateDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Role;
import com.itmang.pojo.vo.RoleVO;

import java.util.List;

public interface RoleService extends IService<Role> {

    /**
     * 新增角色
     * @param roleAddDTO
     */
    void addRole(RoleAddDTO roleAddDTO);

    /**
     * 根据ID查询角色
     * @param id
     * @return
     */
    RoleVO getRoleById(String id);

    /**
     * 更新角色
     * @param roleUpdateDTO
     */
    void updateRole(RoleUpdateDTO roleUpdateDTO);

    /**
     * 删除角色（可批量）
     * @param ids
     */
    void deleteRole(String[] ids);

    /**
     * 分页查询角色
     * @param rolePageDTO
     * @return
     */
    PageResult pageRole(RolePageDTO rolePageDTO);

    /**
     * 给角色分配权限
     * @param roleId
     * @param permissionIds
     */
    void assignPermissions(String roleId, String[] permissionIds);

    List<Role> getRoleByUserId(String id);
}
