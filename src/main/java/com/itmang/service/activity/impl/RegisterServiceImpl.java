package com.itmang.service.activity.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmang.mapper.activity.SignInInformationMapper;
import com.itmang.pojo.dto.AddRegisterDTO;
import com.itmang.pojo.dto.DeleteRegisterDTO;
import com.itmang.pojo.dto.FindRegisterDTO;
import com.itmang.pojo.dto.UpdateRegisterDTO;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.SignInInformation;
import com.itmang.service.activity.RegisterService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class RegisterServiceImpl extends ServiceImpl<SignInInformationMapper, SignInInformation>
        implements RegisterService {

    @Resource
    private SignInInformationMapper signInInformationMapper;

    @Override
    public void addRegisterInformation(List<AddRegisterDTO> addRegisterDTOList,String UserId) {
        String createBy=UserId;
        if (addRegisterDTOList == null || addRegisterDTOList.isEmpty()) {
            log.warn("新增签到信息列表为空");
            return;
        }

        List<SignInInformation> signInInformationList = addRegisterDTOList.stream().map(dto -> {
            SignInInformation info = new SignInInformation();
            info.setId(UUID.randomUUID().toString()); // 主键
            info.setSignInTitle(dto.getSignInTitle());
            info.setSignInContent(dto.getSignInContent());

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
        boolean saved = this.saveBatch(signInInformationList);

        if (!saved) {
            throw new RuntimeException("新增签到信息失败");
        }

        log.info("新增签到信息成功，数量：{}", signInInformationList.size());
    }

    @Override
    public void deleteRegisterInformation(DeleteRegisterDTO deleteRegisterDTO,String UserId) {
        if (deleteRegisterDTO.getId() == null) {
            throw new IllegalArgumentException("删除的 id 不能为空");
        }

        // 查询签到信息
        SignInInformation info = getById(deleteRegisterDTO.getId());
        if (info == null) {
            throw new RuntimeException("未找到对应的签到信息");
        }

        // 逻辑删除：设置 isDelete = 1
        info.setIsDelete(1);
        info.setUpdateTime(LocalDateTime.now());
        // 可选：设置修改人
        info.setUpdateBy(UserId); // TODO: 替换成实际登录用户

        updateById(info);
    }

    @Override
    public void updateRegisterInformation(UpdateRegisterDTO updateRegisterDTO,String UserId) {
        // 1. 根据传入的 ID 查询是否存在
        SignInInformation signIn = this.getById(updateRegisterDTO.getId());
        if (signIn == null) {
            throw new RuntimeException("签到信息不存在，无法修改");
        }

        // 2. 将 DTO 的字段更新到实体类中
        if (StringUtils.isNotBlank(updateRegisterDTO.getSignInTitle())) {
        signIn.setSignInTitle(updateRegisterDTO.getSignInTitle());
        }

        if (StringUtils.isNotBlank(updateRegisterDTO.getSignInContent())) {
            signIn.setSignInContent(updateRegisterDTO.getSignInContent());
        }

        if (updateRegisterDTO.getStartTime()!=null) {
            signIn.setStartTime(updateRegisterDTO.getStartTime());
        }

        if (updateRegisterDTO.getEndTime()!=null) {
            signIn.setEndTime(updateRegisterDTO.getEndTime());
        }

        // 3. 设置更新信息
        signIn.setUpdateBy(UserId); // 或者从登录用户获取
        signIn.setUpdateTime(LocalDateTime.now());

        // 4. 执行更新
        boolean isUpdated = this.updateById(signIn);
        if (!isUpdated) {
            throw new RuntimeException("修改签到信息失败");
        }
    }

    //逻辑删除没判断
    @Override
    public PageResult queryRegisterInformationList(FindRegisterDTO dto) {
        // 使用 MyBatis-Plus LambdaQueryWrapper 构建条件
        LambdaQueryWrapper<SignInInformation> queryWrapper = new LambdaQueryWrapper<>();

        // 精确匹配ID
        if (StringUtils.isNotBlank(dto.getId())) {
            queryWrapper.eq(SignInInformation::getId, dto.getId());
        }

        if (StringUtils.isNotBlank(dto.getCreateBy())) {
            queryWrapper.eq(SignInInformation::getCreateBy, dto.getCreateBy());
        }

        if (StringUtils.isNotBlank(dto.getUpdateBy())) {
            queryWrapper.eq(SignInInformation::getUpdateBy, dto.getUpdateBy());
        }

        // 模糊匹配标题和内容
        if (StringUtils.isNotBlank(dto.getSignInTitle())) {
            queryWrapper.like(SignInInformation::getSignInTitle, dto.getSignInTitle());
        }
        if (StringUtils.isNotBlank(dto.getSignInContent())) {
            queryWrapper.like(SignInInformation::getSignInContent, dto.getSignInContent());
        }

        // 精确匹配逻辑删除
        if (dto.getIsDelete() != null) {
            queryWrapper.eq(SignInInformation::getIsDelete, dto.getIsDelete());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 范围查询开始时间
        if (StringUtils.isNotBlank(dto.getStartTime())) {
            LocalDateTime start = LocalDateTime.parse(dto.getStartTime(), formatter);
            queryWrapper.ge(SignInInformation::getStartTime, start);
        }

        // 范围查询结束时间
        if (StringUtils.isNotBlank(dto.getEndTime())) {
            LocalDateTime end = LocalDateTime.parse(dto.getEndTime(), formatter);
            queryWrapper.le(SignInInformation::getEndTime, end);
        }

        // 可选分页
        List<SignInInformation> records;
        long total;
        if (dto.getPageNum() != null && dto.getPageSize() != null) {
            Page<SignInInformation> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            Page<SignInInformation> result = this.page(page, queryWrapper);
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

