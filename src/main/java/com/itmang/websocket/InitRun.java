package com.itmang.websocket;

import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * ClassName:InitRun
 * Package:com.dingding.websocket
 * Description:
 *
 * @Author: 绯雾sama
 * @Create:2025/6/26 20:03
 * Version: 1.0
 */
@Component
public class InitRun implements ApplicationRunner {

    @Resource
    private NettyWebSocketServer nettyWebSocketServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 在新线程中启动Netty WebSocket服务器
        new Thread(nettyWebSocketServer, "NettyWebSocketThread").start();
    }
}
