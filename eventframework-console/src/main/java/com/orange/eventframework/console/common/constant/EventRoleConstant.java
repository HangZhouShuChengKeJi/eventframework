package com.orange.eventframework.console.common.constant;

/**
 * 事件常量
 *
 * @author yajun.wu
 */
public enum EventRoleConstant {

    /**
     * 事件处理
     */
    EVENT_ROLE("event_role"),
    /**
     * 消费处理
     */
    CONSUMER_ROLE("consumer_role");

    private String value;

    EventRoleConstant(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
