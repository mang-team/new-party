package com.itmang.service.activity.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.*;
import com.itmang.exception.BaseException;

import com.itmang.mapper.activity.VoteInformationMapper;
import com.itmang.mapper.user.UserMapper;
import com.itmang.pojo.dto.AddVoteDTO;
import com.itmang.pojo.dto.DeleteVoteDTO;
import com.itmang.pojo.dto.FindVoteDTO;
import com.itmang.pojo.dto.UpdateVoteDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.User;
import com.itmang.pojo.entity.VoteInformation;
import com.itmang.pojo.vo.BankPageVO;
import com.itmang.pojo.vo.VoteInformationPageVO;
import com.itmang.pojo.vo.VoteInformationVO;
import com.itmang.service.activity.VoteService;
import com.itmang.utils.UserUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class VoteServiceImpl extends ServiceImpl<VoteInformationMapper, VoteInformation>
        implements VoteService {

    @Resource
    private VoteInformationMapper voteInformationMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public void addVoteInformation(List<AddVoteDTO> addVoteDTOList,String userId) {
        String createBy=userId;
        if (addVoteDTOList == null || addVoteDTOList.isEmpty()) {
            log.warn("新增投票信息列表为空");
            return;
        }

        List<VoteInformation> VoteInformationList = addVoteDTOList.stream().map(dto -> {
            //判断参与投票人员是否存在
            // 1. 校验投票参与人ID
            String voterIdsStr = dto.getUserIds();
            if (voterIdsStr == null || voterIdsStr.trim().isEmpty()) {
                throw new BaseException(MessageConstant.VOTER_NOT_EXIST);
            }
            // 2. 调用通用用户ID处理函数，筛选有效参与人（关键：传入投票参与人ID字符串）
            List<String> validVoterIds = UserUtil.processValidUserIds(dto :: getUserIds , userMapper);
            int validCount = validVoterIds.size();
            int originalCount = voterIdsStr.split(",").length;
            // 3. 校验：如果有效参与人为0，或部分参与人无效，抛出异常（根据业务需求调整）
            if (validCount <= 0) {
//                throw new BaseException(MessageConstant.VOTER_NOT_EXIST);
                log.error("参与人员不存在");
            }
            if (validCount != originalCount) {
//                throw new BaseException(MessageConstant.PART_VOTER_INVALID);
                log.error("部分参与人员不存在");
            }

            VoteInformation info = new VoteInformation();
            info.setId(UUID.randomUUID().toString()); // 主键
            info.setUserIds(voterIdsStr);
            info.setOptions(dto.getOptions());
            info.setVoteTitle(dto.getVoteInTitle());
            info.setVoteContent(dto.getVoteInContent());
            // 转换时间字符串为 LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (dto.getStartTime() != null && !dto.getStartTime().isEmpty()) {
                info.setStartTime(LocalDateTime.parse(dto.getStartTime(), formatter));
            }
            if (dto.getEndTime() != null && !dto.getEndTime().isEmpty()) {
                info.setEndTime(LocalDateTime.parse(dto.getEndTime(), formatter));
            }
            info.setCreateBy(createBy);
            info.setCreateTime(LocalDateTime.now());
            info.setUpdateBy(createBy);
            info.setUpdateTime(LocalDateTime.now());
            info.setIsDelete(DeleteConstant.NO); // 默认未删除
            return info;
        }).toList();
        // 批量插入
        boolean saved = this.saveBatch(VoteInformationList);
        if (!saved) {
            throw new BaseException("新增投票信息失败");
        }
        log.info("新增投票信息成功，数量：{}", VoteInformationList.size());
    }

    @Override
    public void deleteVoteInformation(DeleteVoteDTO deleteVoteDTO,String UserId) {
        if (deleteVoteDTO.getId() == null) {
            throw new IllegalArgumentException("删除的 id 不能为空");
        }
        // 查询投票信息
        VoteInformation info = getById(deleteVoteDTO.getId());
        if (info == null || info.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException("未找到对应的投票信息");
        }
        //判断该投票是否在开始之前在结
        if (info.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BaseException("投票已经开始，无法删除");
        }
        info.setIsDelete(DeleteConstant.YES);
        info.setUpdateTime(LocalDateTime.now());
        // 可选：设置修改人
        info.setUpdateBy(UserId);
        updateById(info);
    }

    @Override
    public void updateVoteInformation(UpdateVoteDTO updateVoteDTO,String UserId) {
        // 1. 根据传入的 ID 查询是否存在
        VoteInformation Vote = this.getById(updateVoteDTO.getId());
        if (Vote == null || Vote.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException("投票信息不存在，无法修改");
        }
        // 2. 将 DTO 的字段更新到实体类中
        if (StringUtils.isNotBlank(updateVoteDTO.getVoteInTitle())) {
            Vote.setVoteTitle(updateVoteDTO.getVoteInTitle());
        }
        if (StringUtils.isNotBlank(updateVoteDTO.getVoteInContent())) {
            Vote.setVoteContent(updateVoteDTO.getVoteInContent());
        }
        if (updateVoteDTO.getOptions()!=null) {
            Vote.setOptions(updateVoteDTO.getOptions());
        }
        if (updateVoteDTO.getStartTime()!=null) {
            Vote.setStartTime(updateVoteDTO.getStartTime());
        }
        if (updateVoteDTO.getEndTime()!=null) {
            Vote.setEndTime(updateVoteDTO.getEndTime());
        }
        List<String> addVoterIds = new ArrayList<>();
        //查看是否有修改投票人员
        if (StringUtils.isNotBlank(updateVoteDTO.getUserIds())) {
            String[] newVoterIds = updateVoteDTO.getUserIds().split(",");
            String[] oldVoterIds = Vote.getUserIds().split(",");
            for(String newVoterId : newVoterIds) {
                //判断现在新修改的参与投票的人员是否在oldVoterIds中
                if (Arrays.asList(oldVoterIds).contains(newVoterId)) {
                    addVoterIds.add(newVoterId);
                    continue;
                } else {
                    //是新增的参与投票人员
                    //判断现在新修改的参与投票的人员是否在数据库中
                    User user = userMapper.selectById(newVoterId);
                    if (user == null || user.getIsDelete().equals(DeleteConstant.YES)
                            || user.getStatus().equals(StatusConstant.DISABLE)) {
                        continue;
                    }
                    addVoterIds.add(newVoterId);
                }
            }
            Vote.setUserIds(String.join(",", addVoterIds));
        }
        // 3. 设置更新信息
        Vote.setUpdateBy(UserId); // 或者从登录用户获取
        Vote.setUpdateTime(LocalDateTime.now());
        // 4. 执行更新
        boolean isUpdated = this.updateById(Vote);
        if (!isUpdated) {
            throw new BaseException("修改投票信息失败");
        }
    }

    //Option已转化为String类型
    @Override
    public PageResult queryVoteInformationList(FindVoteDTO dto) {
        //配置默认页码1和分页大小5
        if(dto.getPageNum() == null || dto.getPageNum() < 1){
            dto.setPageNum(PageConstant.PAGE_NUM);
        }
        //查看是否有分页大小
        if(dto.getPageSize() == null || dto.getPageSize() < 1){
            dto.setPageSize(PageConstant.PAGE_SIZE);
        }
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<VoteInformationPageVO> votePageVOList = voteInformationMapper.queryVoteInformationList(dto);
        PageInfo<VoteInformationPageVO> pageInfo = new PageInfo<>(votePageVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 查询投票信息
     * @param id
     * @return
     */
    public VoteInformationVO queryVoteInformation(String id) {
        //判断id是否存在
        if (StringUtils.isBlank(id)) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
         VoteInformation voteInformation = voteInformationMapper.selectById(id);
        if (voteInformation == null) {
            throw new BaseException(MessageConstant.VOTE_NOT_EXIST);
        }
        VoteInformationVO voteInformationVO = BeanUtil.copyProperties(
                voteInformation, VoteInformationVO.class);
        //获取用户名称
        String[] userIds = voteInformation.getUserIds().split(",");
        List<String> userNameList = userMapper.queryUserNames(userIds);
        voteInformationVO.setUserNames(String.join(",", userNameList));
        //获取发布人的名字
        voteInformationVO.setCreateName(userMapper.selectById(voteInformation.getCreateBy()).getUserName());
        return voteInformationVO;
    }
}

