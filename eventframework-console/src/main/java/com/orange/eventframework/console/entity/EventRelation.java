package com.orange.eventframework.console.entity;

import com.orange.eventframework.eventinfo.ConsumeEventInfo;
import com.orange.eventframework.eventinfo.ProduceEventInfo;

import java.io.Serializable;

/**
 * 事件关系
 *
 * @author 小天
 * @date 2019/4/4 12:20
 */
public class EventRelation implements Serializable {

    private String appName;
    private String eventCode;
    private String producerCode;
    private String consumerCode;
    private String sourceConsumerCode;
    private String sourceEventCode;

    public EventRelation() {
    }

    public EventRelation(ProduceEventInfo eventInfo) {
        this.appName= eventInfo.getAppName();
        this.eventCode = eventInfo.getEventCode();
        this.producerCode = eventInfo.getProducerCode();

        this.sourceEventCode = eventInfo.getSourceEventCode();
        this.sourceConsumerCode = eventInfo.getSourceConsumeCode();
    }
    public EventRelation(ConsumeEventInfo eventInfo) {
        this.appName= eventInfo.getAppName();
        this.eventCode = eventInfo.getEventCode();
        this.consumerCode = eventInfo.getConsumerCode();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getProducerCode() {
        return producerCode;
    }

    public void setProducerCode(String producerCode) {
        this.producerCode = producerCode;
    }

    public String getConsumerCode() {
        return consumerCode;
    }

    public void setConsumerCode(String consumerCode) {
        this.consumerCode = consumerCode;
    }

    public String getSourceConsumerCode() {
        return sourceConsumerCode;
    }

    public void setSourceConsumerCode(String sourceConsumerCode) {
        this.sourceConsumerCode = sourceConsumerCode;
    }

    public String getSourceEventCode() {
        return sourceEventCode;
    }

    public void setSourceEventCode(String sourceEventCode) {
        this.sourceEventCode = sourceEventCode;
    }

    @Override
    public String toString() {
        return "EventRelation{" +
                "appName='" + appName + '\'' +
                ", eventCode='" + eventCode + '\'' +
                ", producerCode='" + producerCode + '\'' +
                ", consumerCode='" + consumerCode + '\'' +
                ", sourceConsumerCode='" + sourceConsumerCode + '\'' +
                ", sourceEventCode='" + sourceEventCode + '\'' +
                '}';
    }
}
