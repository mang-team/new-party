package com.itmang.service.study;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.mapper.study.StudyRecordMapper;
import com.itmang.pojo.dto.AddSituationDTO;
import com.itmang.pojo.dto.SituationPageDTO;
import com.itmang.pojo.dto.SituationUpdateDTO;
import com.itmang.pojo.entity.Datas;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.StudyRecord;
import com.itmang.pojo.vo.SituationVO;
import org.springframework.stereotype.Service;


public interface SituationService extends IService<StudyRecord> {


    /**
     * 新增学习情况
     * @param addSituationDTO
     */
    void addSituation(AddSituationDTO addSituationDTO);

    /**
     * 修改学习情况
     * @param situationUpdateDTO
     */
    void updateSituation(SituationUpdateDTO situationUpdateDTO);

    /**
     * 查询学习情况
     * @param id
     * @return
     */
    SituationVO querySituation(String id);

    /**
     * 删除学习情况
     * @param ids
     */
    void deleteSituation(String[] ids);

    /**
     * 分页查询学习情况
     * @param situationPageDTO
     * @return
     */
    PageResult pageSituation(SituationPageDTO situationPageDTO);
}
