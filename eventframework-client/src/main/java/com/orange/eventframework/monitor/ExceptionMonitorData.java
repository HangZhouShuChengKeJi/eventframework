package com.orange.eventframework.monitor;

import java.util.Set;

/**
 * @author maomao
 * @date 2019/5/21
 */
public class ExceptionMonitorData {

    /**
     * 异常类
     */
    private Throwable exception;
    /**
     * 消息服务消费者标识
     */
    private String    consumerCode;
    /**
     * 异常类
     */
    private String    exceptionClass;
    /**
     * 异常信息
     */
    private String    exceptionMessage;
    /**
     * 消息服务地址（默认与事件框架的地址相同）
     */
    private String      nameSrvAddr;
    /**
     * 消息服务topic
     */
    private String      topic;
    /**
     * 消费的事件标识
     */
    private Set<String> consumeEventCodeSet;

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        if (exception == null) {
            return;
        }
        this.exception = exception;
        this.exceptionMessage = exception.getMessage();
        if (exception.getCause() == null) {
            this.exceptionClass = exception.getClass().getName();
        } else {
            this.exceptionClass = exception.getClass().getName() + "," + exception.getCause().getClass().getName();
        }
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getConsumerCode() {
        return consumerCode;
    }

    public void setConsumerCode(String consumerCode) {
        this.consumerCode = consumerCode;
    }

    public String getNameSrvAddr() {
        return nameSrvAddr;
    }

    public void setNameSrvAddr(String nameSrvAddr) {
        this.nameSrvAddr = nameSrvAddr;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Set<String> getConsumeEventCodeSet() {
        return consumeEventCodeSet;
    }

    public void setConsumeEventCodeSet(Set<String> consumeEventCodeSet) {
        this.consumeEventCodeSet = consumeEventCodeSet;
    }
}
