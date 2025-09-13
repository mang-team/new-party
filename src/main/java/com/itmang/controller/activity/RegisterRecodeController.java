package com.itmang.controller.activity;


import com.itmang.controller.BaseController;
import com.itmang.pojo.dto.AddRegisterRecordDTO;
import com.itmang.pojo.dto.DeleteRegisterRecodeDTO;
import com.itmang.pojo.dto.FindRegisterSignDTO;
import com.itmang.pojo.dto.UpdateRegisterRecordDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.service.activity.RegisterRecodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Tag(name= "签到记录相关接口")
@RequestMapping("/user/action/register")
public class RegisterRecodeController extends BaseController {

    @Autowired
    private RegisterRecodeService registerRecodeService;


    @Operation(summary = "新增签到信息")
    @PostMapping("/addRegisterRecord")
    public Result addRegisterInformation(@RequestBody List<AddRegisterRecordDTO> addRegisterRecordDTOList){
        log.info("新增签到信息:{}",addRegisterRecordDTOList);
        String userId = getUserIdFromToken();
        registerRecodeService.addRegisterRecodeInformation(addRegisterRecordDTOList,userId);
        return Result.success();
    }


    @Operation(summary = "删除签到信息")
    @PostMapping("/deleteRegisterRecord")
    public Result deleteRegisterInformation(@RequestBody DeleteRegisterRecodeDTO deleteRegisterRecodeDTO){
        log.info("删除签到信息:{}",deleteRegisterRecodeDTO);
        String userId = getUserIdFromToken();
        //String userId = "1";
        registerRecodeService.deleteRegisterRecodeInformation(deleteRegisterRecodeDTO,userId);
        return Result.success();
    }

    @Operation(summary = "编辑签到信息")
    @PostMapping("/modifyRegisterRecord")
    public Result updateRegisterInformation(
            @RequestBody UpdateRegisterRecordDTO updateRegisterRecordDTO){
        log.info("编辑签到信息:{}",updateRegisterRecordDTO);
        String userId = getUserIdFromToken();
        //String userId="1";
        registerRecodeService.updateRegisterRecodeInformation(updateRegisterRecordDTO,userId);
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
    @PostMapping("/findRegisterRecord")
    public Result<PageResult> queryRegisterInformationList(@RequestBody FindRegisterSignDTO findRegisterSignDTO){
        log.info("分页查询签到信息:{}", findRegisterSignDTO);
        PageResult pageResult = registerRecodeService.queryRegisterRecodeInformationList(findRegisterSignDTO);
        return Result.success(pageResult);
    }


}
