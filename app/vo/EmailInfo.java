package vo;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

public class EmailInfo {

	public static String TEXT = "text/plain;charset=gb2312";
	public static String HTML = "text/html;charset=gb2312";

	private String to;// 收信人,多个以‘，’隔开
	private String cc;// Carbon Copy, 抄送邮件给某人,多个以‘，’隔开
	private String bc;// bcc Blind Carbon Copy,隐蔽副本 隐蔽抄送给某人,多个以‘，’隔开
	private String subject;// 邮件主题
	private BodyPart body;// 邮件内容
	private List attaches;// 邮件附件

	public List getAttaches() {
		return attaches;
	}

	public void setAttaches(List attaches) {
		this.attaches = attaches;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBc() {
		return bc;
	}

	public void setBc(String bc) {
		this.bc = bc;
	}

	public BodyPart getBody() {
		return body;
	}

	public void setBody(BodyPart body) {
		this.body = body;
	}

	public String getTo() {
		return to;
	}

	public void setBody(String content, String contentType) {
		try {
			body = new MimeBodyPart();
			DataHandler dh = new DataHandler(content, contentType);
			body.setDataHandler(dh);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EmailInfo() {
		attaches = new ArrayList();
	}

	public EmailInfo(String to, String subject) {
		this.to = to;
		this.subject = subject;
		attaches = new ArrayList();
	}

	public EmailInfo(String to, String subject, String content) {
		this.to = to;
		this.subject = subject;
		attaches = new ArrayList();
		setBody(content, TEXT);
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setBlindTo(String bc) {
		this.bc = bc;
	}

	public void setCopyTo(String cc) {
		this.cc = cc;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setBodyAsText(String string) {
		setBody(string, TEXT);
	}

	public void setBodyAsHTML(String string) {
		setBody(string, HTML);
	}

	public void setBodyFromFile(String filename) {
		try {
			BodyPart mdp = new MimeBodyPart();
			FileDataSource fds = new FileDataSource(filename);
			DataHandler dh = new DataHandler(fds);
			mdp.setDataHandler(dh);
			attaches.add(mdp);
		} catch (Exception exception) {
		}
	}

	public void setBodyFromUrl(String url) {
		try {
			BodyPart mdp = new MimeBodyPart();
			URLDataSource ur = new URLDataSource(new URL(url));
			DataHandler dh = new DataHandler(ur);
			mdp.setDataHandler(dh);
			attaches.add(mdp);
		} catch (Exception exception) {
		}
	}

	public void addAttachFromString(String string, String showname) {
		try {
			BodyPart mdp = new MimeBodyPart();
			DataHandler dh = new DataHandler(string, TEXT);
			mdp.setFileName(MimeUtility.encodeWord(showname, "gb2312", null));
			mdp.setDataHandler(dh);
			attaches.add(mdp);
		} catch (Exception exception) {
		}
	}

	public void addAttachFromFile(String filename, String showname) {
		try {
			BodyPart mdp = new MimeBodyPart();
			FileDataSource fds = new FileDataSource(filename);
			DataHandler dh = new DataHandler(fds);
			mdp.setFileName(MimeUtility.encodeWord(showname, "gb2312", null));
			mdp.setDataHandler(dh);
			attaches.add(mdp);
		} catch (Exception exception) {
		}
	}

	public void addAttachFromUrl(String url, String showname) {
		try {
			BodyPart mdp = new MimeBodyPart();
			URLDataSource ur = new URLDataSource(new URL(url));
			DataHandler dh = new DataHandler(ur);
			mdp.setFileName(MimeUtility.encodeWord(showname, "gb2312", null));
			mdp.setDataHandler(dh);
			attaches.add(mdp);
		} catch (Exception exception) {
		}
	}

}
