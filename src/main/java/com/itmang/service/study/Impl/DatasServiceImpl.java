package com.itmang.service.study.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.StatusConstant;
import com.itmang.exception.BaseException;
import com.itmang.mapper.study.DatasMapper;
import com.itmang.pojo.entity.Datas;
import com.itmang.service.study.DatasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatasServiceImpl extends ServiceImpl<DatasMapper, Datas> implements DatasService {

    @Autowired
    private DatasMapper datasMapper;

    /**
     * 发布资料
     * @param dataId
     */
    public void releaseData(String dataId) {
        //1、判断资料是否存在
        Datas data = datasMapper.selectById(dataId);
        if (data == null) {
            throw new BaseException(MessageConstant.DATA_NOT_EXISTS);
        }
        //2、判断资料的当前状态
        if (data.getStatus().equals(StatusConstant.ENABLE) ) {
            //资料已经被发布，不能重复发布
            throw new BaseException(MessageConstant.DATA_ALREADY_RELEASED);
        }
        //3、修改资料状态
        //重新创建一个类用于修改
        Datas updateData = Datas.builder()
                .id(dataId)
                .status(StatusConstant.ENABLE)
                .build();
        datasMapper.updateDatasById(updateData);
    }


}
