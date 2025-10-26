package com.itmang.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.dto.RolePageDTO;
import com.itmang.pojo.entity.Role;
import com.itmang.pojo.vo.RoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 分页查询角色
     * @param rolePageDTO
     * @return
     */
    List<RoleVO> pageQuery(RolePageDTO rolePageDTO);

    /**
     * 分页查询角色总数
     * @param rolePageDTO
     * @return
     */
    Long pageQueryCount(RolePageDTO rolePageDTO);
}