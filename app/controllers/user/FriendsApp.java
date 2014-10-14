/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package controllers.user;

import models.Friends;
import models.User;
import models.service.FriendsService;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Result;
import views.html.usercenter.at;
import vo.ExpertDetail;
import vo.page.Page;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;
import ext.msg.model.service.MessageService;

/**
 * 
 *
 *
 * @ClassName: FriendsApp
 * @Description: 好友controller
 * @date 2014-07-03 上午10:46:56
 * @author zhiqinag.zhou
 *
 */
public class FriendsApp extends BaseApp {

    @Transactional
    public static Result list() {
        return ok(at.render());
    }
    
    /**
	 * 添加好友
	 * @return
	 */
	@Transactional
	public static Result addFriend() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String friendId = requestData.get("friendId");
		String messageId = requestData.get("messageId");
		if (StringUtils.isBlank(friendId)) {
			return ok("{\"status\":\"0\",\"error\":\"参数friendId不能为空\"}");
		}
		if (StringUtils.isBlank(messageId)) {
			return ok("{\"status\":\"0\",\"error\":\"参数messageId不能为空\"}");
		}
		User me = User.getFromSession(session());
		User friend = User.findById(Long.parseLong(friendId));
		if (friend == null) {
			return ok("{\"status\":\"0\",\"error\":\"要添加的好友不存在。\"}");
		}
		Boolean flag = FriendsService.addFriend(me, friend); //添加对方进自己的好友
		Boolean flag2 = FriendsService.addFriend(friend, me); //添加自己进对方的好友
		ObjectNodeResult result = null;
		if (flag && flag2) {
			result = new ObjectNodeResult(ObjectNodeResult.STATUS_SUCCESS);
		} else {
			result = new ObjectNodeResult(ObjectNodeResult.STATUS_FAILED);
		}
		MessageService.pushMsgAgreeFriends(me, friend);
		MessageService.handlerMessage(Long.parseLong(messageId)); // 处理消息为用户已处理
		return ok(result.getObjectNode());
	}
	
	/**
	 * 删除好友
	 * @param friendId
	 * @param isBothDeleted --> true：两边都删除，false：只删除自己这边
	 * @return
	 */
	@Transactional
	public static Result deleteFriend(Long friendId,Boolean isBothDeleted) {
		if (friendId == null) {
			return ok("{\"status\":\"0\",\"error\":\"参数friendId不能为空。\"}");
		}
		if (isBothDeleted == null) {
			isBothDeleted = Boolean.TRUE;
		}
		User me = User.getFromSession(session());
		User friend = User.findById(friendId);
		if (friend == null) {
			return ok("{\"status\":\"0\",\"error\":\"要删除的好友不存在。\"}");
		}
		ObjectNodeResult result = null;
		if (isBothDeleted) {
			Boolean flag = FriendsService.deleteFriend(me, friend); // 将对方从自己的好友圈中删除
			Boolean flag2 = FriendsService.deleteFriend(friend, me); // 将自己从对方的好友圈中删除
			if (flag && flag2) {
				result = new ObjectNodeResult(ObjectNodeResult.STATUS_SUCCESS);
			} else {
				result = new ObjectNodeResult(ObjectNodeResult.STATUS_FAILED);
			}
		} else {
			Boolean flag = FriendsService.deleteFriend(me, friend); // 将对方从自己的好友圈中删除
			if (flag) {
				result = new ObjectNodeResult(ObjectNodeResult.STATUS_SUCCESS);
			} else {
				result = new ObjectNodeResult(ObjectNodeResult.STATUS_FAILED);
			}
		}
		
		return ok(result.getObjectNode());
	}
	
	/**
	 * 获取 个人中心 - 圈中的好友的Page数据
	 * @return
	 */
	@Transactional
	public static Result queryFriends() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String pageStr = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "10" : requestData.get("pageSize");
		User currentUser = User.getFromSession(session());
		
		Page<ExpertDetail> pageFriends = null;
		String searchText = requestData.get("searchText");
		Integer page = Integer.parseInt(pageStr);
		if (page != null && page < 0) {
			page = 0;
		}
		if (StringUtils.isNotBlank(searchText)) {
			pageFriends = FriendsService.getFriendPage(page, Integer.parseInt(pageSize), currentUser, searchText.trim());
		} else {
			pageFriends = FriendsService.getFriendPage(page, Integer.parseInt(pageSize), currentUser, "");
		}
		
		if (Logger.isDebugEnabled()) {
			Logger.debug("调用queryFriends()方法返回给个人中心-圈中好友的json   ----> " + play.libs.Json.toJson(pageFriends));
		}
		return ok(play.libs.Json.toJson(pageFriends));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	@Transactional
	public static Result inviteFriends() {
		ObjectNodeResult result = new ObjectNodeResult();
		JsonNode json = request().body().asJson();
		User sender = User.getFromSession(session());
		Long recevierUserId = json.findPath("recevierUserId").asLong();
		String content = json.findPath("content").asText();
		User recevier = User.findById(recevierUserId);
		if (sender != null && recevier != null) {
			boolean isFriend = Friends.isFriend(sender.getId(), recevier.getId());
			if (isFriend) {
				return ok("{\"status\":\"0\",\"error\":\"您们已经是好友关系。\"}");
			}
		}
		
		MessageService.pushMsgFriends(sender, recevier, content);
		return ok(result.getObjectNode());
	}
	
    
}
