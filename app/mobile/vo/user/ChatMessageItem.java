/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-4-29
 */
package mobile.vo.user;

import java.text.SimpleDateFormat;

import mobile.vo.MobileVO;

import org.apache.commons.lang3.StringUtils;

import play.libs.Json;
import vo.ChatMessageRecordVO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * @ClassName: ChatMessage
 * @Description: 聊天记录VO
 * @date 2014-4-29 下午7:25:16
 * @author ShenTeng
 * 
 */
public class ChatMessageItem implements MobileVO {

    /**
     * 聊天记录Id
     */
    private String id;

    /**
     * 发送者用户id
     */
    private Long userId;

    /**
     * 发送者用户名
     */
    private String userName;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 群组id
     */
    private Long groupId;

    /**
     * 群组名称
     */
    private String groupName;

    /**
     * 群组类型 normal (普通) translate (翻译) multicommunicate (多人会话)
     */
    private String groupType;

    /**
     * 群组头像
     */
    private String groupAvatar;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息发送时间
     */
    private String msgTime;

    /**
     * 用户头像 静态资源访问地址
     */
    private String avatar;

    /**
     * 消息总数
     */
    private Long msgNum;

    private ChatMessageItem() {
    }

    public static ChatMessageItem create(ChatMessageRecordVO chatVo) {
        ChatMessageItem detail = new ChatMessageItem();

        if (null != chatVo.getGroupId()) {
            detail.setId("chat_G" + chatVo.getGroupId());
        } else {
            detail.setId("chat_" + chatVo.getUserId());
        }

        detail.setUserId(chatVo.getUserId());
        detail.setUserName(chatVo.getUserName());
        detail.setEmail(chatVo.getEmail());
        detail.setGroupId(chatVo.getGroupId());
        detail.setGroupName(chatVo.getGroupName());
        detail.setGroupType(chatVo.getGroupType());
        if ("normal".equals(chatVo.getGroupType())) {
            detail.setGroupAvatar(chatVo.getGroupAvatar());
        }
        if (StringUtils.isNotBlank(chatVo.getContent())) {
            detail.setContent(chatVo.getContent());
        }
        detail.setAvatar(chatVo.getAvatar());
        detail.setMsgTime(chatVo.getMsgTime() == null ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .format(chatVo.getMsgTime()));
        detail.setMsgNum(chatVo.getMsgNum());
        return detail;
    }

    @Override
    public JsonNode toJson() {
        ObjectNode objectNode = (ObjectNode) Json.toJson(this);

        try {
            objectNode.set("content", this.content == null ? null : Json.parse(this.content));
        } catch (Exception e) {
            ObjectNode contentJsonNode = Json.newObject();
            contentJsonNode.put("subType", "text");
            contentJsonNode.put("data", this.content);

            objectNode.set("content", this.content == null ? null : contentJsonNode);
        }

        return objectNode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(String groupAvatar) {
        this.groupAvatar = groupAvatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(Long msgNum) {
        this.msgNum = msgNum;
    }

}
