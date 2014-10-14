package ext.msg.remote;

public class MessageDTO {
	
	private String messageType;
	
	private String jsonObj;
	
	private String ids;
	
	private String token;
	

	public MessageDTO(String messageType, String jsonObj, String ids) {
		super();
		this.messageType = messageType;
		this.jsonObj = jsonObj;
		this.ids = ids;
	}
	
	public MessageDTO(String messageType, String jsonObj, String ids,String token) {
		super();
		this.messageType = messageType;
		this.jsonObj = jsonObj;
		this.ids = ids;
		this.token = token;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getJsonObj() {
		return jsonObj;
	}

	public void setJsonObj(String jsonObj) {
		this.jsonObj = jsonObj;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	
	

}
