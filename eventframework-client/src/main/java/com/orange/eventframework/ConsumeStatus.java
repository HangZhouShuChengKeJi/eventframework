package com.orange.eventframework;

/**
 * @author 小天
 * @date 2019/3/26 10:58
 */
public enum ConsumeStatus {
    /**
     * 消费成功
     */
    CONSUME_SUCCESS,
    /**
     * 消费失败，稍后重试
     */
    RECONSUME_LATER;
}
