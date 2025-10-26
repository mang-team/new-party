package com.itmang.controller.user;


import com.itmang.constant.MessageConstant;
import com.itmang.exception.BaseException;
import com.itmang.pojo.dto.PermissionAddDTO;
import com.itmang.pojo.dto.PermissionPageDTO;
import com.itmang.pojo.dto.PermissionUpdateDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.PermissionVO;
import com.itmang.service.user.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("user/permission")
@Tag(name = "权限相关接口")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 新增权限
     *
     * @param permissionAddDTO
     * @return
     */
    @Operation(summary = "新增权限")
    @PostMapping("/add")
    public Result addPermission(@RequestBody PermissionAddDTO permissionAddDTO) {
        if (permissionAddDTO == null || StringUtils.isEmpty(permissionAddDTO.getUrl().trim())) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        log.info("新增权限:{}", permissionAddDTO);
        permissionService.addPermission(permissionAddDTO);
        return Result.success();
    }

    /**
     * 根据ID查询权限
     *
     * @param id
     * @return
     */
    @Operation(summary = "根据ID查询权限")
    @GetMapping("/{id}")
    public Result<PermissionVO> getPermissionById(@PathVariable String id) {
        if (id == null) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        log.info("根据ID查询权限:{}", id);
        PermissionVO permissionVO = permissionService.getPermissionById(id);
        return Result.success(permissionVO);
    }

    /**
     * 更新权限
     *
     * @param permissionUpdateDTO
     * @return
     */
    @Operation(summary = "更新权限")
    @PutMapping("/update")
    public Result updatePermission(@RequestBody PermissionUpdateDTO permissionUpdateDTO) {
        if(permissionUpdateDTO == null) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //判断有无更新id
        if (permissionUpdateDTO.getId() == null) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        log.info("更新权限:{}", permissionUpdateDTO);
        permissionService.updatePermission(permissionUpdateDTO);
        return Result.success();
    }

    /**
     * 删除权限（可批量）
     *
     * @param ids
     * @return
     */
    @Operation(summary = "删除权限（可批量）")
    @DeleteMapping("/delete/{ids}")
    public Result deletePermission(@PathVariable String ids) {
        if (ids == null || StringUtils.isEmpty(ids.trim())) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        log.info("删除权限:{}", ids);
        //将ids的参数分割成数组
        permissionService.deletePermission(ids.split(","));
        return Result.success();
    }

    /**
     * 分页查询权限
     *
     * @param permissionPageDTO
     * @return
     */
    @Operation(summary = "分页查询权限")
    @GetMapping("/page")
    public Result<PageResult> pagePermission(PermissionPageDTO permissionPageDTO) {
        if (permissionPageDTO == null) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        log.info("分页查询权限:{}", permissionPageDTO);
        PageResult pageResult = permissionService.pagePermission(permissionPageDTO);
        return Result.success(pageResult);
    }

}
