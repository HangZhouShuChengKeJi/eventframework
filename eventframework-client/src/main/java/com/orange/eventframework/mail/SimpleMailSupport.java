package com.orange.eventframework.mail;

import com.orange.eventframework.util.NetworkUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author maomao
 * @date 2019/5/22
 */
public class SimpleMailSupport {


    /**
     * 发送监控邮件
     *
     * @param subject   邮件主题
     * @param contents  邮件内容
     * @param throwable 异常堆栈
     */
    public static void sendExceptionMail(String mailFrom, String password, String[] mailTo, String subject, Collection<String> contents, Throwable throwable) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("<b>服务器IP：</b>").append(NetworkUtil.getLocalIP()).append("<br>");
            builder.append("<br><b>内容：</b><br>");
            if (contents != null) {
                for (String content : contents) {
                    builder.append(content).append("<br>");
                }
            }
            builder.append("<br><b>异常堆栈：</b><br>");
            if (throwable != null) {
                builder.append(StringUtils.replace(ExceptionUtils.getFullStackTrace(throwable), "\n", "<br>"));
            }
            sendMail(mailFrom, password, mailTo, subject, builder.toString(), null);
        } catch (Throwable ignore) {
        }
    }

    /**
     * 发送邮件
     *
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public static void sendMail(String mailFrom, String password, String[] mailTo, String subject, String content) {
        try {
            sendMail(mailFrom, password, mailTo, subject, content, null);
        } catch (Throwable e) {
            // ignore
        }
    }

    private static void sendMail(String mailFrom, String password, String[] mailTo, String subject, String content, Map<String, String> files) {
        TestMailSender.sendMail(mailFrom, password, mailTo, subject, content, files);
    }

}
