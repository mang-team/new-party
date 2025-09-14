package com.itmang.service.party.Impl;

import com.itmang.pojo.dto.DepartmentDTO;
import com.itmang.pojo.entity.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;
import com.itmang.mapper.party.DepartmentMapper;
import com.itmang.mapper.party.MemberMapper;
import com.itmang.pojo.dto.DepartmentPageQueryDTO;
import com.itmang.pojo.vo.DepartmentVO;
import com.itmang.service.party.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department>
        implements DepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private MemberMapper memberMapper;


    //新增部门
    @Override
    @Transactional
    public void addDept(DepartmentDTO addDepartmentDTO) {
        //新增部门
        // 1. 基础数据验证
        validateDepartmentBasic(addDepartmentDTO);

        // 2. 检查部门名称是否已存在
        if (checkDepartmentNameExists(addDepartmentDTO.getDepartmentName(), null)) {
            throw new BaseException(MessageConstant.DEPARTMENT_NAME_EXISTS);
        }

        // 3. 验证父部门是否存在
        validateFatherDepartment(addDepartmentDTO.getFatherDepartmentId(), null);

        // 4. 转换DTO为Entity并设置创建信息
        Department department = convertAddDTOToEntity(addDepartmentDTO);
        // 5. 保存部门
        departmentMapper.insert(department);

    }


    /**
     * 基础数据验证
     */
    private void validateDepartmentBasic(DepartmentDTO addDepartmentDTO) {
        //部门信息不能为空
        if (addDepartmentDTO == null) {
            throw new BaseException(MessageConstant.DEPARTMENT_EMPTY);
        }
        //部门名称不能为空
        if (StringUtils.isBlank(addDepartmentDTO.getDepartmentName())) {
            throw new BaseException(MessageConstant.DEPARTMENT_NAME_EMPTY);
        }
        //部门名称长度不能超过20个字符
        if (addDepartmentDTO.getDepartmentName().length() > 20) {
            throw new BaseException(MessageConstant.DEPARTMENT_NAME_TOO_LONG);
        }

//        // 设置默认上级部门
//        if (StringUtils.isBlank(addDepartmentDTO.getFatherDepartmentId())) {
//            addDepartmentDTO.setFatherDepartmentId("-1");
//        }
        // 设置默认上级部门，确保不为null
        if (StringUtils.isBlank(addDepartmentDTO.getFatherDepartmentId())) {
            addDepartmentDTO.setFatherDepartmentId("-1");
        }

        // 额外确保不是null
        if (addDepartmentDTO.getFatherDepartmentId() == null) {
            addDepartmentDTO.setFatherDepartmentId("-1");
        }

    }

    /**
     * 将AddDTO转换为Entity
     */
    private Department convertAddDTOToEntity(DepartmentDTO addDepartmentDTO) {
        Department department = new Department();

        // 生成唯一ID
        // 生成部门ID：如果前端传入了ID则使用前端的，否则自动生成
        String deptId;
        if (StringUtils.isNotBlank(addDepartmentDTO.getId())) {
            deptId = addDepartmentDTO.getId();
            // 验证ID是否已存在
            Department existingDept = departmentMapper.selectById(deptId);
            if (existingDept != null) {
                throw new BaseException("部门ID已存在");
            }
        } else {
            deptId = generateNextDeptId();
        }
        String currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new BaseException("当前用户未登录或会话已过期");
        }

        department.setId(deptId);
        department.setCreateBy(currentUserId);
        department.setId(deptId);
        department.setDepartmentName(addDepartmentDTO.getDepartmentName());
        department.setFatherDepartmentId(addDepartmentDTO.getFatherDepartmentId());

        // 设置创建信息
        department.setCreateBy(BaseContext.getCurrentId());
        department.setCreateTime(LocalDateTime.now());
        department.setIsDelete(DeleteConstant.NO);

        // 初始化更新信息（与创建信息相同）
        department.setUpdateBy(department.getCreateBy());
        department.setUpdateTime(department.getCreateTime());

        return department;
    }


    //更新部门信息
    @Override
    @Transactional
    public void updateDept(DepartmentDTO updateDTO) {
        // 新增：校验ID非空
        if (updateDTO.getId() == null) {
            throw new BaseException(MessageConstant.DEPARTMENT_ID_EMPTY);
        }
        // 1、判断部门是否存在
        Department originalDept = departmentMapper.selectById(updateDTO.getId());
        if (originalDept == null || originalDept.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException(MessageConstant.DEPARTMENT_NOT_EXISTS);
        }

        // 2、判断部门名称是否发生变化且需要验证重复
        if (!originalDept.getDepartmentName().equals(updateDTO.getDepartmentName())) {
            boolean nameExists = checkDepartmentNameExists(updateDTO.getDepartmentName(), updateDTO.getId());
            if (nameExists) {
                throw new BaseException(MessageConstant.DEPARTMENT_NAME_EXISTS);
            }
        }

        // 3、判断父部门是否发生变化且需要验证
        if (!originalDept.getFatherDepartmentId().equals(updateDTO.getFatherDepartmentId())) {
            validateFatherDepartment(updateDTO.getFatherDepartmentId(), updateDTO.getId());
        }

        // 4、转换DTO为Entity并更新
        Department updateData = convertToEntity(updateDTO, originalDept);
        departmentMapper.updateById(updateData);
    }

    /**
     * 将DTO转换为Entity，保留原有信息
     */
    private Department convertToEntity(DepartmentDTO dto, Department originalDept) {
        Department department = new Department();
        department.setId(dto.getId());
        department.setDepartmentName(dto.getDepartmentName());
        department.setFatherDepartmentId(dto.getFatherDepartmentId());
        department.setUpdateBy(BaseContext.getCurrentId());
        department.setUpdateTime(LocalDateTime.now());

        // 保留原有的创建信息
        department.setCreateBy(originalDept.getCreateBy());
        department.setCreateTime(originalDept.getCreateTime());
        department.setIsDelete(originalDept.getIsDelete());

        return department;
    }

    //删除部门
    @Override
    @Transactional
    public void deleteDepartments(String[] departmentIds) {
        List<String> canDeleteIds = new ArrayList<>();
        List<String> canNotDeleteIds = new ArrayList<>();

        // 判断部门是否存在且是否可以删除
        for (String id : departmentIds) {
            Department department = departmentMapper.selectById(id);

            if (department == null || department.getIsDelete().equals(DeleteConstant.YES)) {
                canNotDeleteIds.add(id);
                continue;
            }

            if (hasChildrenDepartments(id)) {
                canNotDeleteIds.add(id);
                continue;
            }

            if (hasUsersInDepartment(id)) {
                canNotDeleteIds.add(id);
                continue;
            }

            canDeleteIds.add(id);
        }

        // 执行删除逻辑 - 改为单条更新
        if (!canDeleteIds.isEmpty()) {
            for (String departmentId : canDeleteIds) {
                Department department = new Department();
                department.setId(departmentId);
                department.setIsDelete(DeleteConstant.YES);
                department.setUpdateBy(BaseContext.getCurrentId());
                department.setUpdateTime(LocalDateTime.now());
                departmentMapper.updateById(department); // 单条更新
            }
        }

        // 处理删除结果
        if (!canNotDeleteIds.isEmpty()) {
            if (canDeleteIds.isEmpty()) {
                throw new BaseException(MessageConstant.DEPARTMENT_FAIL_DELETED);
            } else {
                throw new BaseException(MessageConstant.DEPARTMENT_PART_DELETED);
            }
        }
    }


    /**
     * 根据部门ID查询部门详细信息（VO）
     */
    @Override
    public DepartmentVO getDepartmentDetail(String id) {
        if (StringUtils.isBlank(id)) {
            throw new BaseException(MessageConstant.DEPARTMENT_ID_EMPTY);
        }

        // 查询部门信息
        Department department = departmentMapper.selectById(id);
        if (department == null || department.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException(MessageConstant.DEPARTMENT_NOT_EXISTS);
        }


        // 转换为VO并填充附加信息
        DepartmentVO departmentVO = convertToVO(department);
        //   fillAdditionalInfo(departmentVO);(填充完整信息）

        return departmentVO;
    }

    @Override
    public List<DepartmentVO> departmentList() {
        return departmentMapper.listDepartments();
    }


    /**
     * 将Entity转换为VO
     */
    private DepartmentVO convertToVO(Department department) {
        DepartmentVO vo = new DepartmentVO();
        vo.setId(department.getId());
        vo.setDepartmentName(department.getDepartmentName());
        vo.setFatherDepartmentId(department.getFatherDepartmentId());
        vo.setCreateBy(department.getCreateBy());
        vo.setCreateTime(department.getCreateTime());
        vo.setUpdateBy(department.getUpdateBy());
        vo.setUpdateTime(department.getUpdateTime());
        vo.setIsDelete(department.getIsDelete());
        vo.setStatusDisplay(getStatusDisplay(department.getIsDelete()));
        return vo;
    }

