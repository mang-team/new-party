package com.itmang.mapper.activity;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmang.pojo.dto.FindVoteSignDTO;
import com.itmang.pojo.entity.VoteRecord;
import com.itmang.pojo.vo.VoteRecordPageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 投票信息 Mapper
 */
@Mapper
public interface VoteRecordMapper extends BaseMapper<VoteRecord> {

    /**
     * 分页查询投票记录信息
     * @param findVoteSignDTO
     * @return
     */
    List<VoteRecordPageVO> queryVoteRecordList(FindVoteSignDTO findVoteSignDTO);

    /**
     * 批量删除投票记录信息
     * @param deleteIds
     */
    void deleteVoteIds(List<String> deleteIds);
}