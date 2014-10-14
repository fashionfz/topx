/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年11月18日
 */
package ext.msg.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import play.cache.Cache;
import play.db.jpa.JPA;
import vo.ChatMessageRecordVO;
import vo.ChatMsgUnReadNumVO;
import vo.page.Page;

import com.fasterxml.jackson.databind.JsonNode;

import common.Constants;
import ext.MessageCenter.Message.chatMessage.ChatMessageHttpClient.ChatType;
import ext.MessageCenter.Message.chatMessage.ChatMessageResult;
import ext.msg.vo.ChatMsgCount;
import ext.msg.vo.MsgCount;

/**
 * 
 * 
 * @ClassName: Message
 * @Description: 系统消息处理类
 * @date 2013年11月18日 下午1:52:09
 * @author RenYouchao
 * 
 */
@javax.persistence.Entity
@Table(name = "tb_message")
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	/** 发送者id */
	public String senderOnly;
	/** 发送者 */
	public String senderName;
	/** 接受者id */
	public String consumeOnly;
	/** 接受者 */
	public String consumeName;
	/** 消息内容 */
	@Column(length = 4000)
	public String content;
	/** 消息时间 */
	public Date msgTime;
	/** 已读还是未读 */
	public Boolean isRead;
	/**
	 * 系统消息 or 交易消息
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(length = 1)
	public MsgType msgType;
	
	public Boolean isHandler = false;

	public enum MsgType {
		COMMENT,                 //评论消息
		REPLY,                   //回复消息
		TRANSFER_IN,             //转入消息
		TRANSFER_OUT,            //转出消息
		ADD_FRIENDS,             //添加好友消息
		INVIT_GROUP_MEMBER,      //邀请群成员消息
		AGREE_GROUP_INVIT,       //同意加入群消息
		REJECT_GROUP_INTVIT,     //拒绝加入群消息
		REMOVE_GROUP_MEMBER,     //移除群成员消息
		QUIT_GROUP,              //退出群消息
		APPLY_GROUP,             //申请加入群
		RESUME_FINISH,           //通知海外简历翻译完毕接口
		AGREE_FRIENDS,           //同意加入好友
		DISMISS_GROUP,           //解散群
		AGREE_GROUP_APPLY        //同意加入群消息
	}

	public Message() {
		super();
	}

	public static Page<Message> queryMessageByRead(int page, int pageSize, String consumeOnly, Collection<?> msgTypes, 
	        boolean unReadFirst) {
	    String countQL = " select count(m) from Message m where m.consumeOnly = :consumeOnly ";
	    String contentQL = " from Message m where m.consumeOnly = :consumeOnly ";
	    
	    if (CollectionUtils.isNotEmpty(msgTypes)) {
	        countQL += "  and m.msgType in (:msgTypes) ";
	        contentQL += "  and m.msgType in (:msgTypes)  ";
	    }
	    
	    if (unReadFirst) {
	        contentQL += " order by isRead asc, m.id desc ";
	    } else {
	        contentQL += " order by m.id desc ";
	    }
	    
	    TypedQuery<Long> countQuery = JPA.em().createQuery(countQL, Long.class).setParameter("consumeOnly", consumeOnly);
	    TypedQuery<Message> contentQuery = JPA.em().createQuery(contentQL, Message.class).setParameter("consumeOnly", consumeOnly);
	    
	    if (CollectionUtils.isNotEmpty(msgTypes)) {
	        countQuery.setParameter("msgTypes", msgTypes);
	        contentQuery.setParameter("msgTypes", msgTypes);
	    }
	    
	    contentQuery.setFirstResult(page * pageSize).setMaxResults(pageSize);
	    
		Long total = countQuery.getSingleResult();
		List<Message> data = contentQuery.getResultList();
		Page<Message> pageMsg = new Page<Message>(Constants.SUCESS, total, data);
		return pageMsg;
	}
	
	public static List<Message> queryMessage(String consumeOnly) {
		List<Message> data = JPA.em()
				.createQuery("from Message m where m.consumeOnly = :consumeOnly and isRead = false  order by m.id desc", Message.class)
				.setParameter("consumeOnly", consumeOnly).getResultList();
		
		return data;
	}


	public static Page<ChatMessageRecordVO> queryMessageByRead(int page, int pageSize, String consumeOnly, String msgType, User currentUser)
			throws IOException {
		Long total = 0L;
		StringBuffer totalSB = new StringBuffer(""); // 总记录数
		List<ChatMessageRecordVO> data = null;
		if (msgType.endsWith("no_system")) {
			// data =
			// ext.MessageCenter.Message.chatMessage.ChatMessageResult.getChatMessageVOList(currentUser);
			data = ext.MessageCenter.Message.chatMessage.ChatMessageResult.getChatMessageList(page, pageSize, currentUser, totalSB);
			total = new Long(StringUtils.isEmpty(totalSB.toString()) ? "0" : totalSB.toString());
			// queryChatMessageByCondition(0,10,currentUser,DateType.DAY,45,new
			// Date(),"啊","3",new java.util.ArrayList<Long>(),true,false);
		}
		Page<ChatMessageRecordVO> pageMsg = new Page<ChatMessageRecordVO>(Constants.SUCESS, total, data);

		return pageMsg;
	}
	
	/**
	 * 根据chatType查询聊天记录
	 * @param chatType 聊天类型
	 * @return
	 * @throws IOException 
	 */
	public static Page<ChatMessageRecordVO> queryChatMsg(int page, int pageSize,User currentUser,ChatType chatType) throws IOException{
		Long total = 0L;
		StringBuffer totalSB = new StringBuffer(""); // 总记录数
		List<ChatMessageRecordVO> data = null;
			data = ext.MessageCenter.Message.chatMessage.ChatMessageResult.getChatMessageList(page, pageSize, currentUser, totalSB, chatType);
			total = new Long(StringUtils.isEmpty(totalSB.toString()) ? "0" : totalSB.toString());
		Page<ChatMessageRecordVO> pageMsg = new Page<ChatMessageRecordVO>(Constants.SUCESS, total, data);

		return pageMsg;
	}

	/**
	 * 聊天记录内容根据条件查询
	 */
	public static ext.MessageCenter.Message.chatMessage.page.Page<ChatMessageRecordVO> queryChatMessageByCondition(int page, int pageSize, User currentUser, String keyWords, String contentType, List<Long> userIdList, List<Long> groupIdList, String userName, String groupName, Date startDate, Date endDate, boolean queryAll, ChatType chatType, String binding) throws IOException {
		Long total = 0L;
		List<ChatMessageRecordVO> data = null;
		StringBuffer totalSB = new StringBuffer();
		if (StringUtils.equals(binding, "1")) { // 绑定了的分开调用再合并数据
			data = new ArrayList<ChatMessageRecordVO>();
			// 1、根据关键词进行查询
			totalSB = new StringBuffer();
			List<ChatMessageRecordVO> data1 = ext.MessageCenter.Message.chatMessage.ChatMessageResult.queryChatMsgRecordByCondition(currentUser, keyWords, contentType, new ArrayList<Long>(), new ArrayList<Long>(), startDate, endDate, queryAll, (page*pageSize), pageSize, totalSB, chatType);
			total = new Long(StringUtils.isEmpty(totalSB.toString()) ? "0" : totalSB.toString());
//			// 2、根据userIdList和groupIdList进行查询
//			totalSB = new StringBuffer();
//			Long total2 = 0L;
//			List<ChatMessageRecordVO> data2 = null;
//			if (((chatType == ChatType.ONE2ONE && CollectionUtils.isEmpty(userIdList) && StringUtils.isNotBlank(userName)) || (chatType == ChatType.MANY2MANY && CollectionUtils.isEmpty(groupIdList) && StringUtils.isNotBlank(groupName)))) {
//				data2 = new ArrayList<ChatMessageRecordVO>();
//			} else {
//				data2 = ext.MessageCenter.Message.chatMessage.ChatMessageResult.queryChatMsgRecordByCondition(currentUser, "", contentType, userIdList, groupIdList, startDate, endDate, queryAll, (page*pageSize), pageSize, totalSB, chatType);
//				total2 = new Long(totalSB.toString());
//			}
			data.addAll(data1);
//			data.addAll(data2);
//			total = total1 + total2;
		} else {
			if (((chatType == ChatType.ONE2ONE && CollectionUtils.isEmpty(userIdList) && StringUtils.isNotBlank(userName)) || (chatType == ChatType.MANY2MANY && CollectionUtils.isEmpty(groupIdList) && StringUtils.isNotBlank(groupName)))) {
				data = new ArrayList<ChatMessageRecordVO>();
			} else {
				totalSB = new StringBuffer();
				data = ext.MessageCenter.Message.chatMessage.ChatMessageResult.queryChatMsgRecordByCondition(currentUser, keyWords, contentType, userIdList, groupIdList, startDate, endDate, queryAll, (page*pageSize), pageSize, totalSB, chatType);
				total = new Long(StringUtils.isEmpty(totalSB.toString()) ? "0" : totalSB.toString());
			}
		}

		ext.MessageCenter.Message.chatMessage.page.Page<ChatMessageRecordVO> pageMsg = new ext.MessageCenter.Message.chatMessage.page.Page<ChatMessageRecordVO>(Constants.SUCESS, total, data);
		return pageMsg;
	}

	public static ext.MessageCenter.Message.chatMessage.page.Page<ChatMessageRecordVO> queryMsessageList(User me, User you, Long groupId,
			int rows, java.util.Date queryDate, Boolean containCurrent) {
		List<ChatMessageRecordVO> data = null;
		if (groupId != null && groupId != 0) {
			data = queryGroupMessageList(me, groupId, rows, queryDate, containCurrent);
		} else {
			data = queryOne2OneMsessageList(me, you, rows, queryDate, containCurrent);
		}

		java.util.Date lastQueryDate = null;
		Boolean isEmpty = Boolean.FALSE;
		if (data != null && data.size() > 0) {
			ChatMessageRecordVO vo = data.get(0);
			lastQueryDate = vo.getMsgTime();
		} else {
			isEmpty = Boolean.TRUE;
		}

		int listSize = data.size();
		Long total = new Long(listSize);
		ext.MessageCenter.Message.chatMessage.page.Page<ChatMessageRecordVO> pageMsg = new ext.MessageCenter.Message.chatMessage.page.Page<ChatMessageRecordVO>(
				Constants.SUCESS, total, data, lastQueryDate, isEmpty);
		pageMsg.setLastQueryDate(lastQueryDate);
		return pageMsg;
	}

	/**
	 * 上下文查询
	 */
	public static ext.MessageCenter.Message.chatMessage.page.Page<ChatMessageRecordVO> queryChatMessageContext(User me, User you,
			Long groupId, int preRows, int nextRows, java.util.Date queryDate, Boolean containCurrent) {
		List<ChatMessageRecordVO> data = null;
		Long total = 0L;
		if (groupId != null && groupId != 0) {
			data = queryGroupChatMessageContext(me, groupId, preRows, nextRows, queryDate, containCurrent);
		} else {
			data = queryOne2OneChatMessageContext(me, you, preRows, nextRows, queryDate, containCurrent);
		}
		Boolean isEmpty = Boolean.FALSE;
		if (data != null && data.size() > 0) {
		} else {
			isEmpty = Boolean.TRUE;
		}
		total = new Long(data.size());
		ext.MessageCenter.Message.chatMessage.page.Page<ChatMessageRecordVO> pageMsg = new ext.MessageCenter.Message.chatMessage.page.Page<ChatMessageRecordVO>(
				Constants.SUCESS, total, data, null, isEmpty);
		return pageMsg;
	}

	/**
	 * 人与群组之间上下文查询
	 * 
	 * @return
	 */
	public static List<ChatMessageRecordVO> queryGroupChatMessageContext(User me, Long groupId, int preRows, int nextRows,
			java.util.Date queryDate, Boolean containCurrent) {
		List<ChatMessageRecordVO> data = ext.MessageCenter.Message.chatMessage.ChatMessageResult.queryGroupChatMessageContext(me, groupId,
				preRows, nextRows, queryDate, containCurrent);
		return data;
	}

	/**
	 * 人与人之间上下文查询
	 * 
	 * @return
	 */
	public static List<ChatMessageRecordVO> queryOne2OneChatMessageContext(User me, User you, int preRows, int nextRows,
			java.util.Date queryDate, Boolean containCurrent) {
		List<ChatMessageRecordVO> data = ext.MessageCenter.Message.chatMessage.ChatMessageResult.queryOne2OneChatMessageContext(me, you,
				preRows, nextRows, queryDate, containCurrent);
		return data;
	}

	public static void updateHandler(Long id) {
		Message msg = (Message) JPA.em().find(Message.class, id);
		msg.isHandler = true;
		msg.isRead = true;
		Message.saveMessage(msg);
	}

	/**
	 * 查询当前用户与群组之间的聊天消息
	 * 
	 * @param me
	 * @param you
	 * @param groupId
	 * @param rows
	 * @param queryDate
	 * @param containCurrent
	 * @return
	 */
	public static List<ChatMessageRecordVO> queryGroupMessageList(User me, Long groupId, int rows, java.util.Date queryDate,
			Boolean containCurrent) {
		List<ChatMessageRecordVO> data = ext.MessageCenter.Message.chatMessage.ChatMessageResult.getGroupChatMessageVOList(me, groupId,
				rows, queryDate, containCurrent);
		return data;
	}

	/**
	 * 查询当前用户与指定用户之间的聊天消息
	 * 
	 * @param me
	 * @param you
	 * @param rows
	 * @param queryDate
	 * @param containCurrent
	 * @return
	 */
	public static List<ChatMessageRecordVO> queryOne2OneMsessageList(User me, User you, int rows, java.util.Date queryDate,
			Boolean containCurrent) {
		List<ChatMessageRecordVO> data = ext.MessageCenter.Message.chatMessage.ChatMessageResult.getOne2OneChatMessageVOList(me, you, rows,
				queryDate, containCurrent);
		return data;
	}
	
	
	public static void markReaded(String consumeOnly,List<Long> messageIdList) {
	    if (CollectionUtils.isEmpty(messageIdList)) {
	        return;
	    }
		Long count = (Long) JPA.em().createQuery("select count(m) from Message m where m.isRead = false "
				+ " and m.consumeOnly = :consumeOnly "
				+ " and m.id in (:id) ")
				.setParameter("consumeOnly", consumeOnly)
				.setParameter("id", messageIdList)
				.getSingleResult();
		if (count > 0) {
			Cache.remove(Constants.CACHE_MSG_NEWCOUNT  + consumeOnly);
			JPA.em().createQuery("UPDATE Message m SET m.isRead = true WHERE m.consumeOnly = :consumeOnly and m.id in (:id)")
					.setParameter("consumeOnly", consumeOnly).setParameter("id", messageIdList).executeUpdate();
		}
	}

	public static void markReaded(String consumeOnly) {
		Long count = (Long) JPA.em().createQuery("select count(m) from Message m where m.isRead = false and m.consumeOnly = :consumeOnly")
				.setParameter("consumeOnly", consumeOnly).getSingleResult();
		if (count > 0) {
			Cache.remove(Constants.CACHE_MSG_NEWCOUNT  + consumeOnly);
			JPA.em().createQuery("UPDATE Message m SET m.isRead = true WHERE m.consumeOnly = :consumeOnly")
					.setParameter("consumeOnly", consumeOnly).executeUpdate();
		}
	}

	public static void markReaded(String consumeOnly, Collection<?> msgTypes) {
		Long count = (Long) JPA
				.em()
				.createQuery(
						"select count(m) from Message m where m.isRead = false and m.consumeOnly = :consumeOnly and m.msgType in (:msgTypes)")
				.setParameter("consumeOnly", consumeOnly).setParameter("msgTypes", msgTypes).getSingleResult();
		if (count > 0) {
			Cache.remove(Constants.CACHE_MSG_NEWCOUNT  + consumeOnly);
			JPA.em().createQuery("UPDATE Message m SET m.isRead = true WHERE m.consumeOnly = :consumeOnly and m.msgType in (:msgTypes)")
					.setParameter("msgTypes", msgTypes).setParameter("consumeOnly", consumeOnly).executeUpdate();
		}
	}

	public static void markReadedIds(String consumeOnly, List<Long> msgIdList) {
		if (StringUtils.isBlank(consumeOnly) || CollectionUtils.isEmpty(msgIdList)) {
			return;
		}
		Cache.remove(Constants.CACHE_MSG_NEWCOUNT  + consumeOnly);
		JPA.em().createQuery("UPDATE Message m SET m.isRead = true " + "WHERE m.consumeOnly = :consumeOnly and m.id in (:msgIdList)")
				.setParameter("consumeOnly", consumeOnly).setParameter("msgIdList", msgIdList).executeUpdate();
	}

	public static void saveMessage(Message message) {
		Cache.remove(Constants.CACHE_MSG_NEWCOUNT + message.consumeOnly);
		if (message.id == null) {
			JPA.em().persist(message);
		} else {
			JPA.em().merge(message);
		}
	}
	
	public static boolean deleteMessageBySenderRevicer(Long senderId, Long consumeId, Collection<?> msgTypes) {
		List<Message> resultList = JPA.em()
				.createQuery("from Message m where m.senderOnly = :senderId and m.consumeOnly = :consumeId and m.msgType in (:msgTypes)", Message.class)
				.setParameter("senderId", senderId.toString())
				.setParameter("consumeId", consumeId.toString())
				.setParameter("msgTypes", msgTypes)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE)
				.getResultList();
		if (CollectionUtils.isEmpty(resultList)) {
			return false;
		}
		for (Message message :resultList)
			JPA.em().remove(message);
		return true;
	}
	
	
	public static boolean deleteMessageBySenderRevicer(Long senderId, Long consumeId, Long groupId, Collection<?> msgTypes) {
		List<Message> resultList = JPA.em()
				.createQuery("from Message m where m.senderOnly = :senderId and m.consumeOnly = :consumeId and m.msgType in (:msgTypes)", Message.class)
				.setParameter("senderId", senderId.toString())
				.setParameter("consumeId", consumeId.toString())
				.setParameter("msgTypes", msgTypes)
				.getResultList();
		if (CollectionUtils.isEmpty(resultList)) {
			return false;
		}
		for (Message message :resultList){
			if (StringUtils.isNotBlank(message.content)) {
				JsonNode jn = play.libs.Json.parse(message.content);
				Long cGroupId = jn.findPath("groupId").asLong(0);
				if (groupId.equals(cGroupId) && message.isHandler == false)
					JPA.em().remove(message);
			}
		}
		return true;
	}

	/**
	 * 删除消息
	 * @param id
	 *            消息Id
	 * @param userId
	 *            消息接收者Id
	 * @return true - 删除成功，false - 删除失败，消息不存在
	 */
	public static boolean deleteMessageById(Long id, Long userId) {
		Cache.remove(Constants.CACHE_MSG_NEWCOUNT  + userId);
		List<Message> resultList = JPA.em()
				.createQuery("from Message m " + "where m.id = :id and m.consumeOnly = :userIdStr", Message.class).setParameter("id", id)
				.setParameter("userIdStr", userId.toString()).getResultList();

		if (CollectionUtils.isEmpty(resultList)) {
			return false;
		}

		JPA.em().remove(resultList.get(0));
		return true;
	}

	/**
	 * 根据id批量删除消息
	 */
	public static void deleteMessages(List<Long> s, Long userId) {
		JPA.em().createNativeQuery("delete from tb_message where id in (:ids) and consumeOnly = :userIdStr").setParameter("ids", s)
				.setParameter("userIdStr", userId.toString()).executeUpdate();

	}
	
