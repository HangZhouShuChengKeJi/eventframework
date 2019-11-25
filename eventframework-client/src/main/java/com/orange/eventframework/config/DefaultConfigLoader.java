package com.orange.eventframework.config;

import com.orange.eventframework.util.NetworkUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 默认配置加载器
 *
 * @author 小天
 * @date 2019/4/30 15:17
 */
public class DefaultConfigLoader implements ConfigLoader{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String propertiesKeyPrefix = "orange.eventframework";

    /**
     * properties 配置文件路径
     */
    private String propertiesPath = "orange.eventframework.properties";

    public String getPropertiesPath() {
        return propertiesPath;
    }

    public void setPropertiesPath(String propertiesPath) {
        this.propertiesPath = propertiesPath;
    }

    @Override
    public Config load() {

        Map<String, String> settingsMap = new HashMap<>();

        // 加载配置
        loadFromPropertiesFile(settingsMap);
        loadFromSystemEnv(settingsMap);
        loadFromSystemProperties(settingsMap);

        return convertSettingsToConfig(settingsMap);
    }

    /**
     * 将配置参数转换为 {@link Config}
     */
    private Config convertSettingsToConfig(Map<String, String> settingsMap) {
        Config config = new Config();
        if (settingsMap.isEmpty()) {
            return config;
        }

        if(settingsMap.containsKey("orange.eventframework.appName")) {
            config.setAppName(settingsMap.get("orange.eventframework.appName"));
            if (StringUtils.isBlank(config.getAppName())) {
                config.setAppName(Config.DEFAULT_APP_NAME);
            }
        }

        config.setDisabled(Boolean.parseBoolean(settingsMap.get("orange.eventframework.disabled")));

        // 消息队列集群地址（必选）
        config.setNameSrvAddr(settingsMap.get("orange.eventframework.nameSrvAddr"));

        //  框架采集使用的信息
        if(settingsMap.containsKey("orange.eventframework.topic")) {
            config.setTopic(settingsMap.get("orange.eventframework.topic"));
        }
        if(settingsMap.containsKey("orange.eventframework.groupId")) {
            config.setGroupId(settingsMap.get("orange.eventframework.groupId"));
        }

        // 默认的生产者标识。如果未指定的话，使用 appName 作为标识
        if(settingsMap.containsKey("orange.eventframework.defaultDataTopic")) {
            config.setDefaultDataTopic(settingsMap.get("orange.eventframework.defaultDataTopic"));
        }

        // 最大重试消费次数
        if(settingsMap.containsKey("orange.eventframework.maxReconsumeTimes")) {
            config.setMaxReconsumeTimes(Integer.parseInt(settingsMap.get("orange.eventframework.maxReconsumeTimes")));
        }
        if(settingsMap.containsKey("orange.eventframework.consumeThreadMin")) {
            config.setConsumeThreadMin(Integer.parseInt(settingsMap.get("orange.eventframework.consumeThreadMin")));
        }
        if(settingsMap.containsKey("orange.eventframework.consumeThreadMax")) {
            config.setConsumeThreadMax(Integer.parseInt(settingsMap.get("orange.eventframework.consumeThreadMax")));
        }
        if(settingsMap.containsKey("orange.eventframework.pullBatchSize")) {
            config.setPullBatchSize(Integer.parseInt(settingsMap.get("orange.eventframework.pullBatchSize")));
        }

        if(settingsMap.containsKey("orange.eventframework.sendMsgTimeout")) {
            config.setSendMsgTimeout(Integer.parseInt(settingsMap.get("orange.eventframework.sendMsgTimeout")));
        }
        if(settingsMap.containsKey("orange.eventframework.retryTimesWhenSendFailed")) {
            config.setRetryTimesWhenSendFailed(Integer.parseInt(settingsMap.get("orange.eventframework.retryTimesWhenSendFailed")));
        }
        if(settingsMap.containsKey("orange.eventframework.retryTimesWhenSendAsyncFailed")) {
            config.setRetryTimesWhenSendAsyncFailed(Integer.parseInt(settingsMap.get("orange.eventframework.retryTimesWhenSendAsyncFailed")));
        }
        if(settingsMap.containsKey("orange.eventframework.retryAnotherBrokerWhenNotStoreOK")) {
            config.setRetryAnotherBrokerWhenNotStoreOK(Boolean.parseBoolean(settingsMap.get("orange.eventframework.retryAnotherBrokerWhenNotStoreOK")));
        }

        if (config.getClientIP() == null) {
            config.setClientIP(NetworkUtil.getLocalIP());
        }

        return config;
    }

    /**
     * 属性的 key 是否匹配
     */
    private boolean isKeyMatch(String key) {
        return StringUtils.startsWith(key, propertiesKeyPrefix);
    }

    /**
     * 从系统属性加载配置
     */
    private void loadFromSystemProperties(Map<String, String> settingsMap) {
        Properties properties = System.getProperties();
        Enumeration<Object> enumeration = properties.keys();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            if (isKeyMatch(key)) {
                continue;
            }
            settingsMap.put(key, properties.getProperty(key));
        }
    }

    /**
     * 从系统环境变量加载配置
     */
    private void loadFromSystemEnv(Map<String, String> settingsMap) {
        Map<String, String> env = System.getenv();
        for (Map.Entry<String, String> entry : env.entrySet()) {
            if (isKeyMatch(entry.getKey())) {
                continue;
            }
            settingsMap.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 加载 {@link #propertiesPath } 文件里的配置
     */
    private void loadFromPropertiesFile(Map<String, String> settingsMap) {
        Properties properties = null;
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propertiesPath);
            if (inputStream != null) {
                properties = new Properties();
                properties.load(inputStream);
                Enumeration<Object> enumeration = properties.keys();
                while (enumeration.hasMoreElements()) {
                    String key = (String) enumeration.nextElement();
                    if (!isKeyMatch(key)) {
                        continue;
                    }
                    settingsMap.put(key, properties.getProperty(key));
                }
            }
        } catch (IOException e) {
            logger.warn(MessageFormatter.format("加载配置文件异常： {}", this.propertiesPath).getMessage(),
                    e);
        }
    }
}
