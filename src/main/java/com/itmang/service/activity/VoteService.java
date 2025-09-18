package com.itmang.service.activity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.AddVoteDTO;
import com.itmang.pojo.dto.DeleteVoteDTO;
import com.itmang.pojo.dto.FindVoteDTO;
import com.itmang.pojo.dto.UpdateVoteDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.VoteInformation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VoteService extends IService<VoteInformation> {


    void addVoteInformation(List<AddVoteDTO> addVoteDTOList, String UserId);

    void deleteVoteInformation(DeleteVoteDTO deleteVoteDTO, String UserId);

    void updateVoteInformation(UpdateVoteDTO updateVoteDTO, String UserId);

    PageResult queryVoteInformationList(FindVoteDTO findVoteDTO);
    
}