//    /**
//     * 填充附加信息
//     */
//    private void fillAdditionalInfo(DepartmentVO departmentVO) {
//        // 填充父部门名称
//        if (!"-1".equals(departmentVO.getFatherDepartmentId())) {
//            Department fatherDept = getById(departmentVO.getFatherDepartmentId());
//            if (fatherDept != null && !fatherDept.getIsDelete().equals(DeleteConstant.YES)) {
//                departmentVO.setFatherDepartmentName(fatherDept.getDepartmentName());
//            }
//        }
//
//        // 填充子部门数量
//        int childrenCount = getChildrenDepartmentCount(departmentVO.getId());
//        departmentVO.setChildrenCount(childrenCount);
//
//        // 填充成员数量
//        int memberCount = getMemberCountByDepartment(departmentVO.getId());
//        departmentVO.setMemberCount(memberCount);
//    }
//

//    /**
//     * 获取子部门数量
//     */
//    private int getChildrenDepartmentCount(String departmentId) {
//        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Department::getFatherDepartmentId, departmentId)
//                .eq(Department::getIsDelete, DeleteConstant.NO);
//        return (int) count( wrapper);
//    }

//   /**
//     * 获取部门成员数量
//     */
//    /**
//     * 获取部门成员数量
//     */
//    private int getMemberCountByDepartment(String departmentId) {
//        return memberMapper.selectCount(new LambdaQueryWrapper<Member>()
//                .eq(Member::getDepartmentId, departmentId)
//                .eq(Member::getIsDelete, DeleteConstant.NO)
//        );
//    }


    @Override
    public PageResult pageDepartment(DepartmentPageQueryDTO departmentPageDTO) {
        // 使用pageHelper工具进行分页查询
        PageHelper.startPage(departmentPageDTO.getCurrentpage(), departmentPageDTO.getSize());
        // 进行条件查询
        List<DepartmentVO> departmentList = departmentMapper.pageSearch(departmentPageDTO);
        PageInfo<DepartmentVO> pageInfo = new PageInfo<>(departmentList);
        // 构建返回结果

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());

    }
