package com.orange.eventframework.mail;

import org.apache.commons.codec.binary.Base64;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * @author maomao
 * @date 2019/5/22
 */
public class SimpleMailSender {
	/**
	 * 以文本格式发送邮件
	 * 
	 * @param mailInfo 待发送的邮件的信息
	 * @throws Exception
	 */
	public boolean sendTextMail(MailSenderInfo mailInfo) throws Exception {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			// 如果需要身份认证，则创建一个密码验证器
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getInstance(pro, authenticator);
		// 根据session创建一个邮件消息
		Message mailMessage = new MimeMessage(sendMailSession);
		// 创建邮件发送者地址
		Address from = new InternetAddress(mailInfo.getFromAddress());
		// 设置邮件消息的发送者
		mailMessage.setFrom(from);
		// 创建邮件的接收者地址，并设置到邮件消息中
		Address[] to = new InternetAddress[mailInfo.getToAddress().length];
		for (int i = 0; i < to.length; i++) {
			to[i] = new InternetAddress(mailInfo.getToAddress()[i]);
		}
		mailMessage.setRecipients(Message.RecipientType.TO, to);
		// 设置邮件消息的主题
		mailMessage.setSubject(mailInfo.getSubject());
		// 设置邮件消息发送的时间
		mailMessage.setSentDate(new Date());
		// 设置邮件消息的主要内容
		String mailContent = mailInfo.getContent();
		mailMessage.setText(mailContent);
		// 发送邮件
		Transport.send(mailMessage);
		return true;
	}

	/**
	 * 以HTML格式发送邮件
	 * 
	 * @param mailInfo 待发送的邮件信息
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public boolean sendHtmlMail(MailSenderInfo mailInfo, Map<String,String> files) throws Exception {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getInstance(pro, authenticator);
		// 根据session创建一个邮件消息
		Message mailMessage = new MimeMessage(sendMailSession);
		// 创建邮件发送者地址
		Address from = new InternetAddress(mailInfo.getFromAddress(), mailInfo.getSenderName());
		// 设置邮件消息的发送者
		mailMessage.setFrom(from);
		// 创建邮件的接收者地址，并设置到邮件消息中
		Address[] to = new InternetAddress[mailInfo.getToAddress().length];
		for (int i = 0; i < to.length; i++) {
			to[i] = new InternetAddress(mailInfo.getToAddress()[i]);
		}
		// Message.RecipientType.TO属性表示接收者的类型为TO
		mailMessage.setRecipients(Message.RecipientType.TO, to);
		// 设置邮件消息的主题
		mailMessage.setSubject(mailInfo.getSubject());
		// 设置邮件消息发送的时间
		mailMessage.setSentDate(new Date());
		// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
		Multipart mainPart = new MimeMultipart();
		// 创建一个包含HTML内容的MimeBodyPart
		BodyPart html = new MimeBodyPart();

		if (files != null && files.size() > 0) {
			/* 添加附件 */
			for (Map.Entry<String,String> e : files.entrySet()) {
				MimeBodyPart fileBody = new MimeBodyPart();
				DataSource source = new FileDataSource(e.getValue());
				fileBody.setDataHandler(new DataHandler(source));
				// 附件中文显示编码
				fileBody.setFileName("=?UTF-8?B?" + Base64.encodeBase64String(e.getKey().getBytes()) + "?=");
				mainPart.addBodyPart(fileBody);
			}
		}

		// 设置HTML内容
		html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
		mainPart.addBodyPart(html);
		// 将MiniMultipart对象设置为邮件内容
		mailMessage.setContent(mainPart);
		// 发送邮件
		Transport.send(mailMessage);
		return true;
	}
}
