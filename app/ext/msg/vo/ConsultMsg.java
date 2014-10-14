/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年11月19日
 */
package ext.msg.vo;


/**
 *
 *
 * @ClassName: MsgCountVO
 * @Description: 消息的Json类
 * @date 2013年11月19日 上午11:43:22
 * @author RenYouchao
 * 
 */
public class ConsultMsg {
	
	private String msgtype = "consult";
	
	/**请描述**/
	private String status;
	/**请描述**/
	private String userId;
	/**请描述**/
	private String userName;
	/**请描述**/
	private String expertUserId;
	/**请描述**/
	private String consultId;
	/**请描述**/
	private boolean clear = false;
	
	
	
	public ConsultMsg(){
		
	}
	
	public ConsultMsg(String userId, String userName,
			String expertUserId, String consultId) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.expertUserId = expertUserId;
		this.consultId = consultId;
	}

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getExpertUserId() {
		return expertUserId;
	}

	public void setExpertUserId(String expertUserId) {
		this.expertUserId = expertUserId;
	}

	public String getConsultId() {
		return consultId;
	}

	public void setConsultId(String consultId) {
		this.consultId = consultId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the clear
	 */
	public boolean isClear() {
		return clear;
	}

	/**
	 * @param clear the clear to set
	 */
	public void setClear(boolean clear) {
		this.clear = clear;
	}
	
}

