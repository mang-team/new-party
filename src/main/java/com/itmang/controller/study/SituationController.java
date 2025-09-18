package com.itmang.controller.study;


import com.itmang.pojo.dto.AddSituationDTO;
import com.itmang.pojo.dto.SituationPageDTO;
import com.itmang.pojo.dto.SituationUpdateDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.entity.StudyRecord;
import com.itmang.pojo.vo.SituationVO;
import com.itmang.service.study.SituationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("study/situation")
@Tag(name ="学习情况相关接口")
public class SituationController {

    @Autowired
    private SituationService situationService;

    @Operation(summary = "新增学习情况")
    @PostMapping("/add")
    public Result addSituation(@RequestBody AddSituationDTO addSituationDTO){
        log.info("新增学习情况:{}",addSituationDTO);
        situationService.addSituation(addSituationDTO);
        return Result.success();
    }


    @Operation(summary = "编辑学习情况")
    @PostMapping("/update")
    public Result updateSituation(@RequestBody SituationUpdateDTO situationUpdateDTO){
        log.info("修改学习情况:{}",situationUpdateDTO);
        situationService.updateSituation(situationUpdateDTO);
        return Result.success();
    }

    @Operation(summary = "查询学习情况")
    @GetMapping("/{id}")
    public Result<SituationVO> querySituation(@PathVariable String id){
        log.info("查询学习情况:{}",id);
        SituationVO situationVO = situationService.querySituation(id);
        return Result.success(situationVO);
    }

    @Operation(summary = "删除学习情况(可批量)")
    @PostMapping("/delete/{ids}")
    public Result deleteSituation(@PathVariable String[] ids){
        log.info("删除学习情况:{}",ids);
        situationService.deleteSituation(ids);
        return Result.success();
    }

    @Operation(summary = "分页查询学习情况")
    @GetMapping("/page")
    public Result<PageResult> pageSituation(SituationPageDTO situationPageDTO) {
        log.info("分页查询学习情况:{}",situationPageDTO);
        PageResult pageResult = situationService.pageSituation(situationPageDTO);
        return Result.success(pageResult);
    }



}
