package com.itmang.websocket;

import com.alibaba.fastjson.JSON;
import com.itmang.pojo.vo.SignInInformationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * WebSocket消息服务类
 */
@Slf4j
@Service
public class WebSocketMessageService {

    /**
     * 给指定用户发送签到VO对象通知
     * @param userId 用户ID
     * @param signInVO 签到VO对象
     */
    public void sendSignInNotificationToUser(String userId, SignInInformationVO signInVO) {
        try {
            String message = JSON.toJSONString(signInVO);
            WebSocketServerHandler.sendMessageToUser(userId, message);
            log.info("已向用户 {} 发送签到通知: {}", userId, signInVO.getSignInTitle());
        } catch (Exception e) {
            log.error("发送签到通知失败", e);
        }
    }
    
    /**
     * 给多个用户发送签到VO对象通知
     * @param userIds 用户ID列表
     * @param signInVO 签到VO对象
     */
    public void sendSignInNotificationToUsers(Iterable<String> userIds, SignInInformationVO signInVO) {
        try {
            for (String userId : userIds) {
                sendSignInNotificationToUser(userId, signInVO);
                log.info("已向用户 {} 发送签到通知: {}", userId, signInVO.getSignInTitle());
            }
        } catch (Exception e) {
            log.error("发送签到通知失败", e);
        }
    }
}