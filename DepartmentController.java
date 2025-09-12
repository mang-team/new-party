package com.itmang.controller.party;


import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.dto.AddDepartmentDTO;
import com.itmang.pojo.dto.DepartmentPageQueryDTO;
import com.itmang.pojo.dto.DepartmentUpdateDTO;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.DepartmentVO;
import com.itmang.service.party.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("party/department")
@Tag(name ="门相关接口部")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;



    /**
     * 新增部门
     */
    @PostMapping("/add")
    @Operation(summary = "新增部门")
    public void addDepartment(@Valid@RequestBody AddDepartmentDTO addDepartmentDTO) {
           log.info("新增部门：{}", addDepartmentDTO);
          departmentService.addDept(addDepartmentDTO);

        }

        /**
         * 编辑部门信息
         * @param updateDTO 前端传入的部门信息（JSON格式）
         * @return 统一响应结果
         */
        @PostMapping("/update")
        public void updateDept(@Valid @RequestBody DepartmentUpdateDTO updateDTO) {
                departmentService.updateDept(updateDTO);
        }


    @DeleteMapping("/delete/ids")
    @Operation(summary = "批量删除部门")
    public void deleteDepartments(@RequestBody List<String> departmentIds) {
            log.info("批量删除部门：{}", departmentIds);
            departmentService.deleteDepartments(departmentIds);

    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取部门详细信息")
    public Result<DepartmentVO> getDepartmentById(@PathVariable String id) {

            DepartmentVO departmentVO = departmentService.getDepartmentDetail(id);
            return Result.success(departmentVO);
    }
    /**
     * 分页查询部门列表
     * @param departmentPageDTO 查询参数
     * @return 分页结果
     */
    @Operation(summary = "分页查询部门")
    @GetMapping("/page")
    public Result<PageResult> pageDepartment(DepartmentPageQueryDTO departmentPageDTO) {
        log.info("分页查询部门: {}", departmentPageDTO);
        PageResult pageResult = departmentService.pageDepartment(departmentPageDTO);
        return Result.success(pageResult);
    }
    @Operation(summary = "部门列表查询")
    @PostMapping("/list")
    public Result<List<DepartmentVO>> departmentList(@RequestHeader("Authorization") String token) {

        log.info("查询所有部门列表");

        // TODO: token验证逻辑
        if (!validateToken(token)) {
            return Result.error("Token验证失败");
        }

        List<DepartmentVO> departmentList = departmentService.departmentList();
        return Result.success(departmentList);
    }

    private boolean validateToken(String token) {
        return token != null && token.startsWith("Bearer ");
    }






}


