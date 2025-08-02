package com.itmang.service.study;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.AddPaperDTO;
import com.itmang.pojo.dto.PaperPageDTO;
import com.itmang.pojo.dto.PaperUpdateDTO;
import com.itmang.pojo.entity.ExaminationPaper;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.vo.PaperVO;
import org.springframework.stereotype.Service;

import java.awt.print.Paper;

@Service
public interface PaperService extends IService<ExaminationPaper> {


    /**
     * 新增考卷
     * @param addPaperDTO
     */
    void addPaperById(AddPaperDTO addPaperDTO);

    /**
     * 根据id修改考卷状态
     * @param paperId
     * @param submit
     */
    void submitPaperById(String paperId, Integer submit);

    /**
     * 根据id删除考卷
     * @param ids
     */
    void deletePaperById(String[] ids);

    /**
     * 根据id修改考卷答案
     * @param paperUpdateDTO
     */
    void updatePaperAnswer(PaperUpdateDTO paperUpdateDTO);

    /**
     * 根据id查询考卷
     * @param id
     * @return
     */
    PaperVO queryPaperById(String id);

    /**
     * 查询考卷列表
     * @param paperPageDTO
     * @return
     */
    PageResult queryPaperPageList(PaperPageDTO paperPageDTO);

    /**
     * 批量批改考卷
     * @param ids
     */
    void correctPapers(String[] ids);
}
