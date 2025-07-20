package com.itmang.mapper.study;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.entity.Datas;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface DatasMapper extends BaseMapper<Datas> {

    /**
     * 发布资料
     * @param data
     */
    @Options(useGeneratedKeys = false, keyProperty = "")  // 忽略返回值
    void updateDatasById(Datas data);
}
