package com.itmang.controller.action;

import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.service.action.ActionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("action/actionRecord")
@Tag(name = "活动记录模块相关请求接口")
public class ActionRecordController {
    @Autowired
    private ActionService actionService;

//    public Result<PageResult> pageGetActionRecordMessage(){
//        log.debug("分页查询的活动记录信息:{}", );
//        PageResult pageResult = actionService.pageGetDetailActionMessage();
//        return Result<>
//    }
}
