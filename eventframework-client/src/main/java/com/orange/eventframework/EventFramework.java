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
        // 配置文件加载
        this.config = configLoader.load();

        logger.debug("EventFramework 配置文件加载成功");

        // 启动
        bootstrap();

        // 注册 jvm 关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                EventFramework.getInstance().destroy();
            }
        });

        logger.info("EventFramework init successful");
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
