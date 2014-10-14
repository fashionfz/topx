package ext.msg.model.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import models.Comment;
import models.Group;
import models.GroupMember;
import models.User;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import vo.msg.MessageJsonComment;
import vo.msg.MessageJsonFriends;
import vo.msg.MessageJsonInvitMember;
import vo.msg.MessageJsonTS;
import ext.MessageCenter.Message.chatMessage.ChatMessageHttpClient;
import ext.MessageCenter.utils.MCMessageUtil;
import ext.msg.model.Message;
import ext.msg.model.Message.MsgType;

public class MessageService {

	private static ALogger LOGGER = Logger.of(MessageService.class);

	public static void pushMsgSys(String consumeOnly, String consumeName, String content) {
		if (StringUtils.isBlank(consumeOnly) || StringUtils.isBlank(content)) {
			Logger.error("{\"status\":\"404\",\"message\":\"参数部分为空或参数不齐全请检查！\"}");
			return;
		}
		MCMessageUtil.pustEachTxtMessage(new Long(consumeOnly), new Long(consumeOnly), consumeName, consumeName, content);
	}

	public static void pushMsgComment(Comment comment) {
		MessageJsonComment msgJson = new MessageJsonComment(comment.id.toString(), comment.content, String.valueOf(comment.level));
		Message message = new Message(null, comment.commentUser.id.toString(), comment.commentUser.getName(),
				comment.toCommentUser.id.toString(), comment.toCommentUser.getName(), play.libs.Json.toJson(msgJson).toString(),
				new java.util.Date(), false, Message.MsgType.COMMENT);
		Message.saveMessage(message);
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(),
				MsgType.COMMENT.ordinal(),
				comment.commentUser.id, comment.toCommentUser.id, 
				comment.commentUser.getName(), comment.toCommentUser.getName(),
				play.libs.Json.toJson(msgJson).toString());
	}

	public static void pushMsgReply(Comment comment) {
		MessageJsonComment msgJson = new MessageJsonComment(comment.parent.id.toString(), comment.content, null);
		Message message = new Message(null, comment.commentUser.id.toString(), comment.commentUser.getName(),
				comment.toCommentUser.id.toString(), comment.toCommentUser.getName(), play.libs.Json.toJson(msgJson).toString(),
				new java.util.Date(), false, Message.MsgType.REPLY);
		Message.saveMessage(message);
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(), 
				MsgType.REPLY.ordinal(),
				comment.commentUser.id, comment.toCommentUser.id, 
				comment.commentUser.getName(), comment.toCommentUser.getName(),
				play.libs.Json.toJson(msgJson).toString());
	}

	public static void pushMsgInvitMember(User senderUser, User recevierUser, Group group) {
		MessageJsonInvitMember mjdm = new MessageJsonInvitMember(group.id, group.groupName, senderUser.id, senderUser.userName,group.headUrl,group.type);
		Message message = new Message(null, senderUser.id.toString(), senderUser.getName(), recevierUser.id.toString(),
				recevierUser.getName(), play.libs.Json.toJson(mjdm).toString(), new java.util.Date(), false,
				Message.MsgType.INVIT_GROUP_MEMBER);
		message.isHandler = false;
		Set<MsgType> msgTypeSet = new HashSet<MsgType>();
		msgTypeSet.add(MsgType.INVIT_GROUP_MEMBER);
		Message.deleteMessageBySenderRevicer(senderUser.id, recevierUser.id, group.id,msgTypeSet);
		Message.saveMessage(message);
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(), 
				MsgType.INVIT_GROUP_MEMBER.ordinal(),
				senderUser.id, recevierUser.id, 
				senderUser.userName, recevierUser.userName,
				play.libs.Json.toJson(mjdm).toString());
	}
	
	/**
	 * @return 发送的UUID
	 */
	public static String pushMsgInvitAgree(User senderUser, User recevierUser, Group group) {
		MessageJsonInvitMember mjdm = new MessageJsonInvitMember(group.id, group.groupName, recevierUser.id, recevierUser.userName,group.headUrl,group.type);
		Message message = new Message(null, senderUser.id.toString(), senderUser.getName(), recevierUser.id.toString(),
				recevierUser.getName(), play.libs.Json.toJson(mjdm).toString(), new java.util.Date(), false,
				Message.MsgType.AGREE_GROUP_INVIT);
		Message.saveMessage(message);
		String messageId = UUID.randomUUID().toString().toUpperCase();
		if (group.getType() == Group.Type.NORMAL && group.getGroupPriv() != Group.GroupPriv.PUBLIC) {
			MCMessageUtil.pushGroupNotice(messageId, 5, group.id, senderUser.id, group.groupName, senderUser.userName,
					senderUser.userName+"加入该群",group.getType() == Group.Type.NORMAL ? 0 : (group.getType() == Group.Type.MULTICOMMUNICATE ? 1 : 2));
		}
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(), 
				MsgType.AGREE_GROUP_INVIT.ordinal(),
				senderUser.id, recevierUser.id, 
				senderUser.userName, recevierUser.userName,
				play.libs.Json.toJson(mjdm).toString());
		// type：消息类型 1（聊天记录），2（通知消息）....
		if (group.getType() == Group.Type.NORMAL && group.getGroupPriv() != Group.GroupPriv.PUBLIC) {
	        String content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + senderUser.userName + "加入该群" + "\",\"messageId\":\"" + messageId + "\"}";
	        try {
				ChatMessageHttpClient.sendChatMessage(senderUser, models.service.ChatService.GROUP_PREFIX + group.getId(), new java.util.Date(), content,
				        ChatMessageHttpClient.ChatType.MANY2MANY, 2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return messageId;
	}
	
	/**
	 * @return 发送的UUID
	 */
	public static String pushMsgApplyAgree(User senderUser, User recevierUser, Group group) {
		MessageJsonInvitMember mjdm = new MessageJsonInvitMember(group.id, group.groupName, recevierUser.id, recevierUser.userName,group.headUrl,group.type);
		Message message = new Message(null, senderUser.id.toString(), senderUser.getName(), recevierUser.id.toString(),
				recevierUser.getName(), play.libs.Json.toJson(mjdm).toString(), new java.util.Date(), false,
				Message.MsgType.AGREE_GROUP_APPLY);
		Message.saveMessage(message);
		String messageId = UUID.randomUUID().toString().toUpperCase();
		MCMessageUtil.pushGroupNotice(messageId, 5, group.id, senderUser.id, group.groupName, senderUser.userName,
				senderUser.userName+"加入该群",group.getType() == Group.Type.NORMAL ? 0 : (group.getType() == Group.Type.MULTICOMMUNICATE ? 1 : 2));
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(), 
				MsgType.AGREE_GROUP_APPLY.ordinal(),
				senderUser.id, recevierUser.id, 
				senderUser.userName, recevierUser.userName,
				play.libs.Json.toJson(mjdm).toString());
		// type：消息类型 1（聊天记录），2（通知消息）....
        String content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + senderUser.userName + "加入该群" + "\",\"messageId\":\"" + messageId + "\"}";
        try {
			ChatMessageHttpClient.sendChatMessage(senderUser, models.service.ChatService.GROUP_PREFIX + group.getId(), new java.util.Date(), content,
			        ChatMessageHttpClient.ChatType.MANY2MANY, 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return messageId;
	}
	
	public static void pushMsgInvitReject(User senderUser, User recevierUser, Group group) {
		MessageJsonInvitMember mjdm = new MessageJsonInvitMember(group.id, group.groupName, recevierUser.id, recevierUser.userName,group.headUrl,group.type);
		Message message = new Message(null, senderUser.id.toString(), senderUser.getName(), recevierUser.id.toString(),
				recevierUser.getName(), play.libs.Json.toJson(mjdm).toString(), new java.util.Date(), false,
				Message.MsgType.REJECT_GROUP_INTVIT);
		Message.saveMessage(message);
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(), 
				MsgType.REJECT_GROUP_INTVIT.ordinal(),
				senderUser.id, recevierUser.id, 
				senderUser.userName, recevierUser.userName,
				play.libs.Json.toJson(mjdm).toString());
	}
	
	public static void pushMsgDismiss(Group group) {
		Set<GroupMember> members = group.groupmembers;

		MCMessageUtil.pushGroupNotice(UUID.randomUUID().toString().toUpperCase(), 9, group.id, group.owner.id, group.groupName, group.owner.userName,
				group.groupName+"群组已解散",group.getType() == Group.Type.NORMAL ? 0 : (group.getType() == Group.Type.MULTICOMMUNICATE ? 1 : 2));
		for (GroupMember gm : members){
		    if (Objects.equals(gm.getUserId(), group.getOwner().getUserId())) {
		        continue;
		    }
		    MessageJsonInvitMember mjdm = new MessageJsonInvitMember(group.id, group.groupName, 
		            gm.getUserId(), gm.userName,group.headUrl,group.type);
		    String content = Json.toJson(mjdm).toString();
		    
			Message message = new Message(null, group.owner.id.toString(), group.owner.userName, gm.getUserId()
			        .toString(),
					gm.userName, content, new java.util.Date(), false,
					Message.MsgType.DISMISS_GROUP);
			Message.saveMessage(message);
			MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(), 
					MsgType.DISMISS_GROUP.ordinal(),
					group.owner.id, gm.getUserId(), 
					group.owner.userName, gm.userName,
					content);
		}
		MCMessageUtil.pushDeleteGroupMessage(group.id);
	}
	
	public static void pushMsgApply(User senderUser, User recevierUser, Group group,String content) {
		MessageJsonInvitMember mjdm = new MessageJsonInvitMember(group.id, group.groupName, recevierUser.id, recevierUser.userName, content,group.headUrl,group.type);
		Message message = new Message(null, senderUser.id.toString(), senderUser.getName(), recevierUser.id.toString(),
				recevierUser.getName(), play.libs.Json.toJson(mjdm).toString(), new java.util.Date(), false,
				Message.MsgType.APPLY_GROUP);
		Set<MsgType> msgTypeSet = new HashSet<MsgType>();
		msgTypeSet.add(MsgType.APPLY_GROUP);
		Message.deleteMessageBySenderRevicer(senderUser.id, recevierUser.id, group.id, msgTypeSet);
		Message.saveMessage(message);
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(), 
				MsgType.APPLY_GROUP.ordinal(),
				senderUser.id, recevierUser.id, 
				senderUser.userName, recevierUser.userName,
				play.libs.Json.toJson(mjdm).toString());
	}
	
	/**
	 * @return 发送的UUID
	 */
	public static String pushMsgQuit(User senderUser, User recevierUser, Group group) {
		MessageJsonInvitMember mjdm = new MessageJsonInvitMember(group.id, group.groupName, recevierUser.id, recevierUser.userName,group.headUrl,group.type);
		Message message = new Message(null, senderUser.id.toString(), senderUser.getName(), recevierUser.id.toString(),
				recevierUser.getName(), play.libs.Json.toJson(mjdm).toString(), new java.util.Date(), false,
				Message.MsgType.QUIT_GROUP);
		Message.saveMessage(message);
		String messageId = UUID.randomUUID().toString().toUpperCase();
		MCMessageUtil.pushGroupNotice(messageId, 7, group.id, senderUser.id, group.groupName, senderUser.userName,
				senderUser.userName+"退出该群",group.getType() == Group.Type.NORMAL ? 0 : (group.getType() == Group.Type.MULTICOMMUNICATE ? 1 : 2));
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(), 
				MsgType.QUIT_GROUP.ordinal(),
				senderUser.id, recevierUser.id,
				senderUser.userName, recevierUser.userName,
				play.libs.Json.toJson(mjdm).toString());
		return messageId;
	}
	
	/**
	 * @return 发送的UUID
	 */
	public static String pushMsgRemove(User senderUser, User recevierUser, Group group) {
		MessageJsonInvitMember mjdm = new MessageJsonInvitMember(group.id, group.groupName, recevierUser.id, recevierUser.userName,group.headUrl,group.type);
		Message message = new Message(null, senderUser.id.toString(), senderUser.getName(), recevierUser.id.toString(),
				recevierUser.getName(), play.libs.Json.toJson(mjdm).toString(), new java.util.Date(), false,
				Message.MsgType.REMOVE_GROUP_MEMBER);
		Message.saveMessage(message);
		String messageId = UUID.randomUUID().toString().toUpperCase();
		// 这里比较特殊，客户端需要知道被踢者
		MCMessageUtil.pushGroupNotice(messageId, 8, group.id, recevierUser.id, group.groupName, recevierUser.userName,
				recevierUser.userName+"被踢出该群",group.getType() == Group.Type.NORMAL ? 0 : (group.getType() == Group.Type.MULTICOMMUNICATE ? 1 : 2));
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(), 
				MsgType.REMOVE_GROUP_MEMBER.ordinal(),
				senderUser.id, recevierUser.id, 
				senderUser.userName, recevierUser.userName,
				play.libs.Json.toJson(mjdm).toString());
		return messageId;
	}
	

	public static void pushMsgFriends(User senderUser, User recevierUser, String content) {
		MessageJsonFriends mjsf = new MessageJsonFriends(content);
		Message message = new Message(null, senderUser.id.toString(), senderUser.getName(), recevierUser.id.toString(),
				recevierUser.getName(), play.libs.Json.toJson(mjsf).toString(), new java.util.Date(), false, Message.MsgType.ADD_FRIENDS);
		message.isHandler = false;
		Set<MsgType> msgTypeSet = new HashSet<MsgType>();
		msgTypeSet.add(MsgType.ADD_FRIENDS);
		Message.deleteMessageBySenderRevicer(senderUser.id, recevierUser.id,msgTypeSet);
		Message.saveMessage(message);
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(), 
				MsgType.ADD_FRIENDS.ordinal(),
				senderUser.id, recevierUser.id, 
				senderUser.userName, recevierUser.userName,
				play.libs.Json.toJson(mjsf).toString());
	}

	public static void pushMsgTransfer(User senderUser, User recevierUser, String amount, int currency) {
		MessageJsonTS msgJsonIn = new MessageJsonTS(amount, String.valueOf(currency));

		MessageJsonTS msgJsonOut = new MessageJsonTS(amount, String.valueOf(currency));

		Message messageout = new Message(null, recevierUser.id.toString(), recevierUser.getName(), senderUser.id.toString(),
				senderUser.getName(), play.libs.Json.toJson(msgJsonOut).toString(), new java.util.Date(), false,
				Message.MsgType.TRANSFER_OUT);

		Message messagein = new Message(null, senderUser.id.toString(), senderUser.getName(), recevierUser.id.toString(),
				recevierUser.getName(), play.libs.Json.toJson(msgJsonIn).toString(), new java.util.Date(), false,
				Message.MsgType.TRANSFER_IN);

		Message.saveMessage(messagein);
		Message.saveMessage(messageout);
		LOGGER.info("message pushts " + senderUser.id + ":" + recevierUser.id + ":" + senderUser.getName() + ":" + recevierUser.getName());
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(), 
				MsgType.TRANSFER_IN.ordinal(),
				senderUser.id,recevierUser.id, 
				senderUser.userName, recevierUser.userName,
				play.libs.Json.toJson(msgJsonIn).toString());
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(), 
				MsgType.TRANSFER_OUT.ordinal(),
				recevierUser.id,senderUser.id, 
				recevierUser.userName,senderUser.userName,
				play.libs.Json.toJson(msgJsonOut).toString());
	}
	
	public static void pushMsgAgreeFriends(User senderUser, User recevierUser){
		Message message = new Message(null, senderUser.id.toString(), senderUser.getName(), recevierUser.id.toString(),
				recevierUser.getName(), null, new java.util.Date(), false,
				Message.MsgType.AGREE_FRIENDS);
	    Set<MsgType> msgTypeSet = new HashSet<MsgType>();
	    msgTypeSet.add(MsgType.ADD_FRIENDS);
		Message.deleteMessageBySenderRevicer(senderUser.id, recevierUser.id,msgTypeSet);
		Message.saveMessage(message);
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(),
				MsgType.AGREE_FRIENDS.ordinal(),
				senderUser.id, recevierUser.id, 
				senderUser.userName, recevierUser.userName,
				"");
	}
	
	public static void pushResumeComplete(User senderUser, User recevierUser){
		Message message = new Message(null, senderUser.id.toString(), senderUser.getName(), recevierUser.id.toString(), recevierUser.getName(), "{\"content\":\"provider/"+senderUser.getId()+".html\"}",
				new java.util.Date(), false, Message.MsgType.RESUME_FINISH);
		Message.saveMessage(message);
		MCMessageUtil.pushRemindMessage(UUID.randomUUID().toString().toUpperCase(),
				MsgType.RESUME_FINISH.ordinal(),
				senderUser.id, recevierUser.id, 
				senderUser.userName, recevierUser.userName,
				"");
	}

	public static void handlerMessage(Long messageId) {
		Message.updateHandler(messageId);
	}

}
