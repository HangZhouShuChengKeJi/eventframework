package com.orange.eventframework.mail;

import com.orange.eventframework.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author maomao
 * @date 2019/5/22
 */
public class TestMailSender {

	private static final Logger	log			= LoggerFactory.getLogger(TestMailSender.class);

	// 邮件主题
	private static String		subject		= "nihao";
	// 邮件内容
	private static String		content		= "neirong";
	private static int			fromIdx		= 0;
	private static int			total		= 0;
	private static Object		lock		= new Object();
	private static String		logFileName;
	private static final String	fileBase	= "C:/mailfile";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		logFileName = System.currentTimeMillis() + ".txt";
		File logFile = new File("C:/mailfile/log/" + logFileName);
		logFile.createNewFile();
		// 主题
		subject = FileUtils.getFileContent(fileBase, "subject.txt");
		// 内容
		content = FileUtils.getFileContent(fileBase, "content.txt");
		// 发送邮件文件：每行一个账号+空格+密码
		List<String> mailFromList = FileUtils.getFileLineList(fileBase, "from.txt");
		// 接收邮件文件：每行一个账号
		List<String> mailToList = FileUtils.getFileLineList(fileBase, "to.txt");
		int numOnce = 10;
		List<String> subMailToList = new ArrayList<String>();
		for (String mail : mailToList) {
			if (subMailToList.size() < numOnce) {
				subMailToList.add(mail);
			} else {
				new Sender(mailFromList, subMailToList).run();
				subMailToList = new ArrayList<String>();
				subMailToList.add(mail);
			}
		}
		if (subMailToList.size() > 0) {
			new Sender(mailFromList, subMailToList).run();
		}
		while (true) {
			Thread.sleep(5000);
			if (total >= mailToList.size()) {
				break;
			}
		}

	}

	public static boolean sendMail(String mailFrom, String password, String[] mailTo, String subject, String content,
			Map<String, String> files) {
		try {
			MailSenderInfo mailInfo = new MailSenderInfo();
			mailInfo.setValidate(true);
			mailInfo.setUserName(mailFrom);
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

	public static class Sender extends Thread {

		private List<String>	mailFromList;

		private List<String>	subMailToList;

		public Sender(List<String> mailFromList, List<String> subMailToList) {
			this.mailFromList = mailFromList;
			this.subMailToList = subMailToList;
		}

		@Override
		public void run() {
			for (int i = 0; i < 5; i++) {
				String mailFromStr = mailFromList.get(fromIdx);
				synchronized (this) {
					fromIdx++;
				}
				if (fromIdx >= mailFromList.size()) {
					synchronized (this) {
						fromIdx = 0;
					}
				}
				String[] strs = mailFromStr.split(" ");
				boolean result = sendMail(strs[0], strs[1], subMailToList.toArray(new String[subMailToList.size()]), subject, content,
						null);
				if (result) {
					StringBuilder mailToStr = new StringBuilder();
					for (int j = 0; j < subMailToList.size(); j++) {
						if (mailToStr.length() > 0) {
							mailToStr.append(";");
						}
						mailToStr.append(subMailToList.get(j));
					}
					String logStr = "邮件发送成功！发送邮件：" + strs[0] + "接收邮件：" + mailToStr.toString();
					try {
						FileUtils.writeFile("C:/mailfile/log/", logFileName, logStr + "\r\n", true);
					} catch (IOException e) {
						e.printStackTrace();
					}
					synchronized (lock) {
						total += subMailToList.size();
					}
					break;
				} else {
					try {
						FileUtils.writeFile("C:/mailfile/log/", logFileName, "邮件发送失败,重试！发送邮件：" + strs[0] + "\r\n", true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		public List<String> getMailFromList() {
			return mailFromList;
		}

		public void setMailFromList(List<String> mailFromList) {
			this.mailFromList = mailFromList;
		}

		public List<String> getSubMailToList() {
			return subMailToList;
		}

		public void setSubMailToList(List<String> subMailToList) {
			this.subMailToList = subMailToList;
		}

	}
}
