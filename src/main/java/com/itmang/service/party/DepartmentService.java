package com.itmang.service.party;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.DepartmentDTO;
import com.itmang.pojo.dto.DepartmentPageQueryDTO;
import com.itmang.pojo.entity.Department;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.vo.DepartmentVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DepartmentService extends IService<Department> {
    /**
     * 分页查询部门
     * @param departmentPageDTO 查询参数
     * @return 分页结果
     */
    PageResult pageDepartment(DepartmentPageQueryDTO departmentPageDTO);
    /**
     * 新增部门
     */
    void addDept(DepartmentDTO addDepartment);

    /**
     * 更新部门
     */
    void updateDept(DepartmentDTO updateDTO);

    /**
     * 批量删除部门
     */
    void deleteDepartments(String[] departmentIds);

//    /**
//     * 查询部门树形结构（用于前端展示层级）
//     */
//    List<DepartmentVO> listTree();

//    /**
//     * 校验部门名称是否唯一（同一上级下）
//     * @param departmentName 部门名称
//     * @param fatherId 上级部门ID
//     * @param excludeId 排除的部门ID（更新时使用）
//     * @return true=唯一，false=已存在
//     */
//    boolean isNameUnique(String departmentName, String fatherId, String excludeId);

//    /**
//     * 批量查询部门信息（用于关联查询）
//     */
//    Map<String, Department> getDeptMapByIds(List<String> ids);



    /**
     * 根据id查询部门信息
     * @param id
     * @return
     */

    DepartmentVO getDepartmentDetail(String id);


    List<DepartmentVO> departmentList();
}
