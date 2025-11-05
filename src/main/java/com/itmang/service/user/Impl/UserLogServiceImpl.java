package com.itmang.service.user.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.mapper.LogMapper;
import com.itmang.pojo.dto.PageUserLogDto;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.UserLog;
import com.itmang.service.user.UserLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户日志服务实现类
 */
@Slf4j
@Service
public class UserLogServiceImpl extends ServiceImpl<LogMapper, UserLog> implements UserLogService {

    @Autowired
    private LogMapper logMapper;

    /**
     * 分页查询用户日志
     * @param pageUserLogDto 分页查询参数
     * @return 分页结果
     */
    @Override
    public PageResult getUserLogPage(PageUserLogDto pageUserLogDto) {
        // 设置默认分页参数
        if (pageUserLogDto.getPage() == null || pageUserLogDto.getPage() <= 0) {
            pageUserLogDto.setPage(1);
        }
        if (pageUserLogDto.getPageSize() == null || pageUserLogDto.getPageSize() <= 0) {
            pageUserLogDto.setPageSize(10);
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(pageUserLogDto.getPage(), pageUserLogDto.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<UserLog> queryWrapper = new LambdaQueryWrapper<>();
        
        // 按操作人姓名模糊查询
        if (StringUtils.isNotBlank(pageUserLogDto.getUserName())) {
            queryWrapper.like(UserLog::getCreateBy, pageUserLogDto.getUserName());
        }
        
        // 按操作类型查询
        if (pageUserLogDto.getType() != null) {
            queryWrapper.eq(UserLog::getType, pageUserLogDto.getType());
        }
        
        // 按操作时间范围查询
        if (pageUserLogDto.getCreateStartTime() != null && pageUserLogDto.getCreateEndTime() != null) {
            queryWrapper.between(UserLog::getCreateTime, pageUserLogDto.getCreateStartTime(), pageUserLogDto.getCreateEndTime());
        } else if (pageUserLogDto.getCreateStartTime() != null) {
            queryWrapper.ge(UserLog::getCreateTime, pageUserLogDto.getCreateStartTime());
        } else if (pageUserLogDto.getCreateEndTime() != null) {
            queryWrapper.le(UserLog::getCreateTime, pageUserLogDto.getCreateEndTime());
        }
        
        // 按创建时间降序排列
        queryWrapper.orderByDesc(UserLog::getCreateTime);
        
        // 执行分页查询
        List<UserLog> userLogList = logMapper.selectList(queryWrapper);
        PageInfo<UserLog> pageInfo = new PageInfo<>(userLogList);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
}