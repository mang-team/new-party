package com.itmang.mapper.study;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.annotation.AutoFill;
import com.itmang.enumeration.OperationType;
import com.itmang.pojo.dto.DatasPageDTO;
import com.itmang.pojo.entity.Datas;
import com.itmang.pojo.vo.DatasPageVO;
import com.itmang.pojo.vo.DatasVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DatasMapper extends BaseMapper<Datas> {

    /**
     * 发布资料
     * @param data
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateDatasById(Datas data);

    /**
     * 新增资料
     * @param newData
     */
    @AutoFill(value = OperationType.INSERT)
    void insertDatas(Datas newData);

    /**
     * 删除资料（可批量）
     * @param deleteIds
     */
    void removeBatchByIds(String[] deleteIds);

    /**
     * 查询资料
     * @param id
     * @return
     */
    DatasVO selectInformationById(String id);

    /**
     * 分页查询资料
     * @param datasPageDTO
     * @return
     */
    List<DatasPageVO> pageSearch(DatasPageDTO datasPageDTO);
}
