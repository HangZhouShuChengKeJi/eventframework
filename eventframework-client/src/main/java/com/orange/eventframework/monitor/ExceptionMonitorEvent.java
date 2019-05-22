package com.orange.eventframework.monitor;

import org.springframework.context.ApplicationEvent;

/**
 * @author maomao
 * @date 2019/5/21
 */
public class ExceptionMonitorEvent extends ApplicationEvent {

    public ExceptionMonitorEvent(Object source) {
        super(source);
    }

}
