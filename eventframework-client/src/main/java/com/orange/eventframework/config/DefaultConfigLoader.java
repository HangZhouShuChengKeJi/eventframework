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
        config.setAppName(properties.getProperty("orange.eventframework.appName"));
        if (StringUtils.equals(config.getAppName(), "default")) {
            logger.warn("正在使用默认的应用程序名称： “{}”，请注意修改", config.getAppName());
        }
        config.setDisabled(Boolean.parseBoolean(properties.getProperty("orange.eventframework.disabled")));

        //  框架采集使用的信息
        config.setTopic(properties.getProperty("orange.eventframework.topic"));
        config.setGroupId(properties.getProperty("orange.eventframework.groupId"));
        config.setNameSrvAddr(properties.getProperty("orange.eventframework.nameSrvAddr"));
        config.setDefaultDataTopic(properties.getProperty("orange.eventframework.defaultDataTopic"));


        // 默认的生产者标识。如果未指定的话，使用 appName 作为标识
        config.setDefaultDataTopic(properties.getProperty("orange.eventframework.defaultProducerCode", config.getAppName()));

        // 最大重试消费次数
        config.setMaxReconsumeTimes(Integer.parseInt(properties.getProperty("orange.eventframework.maxReconsumeTimes", "3")));

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
