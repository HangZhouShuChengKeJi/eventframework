package com.orange.eventframework;

import com.orange.eventframework.config.Config;
import com.orange.eventframework.message.MessageWrapper;
import com.orange.eventframework.message.RocketMqMessageWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 抽象 mq 事件监听器
 *
 * @author 小天
 * @date 2019/3/13 10:26
 */
public abstract class AbstractMQEventListener implements MessageListenerConcurrently, SmartLifecycle {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 消息服务消费者标识
     */
    private String      consumerCode;
    /**
     * 消息服务地址（默认与事件框架的地址相同）
     */
    private String      nameSrvAddr;
    /**
     * 消息服务topic（默认由事件框架的地址 {@link Config#getDefaultDataTopic()}）
     */
    private String      topic;
    /**
     * 消费的事件标识
     */
    private Set<String> consumeEventCodeSet;

    private EventFramework eventFramework;

    private DefaultMQPushConsumer dataConsumer;

    private volatile boolean isRunning = false;

    /**
     * 是否禁用事件信息报告
     */
    private boolean disableEventInfoReport = false;

    public AbstractMQEventListener(Collection<String> consumeEventCodeSet) {
        if (CollectionUtils.isEmpty(consumeEventCodeSet)) {
            throw new NullPointerException("consumeEventCodeSet 不能为空");
        }
        this.consumeEventCodeSet = new HashSet<>();
        this.consumeEventCodeSet.addAll(consumeEventCodeSet);
    }

    public void setDisableEventInfoReport(boolean disableEventInfoReport) {
        this.disableEventInfoReport = disableEventInfoReport;
    }

    public void setNameSrvAddr(String nameSrvAddr) {
        this.nameSrvAddr = nameSrvAddr;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setConsumerCode(String consumerCode) {
        this.consumerCode = consumerCode;
    }

    @Override
    public void start() {

        if (this.disableEventInfoReport) {
            logger.debug("禁用事件上报");
        }

        this.eventFramework = EventFramework.getInstance();
        if (this.eventFramework.getConfig().isDisabled()) {
            // 修改为禁用状态
            this.disableEventInfoReport = true;
        }

        Config config = this.eventFramework.getConfig();

        // todo 从配置中心拉取消费的队列信息
        String nameSrvAddr = this.nameSrvAddr;
        if (StringUtils.isEmpty(nameSrvAddr)) {
            // 如果未指定 nameSrvAddr, 则使用与框架相同的 nameSrvAddr
            nameSrvAddr = config.getNameSrvAddr();

            if (StringUtils.isBlank(nameSrvAddr)) {
                throw new RuntimeException("未指定消息队列服务地址");
            }

            logger.debug("使用事件框架默认的 nameSrvAddr：{}", nameSrvAddr);
        } else {
            logger.debug("指定了独立的 nameSrvAddr：{}", nameSrvAddr);
        }

        if (StringUtils.isEmpty(this.consumerCode)) {
            this.consumerCode = this.getClass().getName().replace('.', '_');

            logger.debug("使用事件框架默认的 consumerCode：{}", this.consumerCode);
        } else {
            logger.debug("指定了独立的 consumerCode：{}", this.consumerCode);
        }

        this.dataConsumer = new DefaultMQPushConsumer(this.consumerCode);
        this.dataConsumer.setNamesrvAddr(nameSrvAddr);
        // 设置客户端IP
        this.dataConsumer.setClientIP(config.getClientIP());
        this.dataConsumer.setMaxReconsumeTimes(config.getMaxReconsumeTimes());
        this.dataConsumer.setConsumeThreadMax(config.getConsumeThreadMax());
        this.dataConsumer.setConsumeThreadMin(config.getConsumeThreadMin());
        // 每次拉取的消息数量
        this.dataConsumer.setPullBatchSize(config.getPullBatchSize());

        // 将 tag 按照 topic 分组后订阅
        this.consumeEventCodeSet.stream().collect(Collectors.groupingBy(this::getTopic)).forEach((key, value) -> {
            // 使用 " || " 拼接多个 tag
            String tags = String.join(" || ", value);
            try {
                this.dataConsumer.subscribe(key, tags);
            } catch (MQClientException e) {
                throw new IllegalStateException(e);
            }
        });

        this.dataConsumer.registerMessageListener(this);

        try {
            this.dataConsumer.start();
        } catch (MQClientException e) {
            throw new IllegalStateException(e);
        }

        this.isRunning = true;
    }

    /**
     * 根据“事件标识”获取 topic
     *
     * @param eventCode 事件标识
     *
     * @return topic
     */
    private String getTopic(String eventCode) {
        if (this.eventFramework != null) {
            return this.eventFramework.getConfig().getDefaultDataTopic();
        }
        return this.topic;
    }

    @Override
    public void stop() {
        if (this.dataConsumer != null) {
            this.dataConsumer.shutdown();
            this.dataConsumer = null;
        }
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
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
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        EventContext.setConsumer(consumerCode);

        // 初始化为 消费成功
        ConsumeConcurrentlyStatus status = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        for (MessageExt message : msgs) {
            try {
                EventContext.setCurrentMessage(message);
                if (status != ConsumeConcurrentlyStatus.RECONSUME_LATER && consumeMessage(new RocketMqMessageWrapper(message)) == ConsumeStatus.RECONSUME_LATER) {
                    // 只要有一条消息需要重试，就全部重试
                    status = ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            } catch (Throwable t) {
                logger.error("", t);
                status = ConsumeConcurrentlyStatus.RECONSUME_LATER;
            } finally {
                // 上传事件信息
                if (!this.disableEventInfoReport) {
                    this.eventFramework.uploadConsumeEventInfo(message, consumerCode, message.getTags());
                }
            }
        }
        // 清空一下
        EventContext.clear();
        return status;
    }

    /**
     * 消费信息
     *
     * @param message 消息 {@link MessageWrapper}
     *
     * @return 消费结果 {@link ConsumeConcurrentlyStatus}
     */
    public abstract ConsumeStatus consumeMessage(MessageWrapper message);

}
