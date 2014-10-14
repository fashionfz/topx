/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年11月19日
 */
package ext.msg.vo;

/**
 * @ClassName: ChatMsgCount
 * @Description: 聊天消息的Json类
 */
public class ChatMsgCount implements java.io.Serializable {
    
	private static final long serialVersionUID = 5404754602298515125L;
	
	private String status;

	/**
	 * 聊天消息数量 （个人和群组的未读的总和）
	 */
	private int chatMsgNum = 0;
	
	/**
	 * 聊天消息数量json
	 */
	private String chatMsgNumJson;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the chatMsgNum
	 */
	public int getChatMsgNum() {
		return chatMsgNum;
	}

	/**
	 * @param chatMsgNum the chatMsgNum to set
	 */
	public void setChatMsgNum(int chatMsgNum) {
		this.chatMsgNum = chatMsgNum;
	}

	public String getChatMsgNumJson() {
		return chatMsgNumJson;
	}

	public void setChatMsgNumJson(String chatMsgNumJson) {
		this.chatMsgNumJson = chatMsgNumJson;
	}

}

