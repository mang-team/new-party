package com.itmang.service.study;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.AddBankDTO;
import com.itmang.pojo.dto.BankPageDTO;
import com.itmang.pojo.dto.BankUpdateDTO;
import com.itmang.pojo.dto.ChooseBankDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.QuestionBank;
import com.itmang.pojo.vo.BankVO;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public interface BankService extends IService<QuestionBank>{

    /**
     * 添加题库
     * @param addBankDTO
     */
    void addQuestionBank(AddBankDTO addBankDTO);

    /**
     * 删除题库中的题目（可批量）
     * @param ids
     */
    void deleteQuestionBank(String[] ids);

    /**
     * 编辑题库信息
     * @param bankUpdateDTO
     */
    void updateQuestionBank(BankUpdateDTO bankUpdateDTO);

    /**
     * 查询题库中题目的信息
     * @param id
     */
    BankVO queryQuestionBankById(String id);

    /**
     * 分页查询题库列表
     * @param bankPageDTO
     * @return
     */
    PageResult queryQuestionBankList(BankPageDTO bankPageDTO);


    /**
     * 批量查询题目
     * @param ids
     * @return
     */
    List<BankVO> seriesQuestionBank(String[] ids);

    /**
     * 随机查询题目
     * @param chooseBankDTO
     * @return
     */
    String chooseBank(ChooseBankDTO chooseBankDTO);
}
