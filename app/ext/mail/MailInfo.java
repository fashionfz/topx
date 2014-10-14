package ext.mail;

/**
 * 
 *
 * @ClassName: MailInfo
 * @Description: 邮件信息
 * @date 2013-11-22 下午1:47:44
 * @author YangXuelin
 *
 */
public class MailInfo {
	
	/**
	 * 邮件主题
	 */
	private String subject;

	/**
	 * 邮件接收者
	 */
	private String recieveAddress;

	/**
	 * 邮件内容
	 */
	private String content;
	
	public MailInfo() {
		
	}
	
	public MailInfo(String recieveAddress, String content, String subject) {
		this.subject = subject;
		this.recieveAddress = recieveAddress;
		this.content = content;
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

	public void setContent(String content) {
		this.content = content;
	}

	public String getRecieveAddress() {
		return recieveAddress;
	}

	public void setRecieveAddress(String recieveAddress) {
		this.recieveAddress = recieveAddress;
	}

}
