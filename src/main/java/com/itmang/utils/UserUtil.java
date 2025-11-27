package com.itmang.utils;

import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.StatusConstant;
import com.itmang.exception.BaseException;
import com.itmang.mapper.user.UserMapper;
import com.itmang.pojo.entity.User;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UserUtil {

    /**
     * 处理用户ID字符串，筛选出有效用户ID（存在、未删除、启用状态）
     * @param userIdsSupplier 用户ID字符串提供者（适配不同DTO的getUserIds方法）
     * @return 有效用户ID列表
     */
    public static List<String> processValidUserIds(Supplier<String> userIdsSupplier,UserMapper userMapper) {
        List<String> validUserIds = new ArrayList<>();
        try {
            String userIdsStr = userIdsSupplier.get();
            if (userIdsStr == null || userIdsStr.trim().isEmpty()) {
                return validUserIds;
            }

            String[] userIds = userIdsStr.split(",");
            for (String userId : userIds) {
                // 跳过空ID（避免split后出现空字符串）
                if (userId == null || userId.trim().isEmpty()) {
                    continue;
                }
                User user = userMapper.selectById(userId.trim());
                if (user != null
                        && DeleteConstant.NO.equals(user.getIsDelete())
                        && StatusConstant.ENABLE.equals(user.getStatus())) {
                    validUserIds.add(userId.trim());
                }
            }
        } catch (Exception e) {
            throw new BaseException(MessageConstant.USER_PROCESS_FAILED);
        }
        return validUserIds;
    }
}
