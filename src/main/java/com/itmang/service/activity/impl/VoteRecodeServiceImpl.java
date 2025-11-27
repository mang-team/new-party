package com.itmang.service.activity.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.PageConstant;
import com.itmang.exception.BaseException;

import com.itmang.mapper.activity.VoteInformationMapper;
import com.itmang.mapper.activity.VoteRecordMapper;
import com.itmang.pojo.dto.AddVoteRecordDTO;
import com.itmang.pojo.dto.FindVoteSignDTO;
import com.itmang.pojo.dto.UpdateVoteRecordDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.VoteInformation;
import com.itmang.pojo.entity.VoteRecord;
import com.itmang.pojo.vo.VoteInformationPageVO;
import com.itmang.pojo.vo.VoteRecordPageVO;
import com.itmang.service.activity.VoteRecodeService;
import com.itmang.utils.IdGenerate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VoteRecodeServiceImpl extends ServiceImpl<VoteRecordMapper, VoteRecord>
        implements VoteRecodeService {

    @Resource
    private VoteRecordMapper voteRecordMapper;
    @Resource
    private VoteInformationMapper voteInformationMapper;
    @Resource
    private IdGenerate idGenerate;

    /**
     * 新增投票记录信息
     * @param addVoteRecordDTO
     * @param userId
     */
    public void addVoteRecodeInformation(AddVoteRecordDTO addVoteRecordDTO, String userId) {
        //判断传进来的数据是否存在
        if (addVoteRecordDTO == null || StringUtils.isBlank(addVoteRecordDTO.getVoteInformationId())
            || StringUtils.isBlank(addVoteRecordDTO.getUserIds())) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        //查询投票信息
        VoteInformation voteInformation =
                voteInformationMapper.selectById(addVoteRecordDTO.getVoteInformationId());
        if (voteInformation == null || voteInformation.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException(MessageConstant.VOTE_NOT_EXIST);
        }
        //判断当前时间是否是在投票开始时间之前的3分钟之内
        if (LocalDateTime.now().isBefore(voteInformation.getStartTime().minusMinutes(3))) {
            throw new BaseException(MessageConstant.VOTE_TIME_ERROR);
        }
        //满足要求，可以批量新增投票记录
        //1.先验证参与投票人员是否存在
        List<String> newVoterIds = new ArrayList<>();
        String[] userIds = addVoteRecordDTO.getUserIds().split(",");
        for (String id : userIds) {
            //判断当前用户是否存在
            if (id == null || id.equals("")) {
                continue;
            }
            //判断当前用户是否已经存在
            VoteRecord voteRecord = voteRecordMapper.selectOne(new LambdaQueryWrapper<VoteRecord>()
                    .eq(VoteRecord::getVoteInformationId, addVoteRecordDTO.getVoteInformationId())
                    .eq(VoteRecord::getUserId, id));
            if(voteRecord != null){
                continue;
            }
            newVoterIds.add(id);
        }
        //2.批量新增投票记录
        List<VoteRecord> voteRecordList = new ArrayList<>();
        if(newVoterIds.size() > 0){
            for (String id : newVoterIds) {
                VoteRecord voteRecord = new VoteRecord();
                voteRecord.setId(idGenerate.nextUUID(VoteRecord.class));
                voteRecord.setVoteInformationId(addVoteRecordDTO.getVoteInformationId());
                voteRecord.setUserId(id);
                voteRecord.setIsDelete(DeleteConstant.NO);
                voteRecord.setCreateBy(userId);
                voteRecord.setCreateTime(LocalDateTime.now());
            }
            this.saveBatch(voteRecordList);
        }
        //判断逻辑
        if (voteRecordList.size() == 0) {
            throw new BaseException(MessageConstant.ADD_FAIL);
        }

    }

    /**
     * 删除投票记录信息
     * @param ids
     * @param userId
     */
    public void deleteVoteRecodeInformation(String ids, String userId) {
        // 1. 入参校验
        if (StringUtils.isBlank(ids) || StringUtils.isBlank(userId)) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        List<String> idList = Arrays.stream(ids.split(","))
                .map(String::trim) // 去除空格，避免无效ID
                .filter(id -> !StringUtils.isBlank(id)) // 过滤空字符串ID
                .collect(Collectors.toList());
        if (idList.isEmpty()) {
            throw new BaseException(MessageConstant.PARAMETER_ERROR);
        }
        // 2. 批量查询投票记录
        List<VoteRecord> voteRecordList = this.listByIds(idList);
        if (voteRecordList.isEmpty()) {
            throw new BaseException(MessageConstant.VOTE_RECORD_NOT_EXIST);
        }
        // 校验：传入的ID是否都存在（避免部分ID无效导致删除不完整）
        Set<String> existIds = voteRecordList.stream()
                .map(VoteRecord::getId)
                .collect(Collectors.toSet());
        List<String> invalidIds = idList.stream()
                .filter(id -> !existIds.contains(id))
                .collect(Collectors.toList());
        if (!invalidIds.isEmpty()) {
            throw new BaseException(MessageConstant.VOTE_RECORD_NOT_EXIST + "：" + String.join(",", invalidIds));
        }

        // 3. 分组处理：按投票信息ID分组（减少VoteInformation查询次数）
        Map<String, List<VoteRecord>> voteInfoIdToRecords = voteRecordList.stream()
                .collect(Collectors.groupingBy(VoteRecord::getVoteInformationId));

        // 4. 事务内同步更新投票信息（保证数据一致性）
        try {
            // 4.1 批量更新投票信息的参与用户ID（核心优化：修复原逻辑漏洞）
            for (Map.Entry<String, List<VoteRecord>> entry : voteInfoIdToRecords.entrySet()) {
                String voteInfoId = entry.getKey();
                List<String> voterIdsToDelete = entry.getValue().stream()
                        .map(VoteRecord::getUserId)
                        .distinct() // 去重，避免重复删除同一用户ID
                        .collect(Collectors.toList());

                // 查询投票信息（唯一查询，避免原逻辑中in条件的错误）
                VoteInformation voteInformation = voteInformationMapper.selectById(voteInfoId);
                if (voteInformation == null) {
                    throw new BaseException("投票信息不存在：" + voteInfoId);
                }

                // 处理用户ID集合（修复原逻辑：replace可能误删包含目标ID的字符串）
                String userIds = voteInformation.getUserIds();
                if (StringUtils.isNotBlank(userIds)) {
                    // 分割为列表 → 过滤要删除的ID → 重新拼接
                    List<String> userList = new ArrayList<>(Arrays.asList(userIds.split(",")));
                    userList.removeAll(voterIdsToDelete); // 批量删除目标用户ID
                    String newUserIds = String.join(",", userList);
                    voteInformation.setUserIds(newUserIds);
                }
                // 设置更新信息
                voteInformation.setUpdateBy(userId);
                voteInformation.setUpdateTime(LocalDateTime.now());
                voteInformationMapper.updateById(voteInformation);
            }

            // 4.2 批量删除投票记录
            List<String> deleteIds = voteRecordList.stream()
                    .map(VoteRecord::getId)
                    .collect(Collectors.toList());
            voteRecordMapper.deleteVoteIds(deleteIds);
        } catch (Exception e) {
            // 异常回滚，保证数据一致性
            throw new BaseException(MessageConstant.DELETE_VOTE_RECORD_FAILED);
        }
    }

    /**
     * 编辑投票记录信息
     * @param updateVoteRecordDTO
     * @param userId
     */
    public void updateVoteRecodeInformation(UpdateVoteRecordDTO updateVoteRecordDTO, String userId) {
        // 1. 根据传入的 ID 查询是否存在
        VoteRecord Vote = this.getById(updateVoteRecordDTO.getId());
        if (Vote == null || Vote.getIsDelete().equals(DeleteConstant.YES)) {
            throw new BaseException("投票信息不存在，无法修改");
        }
        // 2. 将 DTO 的字段更新到实体类中
        if (updateVoteRecordDTO.getChoose() != null && updateVoteRecordDTO.getChoose() != "") {
            Vote.setChoose(updateVoteRecordDTO.getChoose());
        }
        if (StringUtils.isNotBlank(updateVoteRecordDTO.getNotes())) {
            Vote.setNotes(updateVoteRecordDTO.getNotes());
        }
        // 3. 设置更新信息
        Vote.setUpdateBy(userId); // 或者从登录用户获取
        Vote.setUpdateTime(LocalDateTime.now());
        // 4. 执行更新
        boolean isUpdated = this.updateById(Vote);
        if (!isUpdated) {
            throw new BaseException("修改投票信息失败");
        }
    }

    /**
     * 分页查询投票记录信息
     * @param findVoteSignDTO
     * @return
     */
    public PageResult queryVoteRecodeList(FindVoteSignDTO findVoteSignDTO) {
        //配置默认页码1和分页大小5
        if(findVoteSignDTO.getPageNum() == null || findVoteSignDTO.getPageNum() < 1){
            findVoteSignDTO.setPageNum(PageConstant.PAGE_NUM);
        }
        //查看是否有分页大小
        if(findVoteSignDTO.getPageSize() == null || findVoteSignDTO.getPageSize() < 1){
            findVoteSignDTO.setPageSize(PageConstant.PAGE_SIZE);
        }
        PageHelper.startPage(findVoteSignDTO.getPageNum(), findVoteSignDTO.getPageSize());
        List<VoteRecordPageVO> votePageVOList = voteRecordMapper.queryVoteRecordList(findVoteSignDTO);
        PageInfo<VoteRecordPageVO> pageInfo = new PageInfo<>(votePageVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }


}
