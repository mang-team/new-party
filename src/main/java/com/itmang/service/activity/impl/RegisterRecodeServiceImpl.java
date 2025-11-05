package com.itmang.service.activity.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.itmang.exception.BaseException;

import com.itmang.mapper.activity.SignInRecordMapper;
import com.itmang.pojo.dto.AddRegisterRecordDTO;
import com.itmang.pojo.dto.DeleteRegisterRecodeDTO;
import com.itmang.pojo.dto.FindRegisterSignDTO;
import com.itmang.pojo.dto.UpdateRegisterRecordDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.SignInRecord;
import com.itmang.service.activity.RegisterRecodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegisterRecodeServiceImpl extends ServiceImpl<SignInRecordMapper, SignInRecord>
        implements RegisterRecodeService {

    @Override
    public void addRegisterRecodeInformation(List<AddRegisterRecordDTO> addRegisterRecordDTOList, String userId) {
        // TODO: 新增逻辑
        if (addRegisterRecordDTOList == null || addRegisterRecordDTOList.isEmpty()) {
            log.warn("新增签到记录请求为空，userId={}", userId);
            return;
        }

        List<SignInRecord> list = addRegisterRecordDTOList.stream().map(dto -> {
            SignInRecord record = new SignInRecord();
            // 生成主键（String 类型）
            record.setId(UUID.randomUUID().toString());

            // 关联的签到信息 id（活动 id）
            record.setSignInInformationId(dto.getSignInInformationId());

            // 将传入的 userId 作为签到用户，如果 DTO 自带 userId，请改为 dto.getUserId()
            record.setUserId(userId);

            // 状态（允许 null -> 由 DB 或业务默认处理）
            if (dto.getState() != null) {
                record.setState(dto.getState());
            } else {
                // 如果没有传状态，按业务设默认（这里设为 2 表示未签到，按你表设计可调整）
                record.setState(2);
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
            log.error("批量保存签到记录失败，userId={}, count={}", userId, list.size());


            throw new BaseException("新增签到记录失败");


        }

        log.info("批量新增签到记录成功，userId={}, count={}", userId, list.size());
    }

    @Override
    public void deleteRegisterRecodeInformation(DeleteRegisterRecodeDTO deleteRegisterRecodeDTO, String userId) {
        // TODO: 删除逻辑
        // 根据 id 查询记录
        SignInRecord record = this.getById(deleteRegisterRecodeDTO.getId());
        if (record == null) {


            throw new BaseException("要删除的签到记录不存在");


        }

        // 更新逻辑删除标志
        record.setIsDelete(1); // 1 表示删除
        record.setUpdateBy(userId);
        record.setUpdateTime(LocalDateTime.now());

        this.updateById(record);
    }

    @Override
    public void updateRegisterRecodeInformation(UpdateRegisterRecordDTO updateRegisterRecordDTO, String userId) {
        // TODO: 更新逻辑
        // 1. 根据传入的 ID 查询是否存在
        SignInRecord signIn = this.getById(updateRegisterRecordDTO.getId());
        if (signIn == null) {

            throw new BaseException("签到信息不存在，无法修改");
        }

        if(signIn.getIsDelete()==2){
            // 2. 将 DTO 的字段更新到实体类中
            if (updateRegisterRecordDTO.getState()!=null) {
                signIn.setState(updateRegisterRecordDTO.getState());
            }

            if (StringUtils.isNotBlank(updateRegisterRecordDTO.getNotes())) {
                signIn.setNotes(updateRegisterRecordDTO.getNotes());
            }



            // 3. 设置更新信息
            signIn.setUpdateBy(userId); // 或者从登录用户获取
            signIn.setUpdateTime(LocalDateTime.now());

            // 4. 执行更新
            boolean isUpdated = this.updateById(signIn);
            if (!isUpdated) {
                throw new BaseException("修改签到信息失败");
            }
        }


    }

    @Override
    public PageResult queryRegisterRecodeInformationList(FindRegisterSignDTO dto) {
        // TODO: 查询逻辑
        LambdaQueryWrapper<SignInRecord> qw = new LambdaQueryWrapper<>();

        // 精确匹配
        if (StringUtils.isNotBlank(dto.getId())) {
            qw.eq(SignInRecord::getId, dto.getId());
        }
        if (StringUtils.isNotBlank(dto.getSignInInformationId())) {
            qw.eq(SignInRecord::getSignInInformationId, dto.getSignInInformationId());
        }
        if (StringUtils.isNotBlank(dto.getUserId())) {
            qw.eq(SignInRecord::getUserId, dto.getUserId());
        }
        if (dto.getState() != null) {
            qw.eq(SignInRecord::getState, dto.getState());
        }
        if (StringUtils.isNotBlank(dto.getCreateBy())) {
            qw.eq(SignInRecord::getCreateBy, dto.getCreateBy());
        }
        if (StringUtils.isNotBlank(dto.getUpdateBy())) {
            qw.eq(SignInRecord::getUpdateBy, dto.getUpdateBy());
        }
        if (dto.getIsDelete() != null) {
            qw.eq(SignInRecord::getIsDelete, dto.getIsDelete());
        }

        // 范围查询时间
        if (dto.getCreateTime() != null) {
            qw.eq(SignInRecord::getCreateTime, dto.getCreateTime());
        }
        if (dto.getUpdateTime() != null) {
            qw.eq(SignInRecord::getUpdateTime, dto.getUpdateTime());
        }

        // 可选分页
        List<SignInRecord> records;
        long total;
        if (dto.getPageNum() != null && dto.getPageSize() != null) {
            Page<SignInRecord> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            Page<SignInRecord> result = this.page(page, qw);
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
