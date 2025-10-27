package com.itmang.service.activity.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmang.exception.BaseException;
import com.itmang.mapper.activity.VoteInformationMapper;
import com.itmang.pojo.dto.AddVoteDTO;
import com.itmang.pojo.dto.DeleteVoteDTO;
import com.itmang.pojo.dto.FindVoteDTO;
import com.itmang.pojo.dto.UpdateVoteDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.VoteInformation;
import com.itmang.service.activity.VoteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class VoteServiceImpl extends ServiceImpl<VoteInformationMapper, VoteInformation>
        implements VoteService {

    @Resource
    private VoteInformationMapper VoteInformationMapper;

    @Override
    public void addVoteInformation(List<AddVoteDTO> addVoteDTOList,String UserId) {
        String createBy=UserId;
        if (addVoteDTOList == null || addVoteDTOList.isEmpty()) {
            log.warn("新增投票信息列表为空");
            return;
        }

        List<VoteInformation> VoteInformationList = addVoteDTOList.stream().map(dto -> {
            VoteInformation info = new VoteInformation();
            info.setId(UUID.randomUUID().toString()); // 主键
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
            info.setIsDelete(2); // 默认未删除

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
        if (info == null) {
            throw new BaseException("未找到对应的投票信息");
        }

        // 逻辑删除：设置 isDelete = 1
        info.setIsDelete(1);
        info.setUpdateTime(LocalDateTime.now());
        // 可选：设置修改人
        info.setUpdateBy(UserId); // TODO: 替换成实际登录用户

        updateById(info);
    }

    @Override
    public void updateVoteInformation(UpdateVoteDTO updateVoteDTO,String UserId) {
        // 1. 根据传入的 ID 查询是否存在
        VoteInformation Vote = this.getById(updateVoteDTO.getId());
        if (Vote == null) {
            throw new BaseException("投票信息不存在，无法修改");
        }

        if(Vote.getIsDelete()==2){

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

            // 3. 设置更新信息
            Vote.setUpdateBy(UserId); // 或者从登录用户获取
            Vote.setUpdateTime(LocalDateTime.now());

            // 4. 执行更新
            boolean isUpdated = this.updateById(Vote);
            if (!isUpdated) {
                throw new BaseException("修改投票信息失败");
            }
        }

    }

    //Option已转化为String类型
    @Override
    public PageResult queryVoteInformationList(FindVoteDTO dto) {
        // 使用 MyBatis-Plus LambdaQueryWrapper 构建条件
        LambdaQueryWrapper<VoteInformation> queryWrapper = new LambdaQueryWrapper<>();

        // 精确匹配ID
        if (StringUtils.isNotBlank(dto.getId())) {
            queryWrapper.eq(VoteInformation::getId, dto.getId());
        }

        if (StringUtils.isNotBlank(dto.getCreateBy())) {
            queryWrapper.eq(VoteInformation::getCreateBy, dto.getCreateBy());
        }

        if (StringUtils.isNotBlank(dto.getUpdateBy())) {
            queryWrapper.eq(VoteInformation::getUpdateBy, dto.getUpdateBy());
        }

        if (dto.getOptions() != null && !dto.getOptions().isEmpty()) {
            // 用 JSON 序列化匹配
            String jsonOptions = JSON.toJSONString(dto.getOptions());
            queryWrapper.eq(VoteInformation::getOptions, jsonOptions);
        }


        // 模糊匹配标题和内容
        if (StringUtils.isNotBlank(dto.getVoteInTitle())) {
            queryWrapper.like(VoteInformation::getVoteTitle, dto.getVoteInTitle());
        }
        if (StringUtils.isNotBlank(dto.getVoteInContent())) {
            queryWrapper.like(VoteInformation::getVoteContent, dto.getVoteInContent());
        }

        // 精确匹配逻辑删除
        if (dto.getIsDelete() != null) {
            queryWrapper.eq(VoteInformation::getIsDelete, dto.getIsDelete());
        } else {
            queryWrapper.eq(VoteInformation::getIsDelete, 2); // 默认未删除
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 范围查询开始时间
        if (StringUtils.isNotBlank(dto.getStartTime())) {
            LocalDateTime start = LocalDateTime.parse(dto.getStartTime(), formatter);
            queryWrapper.ge(VoteInformation::getStartTime, start);
        }

        // 范围查询结束时间
        if (StringUtils.isNotBlank(dto.getEndTime())) {
            LocalDateTime end = LocalDateTime.parse(dto.getEndTime(), formatter);
            queryWrapper.le(VoteInformation::getEndTime, end);
        }

        // 可选分页
        List<VoteInformation> records;
        long total;
        if (dto.getPageNum() != null && dto.getPageSize() != null) {
            Page<VoteInformation> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            Page<VoteInformation> result = this.page(page, queryWrapper);
            records = result.getRecords();
            total = result.getTotal();
        } else {
            records = this.list(queryWrapper);
            total = records.size();
        }

        // 返回自定义分页结果
        return PageResult.builder()
                .total(total)
                .records(records)
                .build();
    }
}

