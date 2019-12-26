package com.orange.eventframework;

import com.alibaba.fastjson.JSON;
import com.orange.eventframework.config.Config;
import com.orange.eventframework.eventinfo.ConsumeEventInfo;
import com.orange.eventframework.eventinfo.EventName;
import com.orange.eventframework.eventinfo.ProduceEventInfo;
import io.netty.util.internal.ConcurrentSet;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Set;

/**
 * 事件框架专用的生产者。用于上传事件信息采集数据
 *
 * @author 小天
 * @date 2019/4/30 17:00
 */
class EventInfoProducer {

    private        Logger            logger             = LoggerFactory.getLogger(this.getClass());
    /**
     * 事件框架配置
     */
    private        Config            config;
    /**
     * 事件框架专用的生产者。用于上传事件信息采集数据
     */
    private        DefaultMQProducer efProducer;
    /**
     * 已经注册到服务端的名字
     */
    private static Set<String>       eventCodeNameRegEd = new ConcurrentSet();

    void init(Config config) {
        this.config = config;

        this.efProducer = new DefaultMQProducer(this.config.getGroupId());
        this.efProducer.setNamesrvAddr(this.config.getNameSrvAddr());
        this.efProducer.setRetryTimesWhenSendAsyncFailed(1);
        this.efProducer.setRetryTimesWhenSendFailed(0);
        // 设置客户端IP
        this.efProducer.setClientIP(this.config.getClientIP());
        // 设置实例名称
        this.efProducer.setInstanceName(this.config.getAppName());
        try {
            this.efProducer.start();
        } catch (MQClientException e) {
            throw new IllegalStateException(e);
        }
    }

    void destroy() {
        this.efProducer.shutdown();
    }

    void uploadConsumeEventInfo(MessageExt message, String consumerCode, String consumeEventCode, Date consumeStartTime) {
        ConsumeEventInfo eventInfo = new ConsumeEventInfo(message.getMsgId(), message.getTopic(), message.getTags(), message.getKeys());

        eventInfo.setConsumerCode(consumerCode);
        eventInfo.setEventCode(consumeEventCode);
        eventInfo.setCreateTime(new Date());
        eventInfo.setConsumeStartTime(consumeStartTime);
        eventInfo.setConsumeEndTime(eventInfo.getCreateTime());

        Message msg = new Message(config.getTopic(), Constants.EVENT_INFO_CONSUME, message.getMsgId(), JSON.toJSONBytes(eventInfo));

        try {
            // 采用异步上传
            this.efProducer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    logger.debug("上传事件信息成功： {}", sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    logger.error("上传事件信息出现异常", e);
                }
            });
        } catch (Throwable t) {
            logger.error("上传事件信息出现异常", t);
        }
    }

    void uploadProduceEventInfo(AbstractEvent event, String producerCode, String msgId, Message message) {

        regEventDisplayNameIfNeed(event, msgId);

        ProduceEventInfo eventInfo = new ProduceEventInfo(msgId, message.getTopic(), message.getTags(), message.getKeys());

        EventContext.InternalEventContext eventContext = EventContext.getInternalContext();

        eventInfo.setEventCode(event.getEventCode());
        eventInfo.setProducerCode(producerCode);
        eventInfo.setCreateTime(new Date());

        eventInfo.setAppName(config.getAppName());

        eventInfo.setSourceMsgId(eventContext.getMsgId());
        eventInfo.setSourceMsgTopic(eventContext.getTopic());
        eventInfo.setSourceMsgTag(eventContext.getTag());
        eventInfo.setSourceMsgKey(eventContext.getKey());
        // 使用 tag 作为事件标识
        eventInfo.setSourceEventCode(eventContext.getTag());
        eventInfo.setSourceConsumeCode(eventContext.getConsumerCode());

        Message msg = new Message(this.config.getTopic(), Constants.EVENT_INFO_PRODUCE, msgId, JSON.toJSONBytes(eventInfo));

        try {
            // 采用异步上传
            this.efProducer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    logger.debug("上传事件信息成功： {}", sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    logger.error("上传事件信息出现异常", e);
                }
            });
        } catch (Throwable t) {
            logger.error("上传事件信息出现异常", t);
        }
    }


    /**
     * 向事件框架的中心反馈该事件的显示名
     */
    private void regEventDisplayNameIfNeed(AbstractEvent event, String msgId) {
        if (eventCodeNameRegEd.contains(event.getEventCode())) {
            return;
        }

        EventName eventName = new EventName(event);
        Message msg = new Message(this.config.getTopic(), Constants.EVENT_DISPLAY_NAME, msgId, JSON.toJSONBytes(eventName));

        try {
            // 采用异步上传
            this.efProducer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    eventCodeNameRegEd.add(event.getEventCode());
                    logger.debug("上传事件信息成功： {}", sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    logger.error("上传事件信息出现异常", e);
                }
            });
        } catch (Throwable t) {
            logger.error("上传事件信息出现异常", t);
        }
    }

    /**
     * 向事件框架的中心反馈该消费端的显示名
     */
    void uploadConsumeDisplayName(AbstractMQEventListener listener) {

        EventName eventName = new EventName(listener);
        Message msg = new Message(this.config.getTopic(), Constants.EVENT_DISPLAY_NAME, listener.getClass().getName(), JSON.toJSONBytes(eventName));

        try {
            // 采用异步上传
            this.efProducer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    logger.debug("上传消费端名称成功： {}", sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    logger.error("上传消费端名称出现异常", e);
                }
            });
        } catch (Throwable t) {
            logger.error("上传消费端名称出现异常", t);
        }
    }
}
