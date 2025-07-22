package com.itmang.service.study;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.DatasDTO;
import com.itmang.pojo.dto.DatasPageDTO;
import com.itmang.pojo.entity.Datas;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.vo.DatasVO;
import org.springframework.stereotype.Service;

@Service
public interface DatasService extends IService<Datas> {

    /**
     * 发布资料
     * @param dataId
     */
    void releaseData(String dataId);

    /**
     * 资料缓存
     * @param addDatasDTO
     */
    void addData(DatasDTO addDatasDTO);

    /**
     * 删除资料（可批量）
     * @param dataIds
     */
    void deleteData(String[] dataIds);

    /**
     * 修改资料状态
     * @param id
     * @param status
     */
    void updateStatus(String id, Integer status);

    /**
     * 查询资料
     * @param id
     */
    DatasVO searchData(String id);

    /**
     * 分页查询资料
     * @param datasPageDTO
     * @return
     */
    PageResult pageData(DatasPageDTO datasPageDTO);
}
