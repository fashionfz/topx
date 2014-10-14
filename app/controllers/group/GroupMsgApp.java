/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月22日
 */
package controllers.group;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.base.ObjectNodeResult;
import ext.msg.model.service.MessageService;
import models.Group;
import models.SkillTag;
import models.User;
import models.Group.GroupPriv;
import models.service.ChatService;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import utils.HelomeUtil;
import vo.GroupVO;
import vo.page.Page;

/**
 * 
 * 
 * @ClassName: GroupMsgApp
 * @Description: 消息控制器
 * @date 2013年10月22日 下午3:59:36
 * @author RenYouchao
 * 
 */
public class GroupMsgApp extends Controller {

	
    /**
     * 同意加入群组消息
     * @return
     * @throws IOException 
     */
    @Transactional
    public static Result agreeInvit() throws IOException {
    	User currentUser = User.getFromSession(session());
		ObjectNodeResult result = new ObjectNodeResult();
    	Long groupId = new Long(request().getQueryString("groupId"));   	
    	Long messageId = new Long(request().getQueryString("messageId"));
		Group group = Group.queryGroupById(groupId);
		if (group == null)
			return ok(result.errorkey("group.error.nofound").getObjectNode());
		if (group.getType() != null && group.getType() == Group.Type.NORMAL) { // 普通群组才有系统消息
			User recevierUser = User.findById(group.owner.userId);
			List<User> userList = Group.queryUserListOfGroup(groupId); // 查询群组下面的所有用户
			if (!userList.contains(currentUser)) { // 没有加入的人可以加入
				ChatService.appendMemberToGroup(groupId, currentUser.id);
				MessageService.pushMsgInvitAgree(currentUser, recevierUser, group);
				MessageService.handlerMessage(messageId);
			} else {
				MessageService.handlerMessage(messageId);
				return ok(result.error("你已是该群组的成员.").getObjectNode());
			}
			
		}
    	return ok(result.getObjectNode());
    }
    
    /**
     * 同意加入群主消息
     * @return
     * @throws IOException 
     */
    @Transactional
    public static Result agreeApply() throws IOException {
    	User currentUser = User.getFromSession(session());
		ObjectNodeResult result = new ObjectNodeResult();
    	Long groupId = new Long(request().getQueryString("groupId"));   	
    	Long userId = new Long(request().getQueryString("userId"));
    	Long messageId = new Long(request().getQueryString("messageId"));
		Group group = Group.queryGroupById(groupId);
		if (group == null)
			return ok(result.errorkey("group.error.nofound").getObjectNode());
		User recevierUser = User.findById(userId);
		List<User> userList = Group.queryUserListOfGroup(groupId); // 查询群组下面的所有用户
		if (!userList.contains(recevierUser)) { // 没有加入的人可以加入
			ChatService.appendMemberToGroup(groupId, userId);
			MessageService.pushMsgApplyAgree(currentUser, recevierUser, group);
			MessageService.handlerMessage(messageId);
		}  else {
			MessageService.handlerMessage(messageId);
			return ok(result.error("你已是该群组的成员.").getObjectNode());
		}
    	return ok(result.getObjectNode());
    }
    
    @Transactional
    public static Result reject() {
		ObjectNodeResult result = new ObjectNodeResult();
		Long groupId = new Long(request().getQueryString("groupId"));
		Long userId = new Long(request().getQueryString("userId"));
		Group group = Group.queryGroupById(groupId);
		if (group == null)
			return ok(result.errorkey("group.error.nofound").getObjectNode());
		if (group.getType() != null && group.getType() == Group.Type.NORMAL) { // 普通群组才有系统消息
			User recevierUser = User.findById(group.owner.userId);
			User senderUser = User.findById(userId);
	    	MessageService.pushMsgInvitReject(senderUser, recevierUser, group);
		}
    	return ok(result.getObjectNode());
    }
    
    @Transactional
    public static Result apply() {
		ObjectNodeResult result = new ObjectNodeResult();
		Long groupId = new Long(request().getQueryString("groupId"));
		User currentUser = User.getFromSession(session());
		String content = request().getQueryString("content");
		Group group = Group.queryGroupById(groupId);
		if (group == null)
			return ok(result.errorkey("group.error.nofound").getObjectNode());
		User recevierUser = User.findById(group.owner.userId);
    	MessageService.pushMsgApply(currentUser, recevierUser, group ,content);
    	return ok(result.getObjectNode());
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public static Result invitMembers() throws IOException {
    	User currentUser = User.getFromSession(session());
		ObjectNodeResult result = new ObjectNodeResult();
		JsonNode json = request().body().asJson();
		Long groupId = json.findPath("groupId").asLong();
		Group group = Group.queryGroupById(groupId);
		if (group == null)
			return ok(result.errorkey("group.error.nofound").getObjectNode());
//		String content = json.findPath("content").asText();
		Iterator<JsonNode> userIds = json.findPath("userIds").iterator();
		List<User> userList = Group.queryUserListOfGroup(groupId); // 查询群组下面的所有用户
		while(userIds.hasNext()){
			Long userId = userIds.next().asLong();
			if (group.getType() == Group.Type.MULTICOMMUNICATE) { // 多人会话不需要邀请消息
				ChatService.appendMemberToGroup(groupId, userId);
			} else {
				User recevierUser = User.findById(userId);
				if (!userList.contains(recevierUser)) {
					MessageService.pushMsgInvitMember(currentUser, recevierUser, group);
				}
			}
		}
    	return ok(result.getObjectNode());
    }

}
