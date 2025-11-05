package com.itmang.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmang.pojo.dto.PageUserLogDto;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.UserLog;

/**
 * 用户日志服务接口
 */
public interface UserLogService extends IService<UserLog> {

    /**
     * 分页查询用户日志
     * @param pageUserLogDto 分页查询参数
     * @return 分页结果
     */
    PageResult getUserLogPage(PageUserLogDto pageUserLogDto);
}