package com.heartsuit.emqx.controller;

import com.heartsuit.common.event.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @Author Heartsuit
 * @Date 2021-02-26
 */
@RestController
@Slf4j
@RequestMapping("/emqx")
public class EmqxWebhook {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/webhook")
    public String webhook(@RequestBody HashMap<String, Object> req) {
        log.info(req.toString());

        if(req.get("action").equals("message_publish")){
            String payload = req.get("payload").toString();
            // 发送事件（至TDengine数据存储、WebSocket消息推送）
            MessageEvent messageEvent = new MessageEvent(this, payload);
            applicationEventPublisher.publishEvent(messageEvent);
        }
        return req.toString();
    }
}
