package com.itmang.controller.activity;


import com.itmang.controller.BaseController;
import com.itmang.pojo.dto.AddVoteDTO;
import com.itmang.pojo.dto.DeleteVoteDTO;
import com.itmang.pojo.dto.FindVoteDTO;
import com.itmang.pojo.dto.UpdateVoteDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.service.activity.VoteService;
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
public class VoteController extends BaseController {

    @Autowired
    private VoteService voteService;

    //TODO 新增考试信息接口还需要完善分发考卷的逻辑
    @PostMapping("/addVote")
    public Result addVoteInformation(@RequestBody List<AddVoteDTO> addVoteDTOList){
        log.info("新增签到信息:{}",addVoteDTOList);
        String userId = getUserIdFromToken();
        //String userId = "1";
        voteService.addVoteInformation(addVoteDTOList,userId);
        return Result.success();
    }


    @PostMapping("/deleteVote")
    public Result deleteVoteInformation(@RequestBody DeleteVoteDTO deleteVoteDTO){
        log.info("删除签到信息:{}",deleteVoteDTO);
        String userId = getUserIdFromToken();
        //String userId = "1";
        voteService.deleteVoteInformation(deleteVoteDTO,userId);
        return Result.success();
    }

    @PostMapping("/modifyVote")
    public Result updateVoteInformation(
            @RequestBody UpdateVoteDTO updateVoteDTO){
        log.info("编辑签到信息:{}",updateVoteDTO);
        String userId = getUserIdFromToken();
        //String userId = "1";
        voteService.updateVoteInformation(updateVoteDTO,userId);
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
    @PostMapping("/findVote")
    public Result<PageResult> queryVoteInformationList(@RequestBody FindVoteDTO findVoteDTO){
        log.info("分页查询签到信息:{}",findVoteDTO);
        PageResult pageResult = voteService.queryVoteInformationList(findVoteDTO);
        return Result.success(pageResult);
    }


}
