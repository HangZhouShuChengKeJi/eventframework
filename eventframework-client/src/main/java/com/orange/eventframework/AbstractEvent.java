package com.orange.eventframework;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * 事件框架抽象事件类
 *
 * @author 小天
 * @date 2019/3/12 11:07
 */
public abstract class AbstractEvent extends ApplicationEvent implements Serializable {

    public AbstractEvent() {
        super("");
    }

    /**
     * 事件标识
     */
    @JSONField(serialize = false, deserialize = false)
    public String getEventCode() {
        return this.getClass().getName();
    }

    /**
     * 是否发布到MQ
     */
    @JSONField(serialize = false, deserialize = false)
    public abstract boolean enablePushToMQ();

    @JSONField(serialize = false, deserialize = false)
    public abstract String key();

    @JSONField(serialize = false, deserialize = false)
    @Override
    public Object getSource() {
        return super.getSource();
    }
}
