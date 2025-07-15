package com.itmang.service.studyService;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.entity.Datas;

public interface DatasService extends IService<Datas> {

    /**
     * 发布资料
     * @param dataId
     */
    void releaseData(String dataId);
}
