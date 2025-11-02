package com.itmang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.entity.UserLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日志Mapper接口
 */
@Mapper
public interface LogMapper extends BaseMapper<UserLog> {
}