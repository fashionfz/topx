/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-5
 */
package mobile.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobile.service.result.ServiceResult;
import mobile.vo.result.MobilePage;
import mobile.vo.user.ChatMessageItem;
import mobile.vo.user.NewMessageNum;
import mobile.vo.user.SystemMessageItem;
import models.User;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import play.mvc.Http.Context;
import utils.HelomeUtil;
import vo.ChatMessageRecordVO;
import vo.page.Page;
import ext.MessageCenter.utils.MCMessageUtil;
import ext.msg.model.Message;
import ext.msg.model.Message.MsgType;
import ext.msg.vo.MsgCount;

/**
 * 
 * 
 * @ClassName: MessageService
 * @Description: 移动端个人消息相关服务
 * @date 2014-6-5 下午2:43:04
 * @author ShenTeng
 * 
 */
public class MessageService {

    /**
     * 标记当前用户的消息为已读
     * 
     * @return
     */
    public static ServiceResult markRead(List<String> msgIds) {
        return markRead(msgIds, User.getFromSession(Context.current().session()).id);
    }

    /**
     * 标记消息为已读
     * 
     * @return
     */
    public static ServiceResult markRead(List<String> msgIds, Long userId) {
        List<Long> systemMsgIdList = new ArrayList<>();
        List<String> chatMsgIdList = new ArrayList<>();

        for (String msgId : msgIds) {
            if (StringUtils.isNotBlank(msgId) && msgId.startsWith("chat_") && msgId.length() > "chat_".length()) {
                chatMsgIdList.add(msgId);
            } else {
                long numId = NumberUtils.toLong(msgId, -1);
                if (numId > 0) {
                    systemMsgIdList.add(numId);
                }
            }
        }

        Message.markReaded(String.valueOf(userId), systemMsgIdList);
        for (String chatMsgId : chatMsgIdList) {
            if (chatMsgId.startsWith("chat_G")) {
                Long groupId = HelomeUtil.toLong(chatMsgId.substring("chat_G".length()), null);
                MCMessageUtil.cleanGroupChatNum(groupId, userId);
            } else {
                Long senderId = HelomeUtil.toLong(chatMsgId.split("_")[1], null);
                if (null != senderId) {
                    MCMessageUtil.resetCommunicateNum(senderId, userId);
                }
            }

        }

        return ServiceResult.success();
    }

    /**
     * 获取当前用户的新消息数量VO
     * 
     * @return
     */
    public static NewMessageNum getNewMessageNum() {
        return getNewMessageNum(User.getFromSession(Context.current().session()));
    }

    /**
     * 获取指定用户的新消息数量VO
     * 
     * @param userId 指定用户Id
     * @return
     */
    public static NewMessageNum getNewMessageNum(User user) {
        MsgCount msgCountVO = Message.newsMessageNum(user);
        return NewMessageNum.create(msgCountVO);
    }

    /**
     * 获取当前用户聊天消息Page
     * 
     * @param pageIndex 页码，从1开始
     * @param pageSize 每页显示数
     * @return
     */
    public static MobilePage<ChatMessageItem> getChatMessagePage(Integer pageIndex, Integer pageSize) {
        User user = User.getFromSession(Context.current().session());

        Page<ChatMessageRecordVO> pageMsg;
        try {
            pageMsg = Message.queryMessageByRead(pageIndex - 1, pageSize, String.valueOf(user.id), "no_system", user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<ChatMessageItem> chatMessageList = new ArrayList<>(pageMsg.getList().size());
        for (ChatMessageRecordVO c : pageMsg.getList()) {
            chatMessageList.add(ChatMessageItem.create(c));
        }

        return new MobilePage<>(pageMsg.getTotalRowCount(), chatMessageList);
    }

    /**
     * 获取当前用户系统消息Page
     * 
     * @param pageIndex 页码，从1开始
     * @param pageSize 每页显示数
     * @param autoRead 是否开启自动设置阅读状态：消息查询一次之后，查询出来的消息阅读状态自动设置为已读。
     * @param msgTypeList 消息类型查询过滤条件。传递null或empty代表所有类型。类型参见{@literal ext.msg.model.Message.MsgType}
     * @param unReadFirst 未读消息排前面
     * @return
     */
    public static MobilePage<SystemMessageItem> getSystemMessagePage(Integer pageIndex, Integer pageSize,
            Boolean autoRead, List<String> msgTypeList, boolean unReadFirst) {

        Set<MsgType> msgTypeSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(msgTypeList)) {
            for (String msgTypeStr : msgTypeList) {
                MsgType msgType = EnumUtils.getEnum(MsgType.class, msgTypeStr);
                if (msgType != null) {
                    msgTypeSet.add(msgType);
                }
            }
        }

        User user = User.getFromSession(Context.current().session());

        Page<Message> pageMsg = Message.queryMessageByRead(pageIndex - 1, pageSize, String.valueOf(user.id),
                msgTypeSet, unReadFirst);

        List<SystemMessageItem> sysMsgList = SystemMessageItem.createList(pageMsg.getList());
        
        List<Long> systemMsgIdList = new ArrayList<>();
        for (Message message : pageMsg.getList()) {
            systemMsgIdList.add(message.id);
        }

        if (autoRead) {
            Message.markReaded(String.valueOf(user.id), systemMsgIdList);
        }

        return new MobilePage<>(pageMsg.getTotalRowCount(), sysMsgList);
    }

    /**
     * 删除消息，只能删除系统消息，不能删除会话记录
     * 
     * @param id 消息Id
     * @return
     */
    public static ServiceResult deleteMessage(Long id) {
        User user = User.getFromSession(Context.current().session());

        boolean deleteResult = Message.deleteMessageById(id, user.id);

        if (!deleteResult) {
            return ServiceResult.error("251001", "消息不存在或已经被删除");
        }

        return ServiceResult.success();
    }

    /**
     * 批量删除消息，只能删除系统消息，不能删除会话记录
     * 
     * @param idList 消息Id集合
     * @return
     */
    public static ServiceResult batchDeleteMessage(List<Long> idList) {
        User user = User.getFromSession(Context.current().session());

        Message.deleteMessages(idList, user.id);

        return ServiceResult.success();
    }

}
