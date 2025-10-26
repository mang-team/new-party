package com.itmang.controller.user;


import com.itmang.constant.MessageConstant;
import com.itmang.exception.BaseException;
import com.itmang.pojo.dto.RoleAddDTO;
import com.itmang.pojo.dto.RolePageDTO;
import com.itmang.pojo.dto.RoleUpdateDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Result;
import com.itmang.pojo.vo.RoleVO;
import com.itmang.service.user.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("user/role")
@Tag(name = "角色相关接口")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 新增角色
     *
     * @param roleAddDTO
     * @return
     */
    @Operation(summary = "新增角色")
    @PostMapping("/add")
    public Result addRole(@RequestBody RoleAddDTO roleAddDTO) {
        if (roleAddDTO == null || StringUtils.isEmpty(roleAddDTO.getRoleName())) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        log.info("新增角色:{}", roleAddDTO);
        roleService.addRole(roleAddDTO);
        return Result.success();
    }

    /**
     * 根据ID查询角色
     *
     * @param id
     * @return
     */
    @Operation(summary = "根据ID查询角色")
    @GetMapping("/{id}")
    public Result<RoleVO> getRoleById(@PathVariable String id) {
        if (id == null) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        log.info("根据ID查询角色:{}", id);
        RoleVO roleVO = roleService.getRoleById(id);
        return Result.success(roleVO);
    }

    /**
     * 更新角色
     *
     * @param roleUpdateDTO
     * @return
     */
    @Operation(summary = "更新角色")
    @PutMapping("/update")
    public Result updateRole(@RequestBody RoleUpdateDTO roleUpdateDTO) {
        if (roleUpdateDTO == null) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //判断有无更新id
        if (roleUpdateDTO.getId() == null) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        log.info("更新角色:{}", roleUpdateDTO);
        roleService.updateRole(roleUpdateDTO);
        return Result.success();
    }

    /**
     * 删除角色（可批量）
     *
     * @param ids
     * @return
     */
    @Operation(summary = "删除角色（可批量）")
    @DeleteMapping("/delete/{ids}")
    public Result deleteRole(@PathVariable String ids) {
        if (ids == null || StringUtils.isEmpty(ids.trim())) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        log.info("删除角色:{}", ids);
        //将ids的参数分割成数组
        roleService.deleteRole(ids.split(","));
        return Result.success();
    }

    /**
     * 分页查询角色
     *
     * @param rolePageDTO
     * @return
     */
    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public Result<PageResult> pageRole(RolePageDTO rolePageDTO) {
        if (rolePageDTO == null) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        log.info("分页查询角色:{}", rolePageDTO);
        PageResult pageResult = roleService.pageRole(rolePageDTO);
        return Result.success(pageResult);
    }

    /**
     * 给角色分配权限
     *
     * @param roleId
     * @param permissionIds
     * @return
     */
    @Operation(summary = "给角色分配权限")
    @PostMapping("/assignPermissions")
    public Result assignPermissions(@RequestParam String roleId, @RequestParam String permissionIds) {
        if (roleId == null || roleId.trim().isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        if (permissionIds == null || permissionIds.trim().isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        log.info("给角色分配权限: roleId={}, permissionIds={}", roleId, permissionIds);
        //将permissionIds的参数分割成数组
        String[] permissionIdArray =  permissionIds.split(",");
        roleService.assignPermissions(roleId, permissionIdArray);
        return Result.success();
    }
}
