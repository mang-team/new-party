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
import com.itmang.pojo.entity.Datas;
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
        PageHelper.startPage(bankPageDTO.getPage(), bankPageDTO.getPageSize());
        List<BankPageVO> bankPageVOList = questionBankMapper.queryQuestionBankList(bankPageDTO);
        PageInfo<BankPageVO> pageInfo = new PageInfo<>(bankPageVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }


}
