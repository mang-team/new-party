package com.itmang.controller.action;


import com.itmang.pojo.dto.action.AddActionMessageDTO;
import com.itmang.pojo.dto.action.ModifyActionMessageDTO;
import com.itmang.pojo.dto.action.PageDetailActionMessageDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.service.action.ActionService;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("action/action")
@Tag(name = "活动模块相关请求接口")
public class ActionController {

    @Autowired
    private ActionService actionService;

    /**
     * 分页查询详细活动信息
     * @return
     */
    @PostMapping("/pageGetDetailActionMessage")
    public Result<PageResult> pageGetDetailActionMessage(@RequestBody PageDetailActionMessageDTO actionMessageDTO) {
        log.debug("分页查询详细的活动信息:{}", actionMessageDTO);
        PageResult pageResult = actionService.pageGetDetailActionMessage(actionMessageDTO);
        return Result.success(pageResult);
    }

    /**
     * 分页查询简略活动信息
     * @return
     */
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
    @PostMapping("/modifyActionMessage")
    public Result<String> modifyActionMessage(@RequestBody ModifyActionMessageDTO modifyActionMessageDTO) {
        log.debug("修改活动信息:{}", modifyActionMessageDTO);
        boolean result = actionService.modifyActionMessage(modifyActionMessageDTO);
        return result ? Result.success() : Result.error("修改失败！");
//        actionService.modifyActionMessage(modifyActionMessageDTO);
//        return Result.success();
    }

    /**
     * 新增活动信息
     * @return
     */
    @PostMapping("/addActionMessage")
    public Result<String> addActionMessage(@RequestBody AddActionMessageDTO addActionMessageDTO) {
        log.debug("新增活动信息:{}", addActionMessageDTO);
        actionService.addActionMessage(addActionMessageDTO);
        return Result.success();
    }

}
