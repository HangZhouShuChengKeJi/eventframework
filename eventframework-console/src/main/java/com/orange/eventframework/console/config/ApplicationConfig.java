package com.orange.eventframework.console.config;

import com.orange.eventframework.EventFramework;
import com.orange.eventframework.config.Config;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 小天
 * @date 2019/4/2 14:44
 */
@Configuration
public class ApplicationConfig {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public RestHighLevelClient restHighLevelClient(@Value("${elastic.search.nodes}") String address) {
        if (StringUtils.isBlank(address)) {
            throw new IllegalArgumentException("elasticsearch 服务器地址配置错误");
        }

        String[] addressArr = StringUtils.split(address, ',');

        List<HttpHost> httpHosts = new LinkedList<>();
        for (String s : addressArr) {
            String[] temp = StringUtils.split(s, ':');
            if (temp == null || temp.length != 2) {
                throw new IllegalArgumentException("elasticsearch 服务器地址配置错误");
            }
            httpHosts.add(new HttpHost(temp[0], Integer.parseInt(temp[1]), "http"));
        }
        RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[]{}));

        // 设置节点选择器
        builder.setNodeSelector(NodeSelector.SKIP_DEDICATED_MASTERS);

        // 设置请求参数
        builder.setRequestConfigCallback(
                requestConfigBuilder -> {
                    // 从连接池获取连接超时时间
                    requestConfigBuilder.setConnectionRequestTimeout(5000);
                    // 建立连接超时时间
                    requestConfigBuilder.setConnectTimeout(5000);
                    // 客户端和服务端两次交互间隔超时时间
                    requestConfigBuilder.setSocketTimeout(5000);
                    return requestConfigBuilder;
                });

        builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultIOReactorConfig(
                IOReactorConfig.custom()
                        // io 线程数
                        .setIoThreadCount(2)
                        .build()));

        // 节点故障通知
        builder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(Node node) {
                logger.error("es 节点故障： {}", node.getHost());
            }
        });

        return new RestHighLevelClient(builder);
    }

    @Bean("eventFrameworkConfig")
    public Config config() {
        return EventFramework.getInstance().getConfig();
    }
}
