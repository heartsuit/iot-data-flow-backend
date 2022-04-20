package com.heartsuit.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartsuit.common.event.MessageEvent;
import com.heartsuit.storage.domain.Power;
import com.heartsuit.storage.mapper.PowerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @Author Heartsuit
 * @Date 2021-09-03
 */
@Component
@Slf4j
public class MessageEventListener4Save {
    @Autowired
    private PowerMapper powerMapper;

    @EventListener
    public void handleEvent(MessageEvent event) throws JsonProcessingException {
        String payload = event.getMsg();
        log.info("Message received in Storage: {}", payload);

        Power power = new ObjectMapper().readValue(payload, Power.class);
        int affectRows = powerMapper.insertOne(power);
        log.info("affectRows: {}", affectRows);
    }
}
