package com.orange.eventframework.config;

/**
 * 事件框架配置
 *
 * @author 小天
 * @date 2019/3/14 10:11
 */
public class Config {

    public static final String DEFAULT_APP_NAME = "default";

    /**
     * 应用名称
     */
    private String  appName     = DEFAULT_APP_NAME;
    /**
     * 框架禁用状态（仅禁用事件信息采集）
     */
    private boolean disabled    = true;
    /**
     * rocketmq 事件信息 topic
     */
    private String  topic       = "ef_event";
    /**
     * rocketmq 事件信息生产者组Id
     */
    private String  groupId     = "event_framework";
    /**
     * rocketmq 消息队列 name server 集群地址
     */
    private String  nameSrvAddr = "localhost:9876";

    /**
     * 业务数据默认的 topic
     */
    private String defaultDataTopic    = "ef_data";
    private String defaultProducerCode = "ef_default_producer";

    /**
     * 消费者标识前缀
     */
    private String consumerCodePrefix = null;

    /**
     * 最大重试消费次数
     */
    private int     maxReconsumeTimes                = 3;
    /**
     * 最小消费线程数
     */
    private int     consumeThreadMin                 = 1;
    /**
     * 最大消费线程数
     */
    private int     consumeThreadMax                 = 5;
    /**
     * 最大拉取数量
     */
    private int     pullBatchSize                    = 10;
    /**
     * 消息发送超时时间
     */
    private int     sendMsgTimeout                   = 3000;
    /**
     * 同步模式下，消息发送失败重试次数
     */
    private int     retryTimesWhenSendFailed         = 2;
    /**
     * 异步模式下，消息发送失败重试次数
     */
    private int     retryTimesWhenSendAsyncFailed    = 2;
    /**
     * 内部发送失败时，重试另一个 broker
     */
    private boolean retryAnotherBrokerWhenNotStoreOK = false;
    /**
     * 客户端 IP
     */
    private String  clientIP                         = null;


    public Config() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getNameSrvAddr() {
        return nameSrvAddr;
    }

    public void setNameSrvAddr(String nameSrvAddr) {
        this.nameSrvAddr = nameSrvAddr;
    }

    public String getDefaultDataTopic() {
        return defaultDataTopic;
    }

    public void setDefaultDataTopic(String defaultDataTopic) {
        this.defaultDataTopic = defaultDataTopic;
    }

    public String getDefaultProducerCode() {
        return defaultProducerCode;
    }

    public void setDefaultProducerCode(String defaultProducerCode) {
        this.defaultProducerCode = defaultProducerCode;
    }

    public String getConsumerCodePrefix() {
        return consumerCodePrefix;
    }

    public void setConsumerCodePrefix(String consumerCodePrefix) {
        this.consumerCodePrefix = consumerCodePrefix;
    }

    public int getMaxReconsumeTimes() {
        return maxReconsumeTimes;
    }

    public void setMaxReconsumeTimes(int maxReconsumeTimes) {
        this.maxReconsumeTimes = maxReconsumeTimes;
    }

    public int getConsumeThreadMin() {
        return consumeThreadMin;
    }

    public void setConsumeThreadMin(int consumeThreadMin) {
        this.consumeThreadMin = consumeThreadMin;
    }

    public int getConsumeThreadMax() {
        return consumeThreadMax;
    }

    public void setConsumeThreadMax(int consumeThreadMax) {
        this.consumeThreadMax = consumeThreadMax;
    }

    public int getPullBatchSize() {
        return pullBatchSize;
    }

    public void setPullBatchSize(int pullBatchSize) {
        this.pullBatchSize = pullBatchSize;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public int getSendMsgTimeout() {
        return sendMsgTimeout;
    }

    public void setSendMsgTimeout(int sendMsgTimeout) {
        this.sendMsgTimeout = sendMsgTimeout;
    }

    public int getRetryTimesWhenSendFailed() {
        return retryTimesWhenSendFailed;
    }

    public void setRetryTimesWhenSendFailed(int retryTimesWhenSendFailed) {
        this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
    }

    public int getRetryTimesWhenSendAsyncFailed() {
        return retryTimesWhenSendAsyncFailed;
    }

    public void setRetryTimesWhenSendAsyncFailed(int retryTimesWhenSendAsyncFailed) {
        this.retryTimesWhenSendAsyncFailed = retryTimesWhenSendAsyncFailed;
    }

    public boolean isRetryAnotherBrokerWhenNotStoreOK() {
        return retryAnotherBrokerWhenNotStoreOK;
    }

    public void setRetryAnotherBrokerWhenNotStoreOK(boolean retryAnotherBrokerWhenNotStoreOK) {
        this.retryAnotherBrokerWhenNotStoreOK = retryAnotherBrokerWhenNotStoreOK;
    }
}
