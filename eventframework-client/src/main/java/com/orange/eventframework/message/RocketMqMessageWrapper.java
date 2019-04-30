package com.orange.eventframework.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;

/**
 * rocketmq 消息体包装
 *
 * @author 小天
 * @date 2019/3/26 10:54
 */
public class RocketMqMessageWrapper implements MessageWrapper {

    private transient MessageExt messageExt;
    private volatile  String     messageBodyStr;
    private volatile  JSONObject messageBodyJson;

    public RocketMqMessageWrapper(MessageExt messageExt) {
        this.messageExt = messageExt;
    }

    @Override
    public String getBody() {
        if (messageBodyStr == null) {
            messageBodyStr = new String(getBodyBytes(), StandardCharsets.UTF_8);
        }
        return messageBodyStr;
    }

    @Override
    public <T> T getBody(Class<T> clazz) {
        return getBodyJson().toJavaObject(clazz);
    }

    @Override
    public byte[] getBodyBytes() {
        return messageExt.getBody();
    }

    @Override
    public JSONObject getBodyJson() {
        if (messageBodyJson != null) {
            return messageBodyJson;
        }
        messageBodyJson = JSON.parseObject(getBody());
        return messageBodyJson;
    }

    @Override
    public String getTopic() {
        return messageExt.getTopic();
    }

    @Override
    public String getMsgId() {
        return messageExt.getMsgId();
    }

    @Override
    public String getTag() {
        return messageExt.getTags();
    }

    @Override
    public String getKey() {
        return messageExt.getKeys();
    }
}
