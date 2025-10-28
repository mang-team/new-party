package com.itmang.service.user.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.PageConstant;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;
import com.itmang.mapper.user.PermissionMapper;
import com.itmang.mapper.user.RolePermissionMapper;
import com.itmang.pojo.dto.PermissionAddDTO;
import com.itmang.pojo.dto.PermissionPageDTO;
import com.itmang.pojo.dto.PermissionUpdateDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.Permission;
import com.itmang.pojo.entity.RolePermission;
import com.itmang.pojo.vo.PermissionVO;
import com.itmang.service.user.PermissionService;
import com.itmang.utils.IdGenerate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Resource
    private IdGenerate idGenerate;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    /**
     * 新增权限
     *
     * @param permissionAddDTO
     */
    @Override
    public void addPermission(PermissionAddDTO permissionAddDTO) {
        //查询数据库是否有同路径的权限
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getUrl, permissionAddDTO.getUrl());
        queryWrapper.eq(Permission::getIsDelete,2);
        Long count = permissionMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }

        //克隆实体类
        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionAddDTO, permission);

        // 设置ID
        permission.setId(idGenerate.nextUUID(permission));

        // 设置创建人和创建时间
        permission.setCreateBy(BaseContext.getCurrentId());
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateBy(BaseContext.getCurrentId());
        permission.setUpdateTime(LocalDateTime.now());
        permission.setIsDelete(2); // 未删除状态

        permissionMapper.insertPermission(permission);
    }

    /**
     * 根据ID查询权限
     *
     * @param id
     * @return
     */
    @Override
    public PermissionVO getPermissionById(String id) {
        Permission permission = permissionMapper.getPermissionById(id);
        //如果没有查询出权限就抛异常
        if (permission == null) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        PermissionVO permissionVO = new PermissionVO();
        BeanUtils.copyProperties(permission, permissionVO);
        return permissionVO;
    }

    /**
     * 更新权限
     *
     * @param permissionUpdateDTO
     */
    @Override
    public void updatePermission(PermissionUpdateDTO permissionUpdateDTO) {
        if (permissionUpdateDTO.getId() == null) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }

        //判断修改后的url是否重复
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getUrl, permissionUpdateDTO.getUrl());
        queryWrapper.eq(Permission::getIsDelete, 2);
        Permission dbPermission = permissionMapper.selectOne(queryWrapper);
        if(dbPermission != null && !dbPermission.getId().equals(permissionUpdateDTO.getId())) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }

        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionUpdateDTO, permission);

        // 设置更新人和更新时间
        permission.setUpdateBy(BaseContext.getCurrentId());
        permission.setUpdateTime(LocalDateTime.now());

        int rows = permissionMapper.updateById(permission);
        if(rows == 0) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
    }

    /**
     * 删除权限（可批量）
     *
     * @param ids
     */
    @Override
    public void deletePermission(String[] ids) {
        // 逻辑删除，将isDelete设置为1
        for (String id : ids) {
            //判断权限是否正在被分配给角色使用
            LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RolePermission::getPermissionId, id);
            Long count = rolePermissionMapper.selectCount(queryWrapper);
            if(count > 0) {
                throw new BaseException("此权限正在被使用");
            }

            Permission permission = new Permission();
            permission.setId(id);
            permission.setIsDelete(1); // 删除状态
            permission.setUpdateBy(BaseContext.getCurrentId());
            permission.setUpdateTime(LocalDateTime.now());

            int rows = permissionMapper.updateById(permission);
            if(rows == 0) {
                throw new BaseException(MessageConstant.PARAMETER_ERROR);
            }
        }
    }

    /**
     * 分页查询权限
     *
     * @param permissionPageDTO
     * @return
     */
    @Override
    public PageResult pagePermission(PermissionPageDTO permissionPageDTO) {
        // 设置默认页码和分页大小
        if (permissionPageDTO.getPage() == null || permissionPageDTO.getPage() < 1) {
            permissionPageDTO.setPage(PageConstant.PAGE_NUM);
        }
        if (permissionPageDTO.getPageSize() == null || permissionPageDTO.getPageSize() < 1) {
            permissionPageDTO.setPageSize(PageConstant.PAGE_SIZE);
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(permissionPageDTO.getPage(), permissionPageDTO.getPageSize());
        List<Permission> permissionList = permissionMapper.pagePermission(permissionPageDTO);

        // 转换为VO对象
        List<PermissionVO> permissionVOList = permissionList.stream().map(permission -> {
            PermissionVO permissionVO = new PermissionVO();
            BeanUtils.copyProperties(permission, permissionVO);
            return permissionVO;
        }).collect(Collectors.toList());

        PageInfo<PermissionVO> pageInfo = new PageInfo<>(permissionVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<String> getUserPermission(String id) {
        return permissionMapper.getUserPermission(id);
    }
}
