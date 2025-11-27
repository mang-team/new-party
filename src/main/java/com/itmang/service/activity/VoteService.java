package com.itmang.service.activity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.AddVoteDTO;
import com.itmang.pojo.dto.DeleteVoteDTO;
import com.itmang.pojo.dto.FindVoteDTO;
import com.itmang.pojo.dto.UpdateVoteDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.VoteInformation;
import com.itmang.pojo.vo.VoteInformationVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VoteService extends IService<VoteInformation> {


    /**
     * 添加投票信息
     * @param addVoteDTOList
     * @param UserId
     */
    void addVoteInformation(List<AddVoteDTO> addVoteDTOList, String UserId);

    /**
     * 删除投票信息
     * @param deleteVoteDTO
     * @param UserId
     */
    void deleteVoteInformation(DeleteVoteDTO deleteVoteDTO, String UserId);

    /**
     * 编辑投票信息
     * @param updateVoteDTO
     * @param UserId
     */
    void updateVoteInformation(UpdateVoteDTO updateVoteDTO, String UserId);

    /**
     * 分页查询投票信息
     * @param findVoteDTO
     * @return
     */
    PageResult queryVoteInformationList(FindVoteDTO findVoteDTO);

    /**
     * 查询投票信息
     * @param id
     * @return
     */
    VoteInformationVO queryVoteInformation(String id);
}