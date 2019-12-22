package com.orange.eventframework.eventinfo;

import java.io.Serializable;
import java.util.Date;

/**
 * 事件信息定义
 *
 * @author 小天
 * @date 2019/3/13 10:24
 */
public class EventInfo implements Serializable {

    private String msgId;
    private String topic;
    private String tag;
    private String key;
    /**
     * 事件标识
     */
    private String eventCode;
    /**
     * 生产者标识
     */
    private String producerCode;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 创建时间
     */
    private Date   createTime;

    public EventInfo() {
    }

    public EventInfo(String msgId, String topic, String tag, String key) {
        this.msgId = msgId;
        this.topic = topic;
        this.tag = tag;
        this.key = key;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
