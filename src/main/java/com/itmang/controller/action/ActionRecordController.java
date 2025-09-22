package com.itmang.controller.action;

import com.itmang.pojo.dto.action.AddActionRecordMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionRecordMessageDTO;
import com.itmang.pojo.dto.action.PageActionRecordMessageDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.service.action.ActionRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("action/actionRecord")
@Tag(name = "活动记录模块相关请求接口")
public class ActionRecordController {
    @Autowired
    private ActionRecordService actionRecordService;

    /**
     * 分页查询活动记录表信息
     * @param pageActionRecordMessageDTO
     * @return
     */
    @Operation(summary = "分页查询活动记录表信息")
    @PostMapping("/pageGetActionRecordMessage")
    public Result<PageResult> pageGetActionRecordMessage(@RequestBody PageActionRecordMessageDTO pageActionRecordMessageDTO){
        log.debug("分页查询活动记录信息:{}",pageActionRecordMessageDTO );
        PageResult pageResult = actionRecordService.pageGetActionRecordMessage(pageActionRecordMessageDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增活动记录信息
     * @return
     */
    @Operation(summary = "新增活动记录信息")
    @PostMapping("/addActionRecordMessage")
    public Result<String> addActionRecordMessage(@RequestBody AddActionRecordMessageDTO addActionRecordMessageDTO){
        log.debug("新增活动记录表信息:{}",addActionRecordMessageDTO );
        actionRecordService.addActionRecordMessage(addActionRecordMessageDTO);
        return Result.success();
    }

    /**
     * 根据活动记录id修改活动记录表信息
     * @param modifyActionRecordMessageDTO
     * @return
     */
    @Operation(summary = "根据活动记录id修改活动记录表信息")
    @PostMapping("/modifyActionRecordMessage")
    public Result<String> modifyActionRecordMessage(@RequestBody ModifyActionRecordMessageDTO modifyActionRecordMessageDTO){
        log.debug("根据活动记录id修改活动记录表信息:{}", modifyActionRecordMessageDTO);
        boolean result = actionRecordService.modifyActionRecordMessage(modifyActionRecordMessageDTO);
        return result ? Result.success() : Result.error("修改失败！");
    }

    /**
     * 根据活动记录id批量删除活动记录
     * @param idList
     * @return
     */
    @Operation(summary = "根据活动记录id批量删除活动记录")
    @PostMapping("/deleteActionRecordMessage")
    public Result<String> deleteActionRecordMessage(@RequestParam List<String> idList){
        log.debug("根据活动记录id批量删除活动记录:{}",idList);
        actionRecordService.deleteActionRecordMessage(idList);
        return Result.success();
    }
}
