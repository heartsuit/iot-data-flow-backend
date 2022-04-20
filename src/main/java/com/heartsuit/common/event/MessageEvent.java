package com.heartsuit.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @Author Heartsuit
 * @Date 2021-09-03
 */
@Getter
public class MessageEvent extends ApplicationEvent {
    private String msg;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public MessageEvent(Object source) {
        super(source);
    }

    public MessageEvent(Object source, String msg) {
        super(source);
        this.msg = msg;
    }
}