package com.orange.eventframework.eventinfo;

/**
 * 消费事件信息
 *
 * @author 小天
 * @date 2019/3/13 16:58
 */
public class ConsumeEventInfo extends EventInfo {

    private String consumerCode;

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
}
