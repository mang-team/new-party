package com.itmang.controller.party;


import com.itmang.pojo.dto.DepartmentDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.dto.DepartmentPageQueryDTO;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.DepartmentVO;
import com.itmang.service.party.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("party/department")
@Tag(name = "部门相关接口部")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;


    /**
     * 新增部门
     */
    @PostMapping("/add")
    @Operation(summary = "新增部门")
    public Result addDepartment(@Valid @RequestBody DepartmentDTO DepartmentDTO) {
        log.info("新增部门：{}", DepartmentDTO);
        departmentService.addDept(DepartmentDTO);
        return Result.success();
    }

    /**
     * 编辑部门信息
     *
     * @param updateDTO 前端传入的部门信息（JSON格式）
     * @return 统一响应结果
     */
    @PostMapping("/update")
    public Result updateDept(@Valid @RequestBody DepartmentDTO updateDTO) {
        departmentService.updateDept(updateDTO);
        return Result.success();
    }


    @PostMapping("/delete/{ids}")
    @Operation(summary = "批量删除部门")
    public Result deleteDepartments(@PathVariable String[] ids) {
        log.info("批量删除部门：{}", ids);
        departmentService.deleteDepartments(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取部门详细信息")
    public Result<DepartmentVO> getDepartmentById(@PathVariable String id) {

        DepartmentVO departmentVO = departmentService.getDepartmentDetail(id);
        return Result.success(departmentVO);
    }

    /**
     * 分页查询部门列表
     *
     * @param departmentPageDTO 查询参数
     * @return 分页结果
     */
    @Operation(summary = "分页查询部门")
    @PostMapping("/page")
    public Result<PageResult> pageDepartment(@RequestBody DepartmentPageQueryDTO departmentPageDTO) {
        log.info("分页查询部门: {}", departmentPageDTO);
        PageResult pageResult = departmentService.pageDepartment(departmentPageDTO);
        return Result.success(pageResult);
    }

    @Operation(summary = "部门列表查询")
    @GetMapping("/list")
    public Result<List<DepartmentVO>> departmentList() {

        log.info("查询所有部门列表");

        List<DepartmentVO> departmentList = departmentService.departmentList();
        return Result.success(departmentList);
    }


}


