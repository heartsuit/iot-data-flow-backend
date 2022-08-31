package com.heartsuit.common.event;

import com.heartsuit.alert.AlertMessage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @Author Heartsuit
 * @Date 2021-09-03
 */
@Getter
public class AlertEvent extends ApplicationEvent {
    private AlertMessage alertMessage;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public AlertEvent(Object source) {
        super(source);
    }

    public AlertEvent(Object source, AlertMessage alertMessage) {
        super(source);
        this.alertMessage = alertMessage;
    }
}