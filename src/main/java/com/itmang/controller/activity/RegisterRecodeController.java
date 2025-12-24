package com.itmang.controller.activity;


import com.itmang.controller.BaseController;
import com.itmang.pojo.dto.AddRegisterRecordDTO;
import com.itmang.pojo.dto.DeleteRegisterRecodeDTO;
import com.itmang.pojo.dto.FindRegisterSignDTO;
import com.itmang.pojo.dto.UpdateRegisterRecordDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.entity.SignInRecord;
import com.itmang.pojo.vo.ExaminationVO;
import com.itmang.pojo.vo.SignInRecordPageVO;
import com.itmang.pojo.vo.SignInRecordVO;
import com.itmang.service.activity.RegisterRecodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name= "签到记录相关接口")
@RequestMapping("/activity/registerRecord")
public class RegisterRecodeController extends BaseController {

    @Autowired
    private RegisterRecodeService registerRecodeService;


    @Operation(summary = "新增签到信息")
    @PostMapping("/addRegisterRecord")
    public Result addRegisterInformation(@RequestBody AddRegisterRecordDTO addRegisterRecordDTO){
        log.info("新增签到信息:{}",addRegisterRecordDTO);
        String userId = getUserIdFromToken();
        registerRecodeService.addRegisterRecodeInformation(addRegisterRecordDTO,userId);
        return Result.success();
    }


    @Operation(summary = "删除签到信息（可批量）")
    @PostMapping("/deleteRegisterRecord")
    public Result deleteRegisterInformation(@RequestBody DeleteRegisterRecodeDTO deleteRegisterRecodeDTO){
        log.info("删除签到信息:{}",deleteRegisterRecodeDTO);
        String userId = getUserIdFromToken();
        registerRecodeService.deleteRegisterRecodeInformation(deleteRegisterRecodeDTO,userId);
        return Result.success();
    }

    @Operation(summary = "编辑签到信息")
    @PostMapping("/modifyRegisterRecord")
    public Result updateRegisterInformation(
            @RequestBody UpdateRegisterRecordDTO updateRegisterRecordDTO){
        log.info("编辑签到信息:{}",updateRegisterRecordDTO);
        String userId = getUserIdFromToken();
        registerRecodeService.updateRegisterRecodeInformation(updateRegisterRecordDTO,userId);
        return Result.success();
    }

    @Operation(summary = "查询签到记录")
    @GetMapping("/{id}")
    public Result<SignInRecordVO> queryRegisterInformation(@PathVariable String id){
        log.info("查询签到记录:{}",id);
        SignInRecordVO signInRecord = registerRecodeService.queryRegisterRecodeInformation(id);
        return Result.success(signInRecord);
    }

    @Operation(summary = "分页查询签到信息")
    @GetMapping("/findRegisterRecord")
    public Result<PageResult> queryRegisterInformationList(FindRegisterSignDTO findRegisterSignDTO){
        log.info("分页查询签到信息:{}", findRegisterSignDTO);
        PageResult pageResult = registerRecodeService.queryRegisterRecodeInformationList(findRegisterSignDTO);
        return Result.success(pageResult);
    }

    @Operation(summary = "用户签到")
    @PostMapping("/userSign/{id}")
    public Result userSign(@PathVariable String id){
        log.info("用户签到{}",id);
        registerRecodeService.userSign(id);
        return Result.success();
    }

    @Operation(summary = "修改用户签到状态")
    @PostMapping("/updateStatus/{status}")
    public Result updateUserStatus(@PathVariable Integer status,String id){
        log.info("用户{}签到{}",id,status);
        registerRecodeService.updateUserStatus(status,id);
        return Result.success();
    }



}
