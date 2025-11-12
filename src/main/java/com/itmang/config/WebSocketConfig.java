package com.itmang.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName:WebScoketConfig
 * Package:com.itmang.config
 * Description:
 *
 * @Author: 绯雾sama
 * @Create:2025/11/12 09:43
 * Version: 1.0
 */
@Configuration
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketConfig {

    @Value("${netty.websocket.port:8086}")
    private int port;
}
