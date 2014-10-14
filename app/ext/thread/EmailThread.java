package ext.thread;

import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import utils.EmailUtil;
import vo.EmailInfo;
import ext.config.ConfigFactory;

public class EmailThread implements Runnable {

	// 邮件服务器
	public static String host = ConfigFactory.getString("mail.smtp.host");
	// 用户名 //发信人
	public static String user = ConfigFactory.getString("mail.username");
	// 用户密码
	public static String pass = ConfigFactory.getString("mail.password");
	// 发信人
	public static String from = ConfigFactory.getString("mail.fromEmail");

	@Override
	public void run() {
		// 发送Email线程
		try {
			//Iterator<EmailInfo> iter = EmailUtil.emailMap.keySet().iterator();
			while (!EmailUtil.getEmailQueue().isEmpty()) {
				EmailInfo emailInfo = EmailUtil.getEmailQueue().poll();
				Properties props = new Properties();
				if (host != null && !host.trim().equals(""))
					props.setProperty("mail.smtp.host", host);// key value
				else
					throw new Exception("没有指定发送邮件服务器");
				Session s = Session.getInstance(props, null);
				MimeMessage msg = new MimeMessage(s);
				msg.setSubject(emailInfo.getSubject(), "UTF-8");// 设置邮件主题
				msg.setSentDate(new Date());// 设置邮件发送时间
				if (from != null)
					msg.addFrom(InternetAddress.parse(from));// 指定发件人
				else
					throw new Exception("没有指定发件人");
				if (emailInfo.getTo() != null)
					msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(emailInfo.getTo()));// 指定收件人
				else
					throw new Exception("没有指定收件人地址");
				if (emailInfo.getCc() != null)
					msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(emailInfo.getCc()));// 指定抄送
				if (emailInfo.getBc() != null)
					msg.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(emailInfo.getBc()));// 指定密送
				Multipart mm = new MimeMultipart();
				if (emailInfo.getBody() != null)
					mm.addBodyPart(emailInfo.getBody());// 设置邮件的附件
				for (int i = 0; i < emailInfo.getAttaches().size(); i++) {
					BodyPart part = (BodyPart) emailInfo.getAttaches().get(i);
					mm.addBodyPart(part);
				}
				msg.setContent(mm);// 设置邮件的内容
				msg.saveChanges();// 保存所有改变
				Transport transport = s.getTransport("smtp");// 发送邮件服务器（SMTP）
				transport.connect(host, user, pass);// 访问邮件服务器
				transport.sendMessage(msg, msg.getAllRecipients());// 发送信息
				transport.close();
				//iter.remove();
			}
		} catch (Exception e) {
			play.Logger.error("发送邮件出错", e);
		}
	}
}
