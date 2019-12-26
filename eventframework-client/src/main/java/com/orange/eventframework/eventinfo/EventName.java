package com.orange.eventframework.eventinfo;

import com.orange.eventframework.AbstractEvent;
import com.orange.eventframework.AbstractMQEventListener;

import java.io.Serializable;


/**
 * 用以标记每个事件和消费端的可理解的名字。在事件框架的类初始化完成后，通过消息发送给控制台，注册登记
 *
 * @author yajun.wu
 */
public class EventName implements Serializable {

    /**
     * 消费者code
     */
    private String consumerCode;
    /**
     * 消费者code 对应的方便理解的消费者名称
     */
    private String consumerDisplayName;
    /**
     * 事件code
     */
    private String eventCode;
    /**
     * 事件code 对应的方便理解的名称
     */
    private String eventDisplayName;


    public EventName() {
    }


    public EventName(AbstractEvent event) {
        this.eventCode = event.getEventCode();
        this.eventDisplayName = event.getDisplayName();
    }

    public EventName(AbstractMQEventListener listener) {
        this.consumerCode = listener.getConsumerCode();
        this.consumerDisplayName = listener.getDisplayName();
    }

    public String getConsumerCode() {
        return consumerCode;
    }

    public void setConsumerCode(String consumerCode) {
        this.consumerCode = consumerCode;
    }

    public String getConsumerDisplayName() {
        return consumerDisplayName;
    }

    public void setConsumerDisplayName(String consumerDisplayName) {
        this.consumerDisplayName = consumerDisplayName;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventDisplayName() {
        return eventDisplayName;
    }

    public void setEventDisplayName(String eventDisplayName) {
        this.eventDisplayName = eventDisplayName;
    }
}
