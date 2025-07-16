package com.itmang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.entity.Datas;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DatasMapper extends BaseMapper<Datas> {

    /**
     * 更新资料
     * @param data
     */
    void updateDatas(Datas data);
}
