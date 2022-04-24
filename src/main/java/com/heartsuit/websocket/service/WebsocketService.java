package com.heartsuit.websocket.service;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.Map;

/**
 * @Author Heartsuit
 * @Date 2021-08-04
 */
@ServerEndpoint(path = "${netty-ws.path}", port = "${netty-ws.port}")
@Component
@Slf4j
public class WebsocketService {
    @BeforeHandshake
    public void handshake(Session session, HttpHeaders headers, @RequestParam String req, @RequestParam MultiValueMap reqMap, @PathVariable String arg, @PathVariable Map pathMap) {
        session.setSubprotocols("stomp");
        if (!"ok".equals(req)) {
            System.out.println("Authentication failed!");
            session.close();
        }
    }

    @OnOpen
    public void onOpen(Session session, HttpHeaders headers, @RequestParam String req, @RequestParam MultiValueMap reqMap, @PathVariable String arg, @PathVariable Map pathMap) {
        System.out.println("[new connection]: " + arg);
        WebSocketSession.addClient(session, arg);
        System.out.println(req);
        log.info("有新连接加入！当前在线客户端数量：{}", WebSocketSession.getOnlineCount());
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String client = WebSocketSession.getClientBySession(session);
        System.out.println("[one connection closed]："+ client);
        WebSocketSession.removeClient(session);
        log.info("有一连接关闭！当前在线客户端数量：{}", WebSocketSession.getOnlineCount());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        session.close();
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("[new message]: " + message);
        session.sendText("Hello Netty!欧克");
    }

    @OnBinary
    public void onBinary(Session session, byte[] bytes) {
        for (byte b : bytes) {
            System.out.println(b);
        }
        session.sendBinary(bytes);
    }

    @OnEvent
    public void onEvent(Session session, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("read idle");
                    break;
                case WRITER_IDLE:
                    System.out.println("write idle");
                    break;
                case ALL_IDLE:
                    System.out.println("all idle");
                    break;
                default:
                    break;
            }
        }
    }
}
