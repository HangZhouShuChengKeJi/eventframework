package com.orange.eventframework;

import org.apache.rocketmq.common.message.MessageExt;

/**
 * 事件框架上下文
 *
 * @author 小天
 * @date 2019/3/12 11:31
 */
public final class EventContext {


    private static final ThreadLocal<PublicEventContext> LOCAL_PUBLIC = new ThreadLocal<>();

    /**
     * 事件框架上下文
     */
    public static PublicEventContext get() {
        return get(true);
    }

    /**
     * 事件框架上下文
     * @param init 未初始化时，是否执行初始化操作
     */
    public static PublicEventContext get(boolean init) {
        PublicEventContext eventContext;
        if ((eventContext = LOCAL_PUBLIC.get()) == null && init) {
            LOCAL_PUBLIC.set((eventContext = new PublicEventContext()));
        }
        return eventContext;
    }

    /**
     * 清空事件框架上下文
     */
    public static void clear() {
        LOCAL_PUBLIC.remove();
    }

    private static final ThreadLocal<InternalEventContext> LOCAL_INTERNAL = new ThreadLocal<InternalEventContext>() {
        @Override
        protected InternalEventContext initialValue() {
            return new InternalEventContext();
        }
    };

    static void setCurrentMessage(MessageExt message) {
        InternalEventContext eventContext = LOCAL_INTERNAL.get();
        eventContext.msgId = message.getMsgId();
        eventContext.topic = message.getTopic();
        eventContext.tag = message.getTags();
        eventContext.key = message.getKeys();
        eventContext.bornTimestamp = message.getBornTimestamp();
    }

    static void setConsumer(String consumerCode) {
        InternalEventContext eventContext = LOCAL_INTERNAL.get();
        eventContext.consumerCode = consumerCode;
    }

    /**
     * 获取事件框架内部使用的上下文
     */
    static InternalEventContext getInternalContext() {
        return LOCAL_INTERNAL.get();
    }

    /**
     * 清空事件框架内部使用的上下文
     */
    static void clearInternalEventContext() {
        LOCAL_INTERNAL.remove();
    }


    /**
     * 事件框架内部使用的上下文信息
     */
    static class InternalEventContext {

        private String msgId;
        private String topic;
        private String tag;
        private String key;
        /**
         * 消息生产时间
         */
        private long   bornTimestamp;
        /**
         * 当前的消费者标识
         */
        private String consumerCode;

        String getMsgId() {
            return msgId;
        }

        String getTopic() {
            return topic;
        }

        String getTag() {
            return tag;
        }

        String getKey() {
            return key;
        }

        long getBornTimestamp() {
            return bornTimestamp;
        }

        String getConsumerCode() {
            return consumerCode;
        }
    }


    /**
     * 公开的事件框架上下文
     */
    public static class PublicEventContext {
        /**
         * 是否推送到消息队列，默认为： true
         */
        private boolean enablePushToMQ = true;

        /**
         * 当前上下文禁用发布事件到消息队列
         */
        public void disablePushToMQ() {
            this.enablePushToMQ = false;
        }

        /**
         * 当前上下文启用发布事件到消息队列
         */
        public void enablePushToMQ() {
            this.enablePushToMQ = true;
        }

        boolean isEnablePushToMQ() {
            return enablePushToMQ;
        }
    }
}
