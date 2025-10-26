package com.itmang.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}