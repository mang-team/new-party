package com.itmang.controller.activity;


import com.itmang.controller.BaseController;
import com.itmang.pojo.dto.AddVoteRecordDTO;
import com.itmang.pojo.dto.FindVoteSignDTO;
import com.itmang.pojo.dto.UpdateVoteRecordDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.service.activity.VoteRecodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name= "投票记录相关接口")
@RequestMapping("/activity/voteRecord")
public class VoteRecodeController extends BaseController {

    @Autowired
    private VoteRecodeService voteRecodeService;


    @Operation(summary = "新增投票记录信息")
    @PostMapping("/addVoteRecord")
    public Result addVoteInformation(@RequestBody AddVoteRecordDTO addVoteRecordDTO){
        log.info("新增投票记录信息:{}",addVoteRecordDTO);
        String userId = getUserIdFromToken();
        voteRecodeService.addVoteRecodeInformation(addVoteRecordDTO,userId);
        return Result.success();
    }


    @Operation(summary = "删除投票记录信息")
    @PostMapping("/deleteVoteRecord/{ids}")
    public Result deleteVoteInformation(@PathVariable String ids){
        log.info("删除投票记录信息:{}",ids);
        String userId = getUserIdFromToken();
        voteRecodeService.deleteVoteRecodeInformation(ids,userId);
        return Result.success();
    }

    @Operation(summary = "编辑投票记录信息")
    @PostMapping("/modifyVoteRecord")
    public Result updateVoteInformation(
            @RequestBody UpdateVoteRecordDTO updateVoteRecordDTO){
        log.info("编辑投票记录信息:{}",updateVoteRecordDTO);
        String userId = getUserIdFromToken();
        voteRecodeService.updateVoteRecodeInformation(updateVoteRecordDTO,userId);
        return Result.success();
    }


    @Operation(summary = "分页查询投票记录信息")
    @GetMapping("/findVoteRecord")
    public Result<PageResult> queryVoteInformationList(FindVoteSignDTO findVoteSignDTO){
        log.info("分页查询投票记录信息:{}", findVoteSignDTO);
        PageResult pageResult = voteRecodeService.queryVoteRecodeList(findVoteSignDTO);
        return Result.success(pageResult);
    }

//    @Operation(summary = "查看投票记录信息")
//    @GetMapping("/modifyVoteRecord")
//    public Result updateVoteInformation(
//            @RequestBody UpdateVoteRecordDTO updateVoteRecordDTO){
//        log.info("编辑投票记录信息:{}",updateVoteRecordDTO);
//        String userId = getUserIdFromToken();
//        //String userId="1";
//        voteRecodeService.updateVoteRecodeInformation(updateVoteRecordDTO,userId);
//        return Result.success();
//    }


}
