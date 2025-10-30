package com.itmang.mapper.party;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.itmang.pojo.dto.DepartmentPageQueryDTO;
import com.itmang.pojo.entity.Department;
import com.itmang.pojo.vo.DepartmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {


//     * 校验部门名称唯一性（排除自身ID，用于更新场景）
//     * @param departmentName 部门名称
//     * @param fatherId 上级部门ID（同层级内名称唯一）
//     * @param excludeId 排除的部门ID（更新时使用）
//     * @return 符合条件的部门数量（0=不存在，>0=已存在）
//     */
//    int countByNameAndFatherId(
//            @Param("departmentName") String departmentName,
//            @Param("fatherId") String fatherId,
//            @Param("excludeId") String excludeId
//    );
    void updateBatchById(@Param("list")List<Department> updateList);
    List<DepartmentVO> pageSearch(DepartmentPageQueryDTO departmentPageDTO);

    List<Department> selectByParentId(String fatherDepartmentId);


    String selectParentName(String fatherDepartmentId);

}
