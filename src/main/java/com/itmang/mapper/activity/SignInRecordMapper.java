package com.itmang.mapper.activity;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.entity.SignInRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 签到信息 Mapper
 */
@Mapper
public interface SignInRecordMapper extends BaseMapper<SignInRecord> {
    // 如果有自定义 SQL，可在这里定义方法
}