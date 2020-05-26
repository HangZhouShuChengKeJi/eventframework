package com.orange.eventframework.console.event;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.orange.eventframework.Constants;
import com.orange.eventframework.config.Config;
import com.orange.eventframework.console.common.constant.EventRoleConstant;
import com.orange.eventframework.console.dal.entity.EventNameAlias;
import com.orange.eventframework.console.entity.EventRelation;
import com.orange.eventframework.console.service.EventAliasService;
import com.orange.eventframework.console.service.EventRelationService;
import com.orange.eventframework.eventinfo.ConsumeEventInfo;
import com.orange.eventframework.eventinfo.EventName;
import com.orange.eventframework.eventinfo.ProduceEventInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

/**
 * 事件信息监听器
 *
 * @author 小天
 * @date 2019/4/2 14:48
 */
@Component
public class EventInfoListener implements MessageListenerConcurrently, SmartLifecycle {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private DefaultMQPushConsumer consumer;
    @Value("${eventframework.console.rocketmq.consumer.groupId}")
    public  String                consumerGroupId;
    @Resource
    private EventRelationService  eventRelationService;
    @Resource
    private EventAliasService     eventAliasService;
    @Resource(name = "eventFrameworkConfig")
    private Config                eventFrameworkConfig;

    private Random random;

    private Cache<String, EventRelation> eventInfoCache = CacheBuilder.newBuilder()
                                                                      .maximumSize(100)
                                                                      .concurrencyLevel(20)
                                                                      .build();

    private Cache<String, EventNameAlias> eventNameCache = CacheBuilder.newBuilder()
                                                                      .maximumSize(100)
                                                                      .concurrencyLevel(20)
                                                                      .build();

    @Override
    public void start() {

        this.consumer = new DefaultMQPushConsumer(consumerGroupId);
        this.consumer.setNamesrvAddr(eventFrameworkConfig.getNameSrvAddr());
        this.consumer.setClientIP(eventFrameworkConfig.getClientIP());
        this.consumer.setMaxReconsumeTimes(eventFrameworkConfig.getMaxReconsumeTimes());
        this.consumer.setConsumeThreadMax(eventFrameworkConfig.getConsumeThreadMax());
        this.consumer.setConsumeThreadMin(eventFrameworkConfig.getConsumeThreadMin());
        this.consumer.setPullBatchSize(eventFrameworkConfig.getPullBatchSize());

        try {
            this.consumer.subscribe(eventFrameworkConfig.getTopic(), "*");
        } catch (MQClientException e) {
            throw new IllegalStateException(e);
        }

        logger.info("注册 rocketmq 监听器： topic={}, consumerGroupId={}, tag=*", eventFrameworkConfig.getTopic(), this.consumerGroupId);
        this.consumer.registerMessageListener(this);

        try {
            this.consumer.start();
        } catch (MQClientException e) {
            throw new IllegalStateException(e);
        }

        this.random = new Random();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public void stop() {
        if (this.consumer != null) {
            this.consumer.shutdown();
            this.consumer = null;
        }
    }

    @Override
    public boolean isRunning() {
        return this.consumer != null;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt msg : msgs) {

            // 抽样处理，display_name 不能被抽样
            if (this.random.nextInt(20) != 0 && !Constants.EVENT_DISPLAY_NAME.equals(msg.getTags())) {
                continue;
            }

            try {
                String body = new String(msg.getBody(), StandardCharsets.UTF_8);

                logger.debug("监听到消息： msgId={},topic={},tag={},body={}", msg.getMsgId(), msg.getTopic(), msg.getTags(), body);

                if (Constants.EVENT_INFO_PRODUCE.equals(msg.getTags())) {

                    ProduceEventInfo eventInfo = JSON.parseObject(body, ProduceEventInfo.class);

                    // 保存事件信息
                    eventRelationService.save(eventInfo);

                    // 保存事件关系
                    saveEventRelation(new EventRelation(eventInfo));
                } else if (Constants.EVENT_INFO_CONSUME.equals(msg.getTags())) {
                    ConsumeEventInfo eventInfo = JSON.parseObject(msg.getBody(), ConsumeEventInfo.class);

                    // 保存事件信息
                    eventRelationService.save(eventInfo);

                    // 保存事件关系
                    saveEventRelation(new EventRelation(eventInfo));
                } else if (Constants.EVENT_DISPLAY_NAME.equals(msg.getTags())) {
                    // 修改显示名称
                    updateDisplayName(body);
                    continue;
                } else {
                    continue;
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 保存事件关系
     *
     * @param eventRelation
     */
    private void saveEventRelation(EventRelation eventRelation) {
        if (logger.isDebugEnabled()) {
            logger.debug("监听到事件关系： {}", JSON.toJSONString(eventRelation));
        }

        // 保存事件关系
        String md5 = DigestUtils.md5Hex(eventRelation.toString());
        // 通过缓存减少对 es 的重复写操作，减少压力（事件之间的关系很少改变）
        if (eventInfoCache.getIfPresent(md5) == null) {
            eventRelationService.save(eventRelation);
            eventInfoCache.put(md5, eventRelation);
        }
    }

    /**
     * 更新错题
     */
    private void updateDisplayName(String body) {
        EventName eventName = JSON.parseObject(body, EventName.class);

        EventNameAlias alias;
        String displayName;
        String code;
        EventRoleConstant role;
        if (eventName.getConsumerCode() != null) {
            displayName = eventName.getConsumerDisplayName();
            code = eventName.getConsumerCode();
            role = EventRoleConstant.CONSUMER_ROLE;
        } else {
            displayName = eventName.getEventDisplayName();
            code = eventName.getEventCode();
            role = EventRoleConstant.EVENT_ROLE;
        }

        alias = eventAliasService.getAlias(code, role.getValue());

        if (alias == null) {
            alias = new EventNameAlias();
            alias.setDisplayName(displayName);
            alias.setCode(code);
            alias.setRole(role.getValue());
        } else {
            alias.setDisplayName(displayName);
        }

        String cacheKey = StringUtils.join(new String[]{alias.getCode(), alias.getDisplayName(), alias.getRole()}, "_");

        if (eventNameCache.getIfPresent(cacheKey) == null) {
            eventAliasService.save(alias);
            eventNameCache.put(cacheKey, alias);
        }
    }

}
