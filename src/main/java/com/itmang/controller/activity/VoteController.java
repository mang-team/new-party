package com.itmang.controller.activity;


import com.itmang.controller.BaseController;
import com.itmang.pojo.dto.AddVoteDTO;
import com.itmang.pojo.dto.DeleteVoteDTO;
import com.itmang.pojo.dto.FindVoteDTO;
import com.itmang.pojo.dto.UpdateVoteDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.VoteInformationVO;
import com.itmang.service.activity.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name= "投票相关接口")
@RequestMapping("/activity/vote")
public class VoteController extends BaseController {

    @Autowired
    private VoteService voteService;


    @Operation(summary = "新增投票信息")
    @PostMapping("/addVote")
    public Result addVoteInformation(@RequestBody List<AddVoteDTO> addVoteDTOList){
        log.info("新增投票信息:{}",addVoteDTOList);
        String userId = getUserIdFromToken();
        //String userId = "1";
        voteService.addVoteInformation(addVoteDTOList,userId);
        return Result.success();
    }

    @Operation(summary = "删除投票信息")
    @PostMapping("/deleteVote")
    public Result deleteVoteInformation(@RequestBody DeleteVoteDTO deleteVoteDTO){
        log.info("删除投票信息:{}",deleteVoteDTO);
        String userId = getUserIdFromToken();
        voteService.deleteVoteInformation(deleteVoteDTO,userId);
        return Result.success();
    }

    @Operation(summary = "编辑投票信息")
    @PostMapping("/modifyVote")
    public Result updateVoteInformation(
            @RequestBody UpdateVoteDTO updateVoteDTO){
        log.info("编辑投票信息:{}",updateVoteDTO);
        String userId = getUserIdFromToken();
        voteService.updateVoteInformation(updateVoteDTO,userId);
        return Result.success();
    }



    @Operation(summary = "分页查询投票信息")
    @GetMapping("/findVote")
    public Result<PageResult> queryVoteInformationList(FindVoteDTO findVoteDTO){
        log.info("分页查询投票信息:{}",findVoteDTO);
        PageResult pageResult = voteService.queryVoteInformationList(findVoteDTO);
        return Result.success(pageResult);
    }


    @Operation(summary = "查询投票信息")
    @GetMapping("/queryVote/{id}")
    public Result<VoteInformationVO> queryVoteInformation(@PathVariable String id){
        log.info("查询投票信息:{}",id);
        VoteInformationVO voteInformationVO = voteService.queryVoteInformation(id);
        return Result.success(voteInformationVO);
    }



}
