package com.itmang.controller.action;

import com.itmang.pojo.dto.action.AddActionRecordMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionRecordMessageDTO;
import com.itmang.pojo.dto.action.PageActionRecordMessageDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.action.DetailActionRecordMessageVO;
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
@Tag(name = "活动记录相关接口")
public class ActionRecordController {
    @Autowired
    private ActionRecordService actionRecordService;

    /**
     * 分页查询活动记录表信息
     * @param pageActionRecordMessageDTO
     * @return
     */
    @Operation(summary = "分页查询活动记录表信息")
    @GetMapping("/pageGetActionRecordMessage")
    public Result<PageResult> pageGetActionRecordMessage(PageActionRecordMessageDTO pageActionRecordMessageDTO){
        log.debug("分页查询活动记录信息:{}",pageActionRecordMessageDTO );
        PageResult pageResult = actionRecordService.pageGetActionRecordMessage(pageActionRecordMessageDTO);
        return Result.success(pageResult);
    }

    /**
     * 报名接口
     * @return
     */
    @Operation(summary = "报名接口")
    @PostMapping("/addActionRecordMessage")
    public Result<String> addActionRecordMessage(@RequestBody AddActionRecordMessageDTO addActionRecordMessageDTO){
        log.debug("新增活动记录表信息:{}",addActionRecordMessageDTO );
        actionRecordService.addActionRecordMessage(addActionRecordMessageDTO);
        return Result.success();
    }


    /**
     * 编辑活动心得接口
     * @param modifyActionRecordMessageDTO
     * @return
     */
    @Operation(summary = "编辑活动心得接口")
    @PostMapping("/modifyActionRecordMessage")
    public Result<String> modifyActionRecordMessage(@RequestBody ModifyActionRecordMessageDTO modifyActionRecordMessageDTO){
        log.debug("根据活动记录id修改活动记录表信息:{}", modifyActionRecordMessageDTO);
        actionRecordService.modifyActionRecordMessage(modifyActionRecordMessageDTO);
        return Result.success();
    }

    /**
     * 删除活动记录
     * @param ids
     * @return
     */
    @Operation(summary = "批量删除活动记录（可批量）")
    @PostMapping("/deleteActionRecordMessage/{ids}")
    public Result<String> deleteActionRecordMessage(@PathVariable String[] ids){
        log.info("批量删除活动记录:{}", ids);
        actionRecordService.deleteActionRecordMessage(ids);
        return Result.success();
    }


    @Operation(summary = "查询活动记录详细信息")
    @GetMapping("/getActionRecordMessageById/{id}")
    public Result<DetailActionRecordMessageVO> getActionRecordMessageById(@PathVariable String id){
        log.info("根据活动记录id查询活动记录信息:{}", id);
        DetailActionRecordMessageVO detailActionRecordMessageVO =
                actionRecordService.getActionRecordMessageById(id);
        return Result.success(detailActionRecordMessageVO);
    }

}
