package ext.mail;

import java.util.Properties;

import ext.config.ConfigFactory;

/**
 * 
 * @ClassName: MailServerInfo
 * @Description: 邮件服务器配置信息
 * @date 2013-11-22 下午1:48:32
 * @author RenYouchao
 *
 */
public class MailServerInfo {
	
	private static final Properties MAIL_SERVER_INFO = new Properties();
	
	private static final Properties MAIL_SENDER_INFO = new Properties();
	
	static {
		String host = ConfigFactory.getString("mail.smtp.host");
		String port = ConfigFactory.getString("mail.smtp.port");
		String from = ConfigFactory.getString("mail.fromEmail");
		String username = ConfigFactory.getString("mail.username");
		String password = ConfigFactory.getString("mail.password");
		MAIL_SERVER_INFO.put("mail.smtp.host", host);
		MAIL_SERVER_INFO.put("mail.smtp.port", port);
		MAIL_SENDER_INFO.put("from", from);
		MAIL_SENDER_INFO.put("username", username);
		MAIL_SENDER_INFO.put("password", password);
	}

	public static Properties getMailServerInfoProperties() {
		return MAIL_SERVER_INFO;
	}
	
	public static Properties getMailSenderInfoProperties() {
		return MAIL_SENDER_INFO;
	}
	
}
