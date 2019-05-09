package com.orange.eventframework.config;

import com.orange.eventframework.util.NetworkUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 默认配置加载器
 *
 * @author 小天
 * @date 2019/4/30 15:17
 */
public class DefaultConfigLoader implements ConfigLoader{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

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

        Config config = new Config();
        // 加载配置
        Properties properties = loadProperties();
        if (properties == null || properties.isEmpty()) {
            return config;
        }

        if(properties.containsKey("orange.eventframework.appName")) {
            config.setAppName(properties.getProperty("orange.eventframework.appName"));
            if (StringUtils.isBlank(config.getAppName())) {
                config.setAppName(Config.DEFAULT_APP_NAME);
            }
        }

        config.setDisabled(Boolean.parseBoolean(properties.getProperty("orange.eventframework.disabled")));

        // 消息队列集群地址（必选）
        config.setNameSrvAddr(properties.getProperty("orange.eventframework.nameSrvAddr"));

        //  框架采集使用的信息
        if(properties.containsKey("orange.eventframework.topic")) {
            config.setTopic(properties.getProperty("orange.eventframework.topic"));
        }
        if(properties.containsKey("orange.eventframework.groupId")) {
            config.setGroupId(properties.getProperty("orange.eventframework.groupId"));
        }

        // 默认的生产者标识。如果未指定的话，使用 appName 作为标识
        if(properties.containsKey("orange.eventframework.defaultDataTopic")) {
            config.setDefaultDataTopic(properties.getProperty("orange.eventframework.defaultDataTopic"));
        }

        // 最大重试消费次数
        if(properties.containsKey("orange.eventframework.maxReconsumeTimes")) {
            config.setMaxReconsumeTimes(Integer.parseInt(properties.getProperty("orange.eventframework.maxReconsumeTimes")));
        }

        config.setClientIP(properties.getProperty("orange.eventframework.clientIP", NetworkUtil.getLocalIP()));

        return config;
    }

    private Properties loadProperties() {
        Properties properties = null;
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propertiesPath);
            if (inputStream != null) {
                properties = new Properties();
                properties.load(inputStream);
            }
        } catch (IOException e) {
            logger.warn("加载配置文件异常", e);
        }
        return properties;
    }
}
