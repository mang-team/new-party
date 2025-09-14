package com.itmang.mapper.study;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.annotation.AutoFill;
import com.itmang.enumeration.OperationType;
import com.itmang.pojo.dto.PaperPageDTO;
import com.itmang.pojo.entity.ExaminationPaper;
import com.itmang.pojo.vo.PaperPageVO;
import com.itmang.pojo.vo.PaperVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExaminationPaperMapper extends BaseMapper<ExaminationPaper> {


    /**
     * 新增考卷
     * @param examinationPapers
     */
    void insertExaminationPaper(List<ExaminationPaper> examinationPapers);

    /**
     * 修改考卷状态
     * @param examinationPaper
     */
    @AutoFill(value = OperationType.UPDATE)
    void updatePaperById(ExaminationPaper examinationPaper);

    /**
     * 删除考卷
     * @param canDeletePaperIds
     */
    void deletePaperById(List<String> canDeletePaperIds);

    /**
     * 获取考卷信息
     * @param id
     * @return
     */
    PaperVO getPaperVOById(String id);

    /**
     * 获取考卷列表
     * @param paperPageDTO
     * @return
     */
    List<PaperPageVO> getPaperList(PaperPageDTO paperPageDTO);
}
