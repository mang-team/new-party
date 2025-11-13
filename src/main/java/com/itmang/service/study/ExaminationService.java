package com.itmang.service.study;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.*;
import com.itmang.pojo.entity.ExaminationInformation;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.vo.ExaminationPaperVO;
import com.itmang.pojo.vo.ExaminationTemplateVO;
import com.itmang.pojo.vo.ExaminationVO;
import org.springframework.stereotype.Service;

@Service
public interface ExaminationService extends IService<ExaminationInformation> {

    /**
     * 新增考试信息
     * @param addExaminationDTO
     */
    void addExaminationInformation(AddExaminationDTO addExaminationDTO);

    /**
     * 删除考试信息
     * @param ids
     */
    void deleteExaminationInformation(String[] ids);

    /**
     * 编辑考试信息
     * @param examinationUpdateDTO
     */
    void updateExaminationInformation(ExaminationUpdateDTO examinationUpdateDTO);

    /**
     * 查询考试信息
     * @param id
     * @return
     */
    ExaminationVO queryExaminationInformation(String id);

    /**
     * 查询考试信息列表
     * @param examinationPageDTO
     * @return
     */
    PageResult queryExaminationInformationList(ExaminationPageDTO examinationPageDTO);

    /**
     * 新增考试试题
     * @param addExaminationPaperDTO
     */
    void addExaminationPaper(AddExaminationPaperDTO addExaminationPaperDTO);

    /**
     * 新增考试模版
     * @param addExaminationTemplateDTO
     */
    void addExaminationTemplate(AddExaminationTemplateDTO addExaminationTemplateDTO);

    /**
     * 查询考试模版列表
     * @param examinationTemplatePageDTO
     * @return
     */
    PageResult listExaminationTemplate(ExaminationTemplatePageDTO examinationTemplatePageDTO);

    /**
     * 查询考试模版详情
     * @param id
     * @return
     */
    ExaminationTemplateVO searchExaminationTemplate(String id);

    /**
     * 编辑模版
     * @param updateExaminationTemplateDTO
     */
    void updateExaminationTemplate(UpdateExaminationTemplateDTO updateExaminationTemplateDTO);

    /**
     * 删除模版
     * @param ids
     */
    void deleteExaminationTemplate(String[] ids);

    /**
     * 查询考试试题列表
     * @param examinationPaperPageDTO
     * @return
     */
    PageResult queryExaminationPaperList(ExaminationPaperPageDTO examinationPaperPageDTO);

    /**
     * 查询考试试题详情
     * @param id
     * @return
     */
    ExaminationPaperVO searchExaminationPaper(String id);

    /**
     * 删除试题
     * @param ids
     */
    void deleteExaminationPaper(String[] ids);

    /**
     * 编辑试题
     * @param updateExaminationPaperDTO
     */
    void updateExaminationPaper(UpdateExaminationPaperDTO updateExaminationPaperDTO);
}
