package com.itmang.service.activity;

/**
 * @Author luo
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.AddVoteRecordDTO;
import com.itmang.pojo.dto.DeleteVoteRecodeDTO;
import com.itmang.pojo.dto.FindVoteSignDTO;
import com.itmang.pojo.dto.UpdateVoteRecordDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.VoteRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VoteRecodeService extends IService<VoteRecord> {


    void addVoteRecodeInformation(List<AddVoteRecordDTO> addVoteRecordDTOList, String UserId);

    void deleteVoteRecodeInformation(DeleteVoteRecodeDTO deleteVoteRecodeDTO,String UserId);

    void updateVoteRecodeInformation(UpdateVoteRecordDTO updateVoteRecordDTO,String UserId);

    PageResult queryVoteRecodeInformationList(FindVoteSignDTO findVoteSignDTO);
}