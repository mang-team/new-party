package com.itmang.controller.activity;


import com.itmang.constant.MessageConstant;
import com.itmang.controller.BaseController;
import com.itmang.pojo.dto.AddRegisterDTO;
import com.itmang.pojo.dto.DeleteRegisterDTO;
import com.itmang.pojo.dto.FindRegisterDTO;
import com.itmang.pojo.dto.UpdateRegisterDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.entity.SignInInformation;
import com.itmang.service.activity.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name= "签到相关接口")
@RequestMapping("/activity/register")
public class RegisterController extends BaseController {

    @Autowired
    private RegisterService registerService;

    @Operation(summary = "新增签到信息")
    @PostMapping("/addRegister")
    public Result addRegisterInformation(@RequestBody List<AddRegisterDTO> addRegisterDTOList){
        log.info("新增签到信息:{}",addRegisterDTOList);
        String userId = getUserIdFromToken();
        registerService.addRegisterInformation(addRegisterDTOList,userId);
        return Result.success();
    }


    @Operation(summary = "删除签到信息")
    @PostMapping("/deleteRegister")
    public Result deleteRegisterInformation(@RequestBody DeleteRegisterDTO deleteRegisterDTO){
        log.info("删除签到信息:{}",deleteRegisterDTO);
        String userId = getUserIdFromToken();
        registerService.deleteRegisterInformation(deleteRegisterDTO,userId);
        return Result.success();
    }

    @Operation(summary = "编辑签到信息")
    @PostMapping("/modifyRegister")
    public Result updateRegisterInformation(
            @RequestBody UpdateRegisterDTO updateRegisterDTO){
        log.info("编辑签到信息:{}",updateRegisterDTO);
        String userId = getUserIdFromToken();
        //String userId = "1";
        registerService.updateRegisterInformation(updateRegisterDTO,userId);
        return Result.success();
    }


    @Operation(summary = "分页查询签到信息")
    @GetMapping("/findRegister")
    public Result<PageResult> queryRegisterInformationList(FindRegisterDTO findRegisterDTO){
        log.info("分页查询签到信息:{}",findRegisterDTO);
        PageResult pageResult = registerService.queryRegisterInformationList(findRegisterDTO);
        return Result.success(pageResult);
    }


    @Operation(summary = "查询签到信息详情")
    @GetMapping("/{id}")
    public Result<SignInInformation> queryRegisterInformation(@PathVariable String id){
        log.info("查询签到信息详情:{}",id);
        SignInInformation signInInformation = registerService.getById(id);
        if(signInInformation == null || signInInformation.getIsDelete() == 1){
            return Result.error(MessageConstant.SIGN_IN_NOT_FOUND);
        }
        return Result.success(signInInformation);
    }


}
