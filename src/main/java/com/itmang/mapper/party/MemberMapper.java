package com.itmang.mapper.party;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.itmang.pojo.dto.MemberQueryDTO;
import com.itmang.pojo.entity.Member;
import com.itmang.pojo.vo.MemberBriefVO;
import com.itmang.pojo.vo.MemberVO;
import org.apache.ibatis.annotations.Mapper;



import java.util.List;




@Mapper
public interface MemberMapper extends BaseMapper<Member> {


    // 更新成员信息
    int updateMember(Member member);


    // 根据ID查询成员
    MemberVO selectById(String id);

    /**
     * 分页查询成员
     *

     * @param memberQueryDTO
     * @return
     */
    List<MemberBriefVO> selectMemberList(MemberQueryDTO memberQueryDTO);

    // 删除成员（逻辑删除）
    void removeBatchByIds(String[] array);

    //批量增加成员
    int batchInsertMembers(List<Member> members);


    String findExistingUserId(String userId);

    String findUserIdFromMember(String userId);

    int batchUpdateUsers(List<Member> memberList);
}


