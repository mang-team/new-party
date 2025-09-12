package com.itmang.controller.activity;


import com.itmang.controller.BaseController;
import com.itmang.pojo.dto.AddRegisterDTO;
import com.itmang.pojo.dto.DeleteRegisterDTO;
import com.itmang.pojo.dto.FindRegisterDTO;
import com.itmang.pojo.dto.UpdateRegisterDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.service.activity.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/action/register")
public class RegisterController extends BaseController {

    @Autowired
    private RegisterService registerService;

    //TODO 新增考试信息接口还需要完善分发考卷的逻辑
    @PostMapping("/addRegister")
    public Result addRegisterInformation(@RequestBody List<AddRegisterDTO> addRegisterDTOList){
        log.info("新增签到信息:{}",addRegisterDTOList);
        String userId = getUserIdFromToken();
        //String userId = "1";
        registerService.addRegisterInformation(addRegisterDTOList,userId);
        return Result.success();
    }


    @PostMapping("/deleteRegister")
    public Result deleteRegisterInformation(@RequestBody DeleteRegisterDTO deleteRegisterDTO){
        log.info("删除签到信息:{}",deleteRegisterDTO);
        String userId = getUserIdFromToken();
        registerService.deleteRegisterInformation(deleteRegisterDTO,userId);
        return Result.success();
    }

    @PostMapping("/modifyRegister")
    public Result updateRegisterInformation(
            @RequestBody UpdateRegisterDTO updateRegisterDTO){
        log.info("编辑签到信息:{}",updateRegisterDTO);
        String userId = getUserIdFromToken();
        //String userId = "1";
        registerService.updateRegisterInformation(updateRegisterDTO,userId);
        return Result.success();
    }

//    @Operation(summary = "查询考试信息")
//    @GetMapping("/{id}")
//    public Result<ExaminationVO> queryRegisterInformation(@PathVariable String id){
//        log.info("查询考试信息:{}",id);
//        ExaminationVO examinationVO =
//                examinationService.queryExaminationInformation(id);
//        return Result.success(examinationVO);
//    }

    @Operation(summary = "分页查询签到信息")
    @PostMapping("/findRegister")
    public Result<PageResult> queryRegisterInformationList(@RequestBody FindRegisterDTO findRegisterDTO){
        log.info("分页查询签到信息:{}",findRegisterDTO);
        PageResult pageResult = registerService.queryRegisterInformationList(findRegisterDTO);
        return Result.success(pageResult);
    }


}
