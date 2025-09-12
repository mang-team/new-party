package com.itmang.controller.activity;


import com.itmang.controller.BaseController;
import com.itmang.pojo.dto.AddVoteRecordDTO;
import com.itmang.pojo.dto.DeleteVoteRecodeDTO;
import com.itmang.pojo.dto.FindVoteSignDTO;
import com.itmang.pojo.dto.UpdateVoteRecordDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.service.activity.VoteRecodeService;
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
public class VoteRecodeController extends BaseController {

    @Autowired
    private VoteRecodeService voteRecodeService;

    //TODO 新增考试信息接口还需要完善分发考卷的逻辑
    @PostMapping("/addVoteRecord")
    public Result addVoteInformation(@RequestBody List<AddVoteRecordDTO> addVoteRecordDTOList){
        log.info("新增签到信息:{}",addVoteRecordDTOList);
        String userId = getUserIdFromToken();
        //String userId = "1";
        voteRecodeService.addVoteRecodeInformation(addVoteRecordDTOList,userId);
        return Result.success();
    }


    @PostMapping("/deleteVoteRecord")
    public Result deleteVoteInformation(@RequestBody DeleteVoteRecodeDTO deleteVoteRecodeDTO){
        log.info("删除签到信息:{}",deleteVoteRecodeDTO);
        String userId = getUserIdFromToken();
        //String userId = "1";
        voteRecodeService.deleteVoteRecodeInformation(deleteVoteRecodeDTO,userId);
        return Result.success();
    }

    @PostMapping("/modifyVoteRecord")
    public Result updateVoteInformation(
            @RequestBody UpdateVoteRecordDTO updateVoteRecordDTO){
        log.info("编辑签到信息:{}",updateVoteRecordDTO);
        String userId = getUserIdFromToken();
        //String userId="1";
        voteRecodeService.updateVoteRecodeInformation(updateVoteRecordDTO,userId);
        return Result.success();
    }

//    @Operation(summary = "查询考试信息")
//    @GetMapping("/{id}")
//    public Result<ExaminationVO> queryVoteInformation(@PathVariable String id){
//        log.info("查询考试信息:{}",id);
//        ExaminationVO examinationVO =
//                examinationService.queryExaminationInformation(id);
//        return Result.success(examinationVO);
//    }

    @Operation(summary = "分页查询签到信息")
    @PostMapping("/findVoteRecord")
    public Result<PageResult> queryVoteInformationList(@RequestBody FindVoteSignDTO findVoteSignDTO){
        log.info("分页查询签到信息:{}", findVoteSignDTO);
        PageResult pageResult = voteRecodeService.queryVoteRecodeInformationList(findVoteSignDTO);
        return Result.success(pageResult);
    }


}
