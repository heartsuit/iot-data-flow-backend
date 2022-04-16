package com.heartsuit.emqx.controller;

import lombok.extern.slf4j.Slf4j;
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
    @PostMapping("/webhook")
    public String webhook(@RequestBody HashMap<String, Object> req) {
        log.info(req.toString());

        if(req.get("action").equals("message_publish")){
            String payload = req.get("payload").toString();
            // TODO 发送事件至数据存储
        }
        return req.toString();
    }
}
