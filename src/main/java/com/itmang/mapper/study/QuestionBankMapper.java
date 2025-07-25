package com.itmang.mapper.study;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.entity.QuestionBank;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface QuestionBankMapper extends BaseMapper<QuestionBank> {

    /**
     * 更新题库选中状态
     * @param participateArr
     */
    void updateQuestionBankSelected(String[] participateArr);
}
