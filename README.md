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
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- 注册事件框架监听 spring 事件 -->
    <bean class="com.orange.eventframework.DefaultSpringEventListener" />

    <!-- 配置消费异常报警 -->
    <bean class="com.orange.eventframework.monitor.MailExceptionMonitorHandler">
        <property name="mailFrom" value="${ef.monitor.mail.from}"/>
        <property name="password" value="${ef.monitor.mail.password}"/>
        <property name="mailTo" value="${ef.monitor.mail.to}"/>
        <property name="smtpHost" value="${ef.monitor.mail.smtp.host}"/>
        <property name="smtpPort" value="${ef.monitor.mail.smtp.port}"/>
        <property name="smtpSocketFactoryPort" value="${ef.monitor.mail.smtp.socketFactoryPort}"/>
        <property name="validate" value="true"/>
    </bean>
</beans>
```

# 配置参数说明
支持以下三种方式配置参数，优先级高的配置会覆盖优先级低的配置：
+ `resources` 目录下的 `orange.eventframework.properties` 文件，优先级最低。
+ 环境变量，优先级第二。
+ 系统属性，优先级最高。（通过 `-D` 参数指定）

配置内容及默认值如下：
```ini

# 应用名称（必填项）
orange.eventframework.appName=default

# 消息队列服务地址（必填项）
orange.eventframework.nameSrvAddr=localhost:9876

# 是否禁用事件框架（必填项）
orange.eventframework.disabled=true

# 业务数据默认生产者code，默认取 ${orange.eventframework.appName}
# orange.eventframework.defaultProducerCode=ef_default_producer

#### 以下参数，保持默认配置，请勿随意改动 #####

# 事件信息采集器组Id
orange.eventframework.groupId=event_framework
# 事件信息上传的 topic
orange.eventframework.topic=ef_event
# 业务数据默认 topic
orange.eventframework.defaultDataTopic=ef_data

#### RocketMQ 客户端消费者配置 #####

# 最大消费次数（根据需要调整）
orange.eventframework.maxReconsumeTimes=3
# 消费线程数最小值
orange.eventframework.consumeThreadMin=1
# 消费线程数最大值
orange.eventframework.consumeThreadMax=5
# 最大拉取数量
orange.eventframework.pullBatchSize=10


#### RocketMQ 客户端生产者配置 #####

# 消息发送超时时间，单位：毫秒
orange.eventframework.sendMsgTimeout=3000
# 同步模式下，消息发送失败重试次数
orange.eventframework.retryTimesWhenSendFailed=2
# 异步模式下，消息发送失败重试次数
orange.eventframework.retryTimesWhenSendAsyncFailed=2
# 内部发送失败时，重试另一个 broker
orange.eventframework.retryAnotherBrokerWhenNotStoreOK=false

```

## 关于宿主机IP
**宿主机IP** 属性用于向 RocketMQ 汇报本机IP。首先取 `ef.host.ip` 属性作为宿主机IP；如果取不到，则去取 `EF_HOST_IP` 环境变量；否则会读取第一块网卡的IP作为宿主机IP。

通过 `ef.host.ip` 系统属性指定宿主机IP：
```bash
java -Def.host.ip=192.168.1.100 -jar demo.jar
```

通过 `EF_HOST_IP` 环境变量指定宿主机IP：
```bash
export EF_HOST_IP=192.168.1.100
```

# 版权
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) [杭州数橙科技有限公司](https://github.com/HangZhouShuChengKeJi)

