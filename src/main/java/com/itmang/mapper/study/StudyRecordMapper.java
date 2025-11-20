package com.itmang.mapper.study;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.annotation.AutoFill;
import com.itmang.enumeration.OperationType;
import com.itmang.pojo.dto.SituationPageDTO;
import com.itmang.pojo.entity.StudyRecord;
import com.itmang.pojo.vo.LearnSituationVO;
import com.itmang.pojo.vo.SituationPageVO;
import com.itmang.pojo.vo.SituationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface StudyRecordMapper extends BaseMapper<StudyRecord> {


    /**
     * 新增学习情况
     * @param studyRecord
     */
    @AutoFill(value = OperationType.INSERT)
    void addStudyRecord(StudyRecord studyRecord);

    /**
     * 编辑学习情况
     * @param updateStudyRecord
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateStudyRecord(StudyRecord updateStudyRecord);

    /**
     * 查询学习情况
     * @param id
     * @return
     */
    SituationVO querySituation(String id);

    /**
     * 批量删除学习情况
     * @param deleteIds
     */
    void removeBatchByIds(String[] deleteIds);

    /**
     * 分页查询学习情况
     * @param situationPageDTO
     * @return
     */
    List<SituationPageVO> pageSearch(SituationPageDTO situationPageDTO);

    /**
     * 查询学习资料详情
     * @param id
     * @return
     */
    LearnSituationVO queryLearnSituation(String id);
}