//	public static MsgCount messageTypeNum(String consumeOnly) {
//		
//	}

	public static MsgCount newsMessageNum(User user) {
		String userId = String.valueOf(user.id);
		String newMsgkey = Constants.CACHE_MSG_NEWCOUNT  + userId;
		MsgCount msgCountVO = (MsgCount) Cache.get(newMsgkey);
		if (msgCountVO == null) {
			msgCountVO = new MsgCount();
			List<Object[]> results = JPA
					.em()
					.createQuery(
							"select m.msgType, count(m) from Message m "
									+ "where m.consumeOnly = :consumeOnly and m.isRead = false group by m.msgType")
					.setParameter("consumeOnly", userId).getResultList();

			Integer systemMsgNum = 0;
			msgCountVO.setConsumeOnly(userId);
			for (Object[] e : results) {
				Integer num = ((Long) e[1]).intValue();
				if (MsgType.COMMENT == e[0]) {
					msgCountVO.setCommentMsgNum(num);
				} else if (MsgType.REPLY == e[0]) {
					msgCountVO.setReplyMsgNum(num);
				} else if (MsgType.TRANSFER_IN == e[0]) {
					msgCountVO.setTransferInMsgNum(num);
				} else if (MsgType.TRANSFER_OUT == e[0]) {
					msgCountVO.setTransferOutMsgNum(num);
				} else if (MsgType.ADD_FRIENDS == e[0]) {
					msgCountVO.setAddFriendsNum(num);
				} else if (MsgType.AGREE_FRIENDS == e[0]) {
					msgCountVO.setAgreeFriendsNum(num);
				} else if (MsgType.APPLY_GROUP == e[0]) {
					msgCountVO.setApplyGroupNum(num);
				} else if (MsgType.AGREE_GROUP_APPLY == e[0]) {
				    msgCountVO.setAgreeGroupApplyNum(num);
                } else if (MsgType.QUIT_GROUP == e[0]) {
					msgCountVO.setQuitGroupNum(num);
				} else if (MsgType.DISMISS_GROUP == e[0]) {
				    msgCountVO.setDismissGroupNum(num);
                } else if (MsgType.REJECT_GROUP_INTVIT == e[0]) {
					msgCountVO.setRejectGroupIntvitNum(num);
				} else if (MsgType.REMOVE_GROUP_MEMBER == e[0]) {
					msgCountVO.setRemoveGroupMemberNum(num);
				} else if (MsgType.RESUME_FINISH == e[0]) {
					msgCountVO.setResumeFinishNum(num);
				} else if (MsgType.INVIT_GROUP_MEMBER == e[0]) {
					msgCountVO.setInvitGroupMemberNum(num);
				} else if (MsgType.AGREE_GROUP_INVIT == e[0]) {
                    msgCountVO.setAgreeGroupInvitNum(num);
                } 
				systemMsgNum += num;
			}

			msgCountVO.setSystemMsgNum(systemMsgNum);
			Cache.set(newMsgkey, msgCountVO, 60 * 60);
		}
//		try {
//			List<ChatMsgUnReadNumVO> cmurnVO = ChatMessageResult.getChatMsgUnReadNumVOList(user);
//			if (CollectionUtils.isNotEmpty(cmurnVO)) {
//				Long totalChatMsgNum = 0L;
//				for (ChatMsgUnReadNumVO vo : cmurnVO) {
//					totalChatMsgNum += vo.getMsgNum();
//				}
//				msgCountVO.setChatMsgNum(totalChatMsgNum.intValue());
//			}
//			msgCountVO.setChatMsgNumJson(play.libs.Json.toJson(cmurnVO).toString());
//		} catch (IOException e) {
//			e.printStackTrace();
//			msgCountVO.setChatMsgNumJson("{\"status\":\"0\",\"error\":\"获取服务端连接超时\"}");
//		}
		return msgCountVO;
	}
	
	/**
	 * 未读聊天消息信息查询
	 * @return
	 */
	public static ChatMsgCount chatMsgUnReadInfo(User user) {
		ChatMsgCount msgCountVO = new ChatMsgCount();
		try {
			List<ChatMsgUnReadNumVO> cmurnVO = ChatMessageResult.getChatMsgUnReadNumVOList(user);
			if (CollectionUtils.isNotEmpty(cmurnVO)) {
				Long totalChatMsgNum = 0L;
				for (ChatMsgUnReadNumVO vo : cmurnVO) {
					totalChatMsgNum += vo.getMsgNum();
				}
				msgCountVO.setChatMsgNum(totalChatMsgNum.intValue());
			}
			msgCountVO.setChatMsgNumJson(play.libs.Json.toJson(cmurnVO).toString());
		} catch (IOException e) {
			e.printStackTrace();
			msgCountVO.setChatMsgNumJson("{\"status\":\"0\",\"error\":\"获取服务端连接超时\"}");
		}
		return msgCountVO;
	}

	public Message(Long id, String senderOnly, String senderName, String consumeOnly, String consumeName, String content, Date msgTime,
			Boolean isRead, MsgType msgType) {
		super();
		this.id = id;
		this.senderOnly = senderOnly;
		this.senderName = senderName;
		this.consumeOnly = consumeOnly;
		this.consumeName = consumeName;
		this.content = content;
		this.msgTime = msgTime;
		this.isRead = isRead;
		this.msgType = msgType;
	}

	/**
	 * 从缓存中获取用户的两个人之间的未读消息
	 * 
	 * @param sendId
	 * @param receiveId
	 * @return
	 */
	public static Long getUnReadMsgNum(Long sendId, Long receiveId) {
		String key = "communicateNum" + String.valueOf(sendId) + "to" + String.valueOf(receiveId);
		// String key ="communicateNum3to6";
		// Long unReadMsgNum = (Long)
		// utils.MemCachedUtil.getInstance().get(key);
		Long unReadMsgNum = (Long) Cache.get(key);
		if (unReadMsgNum != null) {
			return unReadMsgNum;
		}
		return 0L;
	}

	/**
	 * 从缓存中读取用户的未读消息总数
	 * 
	 * @deprecated 不再使用
	 * @param userId
	 *            用户的id
	 * @return
	 */
	public static Long getUnReadMsgNum(Long userId) {
		String key = "communicateTotal" + String.valueOf(userId);
		// Long unReadMsgNum = (Long)
		// utils.MemCachedUtil.getInstance().get(key);
		Long unReadMsgNum = (Long) Cache.get(key);
		if (unReadMsgNum != null) {
			return unReadMsgNum;
		}
		return 0L;
	}

	/**
	 * 个人未读的群消息个数
	 * 
	 * @param userId
	 *            用户id
	 * @param groupCode
	 *            G+群id
	 * @description
	 */
	public static Long getUnReadMsgNum(Long userId, String groupCode) {
		String key = "communicateNum" + groupCode + "to" + String.valueOf(userId);
		Long unReadMsgNum = (Long) Cache.get(key);
		if (unReadMsgNum != null) {
			return unReadMsgNum;
		}
		return 0L;
	}
	
	public static Message queryById(Long messageId) {
	    if (null == messageId) {
	        return null;
	    }
	    
	    List<Message> resultList = JPA.em().createQuery("from Message where id = :messageId", Message.class)
	            .setParameter("messageId", messageId).getResultList();
	    
	    return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
	}
}
