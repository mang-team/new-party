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
@Tag(name= "投票记录相关接口")
@RequestMapping("/user/action/register")
public class VoteRecodeController extends BaseController {

    @Autowired
    private VoteRecodeService voteRecodeService;


    @Operation(summary = "新增投票记录信息")
    @PostMapping("/addVoteRecord")
    public Result addVoteInformation(@RequestBody List<AddVoteRecordDTO> addVoteRecordDTOList){
        log.info("新增投票记录信息:{}",addVoteRecordDTOList);
        String userId = getUserIdFromToken();
        //String userId = "1";
        voteRecodeService.addVoteRecodeInformation(addVoteRecordDTOList,userId);
        return Result.success();
    }


    @Operation(summary = "删除投票记录信息")
    @PostMapping("/deleteVoteRecord")
    public Result deleteVoteInformation(@RequestBody DeleteVoteRecodeDTO deleteVoteRecodeDTO){
        log.info("删除投票记录信息:{}",deleteVoteRecodeDTO);
        String userId = getUserIdFromToken();
        //String userId = "1";
        voteRecodeService.deleteVoteRecodeInformation(deleteVoteRecodeDTO,userId);
        return Result.success();
    }

    @Operation(summary = "编辑投票记录信息")
    @PostMapping("/modifyVoteRecord")
    public Result updateVoteInformation(
            @RequestBody UpdateVoteRecordDTO updateVoteRecordDTO){
        log.info("编辑投票记录信息:{}",updateVoteRecordDTO);
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

    @Operation(summary = "分页查询投票记录信息")
    @PostMapping("/findVoteRecord")
    public Result<PageResult> queryVoteInformationList(@RequestBody FindVoteSignDTO findVoteSignDTO){
        log.info("分页查询投票记录信息:{}", findVoteSignDTO);
        PageResult pageResult = voteRecodeService.queryVoteRecodeInformationList(findVoteSignDTO);
        return Result.success(pageResult);
    }


}
