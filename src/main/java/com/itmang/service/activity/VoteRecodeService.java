package com.itmang.service.activity;

/**
 * @Author luo
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.AddVoteRecordDTO;
import com.itmang.pojo.dto.FindVoteSignDTO;
import com.itmang.pojo.dto.UpdateVoteRecordDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.VoteRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VoteRecodeService extends IService<VoteRecord> {


    /**
     * 新增投票记录信息
     * @param addVoteRecordDTO
     * @param userId
     */
    void addVoteRecodeInformation(AddVoteRecordDTO addVoteRecordDTO, String userId);

    /**
     * 删除投票记录信息
     * @param ids
     * @param UserId
     */
    void deleteVoteRecodeInformation(String ids,String UserId);

    /**
     * 编辑投票记录信息
     * @param updateVoteRecordDTO
     * @param UserId
     */
    void updateVoteRecodeInformation(UpdateVoteRecordDTO updateVoteRecordDTO,String UserId);

    /**
     * 分页查询投票记录信息
     * @param findVoteSignDTO
     * @return
     */
    PageResult queryVoteRecodeList(FindVoteSignDTO findVoteSignDTO);

}