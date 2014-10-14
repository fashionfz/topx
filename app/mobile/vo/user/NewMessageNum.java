/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-5-5
 */
package mobile.vo.user;

import mobile.vo.MobileVO;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

import ext.msg.vo.MsgCount;

/**
 * 
 * 
 * @ClassName: MessageNum
 * @Description: 新消息数
 * @date 2014-5-5 上午11:03:36
 * @author ShenTeng
 * 
 */
public class NewMessageNum implements MobileVO {

    /** 聊天消息数 */
    private Integer chatMsgNum;
    /** 系统消息数 */
    private Integer systemMsgNum;
    /** 评论消息数 */
    private Integer commentMsgNum;
    /** 回复消息数 */
    private Integer replyMsgNum;
    /** 转账转入消息数 */
    private Integer transferInMsgNum;
    /** 转账转出消息数 */
    private Integer transferOutMsgNum;
    /** 好友邀请消息数 */
    public Integer addFriendsNum;
    /** 同意加好友消息数 */
    public Integer agreeFriendsNum;
    /** 邀请群成员消息数 */
    public Integer invitGroupMemberNum;
    /** 同意群成员邀请消息数 */
    public Integer agreeGroupInvitNum;
    /** 移除群成员消息数 */
    public Integer removeGroupMemberNum;
    /** 退出群通知消息数 */
    public Integer quitGroupNum;
    /** 解散群消息数 */
    public Integer dismissGroupNum;
    /** 申请加入群消息数 */
    public Integer applyGroupNum;
    /** 同意加入群申请消息数 */
    public Integer agreeGroupApplyNum;
    /** 通知海外简历翻译完毕消息数 */
    public Integer resumeFinishNum;

    private NewMessageNum() {
    }

    public static NewMessageNum create(MsgCount count) {
        NewMessageNum newMessageNum = new NewMessageNum();
        newMessageNum.setChatMsgNum(count.getChatMsgNum());
        newMessageNum.setCommentMsgNum(count.getCommentMsgNum());
        newMessageNum.setReplyMsgNum(count.getReplyMsgNum());
        newMessageNum.setSystemMsgNum(count.getSystemMsgNum());
        newMessageNum.setTransferInMsgNum(count.getTransferInMsgNum());
        newMessageNum.setTransferOutMsgNum(count.getTransferOutMsgNum());
        newMessageNum.setAddFriendsNum(count.getAddFriendsNum());
        newMessageNum.setAgreeFriendsNum(count.getAgreeFriendsNum());
        newMessageNum.setInvitGroupMemberNum(count.getInvitGroupMemberNum());
        newMessageNum.setAgreeGroupInvitNum(count.getAgreeGroupInvitNum());
        newMessageNum.setRemoveGroupMemberNum(count.getRemoveGroupMemberNum());
        newMessageNum.setQuitGroupNum(count.getQuitGroupNum());
        newMessageNum.setDismissGroupNum(count.getDismissGroupNum());
        newMessageNum.setApplyGroupNum(count.getApplyGroupNum());
        newMessageNum.setAgreeGroupApplyNum(count.getAgreeGroupApplyNum());
        newMessageNum.setResumeFinishNum(count.getResumeFinishNum());

        return newMessageNum;
    }

    @Override
    public JsonNode toJson() {
        return Json.toJson(this);
    }

    public Integer getChatMsgNum() {
        return chatMsgNum;
    }

    public void setChatMsgNum(Integer chatMsgNum) {
        this.chatMsgNum = chatMsgNum;
    }

    public Integer getSystemMsgNum() {
        return systemMsgNum;
    }

    public void setSystemMsgNum(Integer systemMsgNum) {
        this.systemMsgNum = systemMsgNum;
    }

    public Integer getCommentMsgNum() {
        return commentMsgNum;
    }

    public void setCommentMsgNum(Integer commentMsgNum) {
        this.commentMsgNum = commentMsgNum;
    }

    public Integer getReplyMsgNum() {
        return replyMsgNum;
    }

    public void setReplyMsgNum(Integer replyMsgNum) {
        this.replyMsgNum = replyMsgNum;
    }

    public Integer getTransferInMsgNum() {
        return transferInMsgNum;
    }

    public void setTransferInMsgNum(Integer transferInMsgNum) {
        this.transferInMsgNum = transferInMsgNum;
    }

    public Integer getTransferOutMsgNum() {
        return transferOutMsgNum;
    }

    public void setTransferOutMsgNum(Integer transferOutMsgNum) {
        this.transferOutMsgNum = transferOutMsgNum;
    }

    public Integer getAddFriendsNum() {
        return addFriendsNum;
    }

    public void setAddFriendsNum(Integer addFriendsNum) {
        this.addFriendsNum = addFriendsNum;
    }

    public Integer getAgreeFriendsNum() {
        return agreeFriendsNum;
    }

    public void setAgreeFriendsNum(Integer agreeFriendsNum) {
        this.agreeFriendsNum = agreeFriendsNum;
    }

    public Integer getInvitGroupMemberNum() {
        return invitGroupMemberNum;
    }

    public void setInvitGroupMemberNum(Integer invitGroupMemberNum) {
        this.invitGroupMemberNum = invitGroupMemberNum;
    }

    public Integer getAgreeGroupInvitNum() {
        return agreeGroupInvitNum;
    }

    public void setAgreeGroupInvitNum(Integer agreeGroupInvitNum) {
        this.agreeGroupInvitNum = agreeGroupInvitNum;
    }

    public Integer getRemoveGroupMemberNum() {
        return removeGroupMemberNum;
    }

    public void setRemoveGroupMemberNum(Integer removeGroupMemberNum) {
        this.removeGroupMemberNum = removeGroupMemberNum;
    }

    public Integer getQuitGroupNum() {
        return quitGroupNum;
    }

    public void setQuitGroupNum(Integer quitGroupNum) {
        this.quitGroupNum = quitGroupNum;
    }

    public Integer getDismissGroupNum() {
        return dismissGroupNum;
    }

    public void setDismissGroupNum(Integer dismissGroupNum) {
        this.dismissGroupNum = dismissGroupNum;
    }

    public Integer getApplyGroupNum() {
        return applyGroupNum;
    }

    public void setApplyGroupNum(Integer applyGroupNum) {
        this.applyGroupNum = applyGroupNum;
    }

    public Integer getAgreeGroupApplyNum() {
        return agreeGroupApplyNum;
    }

    public void setAgreeGroupApplyNum(Integer agreeGroupApplyNum) {
        this.agreeGroupApplyNum = agreeGroupApplyNum;
    }

    public Integer getResumeFinishNum() {
        return resumeFinishNum;
    }

    public void setResumeFinishNum(Integer resumeFinishNum) {
        this.resumeFinishNum = resumeFinishNum;
    }

}
