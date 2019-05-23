package com.orange.eventframework.mail;

import java.security.Security;
import java.util.Properties;

/**
 * @author maomao
 * @date 2019/5/22
 */
public class MailSenderInfo {
	// 发送邮件的服务器的IP和端口
	private String		mailServerHost;
	private String		mailServerPort	= "25";
	private String      mailServerSocketFactoryPort = "25";
	// 邮件发送者的地址
	private String		fromAddress;
	// 邮件接收者的地址
	private String[]	toAddress;
	// 登陆邮件发送服务器的用户名和密码
	private String		userName;
	private String		password;
	// 是否需要身份验证
	private boolean		validate		= false;
	// 邮件主题
	private String		subject;
	// 邮件的文本内容
	private String		content;
	// 邮件附件的文件名
	private String[]	attachFileNames;

	/**
	 * 获得邮件会话属性
	 */
	public Properties getProperties() {
		Properties p = new Properties();
		if (fromAddress.toLowerCase().endsWith("qq.com")) {
			p.put("mail.smtp.host", "smtp.qq.com");
		} else if (fromAddress.toLowerCase().endsWith("163.com")) {
			p.put("mail.smtp.host", "smtp.163.com");
		} else if (fromAddress.toLowerCase().endsWith("126.com")) {
			p.put("mail.smtp.host", "smtp.126.com");
		} else if (fromAddress.toLowerCase().endsWith("sina.cn") || fromAddress.toLowerCase().endsWith("sina.com")) {
			p.put("mail.smtp.host", "smtp.sina.com");
		} else if (fromAddress.toLowerCase().endsWith("sohu.com")) {
			p.put("mail.smtp.host", "smtp.sohu.com");
		} else if (fromAddress.toLowerCase().endsWith("tom.com")) {
			p.put("mail.smtp.host", "smtp.tom.com");
		} else {
			p.put("mail.smtp.host", this.mailServerHost);
		}
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		p.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		p.put("mail.smtp.socketFactory.fallback", "false");
		p.put("mail.smtp.port", this.mailServerPort);
		p.put("mail.smtp.socketFactory.port", this.mailServerSocketFactoryPort);
		p.put("mail.smtp.auth", validate ? "true" : "false");
		return p;
	}

	public String getMailServerHost() {
		return mailServerHost;
	}

	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	public String getMailServerPort() {
		return mailServerPort;
	}

	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String[] getAttachFileNames() {
		return attachFileNames;
	}

	public void setAttachFileNames(String[] fileNames) {
		this.attachFileNames = fileNames;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getToAddress() {
		return toAddress;
	}

	public void setToAddress(String[] toAddress) {
		this.toAddress = toAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String textContent) {
		this.content = textContent;
	}

	public String getMailServerSocketFactoryPort() {
		return mailServerSocketFactoryPort;
	}

	public void setMailServerSocketFactoryPort(String mailServerSocketFactoryPort) {
		this.mailServerSocketFactoryPort = mailServerSocketFactoryPort;
	}
}
