package ext.msg.vo;

public class Kickoff {
	
	private String msgtype = "kickoff";
	
	private String token;
	
	private Boolean isKick;

	private String status;

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @return the isKick
	 */
	public Boolean getIsKick() {
		return isKick;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @param isKick the isKick to set
	 */
	public void setIsKick(Boolean isKick) {
		this.isKick = isKick;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the msgtype
	 */
	public String getMsgtype() {
		return msgtype;
	}

	/**
	 * @param msgtype the msgtype to set
	 */
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

}
