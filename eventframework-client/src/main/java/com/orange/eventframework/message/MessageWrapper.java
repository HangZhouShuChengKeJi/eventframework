package com.orange.eventframework.message;

import com.alibaba.fastjson.JSONObject;

/**
 * mq 消息包装
 *
 * @author 小天
 * @date 2019/3/26 10:51
 */
public interface MessageWrapper {

    /**
     * 消息体
     */
    String getBody();

    /**
     * 将消息转成指定的类型。<br>
     *
     * 注：需要自行处理类型兼容问题
     *
     * @see JSONObject#toJavaObject(Class)
     */
    <T> T getBody(Class<T> clazz);

    /**
     * 消息体字节数组
     */
    byte[] getBodyBytes();

    /**
     * json 格式的消息体 {@link JSONObject}
     */
    JSONObject getBodyJson();

    /**
     * 消息主题
     */
    String getTopic();

    /**
     * 消息Id
     */
    String getMsgId();

    /**
     * 消息标签
     */
    String getTag();

    /**
     * 消息的 key
     */
    String getKey();
}
