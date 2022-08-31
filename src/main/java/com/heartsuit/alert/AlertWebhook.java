package com.heartsuit.alert;

import com.heartsuit.common.event.AlertEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2021-11-10
 */
@RestController
@Slf4j
@RequestMapping("/alert")
public class AlertWebhook {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/webhook")
    public String webhook(@RequestBody List<AlertMessage> req) {
        log.info(req.toString());

        if (req.size() > 0) {
            for (AlertMessage msg : req) {
                AlertEvent alertEvent = new AlertEvent(this, msg);
                applicationEventPublisher.publishEvent(alertEvent);
            }
        }
        return req.toString();
    }
}
