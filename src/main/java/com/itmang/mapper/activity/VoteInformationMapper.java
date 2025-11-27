package com.itmang.mapper.activity;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.dto.FindVoteDTO;
import com.itmang.pojo.entity.VoteInformation;
import com.itmang.pojo.vo.BankPageVO;
import com.itmang.pojo.vo.VoteInformationPageVO;
import com.itmang.pojo.vo.VoteInformationVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VoteInformationMapper extends BaseMapper<VoteInformation> {

    /**
     * 查询投票信息
     * @param id
     * @return
     */
    VoteInformationVO selectVoteById(String id);

    /**
     * 分页查询投票信息
     * @param dto
     * @return
     */
    List<VoteInformationPageVO> queryVoteInformationList(FindVoteDTO dto);
}