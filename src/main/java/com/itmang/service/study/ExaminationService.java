package com.itmang.service.study;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.AddExaminationDTO;
import com.itmang.pojo.dto.ExaminationPageDTO;
import com.itmang.pojo.dto.ExaminationUpdateDTO;
import com.itmang.pojo.entity.ExaminationInformation;
import com.itmang.pojo.entity.PageResult;
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
}
