package com.itmang.mapper.study;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.annotation.AutoFill;
import com.itmang.enumeration.OperationType;
import com.itmang.pojo.dto.ExaminationPageDTO;
import com.itmang.pojo.dto.ExaminationPaperPageDTO;
import com.itmang.pojo.dto.ExaminationTemplatePageDTO;
import com.itmang.pojo.entity.ExaminationInformation;
import com.itmang.pojo.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface ExaminationInformationMapper extends BaseMapper<ExaminationInformation> {


    /**
     * 插入考试信息
     * @param examinationInformation
     */
    @AutoFill(value = OperationType.INSERT)
    void insertExaminationInformation(ExaminationInformation examinationInformation);

    /**
     * 批量删除考试信息
     * @param array
     */
    Integer removeBatchByIds(String[] array,Integer type);

    /**
     * 修改考试信息
     * @param updateExaminationInformation
     */
    void updateExaminationInformation(ExaminationInformation updateExaminationInformation);


    /**
     * 查询考试信息列表
     * @param examinationPageDTO
     * @return
     */
    List<ExaminationPageVO> selectExaminationInformationList(ExaminationPageDTO examinationPageDTO);

    /**
     * 查询考试模版列表
     * @param examinationTemplatePageDTO
     * @return
     */
    List<ExaminationTemplatePageVO> selectExaminationTemplateList(ExaminationTemplatePageDTO examinationTemplatePageDTO);

    /**
     * 查询考试模版详情
     * @param id
     * @return
     */
    ExaminationTemplateVO selectExaminationTemplate(String id);

    /**
     * 查询考试试卷列表
     * @param examinationPaperPageDTO
     * @return
     */
    List<ExaminationPaperPageVO> selectExaminationPaperList(ExaminationPaperPageDTO examinationPaperPageDTO);
}
