package com.orange.eventframework.monitor;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;

/**
 * @author maomao
 * @date 2019/5/21
 */
public abstract class ExceptionMonitorHandler implements SmartApplicationListener, InitializingBean, DisposableBean {

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType.isAssignableFrom(ExceptionMonitorEvent.class);
    }

    @Override
    public boolean supportsSourceType(Class<?> aClass) {
        return true;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        Object source;
        if ((source = applicationEvent.getSource()) == null) {
            return;
        }
        if (source instanceof ExceptionMonitorData) {
            onExceptionMonitorEvent((ExceptionMonitorEvent) applicationEvent);
        }
    }

    protected abstract void onExceptionMonitorEvent(ExceptionMonitorEvent exceptionMonitorEvent);
}
