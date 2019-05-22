package com.orange.eventframework.monitor;

import com.orange.eventframework.mail.SimpleMailSupport;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author maomao
 * @date 2019/5/22
 */
public class MailExceptionMonitorHandler extends ExceptionMonitorHandler {


    private static final int CORE_POOL_SIZE = 1;
    private static final int MAXIMUM_POOL_SIZE = 10;
    /**
     * 空闲线程存活最大时间
     */
    private static final int KEEP_ALIVE_TIME = 2 * 60;

    private ThreadPoolExecutor poolExecutor;

    private String mailFrom;
    private String password;
    private String mailTo;

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    @Override
    protected void onExceptionMonitorEvent(ExceptionMonitorEvent exceptionMonitorEvent) {
        poolExecutor.submit(() -> {
            if (StringUtils.isBlank(mailFrom) || StringUtils.isBlank(password) || StringUtils.isBlank(mailTo)) {
                return;
            }
            ExceptionMonitorData exceptionMonitorData = (ExceptionMonitorData) exceptionMonitorEvent.getSource();
            SimpleMailSupport.sendExceptionMail(mailFrom, password, mailTo.split(","), "eventframework 事件消费异常",
                    new LinkedList<String>() {
                        {
                            add("nameServer: " + exceptionMonitorData.getNameSrvAddr() + "<br>");
                            add("consumerCode: " + exceptionMonitorData.getConsumerCode() + "<br>");
                            add("topic: " + exceptionMonitorData.getTopic() + "<br>");
                            add("consumeEventCodeSet: " + exceptionMonitorData.getConsumeEventCodeSet().toString() + "<br>");
                        }
                    }, exceptionMonitorData.getException());
        });
    }

    @Override
    public void destroy() throws Exception {
        if (this.poolExecutor != null) {
            if (!this.poolExecutor.isShutdown()) {
                this.poolExecutor.shutdown();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.poolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {

            private int count = 0;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "email-monitor-handler-" + (++count));
            }
        });
        this.poolExecutor.allowCoreThreadTimeOut(true);
    }

    @Override
    public int getOrder() {
        return 100;
    }

}
