package com.orange.eventframework.mail;

import com.orange.eventframework.util.NetworkUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * @author maomao
 * @date 2019/5/22
 */
public class SimpleMailSupport {

    private static final Logger log = LoggerFactory.getLogger(SimpleMailSupport.class);

    /**
     * 发送监控邮件
     *
     * @param subject   邮件主题
     * @param contents  邮件内容
     * @param throwable 异常堆栈
     */
    public static void sendExceptionMail(String stmpHost, String stmpPort, String stmpSocketFactoryPort, String senderName, boolean validate, String mailFrom, String password, String[] mailTo, String subject, Collection<String> contents, Throwable throwable) {
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
                builder.append(StringUtils.replace(ExceptionUtils.getStackTrace(throwable), "\n", "<br>"));
            }
            sendMail(stmpHost, stmpPort, stmpSocketFactoryPort, senderName, validate, mailFrom, password, mailTo, subject, builder.toString());
        } catch (Throwable ignore) {
        }
    }

    /**
     * 发送邮件
     *
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public static void sendMail(String stmpHost, String stmpPort, String stmpSocketFactoryPort, String senderName, boolean validate, String mailFrom, String password, String[] mailTo, String subject, String content) {
        try {
            sendMail(stmpHost, stmpPort, stmpSocketFactoryPort, senderName, validate, mailFrom, password, mailTo, subject, content, null);
        } catch (Throwable e) {
            // ignore
        }
    }

    private static boolean sendMail(String stmpHost, String stmpPort, String stmpSocketFactoryPort, String senderName, boolean validate, String mailFrom, String password, String[] mailTo, String subject, String content, Map<String, String> files) {
        try {
            MailSenderInfo mailInfo = new MailSenderInfo();
            mailInfo.setMailServerHost(stmpHost);
            mailInfo.setMailServerPort(stmpPort);
            mailInfo.setMailServerSocketFactoryPort(stmpSocketFactoryPort);
            mailInfo.setValidate(validate);
            mailInfo.setUserName(mailFrom);
            mailInfo.setSenderName(senderName);
            mailInfo.setPassword(password);// 您的邮箱密码
            mailInfo.setFromAddress(mailFrom);
            mailInfo.setToAddress(mailTo);
            mailInfo.setSubject(subject);
            mailInfo.setContent(content);
            // 这个类主要来发送邮件
            SimpleMailSender sms = new SimpleMailSender();
            // SimpleMailSender.sendTextMail(mailInfo);// 发送文体格式
            return sms.sendHtmlMail(mailInfo, files);// 发送html格式
        } catch (Exception ex) {
            log.error("", ex);
            return false;
        }
    }

}
