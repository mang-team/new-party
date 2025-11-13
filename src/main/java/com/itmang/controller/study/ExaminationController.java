package com.itmang.controller.study;


import com.itmang.pojo.dto.*;
import com.itmang.pojo.entity.ExaminationInformation;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.*;
import com.itmang.service.study.ExaminationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.analysis.function.Add;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("study/examination")
@Tag(name ="考试相关接口")
public class ExaminationController {

    @Autowired
    private ExaminationService examinationService;

    @Operation(summary = "新增考试信息")
    @PostMapping("/add")
    public Result addExaminationInformation(@RequestBody AddExaminationDTO addExaminationDTO){
        log.info("新增考试信息:{}",addExaminationDTO);
        examinationService.addExaminationInformation(addExaminationDTO);
        return Result.success();
    }

    @Operation(summary = "新增考试试题")
    @PostMapping("/paper/add")
    public Result addExaminationPaper(@RequestBody AddExaminationPaperDTO addExaminationPaperDTO){
        log.info("新增考试试题:{}",addExaminationPaperDTO);
        examinationService.addExaminationPaper(addExaminationPaperDTO);
        return Result.success();
    }

    @Operation(summary = "新增模版")
    @PostMapping("/template/add")
    public Result addExaminationTemplate(@RequestBody AddExaminationTemplateDTO addExaminationTemplateDTO){
        log.info("新增模版:{}",addExaminationTemplateDTO);
        examinationService.addExaminationTemplate(addExaminationTemplateDTO);
        return Result.success();
    }

    @Operation(summary = "删除考试信息（可批量）")
    @PostMapping("/delete/{ids}")
    public Result deleteExaminationInformation(@PathVariable String[] ids){
        log.info("删除考试信息:{}",ids);
        examinationService.deleteExaminationInformation(ids);
        return Result.success();
    }

    @Operation(summary = "编辑考试信息")
    @PostMapping("/update")
    public Result updateExaminationInformation(
            @RequestBody ExaminationUpdateDTO examinationUpdateDTO){
        log.info("编辑考试信息:{}",examinationUpdateDTO);
        examinationService.updateExaminationInformation(examinationUpdateDTO);
        return Result.success();
    }

    @Operation(summary = "查询考试信息")
    @GetMapping("/{id}")
    public Result<ExaminationVO> queryExaminationInformation(@PathVariable String id){
        log.info("查询考试信息:{}",id);
        ExaminationVO examinationVO =
                examinationService.queryExaminationInformation(id);
        return Result.success(examinationVO);
    }

    @Operation(summary = "分页查询考试信息")
    @GetMapping("/page")
    public Result<PageResult> queryExaminationInformationList(ExaminationPageDTO examinationPageDTO){
        log.info("分页查询考试信息:{}",examinationPageDTO);
        PageResult pageResult =
                examinationService.queryExaminationInformationList(examinationPageDTO);
        return Result.success(pageResult);
    }

    @Operation(summary = "列表查询模版")
    @GetMapping("/template/list")
    public Result<PageResult> listExaminationTemplate(ExaminationTemplatePageDTO examinationTemplatePageDTO){
        log.info("列表查询模版:{}",examinationTemplatePageDTO);
        PageResult pageResult =
                examinationService.listExaminationTemplate(examinationTemplatePageDTO);
        return Result.success(pageResult);
    }

    @Operation(summary = "查询模版详情")
    @GetMapping("/template/{id}")
    public Result<ExaminationTemplateVO> searchExaminationTemplate(@PathVariable String id){
        log.info("查询模版详情:{}",id);
        ExaminationTemplateVO examinationTemplateVO =
                examinationService.searchExaminationTemplate(id);
        return Result.success(examinationTemplateVO);
    }

    @Operation(summary = "编辑模版")
    @PostMapping("/template/update")
    public Result updateExaminationTemplate(
            @RequestBody UpdateExaminationTemplateDTO updateExaminationTemplateDTO){
        log.info("编辑模版:{}",updateExaminationTemplateDTO);
        examinationService.updateExaminationTemplate(updateExaminationTemplateDTO);
        return Result.success();
    }

    @Operation(summary = "删除模版（可批量）")
    @PostMapping("/template/delete/{ids}")
    public Result deleteExaminationTemplate(@PathVariable String[] ids){
        log.info("删除模版:{}",ids);
        examinationService.deleteExaminationTemplate(ids);
        return Result.success();
    }

    @Operation(summary = "列表查询考试试题")
    @GetMapping("/paper/list")
    public Result<PageResult> queryExaminationPaperList(ExaminationPaperPageDTO examinationPaperPageDTO){
        log.info("列表查询考试试题:{}",examinationPaperPageDTO);
        PageResult pageResult =
                examinationService.queryExaminationPaperList(examinationPaperPageDTO);
        return Result.success(pageResult);
    }

    @Operation(summary = "查询试题详情")
    @GetMapping("/paper/{id}")
    public Result<ExaminationPaperVO> searchExaminationPaper(@PathVariable String id){
        log.info("查询模版详情:{}",id);
        ExaminationPaperVO examinationPaperVO =
                examinationService.searchExaminationPaper(id);
        return Result.success(examinationPaperVO);
    }

    @Operation(summary = "删除试题（可批量）")
    @PostMapping("/paper/delete/{ids}")
    public Result deleteExaminationPaper(@PathVariable String[] ids){
        log.info("删除试题:{}",ids);
        examinationService.deleteExaminationPaper(ids);
        return Result.success();
    }

    @Operation(summary = "编辑试题")
    @PostMapping("/paper/update")
    public Result updateExaminationPaper(@RequestBody UpdateExaminationPaperDTO updateExaminationPaperDTO){
        log.info("编辑试题:{}",updateExaminationPaperDTO);
        examinationService.updateExaminationPaper(updateExaminationPaperDTO);
        return Result.success();
    }
}
