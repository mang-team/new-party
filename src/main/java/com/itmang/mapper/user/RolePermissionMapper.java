package com.itmang.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    List<String> getPermissionUrlsByRoleIds(List<String> roleIds);
}