//部门列表

    private String getStatusDisplay(Integer isDelete) {
        if (DeleteConstant.YES.equals(isDelete)) {
            return "已删除";
        } else {
            return "正常";
        }
    }


    //辅助方法

    private String generateNextDeptId() {
        // 查询当前最大ID
        String maxId = departmentMapper.selectMaxDeptId();

        if (maxId == null) {
            // 若没有数据，默认从DEPT_001开始
            return "DEPT_001";
        }

        // 提取数字部分（假设格式固定为DEPT_XXX）
        String numStr = maxId.substring(5); // 从"DEPT_"后开始截取，得到"010"
        int num = Integer.parseInt(numStr);
        num++; // 自增1

        // 补零保持3位格式（如1→001，10→010，100→100）
        return String.format("DEPT_%03d", num);
    }
//更新部门要用到的辅助方法

    /**
     * 验证父部门是否存在且有效
     */
    private void validateFatherDepartment(String fatherDepartmentId, String currentDeptId) {
        if (StringUtils.isBlank(fatherDepartmentId)) {
            fatherDepartmentId = "-1"; // 设置默认值
        }

        // 根部门检查
        if ("-1".equals(fatherDepartmentId)) {
            return;
        }

        // 检查父部门是否存在
        Department fatherDept = getById(fatherDepartmentId);
        if (fatherDept == null || fatherDept.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException(MessageConstant.FATHER_DEPARTMENT_NOT_EXISTS);
        }

        // 防止将自己设为父部门
        if (currentDeptId != null && currentDeptId.equals(fatherDepartmentId)) {
            throw new BaseException(MessageConstant.CANNOT_SET_SELF_AS_PARENT);
        }

    }


    /**
     * 检查部门名称是否已存在
     */
    private boolean checkDepartmentNameExists(String departmentName, String excludeId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getDepartmentName, departmentName)
                .eq(Department::getIsDelete, DeleteConstant.NO);

        if (excludeId != null) {
            wrapper.ne(Department::getId, excludeId);
        }

        return count(wrapper) > 0;
    }

    //删除部门
         /*
          验证部门是否可以删除
          */
    private void validateDepartmentCanDelete(String departmentId) {
        // 1. 检查部门是否存在
        Department department = getById(departmentId);
        if (department == null || department.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException(MessageConstant.DEPARTMENT_NOT_EXISTS);
        }

        // 2. 检查是否有子部门（简化版，不查询数量）
        if (hasChildrenDepartments(departmentId)) {
            throw new BaseException(MessageConstant.DEPARTMENT_HAS_CHILDREN);
        }
        // 3. 检查部门下是否有用户
        if (hasUsersInDepartment(departmentId)) {
            throw new BaseException(MessageConstant.DEPARTMENT_HAS_USERS);
        }
    }

    private boolean hasUsersInDepartment(String departmentId) {

        return memberMapper.selectCount(new LambdaQueryWrapper<Member>()
                .eq(Member::getDepartmentId, departmentId)
                .eq(Member::getIsDelete, DeleteConstant.NO)) > 0;
    }


    /**
     * 检查是否有子部门（简化版）
     */
    private boolean hasChildrenDepartments(String departmentId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getFatherDepartmentId, departmentId)
                .eq(Department::getIsDelete, DeleteConstant.NO)
                .last("LIMIT 1"); // 只需要知道是否存在
        return count(wrapper) > 0;
    }

}



