package com.orange.eventframework;

import com.orange.eventframework.config.Config;
import com.orange.eventframework.message.MessageWrapper;
import com.orange.eventframework.message.RocketMqMessageWrapper;
import com.orange.eventframework.monitor.ExceptionMonitorData;
import com.orange.eventframework.monitor.ExceptionMonitorEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 抽象 mq 事件监听器
 *
 * @author 小天
 * @date 2019/3/13 10:26
 */
public abstract class AbstractMQEventListener implements MessageListenerConcurrently, SmartLifecycle, ApplicationListener<ContextRefreshedEvent> {

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
    /**
     * 最大重试消费次数
     */
    private Integer     maxReconsumeTimes;
    /**
     * 最小消费线程数
     */
    private Integer     consumeThreadMin;
    /**
     * 最大消费线程数
     */
    private Integer     consumeThreadMax;
    /**
     * 每批次最大拉取数量
     */
    private Integer     pullBatchSize;
    /**
     * 消费端的显示名字
     */
    private String      displayName;

    private EventFramework eventFramework;

    private DefaultMQPushConsumer dataConsumer;

    private volatile boolean isRunning = false;


    /**
     * 是否禁用事件信息报告
     */
    private boolean disableEventInfoReport = false;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    /**
     * @param consumeEventCodeSet 消费的事件集合
     */
    public AbstractMQEventListener(Collection<String> consumeEventCodeSet) {
        this(consumeEventCodeSet, null, null, null, null);
    }

    /**
     * @param consumeEventCodeSet 消费的事件集合
     * @param maxReconsumeTimes   最大重试消费次数
     * @param consumeThreadMin    最小消费线程数
     * @param consumeThreadMax    最大消费线程数
     * @param pullBatchSize       每批次最大拉取数量
     */
    public AbstractMQEventListener(Collection<String> consumeEventCodeSet, Integer maxReconsumeTimes, Integer consumeThreadMin, Integer consumeThreadMax, Integer pullBatchSize) {
        if (CollectionUtils.isEmpty(consumeEventCodeSet)) {
            throw new NullPointerException("consumeEventCodeSet 不能为空");
        }
        this.consumeEventCodeSet = new HashSet<>();
        this.consumeEventCodeSet.addAll(consumeEventCodeSet);

        this.maxReconsumeTimes = maxReconsumeTimes;
        this.consumeThreadMin = consumeThreadMin;
        this.consumeThreadMax = consumeThreadMax;
        this.pullBatchSize = pullBatchSize;
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

    public Integer getMaxReconsumeTimes() {
        return maxReconsumeTimes;
    }

    public Integer getConsumeThreadMin() {
        return consumeThreadMin;
    }

    public Integer getConsumeThreadMax() {
        return consumeThreadMax;
    }

    public Integer getPullBatchSize() {
        return pullBatchSize;
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

        // 获取全局配置
        Config config = this.eventFramework.getConfig();

        if (this.maxReconsumeTimes == null) {
            this.maxReconsumeTimes = config.getMaxReconsumeTimes();
        }
        if (this.consumeThreadMax == null) {
            this.consumeThreadMax = config.getConsumeThreadMax();
        }
        if (this.consumeThreadMin == null) {
            this.consumeThreadMin = config.getConsumeThreadMin();
        }
        if (this.pullBatchSize == null) {
            this.pullBatchSize = config.getPullBatchSize();
        }

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
        this.dataConsumer.setMaxReconsumeTimes(this.maxReconsumeTimes);
        this.dataConsumer.setConsumeThreadMax(this.consumeThreadMax);
        this.dataConsumer.setConsumeThreadMin(this.consumeThreadMin);
        // 每次拉取的消息数量
        this.dataConsumer.setPullBatchSize(this.pullBatchSize);

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
            logger.warn("停止 RocketMQ 消费者：{}", getClass().getSimpleName());
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
            Date consumeStartTime = new Date();
            try {
                EventContext.setCurrentMessage(message);
                if (status != ConsumeConcurrentlyStatus.RECONSUME_LATER && consumeMessage(new RocketMqMessageWrapper(message)) == ConsumeStatus.RECONSUME_LATER) {
                    // 只要有一条消息需要重试，就全部重试
                    status = ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            } catch (Throwable t) {
                logger.error("", t);
                status = ConsumeConcurrentlyStatus.RECONSUME_LATER;
                // send exception event
                Config config = this.eventFramework.getConfig();
                ExceptionMonitorData exceptionMonitorData = new ExceptionMonitorData();
                exceptionMonitorData.setException(t);
                exceptionMonitorData.setNameSrvAddr(StringUtils.isBlank(nameSrvAddr) ? config.getNameSrvAddr() : nameSrvAddr);
                exceptionMonitorData.setTopic(getTopic(null));
                exceptionMonitorData.setConsumerCode(consumerCode);
                exceptionMonitorData.setConsumeEventCodeSet(new HashSet<>(consumeEventCodeSet));
                eventPublisher.publishEvent(new ExceptionMonitorEvent(exceptionMonitorData));
            } finally {
                // 上传事件信息
                if (!this.disableEventInfoReport) {
                    this.eventFramework.uploadConsumeEventInfo(message, consumerCode, message.getTags(), consumeStartTime);
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


    public abstract String getDisplayName();


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        if (this.disableEventInfoReport) {
            return;
        }
        this.eventFramework.uploadConsumerDisplayName(this);
    }


    public String getConsumerCode() {
        return consumerCode;
    }
}
