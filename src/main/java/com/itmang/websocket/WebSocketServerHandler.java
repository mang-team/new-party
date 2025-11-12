package com.itmang.websocket;

import com.itmang.constant.JwtClaimsConstant;
import com.itmang.properties.JwtProperties;
import com.itmang.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket消息处理器
 */
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private RedisTemplate redisTemplate;

    // 存储所有连接的通道
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 存储用户ID与通道的映射关系
    public static ConcurrentHashMap<String, Channel> userChannels = new ConcurrentHashMap<>();

    /**
     * 通道就绪事件
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        log.info("客户端连接：{}", ctx.channel().remoteAddress());
    }

    /**
     * 通道未就绪事件
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
        // 移除用户与通道的映射关系
        userChannels.entrySet().removeIf(entry -> entry.getValue().equals(ctx.channel()));
        log.info("客户端断开连接：{}", ctx.channel().remoteAddress());
    }

    /**
     * 读取客户端发送的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String message = msg.text();
        log.info("收到客户端消息：{}", message);

        // 如果消息是token，建立用户ID与通道的映射关系
        if (message.startsWith("token:")) {
            if(redisTemplate.hasKey("blacklist:" + message)){
               return;
            }
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), message);
            String userId = String.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            userChannels.put(userId, ctx.channel());
            log.info("用户 {} 认证成功", userId);
            ctx.channel().writeAndFlush(new TextWebSocketFrame("认证成功"));
        } else {
            // 其他消息
            ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器收到消息: " + message));
        }
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("WebSocket连接异常", cause);
        ctx.close();
    }

    /**
     * 给指定用户发送消息
     *
     * @param userId  用户ID
     * @param message 消息内容
     */
    public static void sendMessageToUser(String userId, String message) {
        Channel channel = userChannels.get(userId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(message));
        }
    }
}