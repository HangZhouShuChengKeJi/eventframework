package com.orange.eventframework.console.config;

import com.orange.commons.support.elasticsearch.ESHelper;
import com.orange.eventframework.EventFramework;
import com.orange.eventframework.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;

/**
 * @author 小天
 * @date 2019/4/2 14:44
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public ESHelper esHelper(@Value("${elastic.search.nodes}") String address) throws UnknownHostException {
        return new ESHelper(address, null);
    }

    @Bean("eventFrameworkConfig")
    public Config config() {
        return EventFramework.getInstance().getConfig();
    }
}
