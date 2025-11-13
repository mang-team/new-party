package com.itmang.controller.study;


import com.itmang.pojo.dto.AddPaperDTO;
import com.itmang.pojo.dto.PaperPageDTO;
import com.itmang.pojo.dto.PaperUpdateDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.PaperVO;
import com.itmang.service.study.PaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("study/paper")
@Tag(name ="考卷相关接口")
public class PaperController {

    @Autowired
    private PaperService paperService;

    @Operation(summary = "新增考卷（可批量）")
    @PostMapping("/add")
    public Result addPaperById(@RequestBody AddPaperDTO addPaperDTO){
        log.info("新增考卷:{}",addPaperDTO);
        paperService.addPaperById(addPaperDTO);
        return Result.success();
    }

    @Operation(summary = "修改考卷状态")
    @PostMapping("/submit/{submit}")
    public Result submitPaperById(@PathVariable Integer submit,String paperId){
        log.info("修改考卷{}状态:{}",paperId,submit);
        paperService.submitPaperById(paperId,submit);
        return Result.success();
    }

    @Operation(summary = "删除考卷（可批量）")
    @PostMapping("/delete/{ids}")
    public Result deletePaperById(@PathVariable String[] ids){
        log.info("删除考卷:{}",ids);
        paperService.deletePaperById(ids);
        return Result.success();
    }

    // PaperController.java
    @Operation(summary = "编辑考卷答题信息")
    @PostMapping("/update/answer")
    public Result updatePaperAnswer(@RequestBody PaperUpdateDTO paperUpdateDTO){
        log.info("编辑考卷答题信息:{}", paperUpdateDTO);
        paperService.updatePaperAnswer(paperUpdateDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询考卷详细信息")
    public Result<PaperVO> queryPaperById(@PathVariable String id){
        log.info("查询考卷详细信息:{}", id);
        PaperVO paperVO = paperService.queryPaperById(id);
        return Result.success(paperVO);
    }

    // PaperController.java 中新增
    @Operation(summary = "分页查询考卷列表")
    @GetMapping("/page")
    public Result<PageResult> queryPaperPageList(PaperPageDTO paperPageDTO){
        log.info("分页查询考卷列表:{}", paperPageDTO);
        PageResult pageResult = paperService.queryPaperPageList(paperPageDTO);
        return Result.success(pageResult);
    }

    // PaperController.java 新增
    @Operation(summary = "批量批阅考卷")
    @PostMapping("/correct/{ids}")
    public Result batchGradePapers(@PathVariable String[] ids){
        log.info("批量批阅考卷:{}", ids);
        paperService.correctPapers(ids);
        return Result.success();
    }

//    @Operation(summary = "开始接口")
//    @GetMapping("/paper/start")
//    public Result PaperStart(){
//
//        return Result.success();
//    }
//
//    @Operation(summary = "结束接口")
//    @GetMapping("/paper/end")
//    public Result PaperEnd(){
//
//        return Result.success();
//    }




}
