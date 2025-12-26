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
     * 查询详细活动信息
     * @return
     */
    @Operation(summary = "详细活动信息")
    @GetMapping("/GetDetailActionMessage/{id}")
    public Result<DetailActionMessageVO> pageGetDetailActionMessage(@PathVariable String id) {
        log.debug("查询详细的活动信息:{}", id);
        DetailActionMessageVO detailActionMessageVO = actionService.GetDetailActionMessage(id);
        return Result.success(detailActionMessageVO);
    }

    /**
     * 分页查询活动信息
     * @return
     */
    @Operation(summary = "分页查询活动信息")
    @GetMapping("/pageGetActionMessage")
    public Result<PageResult> pageGetShortActionMessage(PageDetailActionMessageDTO actionMessageDTO){
        log.debug("分页查询活动信息:{}", actionMessageDTO);
        PageResult pageResult = actionService.pageGetActionMessage(actionMessageDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据活动id修改活动信息
     * @return
     */
    @Operation(summary = "修改活动信息")
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
     * @param ids
     * @return
     */
    //请求参数应该是一个删除id集合的json字符串
    @Operation(summary = "删除活动信息（可批量）")
    @PostMapping("/deleteActionMessage/{ids}")
    public Result<String> deleteActionMessage(@PathVariable String[] ids){
        log.info("删除活动信息:{}", ids);
        actionService.deleteActionMessage(ids);
        return Result.success();
    }

}
