package com.orange.eventframework.eventinfo;

import java.util.Date;

/**
 * 消费事件信息
 *
 * @author 小天
 * @date 2019/3/13 16:58
 */
public class ConsumeEventInfo extends EventInfo {

    private String consumerCode;
    /**
     * 消费开始时间
     */
    private Date   consumeStartTime;
    /**
     * 消费结束时间
     */
    private Date   consumeEndTime;

    public ConsumeEventInfo() {
    }

    public ConsumeEventInfo(String msgId, String topic, String tag, String key) {
        super(msgId, topic, tag, key);
    }

    public String getConsumerCode() {
        return consumerCode;
    }

    public void setConsumerCode(String consumerCode) {
        this.consumerCode = consumerCode;
    }

    public Date getConsumeStartTime() {
        return consumeStartTime;
    }

    public void setConsumeStartTime(Date consumeStartTime) {
        this.consumeStartTime = consumeStartTime;
    }

    public Date getConsumeEndTime() {
        return consumeEndTime;
    }

    public void setConsumeEndTime(Date consumeEndTime) {
        this.consumeEndTime = consumeEndTime;
    }
}
