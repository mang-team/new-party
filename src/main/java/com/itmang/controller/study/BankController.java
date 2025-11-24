package com.itmang.controller.study;


import com.itmang.pojo.dto.AddBankDTO;
import com.itmang.pojo.dto.BankPageDTO;
import com.itmang.pojo.dto.BankUpdateDTO;
import com.itmang.pojo.dto.ChooseBankDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.QuestionBank;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.BankVO;
import com.itmang.service.study.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("study/bank")
@Tag(name ="题库相关接口")
public class BankController {

    @Autowired
    private BankService bankService;

    //TODO 还需完成批量新增题目接口
    @Operation(summary = "新增题库信息")
    @PostMapping("/add")
    public Result addQuestionBank(@RequestBody AddBankDTO addBankDTO){
        log.info("新增题库信息:{}",addBankDTO);
        bankService.addQuestionBank(addBankDTO);
        return Result.success();
    }

    @Operation(summary = "删除题库中的题目（可批量）")
    @PostMapping("/delete/{ids}")
    public Result deleteQuestionBank(@PathVariable String[] ids){
        log.info("删除题库中的题目:{}",ids);
        bankService.deleteQuestionBank(ids);
        return Result.success();
    }

    @Operation(summary = "编辑题库信息")
    @PostMapping("/update")
    public Result updateQuestionBank(@RequestBody BankUpdateDTO bankUpdateDTO){
        log.info("编辑题库信息:{}",bankUpdateDTO);
        bankService.updateQuestionBank(bankUpdateDTO);
        return Result.success();
    }

    @Operation(summary = "查询题库信息")
    @GetMapping("/{id}")
    public Result<BankVO> queryQuestionBank(@PathVariable String id){
        log.info("查询题库信息:{}",id);
        BankVO bankVO = bankService.queryQuestionBankById(id);
        return Result.success(bankVO);
    }

    @Operation(summary = "分页查询题库列表")
    @GetMapping("/page")
    public Result<PageResult> queryQuestionBankList(BankPageDTO bankPageDTO){
        log.info("分页查询题库列表:{}",bankPageDTO);
        PageResult pageResult = bankService.queryQuestionBankList(bankPageDTO);
        return Result.success(pageResult);
    }


    @Operation(summary = "批量查询题目(有答案)")
    @GetMapping("/list/{ids}")
    public Result<List<BankVO>> seriesQuestionBank(@PathVariable String[] ids){
        log.info("批量查询题目:{}",ids);
        List<BankVO> bankVOList = bankService.seriesQuestionBank(ids);
        return Result.success(bankVOList);
    }

    @Operation(summary = "自动选题")
    @PostMapping("/choice")
    public Result<String> chooseBank(ChooseBankDTO chooseBankDTO){
        log.info("自动选题:{}",chooseBankDTO);
        String ids = bankService.chooseBank(chooseBankDTO);
        return Result.success(ids);
    }

}
