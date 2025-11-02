package com.itmang.service.user;

import com.itmang.pojo.entity.UserLog;

/**
 * 日志服务接口
 */
public interface LogService {

    /**
     * 保存用户操作日志
     * @param userLog 用户日志实体
     */
    void saveLog(UserLog userLog);
}