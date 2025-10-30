package com.itmang.mapper.activity;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.entity.VoteInformation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VoteInformationMapper extends BaseMapper<VoteInformation> {
    // 如果有自定义 SQL，可在这里定义方法
}