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
public class MsgCount implements java.io.Serializable {
    
    /**请描述**/
	private static final long serialVersionUID = 5404754602298515125L;

	private String status;
	
	private String msgtype = "message";
	/**消息的消费者*/
	private String consumeOnly;
	/**系统消息数量 */
	public int systemMsgNum = 0;
	/**评论消息数量*/
	public int commentMsgNum = 0;
	/**回复消息数量*/
	public int replyMsgNum = 0;
	/**转账转入消息数量*/
	public int transferInMsgNum = 0;
	/**转账转出消息数量*/
	public int transferOutMsgNum = 0;
	
	public int addFriendsNum = 0;
	
	public int invitGroupMemberNum = 0;
	
	public int agreeGroupInvitNum = 0;
	
	public int rejectGroupIntvitNum = 0;
	
	public int removeGroupMemberNum = 0;
	
	public int dismissGroupNum = 0;
	
	public int quitGroupNum = 0;
	
	public int applyGroupNum = 0;
	
	public int agreeGroupApplyNum = 0;
	
	public int resumeFinishNum = 0;
	
	public int agreeFriendsNum = 0;
	
	/**
	 * 聊天消息数量 （个人和群组的未读的总和）
	 */
	private int chatMsgNum = 0;
	
	/**
	 * 聊天消息数量json
	 */
	private String chatMsgNumJson;

	public int getSystemMsgNum() {
		return systemMsgNum;
	}

	public void setSystemMsgNum(int systemMsgNum) {
		this.systemMsgNum = systemMsgNum;
	}
	
	public int getCommentMsgNum() {
        return commentMsgNum;
    }

    public void setCommentMsgNum(int commentMsgNum) {
        this.commentMsgNum = commentMsgNum;
    }

    public int getReplyMsgNum() {
        return replyMsgNum;
    }

    public void setReplyMsgNum(int replyMsgNum) {
        this.replyMsgNum = replyMsgNum;
    }

    public int getTransferInMsgNum() {
        return transferInMsgNum;
    }

    public void setTransferInMsgNum(int transferInMsgNum) {
        this.transferInMsgNum = transferInMsgNum;
    }

    public int getTransferOutMsgNum() {
        return transferOutMsgNum;
    }

    public void setTransferOutMsgNum(int transferOutMsgNum) {
        this.transferOutMsgNum = transferOutMsgNum;
    }

    public String getConsumeOnly() {
		return consumeOnly;
	}

	public void setConsumeOnly(String consumeOnly) {
		this.consumeOnly = consumeOnly;
	}

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

	/**
	 * @return the addFriendsNum
	 */
	public int getAddFriendsNum() {
		return addFriendsNum;
	}

	/**
	 * @return the invitGroupMemberNum
	 */
	public int getInvitGroupMemberNum() {
		return invitGroupMemberNum;
	}

	/**
	 * @return the agreeGroupInvitNum
	 */
	public int getAgreeGroupInvitNum() {
		return agreeGroupInvitNum;
	}

	/**
	 * @return the rejectGroupIntvitNum
	 */
	public int getRejectGroupIntvitNum() {
		return rejectGroupIntvitNum;
	}

	/**
	 * @return the removeGroupMemberNum
	 */
	public int getRemoveGroupMemberNum() {
		return removeGroupMemberNum;
	}

	/**
	 * @return the quitGroupNum
	 */
	public int getQuitGroupNum() {
		return quitGroupNum;
	}

	/**
	 * @return the applyGroupNum
	 */
	public int getApplyGroupNum() {
		return applyGroupNum;
	}

	/**
	 * @return the resumeFinishNum
	 */
	public int getResumeFinishNum() {
		return resumeFinishNum;
	}

	/**
	 * @return the agreeFriendsNum
	 */
	public int getAgreeFriendsNum() {
		return agreeFriendsNum;
	}

	/**
	 * @param addFriendsNum the addFriendsNum to set
	 */
	public void setAddFriendsNum(int addFriendsNum) {
		this.addFriendsNum = addFriendsNum;
	}

	/**
	 * @param invitGroupMemberNum the invitGroupMemberNum to set
	 */
	public void setInvitGroupMemberNum(int invitGroupMemberNum) {
		this.invitGroupMemberNum = invitGroupMemberNum;
	}

	/**
	 * @param agreeGroupInvitNum the agreeGroupInvitNum to set
	 */
	public void setAgreeGroupInvitNum(int agreeGroupInvitNum) {
		this.agreeGroupInvitNum = agreeGroupInvitNum;
	}

	/**
	 * @param rejectGroupIntvitNum the rejectGroupIntvitNum to set
	 */
	public void setRejectGroupIntvitNum(int rejectGroupIntvitNum) {
		this.rejectGroupIntvitNum = rejectGroupIntvitNum;
	}

	/**
	 * @param removeGroupMemberNum the removeGroupMemberNum to set
	 */
	public void setRemoveGroupMemberNum(int removeGroupMemberNum) {
		this.removeGroupMemberNum = removeGroupMemberNum;
	}

	/**
	 * @param quitGroupNum the quitGroupNum to set
	 */
	public void setQuitGroupNum(int quitGroupNum) {
		this.quitGroupNum = quitGroupNum;
	}

	/**
	 * @param applyGroupNum the applyGroupNum to set
	 */
	public void setApplyGroupNum(int applyGroupNum) {
		this.applyGroupNum = applyGroupNum;
	}

	public int getDismissGroupNum() {
        return dismissGroupNum;
    }

    public void setDismissGroupNum(int dismissGroupNum) {
        this.dismissGroupNum = dismissGroupNum;
    }

    public int getAgreeGroupApplyNum() {
        return agreeGroupApplyNum;
    }

    public void setAgreeGroupApplyNum(int agreeGroupApplyNum) {
        this.agreeGroupApplyNum = agreeGroupApplyNum;
    }

    /**
	 * @param resumeFinishNum the resumeFinishNum to set
	 */
	public void setResumeFinishNum(int resumeFinishNum) {
		this.resumeFinishNum = resumeFinishNum;
	}

	/**
	 * @param agreeFriendsNum the agreeFriendsNum to set
	 */
	public void setAgreeFriendsNum(int agreeFriendsNum) {
		this.agreeFriendsNum = agreeFriendsNum;
	}

}

