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
public class Consult {
	
	
	
	public Consult(){
		
	}

	public Consult(String userId, String userName, String email,
			String eUserId,String expertId,String expertName,String merchandiseId) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		this.eUserId = eUserId;
		this.expertId = expertId;
		this.expertName = expertName;
		this.merchandiseId = merchandiseId;
	}

	private String status;
	
	private String msgtype = "consult";
	
	private String userId;
	
	private String userName;
	
	private String email;
	
	private String eUserId;
	
	private String expertName;
	
	private String expertId;
	
	private String merchandiseId;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public String getExpertName() {
		return expertName;
	}

	public void setExpertName(String expertName) {
		this.expertName = expertName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String geteUserId() {
		return eUserId;
	}

	public void seteUserId(String eUserId) {
		this.eUserId = eUserId;
	}

	public String getExpertId() {
		return expertId;
	}

	public void setExpertId(String expertId) {
		this.expertId = expertId;
	}

	public String getMerchandiseId() {
		return merchandiseId;
	}

	public void setMerchandiseId(String merchandiseId) {
		this.merchandiseId = merchandiseId;
	}
}

