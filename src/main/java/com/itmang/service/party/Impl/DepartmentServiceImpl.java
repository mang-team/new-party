package com.itmang.service.party.Impl;

import com.itmang.pojo.dto.AddDepartmentDTO;
import com.itmang.pojo.dto.DepartmentDTO;
import com.itmang.pojo.entity.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.itmang.utils.IdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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
    public void addDept(AddDepartmentDTO addDepartmentDTO) {
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
     * 将AddDTO转换为Entity
     */
    private Department convertAddDTOToEntity(AddDepartmentDTO addDepartmentDTO) {
        Department department = new Department();

        // 生成唯一ID
        // 生成部门ID：如果前端传入了ID则使用前端的，否则自动生成

            //先生成id
            IdGenerate idGenerate = new IdGenerate();
            String deptId = idGenerate.nextUUID(Department.class);
            String currentUserId = BaseContext.getCurrentId();
    if (currentUserId == null) {
        throw new BaseException("用户未登录，无法操作");
    }
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

    /*//删除部门
    @Override
    @Transactional
    public void deleteDepartments(String[] departmentIds) {
        List<String> canDeleteIds = new ArrayList<>();
       // List<String> canNotDeleteIds = new ArrayList<>();
        List<DepartmentDeleteError> errorList = new ArrayList<>();
        // 判断部门是否存在且是否可以删除
        for (String id : departmentIds) {
            Department department = departmentMapper.selectById(id);

            if (department == null || department.getIsDelete().equals(DeleteConstant.YES)) {
              //  canNotDeleteIds.add(id);
                errorList.add(new DepartmentDeleteError(id, "部门不存在或者已经删除"));
                continue;
            }

            if (hasChildrenDepartments(id)) {
              //  canNotDeleteIds.add(id);
                errorList.add(new DepartmentDeleteError(id, "部门有子部门"));
                continue;
            }

            if (hasUsersInDepartment(id)) {
               // canNotDeleteIds.add(id);
                errorList.add(new DepartmentDeleteError(id, "部门有成员"));
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
        if (!errorList.isEmpty()) {
            String errorMessage = canDeleteIds.isEmpty() ?
                    MessageConstant.DEPARTMENT_FAIL_DELETED :
                    MessageConstant.DEPARTMENT_PART_DELETED;

            throw new DepartmentDeleteException(errorMessage, errorList, canDeleteIds);
        }
    }*/

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
    public List<DepartmentVO> getChildrenByParentId(String fatherDepartmentId) {
// 1. 构建查询条件
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Department::getFatherDepartmentId, fatherDepartmentId);
        queryWrapper.eq(Department::getIsDelete, DeleteConstant.NO);
        queryWrapper.orderByAsc(Department::getCreateTime);

        // 2. 查询部门列表
        List<Department> departmentList = this.list(queryWrapper);

        // 3. 转换为VO列表
        return departmentList.stream()
                .map(this::convertToVO) // 使用你已有的convertToVO方法
                .collect(Collectors.toList());
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
//
//    private String getStatusDisplay(Integer isDelete) {
//        if (DeleteConstant.YES.equals(isDelete)) {
//            return "已删除";
//        } else {
//            return "正常";
//        }
//    }


    //辅助方法


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
                .eq(Department::getIsDelete, DeleteConstant.NO);// 只需要知道是否存在
        return departmentMapper.selectCount(wrapper) > 0;
    }

}



