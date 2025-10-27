package com.itmang.service.activity.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmang.exception.BaseException;
import com.itmang.mapper.activity.VoteRecordMapper;
import com.itmang.pojo.dto.AddVoteRecordDTO;
import com.itmang.pojo.dto.DeleteVoteRecodeDTO;
import com.itmang.pojo.dto.FindVoteSignDTO;
import com.itmang.pojo.dto.UpdateVoteRecordDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.VoteRecord;
import com.itmang.service.activity.VoteRecodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VoteRecodeServiceImpl extends ServiceImpl<VoteRecordMapper, VoteRecord>
        implements VoteRecodeService {

    @Override
    public void addVoteRecodeInformation(List<AddVoteRecordDTO> addVoteRecordDTOList, String userId) {
        // TODO: 新增逻辑
        if (addVoteRecordDTOList == null || addVoteRecordDTOList.isEmpty()) {
            log.warn("新增投票记录请求为空，userId={}", userId);
            return;
        }

        List<VoteRecord> list = addVoteRecordDTOList.stream().map(dto -> {
            VoteRecord record = new VoteRecord();
            // 生成主键（String 类型）
            record.setId(UUID.randomUUID().toString());

            // 关联的投票信息 id（活动 id）
            record.setVoteInformationId(dto.getVoteInformationId());

            // 将传入的 userId 作为投票用户，如果 DTO 自带 userId，请改为 dto.getUserId()
            record.setUserId(userId);

            if (dto.getChoose() != null) {
                record.setChoose(dto.getChoose());
            }

            // 备注（允许空字符串表示清空）
            record.setNotes(dto.getNotes());

            // 审计字段
            record.setCreateBy(userId);
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateBy(userId);
            record.setUpdateTime(LocalDateTime.now());

            // 逻辑删除标志：2 表示未删除
            record.setIsDelete(2);

            return record;
        }).collect(Collectors.toList());

        boolean saved = this.saveBatch(list);
        if (!saved) {
            log.error("批量保存投票记录失败，userId={}, count={}", userId, list.size());
            throw new BaseException("新增投票记录失败");
        }

        log.info("批量新增投票记录成功，userId={}, count={}", userId, list.size());
    }

    @Override
    public void deleteVoteRecodeInformation(DeleteVoteRecodeDTO deleteVoteRecodeDTO, String userId) {
        // TODO: 删除逻辑
        // 根据 id 查询记录
        VoteRecord record = this.getById(deleteVoteRecodeDTO.getId());
        if (record == null) {
            throw new BaseException("要删除的投票记录不存在");
        }

        // 更新逻辑删除标志
        record.setIsDelete(1); // 1 表示删除
        record.setUpdateBy(userId);
        record.setUpdateTime(LocalDateTime.now());

        this.updateById(record);
    }

    @Override
    public void updateVoteRecodeInformation(UpdateVoteRecordDTO updateVoteRecordDTO, String userId) {
        // TODO: 更新逻辑
        // 1. 根据传入的 ID 查询是否存在
        VoteRecord Vote = this.getById(updateVoteRecordDTO.getId());
        if (Vote == null) {
            throw new BaseException("投票信息不存在，无法修改");
        }

        if(Vote.getIsDelete()==2){
            // 2. 将 DTO 的字段更新到实体类中
            if (updateVoteRecordDTO.getChoose()!=null) {
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


    }

    @Override
    public PageResult queryVoteRecodeInformationList(FindVoteSignDTO dto) {
        // TODO: 查询逻辑
        LambdaQueryWrapper<VoteRecord> qw = new LambdaQueryWrapper<>();

        // 精确匹配
        if (StringUtils.isNotBlank(dto.getId())) {
            qw.eq(VoteRecord::getId, dto.getId());
        }
        if (StringUtils.isNotBlank(dto.getVoteInformationId())) {
            qw.eq(VoteRecord::getVoteInformationId, dto.getVoteInformationId());
        }
        if (StringUtils.isNotBlank(dto.getUserId())) {
            qw.eq(VoteRecord::getUserId, dto.getUserId());
        }
        if (dto.getChoose() != null) {
            qw.eq(VoteRecord::getChoose, dto.getChoose());
        }
        if (StringUtils.isNotBlank(dto.getCreateBy())) {
            qw.eq(VoteRecord::getCreateBy, dto.getCreateBy());
        }
        if (StringUtils.isNotBlank(dto.getUpdateBy())) {
            qw.eq(VoteRecord::getUpdateBy, dto.getUpdateBy());
        }
        if (dto.getIsDelete() != null) {
            qw.eq(VoteRecord::getIsDelete, dto.getIsDelete());
        }

        // 范围查询时间
        if (dto.getCreateTime() != null) {
            qw.eq(VoteRecord::getCreateTime, dto.getCreateTime());
        }
        if (dto.getUpdateTime() != null) {
            qw.eq(VoteRecord::getUpdateTime, dto.getUpdateTime());
        }

        // 可选分页
        List<VoteRecord> records;
        long total;
        if (dto.getPageNum() != null && dto.getPageSize() != null) {
            Page<VoteRecord> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            Page<VoteRecord> result = this.page(page, qw);
            records = result.getRecords();
            total = result.getTotal();
        } else {
            records = this.list(qw);
            total = records.size();
        }

        // 返回自定义分页结果
        return PageResult.builder()
                .total(total)
                .records(records)
                .build();
    }
}
