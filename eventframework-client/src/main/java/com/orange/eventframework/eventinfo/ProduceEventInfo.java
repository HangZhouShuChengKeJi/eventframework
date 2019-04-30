package com.orange.eventframework.eventinfo;

/**
 * 生产事件信息
 *
 * @author 小天
 * @date 2019/3/13 16:57
 */
public class ProduceEventInfo extends EventInfo {

    private String sourceMsgId;
    private String sourceMsgTopic;
    private String sourceMsgTag;
    private String sourceMsgKey;
    private String sourceEventCode;
    private String sourceConsumeCode;

    public ProduceEventInfo() {
    }

    public ProduceEventInfo(String msgId, String topic, String tag, String key) {
        super(msgId, topic, tag, key);
    }

    public String getSourceMsgId() {
        return sourceMsgId;
    }

    public void setSourceMsgId(String sourceMsgId) {
        this.sourceMsgId = sourceMsgId;
    }

    public String getSourceMsgTopic() {
        return sourceMsgTopic;
    }

    public void setSourceMsgTopic(String sourceMsgTopic) {
        this.sourceMsgTopic = sourceMsgTopic;
    }

    public String getSourceMsgTag() {
        return sourceMsgTag;
    }

    public void setSourceMsgTag(String sourceMsgTag) {
        this.sourceMsgTag = sourceMsgTag;
    }

    public String getSourceMsgKey() {
        return sourceMsgKey;
    }

    public void setSourceMsgKey(String sourceMsgKey) {
        this.sourceMsgKey = sourceMsgKey;
    }


    public String getSourceEventCode() {
        return sourceEventCode;
    }

    public void setSourceEventCode(String sourceEventCode) {
        this.sourceEventCode = sourceEventCode;
    }

    public String getSourceConsumeCode() {
        return sourceConsumeCode;
    }

    public void setSourceConsumeCode(String sourceConsumeCode) {
        this.sourceConsumeCode = sourceConsumeCode;
    }
}
