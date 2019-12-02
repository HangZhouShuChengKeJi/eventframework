package com.orange.eventframework;

import com.orange.eventframework.config.Config;
import com.orange.eventframework.config.ConfigLoader;
import com.orange.eventframework.config.DefaultConfigLoader;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事件框架
 *
 * @author 小天
 * @date 2019/4/30 15:12
 */
public final class EventFramework {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 事件框架配置
     */
    private Config            config;
    /**
     * 事件信息生产者
     */
    private EventInfoProducer eventInfoProducer;

    private static EventFramework INSTACNE;

    public static EventFramework getInstance() {
        if (INSTACNE != null) {
            return INSTACNE;
        }
        INSTACNE = new EventFramework();
        try {
            INSTACNE.init();
        } catch (Throwable e) {
            INSTACNE = null;
            throw e;
        }
        return INSTACNE;
    }

    /**
     * 初始化入口
     */
    public void init() {
        init(new DefaultConfigLoader());
    }

    /**
     * 初始化入口
     */
    public void init(ConfigLoader configLoader) {
        logger.info("EventFramework init ...");

        // 配置文件加载
        logger.debug("EventFramework load config ...");
        this.config = configLoader.load();

        // 检查配置
        logger.debug("EventFramework check config ...");
        checkConfig(this.config);

        logger.info("appName: \t\t{}", config.getAppName());
        logger.info("disabled: \t\t{}", config.isDisabled());
        logger.info("topic: \t\t{}", config.getTopic());
        logger.info("groupId: \t\t{}", config.getGroupId());
        logger.info("nameSrvAddr: \t\t{}", config.getNameSrvAddr());
        logger.info("defaultDataTopic: \t\t{}", config.getDefaultDataTopic());
        logger.info("defaultProducerCode: \t\t{}", config.getDefaultProducerCode());
        logger.info("maxReconsumeTimes: \t\t{}", config.getMaxReconsumeTimes());
        logger.info("consumeThreadMin: \t\t{}", config.getConsumeThreadMin());
        logger.info("consumeThreadMax: \t\t{}", config.getConsumeThreadMax());
        logger.info("pullBatchSize: \t\t{}", config.getPullBatchSize());
        logger.info("sendMsgTimeout: \t\t{}", config.getSendMsgTimeout());
        logger.info("retryTimesWhenSendFailed: \t\t{}", config.getRetryTimesWhenSendFailed());
        logger.info("retryTimesWhenSendAsyncFailed: \t\t{}", config.getRetryTimesWhenSendAsyncFailed());
        logger.info("retryAnotherBrokerWhenNotStoreOK: \t\t{}", config.isRetryAnotherBrokerWhenNotStoreOK());
        logger.info("clientIP: \t\t{}", config.getClientIP());

        // 启动
        logger.debug("EventFramework bootstrap ...");
        bootstrap();

        // 注册 jvm 关闭钩子
        logger.debug("EventFramework add shutdown hook ...");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                EventFramework.getInstance().destroy();
            }
        });

        logger.info("EventFramework init successful");
    }

    /**
     * 检查配置
     */
    private void checkConfig(Config config) {
        if (Config.DEFAULT_APP_NAME.equals(config.getAppName())) {
            logger.warn("正在使用默认的应用程序名称： “{}”，请注意修改", config.getAppName());
        }
    }

    /**
     * 启动
     */
    private void bootstrap() {
        // 判断是否禁用
        if (this.config.isDisabled()) {
            return;
        }
        // 初始化 事件框架 生产者
        this.eventInfoProducer = new EventInfoProducer();
        this.eventInfoProducer.init(this.config);
    }

    /**
     * 销毁
     */
    public void destroy() {
        if (this.eventInfoProducer != null) {
            this.eventInfoProducer.destroy();
        }
    }

    /**
     * 获取事件框架配置
     */
    public Config getConfig() {
        return config;
    }

    void uploadProduceEventInfo(AbstractEvent event, String producerCode, String msgId, Message message) {
        this.eventInfoProducer.uploadProduceEventInfo(event, producerCode, msgId, message);
    }

    void uploadConsumeEventInfo(MessageExt message, String consumerCode, String consumeEventCode) {
        this.eventInfoProducer.uploadConsumeEventInfo(message, consumerCode, consumeEventCode);
    }


}
