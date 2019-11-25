package com.orange.eventframework;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.orange.eventframework.config.Config;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 默认的全局 spring 事件监听器
 *
 * @author 小天
 * @date 2019/3/12 11:02
 */
public class DefaultSpringEventListener implements SmartApplicationListener, SmartLifecycle {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DefaultMQProducer dataProducer;

    private EventFramework eventFramework;

    private String producerCode;
    /**
     * 消息服务地址（默认与事件框架的地址相同）
     */
    private String nameSrvAddr;
    /**
     * 消息服务topic（默认由事件框架的地址 {@link Config#getDefaultDataTopic()}）
     */
    private String topic;

    private volatile boolean isRunning           = false;
    /**
     * 禁用自动推送事件到 mq
     */
    private          boolean disableAutoPushToMQ = false;

    public DefaultSpringEventListener() {
    }

    public void setNameSrvAddr(String nameSrvAddr) {
        this.nameSrvAddr = nameSrvAddr;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setProducerCode(String producerCode) {
        this.producerCode = producerCode;
    }

    public void setDisableAutoPushToMQ(boolean disableAutoPushToMQ) {
        this.disableAutoPushToMQ = disableAutoPushToMQ;
    }

    @Override
    public void start() {

        if (disableAutoPushToMQ) {
            logger.debug("禁用自动发布到MQ");
            return;
        }

        if (this.eventFramework != null) {
            // 避免重复初始化
            return;
        }

        this.eventFramework = EventFramework.getInstance();

        Config config = this.eventFramework.getConfig();

        if (config.isDisabled()) {
            this.eventFramework = null;
            // 修改为禁用状态
            this.disableAutoPushToMQ = true;
            return;
        }

        if (StringUtils.isEmpty(this.nameSrvAddr)) {
            // 如果未指定 nameSrvAddr, 则使用与框架相同的 nameSrvAddr
            this.nameSrvAddr = config.getNameSrvAddr();
            logger.debug("使用事件框架默认的 nameSrvAddr：{}", this.nameSrvAddr);
        } else {
            logger.debug("指定了独立的 nameSrvAddr：{}", this.nameSrvAddr);
        }

        if (StringUtils.isEmpty(this.topic)) {
            // 如果未指定 nameSrvAddr, 则使用与框架相同的 nameSrvAddr
            this.topic = config.getDefaultDataTopic();
            logger.debug("使用事件框架默认的 topic：{}", this.topic);
        } else {
            logger.debug("指定了独立的 topic：{}", this.topic);
        }

        if (StringUtils.isEmpty(this.producerCode)) {
            // 如果未指定 nameSrvAddr, 则使用与框架相同的 nameSrvAddr
            this.producerCode = config.getDefaultProducerCode();
            logger.debug("使用事件框架默认的 producerCode：{}", this.producerCode);
        } else {
            logger.debug("指定了独立的 producerCode：{}", this.producerCode);
        }

        this.dataProducer = new DefaultMQProducer(this.producerCode);
        this.dataProducer.setNamesrvAddr(this.nameSrvAddr);
        this.dataProducer.setSendMsgTimeout(config.getSendMsgTimeout());
        this.dataProducer.setRetryTimesWhenSendFailed(config.getRetryTimesWhenSendFailed());
        this.dataProducer.setRetryTimesWhenSendAsyncFailed(config.getRetryTimesWhenSendAsyncFailed());
        this.dataProducer.setRetryAnotherBrokerWhenNotStoreOK(config.isRetryAnotherBrokerWhenNotStoreOK());
        try {
            this.dataProducer.start();
        } catch (MQClientException e) {
            throw new IllegalStateException(e);
        }
        this.isRunning = true;
    }

    @Override
    public void stop() {
        if (this.dataProducer != null) {
            logger.warn("停止 RocketMQ 生产者：{}", getClass().getSimpleName());
            this.dataProducer.shutdown();
            this.dataProducer = null;
        }
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return AbstractEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        callback.run();
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent e) {
        if (!(e instanceof AbstractEvent)) {
            return;
        }
        if (this.disableAutoPushToMQ) {
            // 禁用自动发布
            return;
        }

        final AbstractEvent event = (AbstractEvent) e;
        if (!event.enablePushToMQ()) {
            // 事件自身的禁用属性
            return;
        }

        EventContext.PublicEventContext eventContext;
        if ((eventContext = EventContext.get(false)) != null && !eventContext.isEnablePushToMQ()) {
            // 上下文里禁用
            return;
        }

        if(TransactionSynchronizationManager.isSynchronizationActive()) {
            try {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        // 事物提交后处理
                        handleEvent(event);
                    }
                });
            } catch (Throwable throwable) {
                logger.debug("注册事务后操作出现异常", throwable);

                // 直接执行
                handleEvent(event);
            }
        } else {
            handleEvent(event);
        }
    }

    private void handleEvent(AbstractEvent event) {
        // 根据事件获取 topic 信息
        String topic = getTopic(event);

        Message msg = new Message(topic, event.getEventCode(), event.key(), JSON.toJSONBytes(event, SerializerFeature.SkipTransientField));

        try {
            SendResult sendResult = this.dataProducer.send(msg);

            if (eventFramework != null) {
                // 上报事件采集信息
                this.eventFramework.uploadProduceEventInfo(event, this.producerCode, sendResult.getMsgId(), msg);
            }
        } catch (Throwable t) {
            logger.error("事件消息发送失败", t);
        }
    }

    private String getTopic(AbstractEvent event) {
        // 需要根据事件获取 topic 信息
        return this.topic;
    }

}
