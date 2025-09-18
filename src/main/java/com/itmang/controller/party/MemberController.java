package com.itmang.controller.party;
import com.itmang.pojo.dto.MemberAddDTO;
import com.itmang.pojo.dto.MemberQueryDTO;
import com.itmang.pojo.dto.MemberUpdateDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.MemberVO;
import com.itmang.service.party.MemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/party/member")
@Tag(name = "成员相关接口")
public class MemberController {

    // 注入MemberService实例
    @Autowired
    private MemberService memberService;



    @Operation(summary = "分页查询部门成员")
    @PostMapping("/page")
    @Transactional(readOnly = true)
    public Result<PageResult> queryMemberList(@RequestBody  MemberQueryDTO memberQueryDTO){
        log.info("分页查询部门成员:{}", memberQueryDTO);
        PageResult pageResult = memberService.queryMemberList(memberQueryDTO);
        return Result.success(pageResult);
    }


    @Operation(summary = "查询成员详细信息")
    @GetMapping("/detail/{id}")
    public Result<MemberVO> getMemberDetail(@PathVariable String id) {
        log.info("查询成员详细信息:id={}", id);
        MemberVO member = memberService.getMemberDetailById(id);
        return Result.success(member);
    }


    @Operation(summary = "编辑成员信息")
    @PostMapping("/update")
    public Result updateMember(@Valid @RequestBody MemberUpdateDTO memberUpdateDTO) {
        log.info("编辑成员信息:{}", memberUpdateDTO);
        memberService.updateMember(memberUpdateDTO);
        return Result.success();
    }



    @PostMapping("/batch")
    @ApiOperation("批量新增成员")
    public Result<String> batchAddMembers(@Valid @RequestBody List<MemberAddDTO> memberDTOList) {
        memberService.batchAddMembers(memberDTOList);
        return Result.success();

    }

    @Operation(summary = "删除成员信息（可批量）")
    @PostMapping("/delete/{ids}")
    public Result deleteMember(@PathVariable String[] ids){
        log.info("删除成员id:{}",ids);
        memberService.deleteMember(ids);
        return Result.success();
    }



}