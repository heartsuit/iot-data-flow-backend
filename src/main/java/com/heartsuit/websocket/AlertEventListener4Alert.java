package com.heartsuit.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartsuit.common.event.AlertEvent;
import com.heartsuit.websocket.service.WebSocketSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @Author Heartsuit
 * @Date 2021-11-10
 */
@Component
@Slf4j
public class AlertEventListener4Alert {
    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @EventListener
    public void handleEvent(AlertEvent event) throws JsonProcessingException {
        log.info("Alert Message received in WebSocket: {}", event.getAlertMessage());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        objectMapper.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));
        String jsonAlert = objectMapper.writeValueAsString(event.getAlertMessage());
        WebSocketSession.sendMessage2All(jsonAlert);
    }
}
