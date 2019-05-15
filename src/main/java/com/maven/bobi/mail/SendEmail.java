package com.maven.bobi.mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SendEmail {
	
	private Log log = LogFactory.getLog(SendEmail.class);

	public void sendMail(String str, String time) {
		log.info("邮件发送！！！");
		InputStream instream = ClassLoader
				.getSystemResourceAsStream("mail.properties");
		Properties pro = new Properties();
		try {
			pro.load(instream);
		} catch (IOException e) {
			log.error("邮件配置读取错误：", e);
		}

		// 收件人电子邮箱
		String to = pro.getProperty("to").trim();
		// String to = "425902134@qq.com";

		// 发件人电子邮箱
		final String from = pro.getProperty("from").trim();
		// String from = "1789120310@qq.com";

		// 授权码
		final String key = pro.getProperty("key").trim();
		
		// 指定发送邮件的主机为 smtp.qq.com
		String host = "smtp.qq.com"; // QQ 邮件服务器

		// 获取系统属性
		Properties properties = System.getProperties();

		// 设置邮件服务器
		properties.setProperty("mail.smtp.host", host);

		properties.put("mail.smtp.auth", "true");
		// 获取默认session对象
		Session session = Session.getDefaultInstance(properties,
				new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(from, key); // 发件人邮件用户名、授权码
					}
				});

		try {
			// 创建默认的 MimeMessage 对象
			MimeMessage message = new MimeMessage(session);

			// Set From: 头部头字段
			message.setFrom(new InternetAddress(from));

			// Set To: 头部头字段
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));

			// Set Subject: 头部头字段
			message.setSubject(str);

			// 设置消息体
			message.setText(str + "打卡成功\r\n" + time);

			// 发送消息
			Transport.send(message);
			log.info(str + time + " Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}