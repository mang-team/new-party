package com.itmang.service.study.Impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.*;
import com.itmang.exception.BaseException;
import com.itmang.mapper.study.QuestionBankMapper;
import com.itmang.pojo.dto.AddBankDTO;
import com.itmang.pojo.dto.BankPageDTO;
import com.itmang.pojo.dto.BankUpdateDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.QuestionBank;
import com.itmang.pojo.vo.BankPageVO;
import com.itmang.pojo.vo.BankVO;
import com.itmang.service.study.BankService;
import com.itmang.utils.IdGenerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements BankService {

    @Autowired
    private QuestionBankMapper questionBankMapper;


    /**
     * 添加题库
     * @param addBankDTO
     */
    public void addQuestionBank(AddBankDTO addBankDTO) {
        //判断传入的数据是否完整
        if (addBankDTO == null || addBankDTO.getQuestion() == null || addBankDTO.getQuestion() == ""
                || addBankDTO.getType() == null || addBankDTO.getType() == ""
                || addBankDTO.getAnswerType() == null || addBankDTO.getAnswerType() == "") {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        if ((addBankDTO.getAnswerType().equals(QuestionOptionConstant.SINGLE_CHOICE)  ||
                addBankDTO.getAnswerType().equals(QuestionOptionConstant.MULTI_CHOICE))
                && (addBankDTO.getQuestionOption() == null || addBankDTO.getQuestionOption() == "")){
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //先查询是否存在这个题目
        QuestionBank questionBank = questionBankMapper.selectOne(
                new QueryWrapper<QuestionBank>()
                        .eq("question", addBankDTO.getQuestion())
                        .eq("type", addBankDTO.getType())
                        .eq("is_delete", DeleteConstant.NO));
        if (questionBank != null) {
            throw new BaseException(MessageConstant.QUESTION_BANK_EXIST);
        }
        //题库中没有这个题目，可以添加
        //需要判断选择题中的提供的答案是否有提供到选项中
        //TODO 后续可增加判断功能
//        if (addBankDTO.getAnswerType().equals(QuestionOptionConstant.SINGLE_CHOICE)  ||
//            addBankDTO.getAnswerType().equals(QuestionOptionConstant.MULTI_CHOICE)) {
//            String[] answerArray = addBankDTO.getQuestionOption().split(",");
//            for (String answer : answerArray) {
//                //进行判断选项中是否有给的答案的选项
//                if (!addBankDTO.getAnswerType().contains(answer)) {
//                    throw new BaseException(MessageConstant.ANSWER_NOT_EXIST);
//                }
//            }
//        }
        //判断传入的题目类型是否是指定的四种题目类型
        if (!addBankDTO.getType().equals(QuestionOptionConstant.SINGLE_CHOICE) &&
            !addBankDTO.getType().equals(QuestionOptionConstant.MULTI_CHOICE) &&
            !addBankDTO.getType().equals(QuestionOptionConstant.JUDGEMENT) &&
            !addBankDTO.getType().equals(QuestionOptionConstant.FILL_BLANKS)) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //添加题目
        IdGenerate idGenerate = new IdGenerate();
        QuestionBank addQuestionBank = BeanUtil.copyProperties(addBankDTO, QuestionBank.class);
        addQuestionBank.setIsDelete(DeleteConstant.NO);
        addQuestionBank.setIsChoose(StatusConstant.DISABLE);
        addQuestionBank.setId(idGenerate.nextUUID(QuestionBank.class));
        questionBankMapper.insertQuestionBank(addQuestionBank);
    }

    /**
     * 删除题库中的题目（可批量）
     * @param ids
     */
    public void deleteQuestionBank(String[] ids) {
        List<String> canDeleteIds = new ArrayList<>();
        //判断资料是否存在
        for (String id : ids) {
            QuestionBank questionBank = questionBankMapper.selectById(id);
            if(questionBank == null || questionBank.getIsDelete().equals(DeleteConstant.YES)){
                continue;
            }
            //不是空值，判断状态，题目是否被选中
            if(questionBank.getIsChoose().equals(StatusConstant.ENABLE)){
                continue;
            }
            //将满足条件的存入canDeleteIds集合中
            canDeleteIds.add(id);
        }
        // 执行删除逻辑
        if(!canDeleteIds.isEmpty()) {
            questionBankMapper.removeBatchByIds(canDeleteIds.toArray(new String[0]));
            if(canDeleteIds.size() != ids.length){
                throw new BaseException(MessageConstant.QUESTION_PART_DELETED);
            }
        }else{
            throw new BaseException(MessageConstant.QUESTION_DELETED_FAIL);
        }

    }

    /**
     * 编辑题库信息
     * @param bankUpdateDTO
     */
    public void updateQuestionBank(BankUpdateDTO bankUpdateDTO) {
        //判断传入的数据是否完整
        if (bankUpdateDTO == null || bankUpdateDTO.getId() == null || bankUpdateDTO.getId().isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        // 如果只有id传进来，其他数据都是空的话就报错
        if ((bankUpdateDTO.getQuestion() == null || bankUpdateDTO.getQuestion().isEmpty()) &&
            (bankUpdateDTO.getType() == null || bankUpdateDTO.getType().isEmpty()) &&
            (bankUpdateDTO.getAnswerType() == null || bankUpdateDTO.getAnswerType().isEmpty()) &&
            (bankUpdateDTO.getQuestionOption() == null || bankUpdateDTO.getQuestionOption().isEmpty())) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //先查询是否存在这个题目
        QuestionBank questionBank =
                questionBankMapper.selectById(bankUpdateDTO.getId());
        if(questionBank == null || questionBank.getIsDelete().equals(DeleteConstant.YES)){
            throw new BaseException(MessageConstant.QUESTION_BANK_NOT_EXIST);
        }
        //编辑题库中的题目
        questionBankMapper.updateQuestionBank(
                BeanUtil.copyProperties(bankUpdateDTO, QuestionBank.class));
    }

    /**
     * 查询题库中题目的信息
     * @param id
     */
    public BankVO queryQuestionBankById(String id) {
        BankVO bankVO = questionBankMapper.queryQuestionBankById(id);
        if(bankVO == null){
            throw new BaseException(MessageConstant.QUESTION_BANK_NOT_EXIST);
        }
        return bankVO;
    }

    /**
     * 分页查询题库列表
     * @param bankPageDTO
     * @return
     */
    public PageResult queryQuestionBankList(BankPageDTO bankPageDTO) {
        //配置默认页码1和分页大小5
        if(bankPageDTO.getPage() == null || bankPageDTO.getPage() < 1){
            bankPageDTO.setPage(PageConstant.PAGE_NUM);
        }
        //查看是否有分页大小
        if(bankPageDTO.getPageSize() == null || bankPageDTO.getPageSize() < 1){
            bankPageDTO.setPageSize(PageConstant.PAGE_SIZE);
        }
        //对是否选中的题目的标识符进行判断，若不是1或者2则报错
        if (bankPageDTO.getIsChoose() != null && 
            !(bankPageDTO.getIsChoose().equals(StatusConstant.ENABLE)
                    || bankPageDTO.getIsChoose().equals(StatusConstant.DISABLE))) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //对题目类型进行判断，若不是四种题目类型则报错
        if (bankPageDTO.getType() != null && !bankPageDTO.getType().isEmpty()
                && !(bankPageDTO.getType().equals(QuestionOptionConstant.SINGLE_CHOICE)
                        || bankPageDTO.getType().equals(QuestionOptionConstant.MULTI_CHOICE)
                        || bankPageDTO.getType().equals(QuestionOptionConstant.JUDGEMENT)
                        || bankPageDTO.getType().equals(QuestionOptionConstant.FILL_BLANKS))) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        PageHelper.startPage(bankPageDTO.getPage(), bankPageDTO.getPageSize());
        List<BankPageVO> bankPageVOList = questionBankMapper.queryQuestionBankList(bankPageDTO);
        PageInfo<BankPageVO> pageInfo = new PageInfo<>(bankPageVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }


}
