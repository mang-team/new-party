package com.itmang.service.study;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.entity.Datas;
import org.springframework.stereotype.Service;

@Service
public interface DatasService extends IService<Datas> {

    /**
     * 发布资料
     * @param dataId
     */
    void releaseData(String dataId);
}
