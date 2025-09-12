package com.itmang.service.party;

import com.itmang.pojo.dto.MemberAddDTO;
import com.itmang.pojo.dto.MemberQueryDTO;
import com.itmang.pojo.dto.MemberUpdateDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.vo.MemberVO;
import java.util.List;

public interface MemberService {

    /**
     * 分页查询成员简略信息

     Page<MemberBriefVO> getMemberBriefPage(MemberQueryDTO memberQueryDTO);
*/
    PageResult queryMemberList(MemberQueryDTO memberQueryDTO);
    /**
     * 新增成员
     */
    void addMember(MemberAddDTO memberAddDTO);

    /**
     * 更新成员信息
     */
    void updateMember(MemberUpdateDTO memberUpdateDTO);

    /**
     * 删除成员（逻辑删除）
     */
    void deleteMember(String[] ids);

    /**
     * 查询成员详细信息
     */
    MemberVO getMemberDetailById(String id);



    void batchAddMembers(List<MemberAddDTO> memberDTOList);
}