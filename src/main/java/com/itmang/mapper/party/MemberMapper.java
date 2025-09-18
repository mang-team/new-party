package com.itmang.mapper.party;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.dto.MemberAddDTO;
import com.itmang.pojo.dto.MemberQueryDTO;
import com.itmang.pojo.entity.Member;
import com.itmang.pojo.vo.MemberBriefVO;
import com.itmang.pojo.vo.MemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Set;

@Mapper
public interface MemberMapper extends BaseMapper<Member> {

    // 新增成员
    // int insertMember(Member member);


    // 更新成员信息
    int updateMember(Member member);


    // 根据ID查询成员
    MemberVO selectById(String id);

    /**
     * 分页查询成员
     * @param memberQueryDTO
     * @return
     */
    List<MemberBriefVO> selectMemberList(MemberQueryDTO memberQueryDTO);

    // 删除成员（逻辑删除）
    void removeBatchByIds(String[] array);

    //批量增加成员
    int batchInsertMembers(List<Member> members);

    //查询成员电话号码是否存在
    Set<String> findExistingPhones(List<String> phoneList);

    //查询成员身份证是否存在
    Set<String> findExistingIdCards(List<String> idCardList);

    String findExistingUserId(String userId);

}