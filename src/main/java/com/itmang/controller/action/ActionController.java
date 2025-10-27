package com.itmang.controller.action;


import com.itmang.pojo.dto.action.AddActionMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionMessageDTO;
import com.itmang.pojo.dto.action.PageDetailActionMessageDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.action.DetailActionMessageVO;
import com.itmang.service.action.ActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("action/action")
@Tag(name = "活动相关接口")
public class ActionController {

    @Autowired
    private ActionService actionService;

    /**
     * 根据活动信息id查询详细活动信息
     * @return
     */
    @Operation(summary = "根据活动信息id查询详细活动信息")
    @PostMapping("/getDetailActionMessage")
    public Result<DetailActionMessageVO> getDetailActionMessage(@RequestParam String id) {
        log.debug("根据活动信息id查询详细活动信息:{}", id);
        DetailActionMessageVO detailActionMessage = actionService.getDetailActionMessage(id);
        return Result.success(detailActionMessage);
    }

    /**
     * 分页查询简略活动信息
     * @return
     */
    @Operation(summary = "分页查询简略活动信息")
    @PostMapping("/pageGetShortActionMessage")
    public Result<PageResult> pageGetShortActionMessage(@RequestBody PageDetailActionMessageDTO actionMessageDTO){
        log.debug("分页查询简略活动信息:{}", actionMessageDTO);
        PageResult pageResult = actionService.pageGetShortActionMessage(actionMessageDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据活动id修改活动信息
     * @return
     */
    @Operation(summary = "根据活动id修改活动信息")
    @PostMapping("/modifyActionMessage")
    public Result<String> modifyActionMessage(@RequestBody ModifyActionMessageDTO modifyActionMessageDTO) {
        log.debug("修改活动信息:{}", modifyActionMessageDTO);
        boolean result = actionService.modifyActionMessage(modifyActionMessageDTO);
        return result ? Result.success() : Result.error("修改失败！");
    }

    /**
     * 新增活动信息
     * @return
     */
    @Operation(summary = "新增活动信息")
    @PostMapping("/addActionMessage")
    public Result<String> addActionMessage(@RequestBody AddActionMessageDTO addActionMessageDTO) {
        log.debug("新增活动信息:{}", addActionMessageDTO);
        actionService.addActionMessage(addActionMessageDTO);
        return Result.success();
    }

    /**
     * 根据活动id批量删除活动信息
     * @param idList
     * @return
     */
    @Operation(summary = "根据活动id批量删除活动信息")
    @PostMapping("/deleteActionMessage")
    public Result<String> deleteActionMessage(@RequestParam List<String> idList){
        log.debug("根据活动id批量删除活动信息:{}",idList);
        actionService.deleteActionMessage(idList);
        return Result.success();
    }

    /**
     * 修改活动状态
     * @param id
     * @param state
     * @return
     */
    @Operation(summary = "修改活动状态")
    @PostMapping("/modifyState")
    public Result<String> modifyState(@RequestParam String id,@RequestParam int state){
        log.debug("修改活动状态:{},{}",id,state);
        boolean result = actionService.modifyState(id,state);
        return result ? Result.success() : Result.error("修改失败！");
    }
}
