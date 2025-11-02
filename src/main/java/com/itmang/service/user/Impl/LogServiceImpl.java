package com.itmang.service.user.Impl;

import com.itmang.mapper.LogMapper;
import com.itmang.pojo.entity.UserLog;
import com.itmang.service.user.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 日志服务实现类
 */
@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    /**
     * 保存用户操作日志
     * @param userLog 用户日志实体
     */
    @Override
    public void saveLog(UserLog userLog) {
        try {
            logMapper.insert(userLog);
            log.info("用户操作日志保存成功: {}", userLog);
        } catch (Exception e) {
            log.error("保存用户操作日志失败: {}", e.getMessage());
        }
    }
}