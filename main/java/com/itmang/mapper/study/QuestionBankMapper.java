package com.itmang.mapper.study;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.annotation.AutoFill;
import com.itmang.enumeration.OperationType;
import com.itmang.pojo.dto.BankPageDTO;
import com.itmang.pojo.entity.QuestionBank;
import com.itmang.pojo.vo.BankPageVO;
import com.itmang.pojo.vo.BankVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionBankMapper extends BaseMapper<QuestionBank> {

    /**
     * 修改题库信息
     * @param array
     */
    void updateQuestionBankSelected(String[] array);

    /**
     * 新增题库信息
     * @param addQuestionBank
     */
    @AutoFill(OperationType.INSERT)
    void insertQuestionBank(QuestionBank addQuestionBank);

    /**
     * 删除题库中的题目
     * @param array
     */
    void removeBatchByIds(String[] array);

    /**
     * 修改题库中的题目
     * @param questionBank
     */
    @AutoFill(OperationType.UPDATE)
    void updateQuestionBank(QuestionBank questionBank);

    /**
     * 查询题库中题目的信息
     * @param id
     * @return
     */
    BankVO queryQuestionBankById(String id);

    /**
     * 分页查询题库列表
     * @param bankPageDTO
     * @return
     */
    List<BankPageVO> queryQuestionBankList(BankPageDTO bankPageDTO);
}
