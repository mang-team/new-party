package com.itmang.controller.study;


import com.itmang.pojo.dto.AddExaminationDTO;
import com.itmang.pojo.dto.ExaminationPageDTO;
import com.itmang.pojo.dto.ExaminationUpdateDTO;
import com.itmang.pojo.entity.ExaminationInformation;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.ExaminationPageVO;
import com.itmang.pojo.vo.ExaminationVO;
import com.itmang.service.study.ExaminationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.analysis.function.Add;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("study/examination")
@Tag(name ="考试相关接口")
public class ExaminationController {

    @Autowired
    private ExaminationService examinationService;

    //TODO 新增考试信息接口还需要完善分发考卷的逻辑
    @Operation(summary = "新增考试信息")
    @PostMapping("/add")
    public Result addExaminationInformation(@RequestBody AddExaminationDTO addExaminationDTO){
        log.info("新增考试信息:{}",addExaminationDTO);
        examinationService.addExaminationInformation(addExaminationDTO);
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


}
