package com.heartsuit.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartsuit.common.event.MessageEvent;
import com.heartsuit.storage.domain.Power;
import com.heartsuit.websocket.service.WebSocketSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.yeauty.pojo.Session;

/**
 * @Author Heartsuit
 * @Date 2021-09-13
 */
@Component
@Slf4j
public class MessageEventListener4Push {
    @EventListener
    public void handleEvent(MessageEvent event) throws JsonProcessingException {
        String payload = event.getMsg();
        log.info("Message received in WebSocket: {}", payload);

        Power power = new ObjectMapper().readValue(payload, Power.class);
        String city = power.getCity();
        Session session = WebSocketSession.getSessionByClient(city);
        if (null != session) {
            WebSocketSession.sendMessage2Target(session, payload);
        }
    }
}
