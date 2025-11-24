package com.itmang.mapper.study;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.annotation.AutoFill;
import com.itmang.enumeration.OperationType;
import com.itmang.pojo.dto.BankPageDTO;
import com.itmang.pojo.dto.DatasPageDTO;
import com.itmang.pojo.entity.Datas;
import com.itmang.pojo.entity.QuestionBank;
import com.itmang.pojo.vo.BankPageVO;
import com.itmang.pojo.vo.BankVO;
import com.itmang.pojo.vo.DatasPageVO;
import com.itmang.pojo.vo.DatasVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionBankMapper extends BaseMapper<QuestionBank> {

    /**
     * 修改题库信息
     * @param array
     */
    void updateQuestionBankSelected(String[] array,Integer status);

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


    /**
     * 批量查询题库列表
     * @param ids
     * @return
     */
    List<BankVO> seriesQuestionBank(String[] ids);

    /**
     * 处理题目次数
     * @param addQuestionId
     * @param times
     */
    void updateQuestionBankTimes(String addQuestionId, Integer times);

}
