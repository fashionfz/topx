/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年7月11日
 */
package mobile.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mobile.service.result.ServiceResult;
import mobile.util.ServiceResults;
import mobile.vo.result.MobilePage;
import mobile.vo.user.User;
import models.Expert;
import models.Friends;
import models.service.FriendsService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import play.Logger;
import play.Logger.ALogger;
import play.mvc.Http.Context;
import vo.page.Page;
import ext.msg.model.Message;
import ext.msg.model.Message.MsgType;
import ext.msg.model.service.MessageService;

/**
 *
 *
 * @ClassName: FriendsService
 * @Description: 好友服务
 * @date 2014年7月11日 下午3:59:36
 * @author ShenTeng
 * 
 */
public class FriendService {

    private static final ALogger LOGGER = Logger.of(FriendService.class);

    /**
     * 当前用户发起好友邀请
     * 
     * @param inviteeUserId 被邀请人用户Id。必须
     * @param content 邀请消息。必须
     * @return
     */
    public static ServiceResult inviteFriend(Long inviteeUserId, String content) {
        models.User me = models.User.getFromSession(Context.current().session());
        models.User invitee = models.User.findById(inviteeUserId);
        if (null == invitee) {
            return ServiceResults.illegalParameters("被邀请人不存在");
        }
        if (Friends.isFriend(me.id, invitee.id)) {
            return ServiceResult.error("286001", "被邀请人已经是好友");
        }

        MessageService.pushMsgFriends(me, invitee, content);

        return ServiceResult.success();
    }

    /**
     * 批量要求好友
     * 
     * @param inviteeUserIdList 被邀请人用户Id集合。必须
     * @param content 邀请消息。必须
     * @return
     */
    public static ServiceResult batchInviteFriend(List<Long> inviteeUserIdList, String content) {
        models.User me = models.User.getFromSession(Context.current().session());

        for (Long inviteeUserId : inviteeUserIdList) {
            models.User invitee = models.User.findById(inviteeUserId);
            MessageService.pushMsgFriends(me, invitee, content);
        }

        return ServiceResult.success();
    }

    /**
     * 同意好友邀请
     * 
     * @param messageId 好友邀请消息Id，必须
     * @return
     */
    public static ServiceResult acceptInvite(Long messageId) {
        models.User me = models.User.getFromSession(Context.current().session());

        Message message = Message.queryById(messageId);
        if (null == message || message.msgType != MsgType.ADD_FRIENDS
                || !Objects.equals(message.consumeOnly, me.id.toString())) {
            return ServiceResult.error("100005", "非法的messageId = " + messageId);
        }

        models.User senderUser = models.User.findById(NumberUtils.toLong(message.senderOnly, -1));
        if (null == senderUser) {
            LOGGER.error("invalid senderId. message.content = " + message.content);
            return ServiceResult.error("100001", "系统错误");
        }

        Boolean flag = FriendsService.addFriend(me, senderUser); // 添加对方进自己的好友
        Boolean flag2 = FriendsService.addFriend(senderUser, me); // 添加自己进对方的好友

        MessageService.pushMsgAgreeFriends(me, senderUser);
        MessageService.handlerMessage(messageId); // 处理消息为用户已处理

        if (flag && flag2) {
            return ServiceResult.success();
        } else {
            return ServiceResult.error("287001", "接受好友邀请失败");
        }
    }

    /**
     * 获取当前用户的好友Page
     * 
     * @param pageIndex 页码，从1开始,必须
     * @param pageSize 每页显示数，必须
     * @param searchText 搜索文字，非必须
     * @param excludeGroupId 排除指定群组Id的群组成员，非必须
     * @return
     */
    public static MobilePage<User> getFriendPage(Integer pageIndex, Integer pageSize, String searchText,
            Long excludeGroupId) {
        models.User me = models.User.getFromSession(Context.current().session());

        Page<Expert> expertPage = Friends.queryExpertByPage(pageIndex - 1, pageSize, me, searchText, excludeGroupId);

        List<User> list = new ArrayList<User>();
        if (CollectionUtils.isNotEmpty(expertPage.getList())) {
            for (Expert e : expertPage.getList()) {
                list.add(User.create(e));
            }
        }

        MobilePage<User> page = new MobilePage<User>(expertPage.getTotalRowCount(), list);
        return page;
    }

    /**
     * 删除当前用户的指定好友
     * 
     * @param friendUserId 好友用户Id。必须
     * @param isBothDeleted true - 两边都删除，false - 只删除自己这边。必须
     * @return
     */
    public static ServiceResult deleteFriend(Long friendUserId, Boolean isBothDeleted) {
        models.User me = models.User.getFromSession(Context.current().session());
        models.User friend = models.User.findById(friendUserId);
        if (friend == null) {
            return ServiceResults.illegalParameters("好友不存在");
        }
        Boolean flag;
        if (isBothDeleted) {
            Boolean flag1 = FriendsService.deleteFriend(me, friend); // 将对方从自己的好友圈中删除
            Boolean flag2 = FriendsService.deleteFriend(friend, me); // 将自己从对方的好友圈中删除
            flag = flag1 && flag2;
        } else {
            Boolean flag1 = FriendsService.deleteFriend(me, friend); // 将对方从自己的好友圈中删除
            flag = flag1;
        }

        if (flag) {
            return ServiceResult.success();
        } else {
            return ServiceResult.error("289001", "删除好友失败");
        }
    }

}
