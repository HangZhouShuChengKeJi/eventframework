# eventframework
基于 [RocketMQ](http://rocketmq.apache.org/) 的事件框架

# 依赖
```xml
    <dependency>
        <artifactId>eventframework-client</artifactId>
        <groupId>com.orange.server</groupId>
        <version>${eventframework.version}</version>
    </dependency>
```

# 使用
在 spring 容器中注册监听器：
```xml
    <!-- 注册事件框架监听 spring 事件 -->
    <bean class="com.orange.eventframework.DefaultSpringEventListener" />
```

# 配置参数说明
```properties

# 应用名称（必填项）
orange.eventframework.appName=

# 消息队列服务地址（必填项）
orange.eventframework.nameSrvAddr=localhost:9876

# 是否禁用事件框架（必填项）
orange.eventframework.disabled=false

#### 以下参数，保持默认配置，请勿随意改动 #####

# 事件信息采集器组Id
orange.eventframework.groupId=event_framework
# 事件信息上传的 topic
orange.eventframework.topic=ef_event
# 业务数据默认 topic
orange.eventframework.defaultDataTopic=ef_data

# 最大消费次数（根据需要调整）
orange.eventframework.maxReconsumeTimes=3

```

# 版权
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) [杭州数橙科技有限公司](https://github.com/HangZhouShuChengKeJi)

