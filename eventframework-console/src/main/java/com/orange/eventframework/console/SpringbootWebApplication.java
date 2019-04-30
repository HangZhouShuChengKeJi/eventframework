package com.orange.eventframework.console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author 小天
 * @date 2019/1/18 15:13
 */
@PropertySource({
        "classpath:/application.properties",
        "classpath:/spring-mvc.properties",
        "classpath:/spring-server.properties",
        "classpath:/spring-freemarker.properties",
        "classpath:/spring-devtools.properties"
})
@SpringBootApplication(exclude = RestClientAutoConfiguration.class)
public class SpringbootWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootWebApplication.class, args);
    }
}

