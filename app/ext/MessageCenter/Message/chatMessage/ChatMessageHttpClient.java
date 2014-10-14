package ext.MessageCenter.Message.chatMessage;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Group;
import models.User;
import models.service.ChatService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import play.Logger;
import play.Logger.ALogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.jackjson.JackJsonUtil;
import ext.MessageCenter.utils.JsonUtils;
import ext.config.ConfigFactory;

/**
 * 聊天记录客户端
 * @version 1.6
 */
public class ChatMessageHttpClient {
	
	static ALogger logger = Logger.of(ChatMessageHttpClient.class);
	
	/**
     * 请求超时时间，默认20000ms
     */
	public static final int REQUEST_TIMEOUT = 20 * 1000;
	
	public static final int SOCKET_BUFFER_SIZE = 8192;
	
	public final static String PRDUCT_NAME = "helome";
	
	/**
	 * chatRecord.client.sendUrl
	 */
	public final static String SEND_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "chatmessage/send";
	/**
	 * chatMessage.client.listUrl
	 */
	public final static String LIST_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "chatmessage/list";
	/**
	 * chatmessage/queryPrevList（此接口替换原来的chatmessage/list）
	 */
	public final static String PERV_LIST_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "chatmessage/queryPrevList";
	/**
	 * chatMessage.client.queryLastUrl
	 */
	public final static String QUERY_LAST_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "chatmessage/queryLast";
	/**
	 * chatMessage.client.queryRelationshipUrl
	 */
	public final static String QUERY_RELATION_SHIP_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "chatmessage/queryRelationship";
	/**
	 * chatMessage.client.queryContextUrl
	 */
	public final static String QUERY_CONTEXT_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "chatmessage/queryContext";
	
	/**
	 * relation/group/join 加群
	 */
	public final static String RELATION_GROUP_JOIN_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "relation/group/join";
	
	/**
	 * relation/group/quit 退群
	 */
	public final static String RELATION_GROUP_QUIT_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "relation/group/quit";
	
	/**
	 * chatMessage.client.queryLatest
	 */
	public final static String QUERY_LATEST_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "chatmessage/queryLatest";
	
	/**
	 * chatMessage.client.simpleQuery
	 * @deprecated
	 */
	public final static String SIMPLE_QUERY_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "chatmessage/simpleQuery";
	
	/**
	 * chatMessage.client.standardQuery
	 */
	public final static String STANDARD_QUERY_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "chatmessage/standardQuery";
	
	/**
	 * /chatmessage/seniorQuery
	 */
	public final static String SENIOR_QUERY_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "chatmessage/seniorQuery";
	
	/**
	 * chatmessage/listByPage （此接口替换原来的chatmessage/seniorQuery）
	 */
	public final static String LIST_BYPAGE_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "chatmessage/listByPage";
	
	/**
	 * relation/group/groups 查询群组信息接口
	 */
	public final static String GROUPS_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "relation/group/groups";
	
	/**
	 * relation/group/members 查询群组成员信息接口
	 */
	public final static String MEMBERS_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "relation/group/members";
	/**
	 * relation/group/list 查询群组信息
	 */
	public final static String GROUP_LIST_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "relation/group/list";
	
	/**
	 * relation/group/create 添加群信息（保存群的基本信息）
	 */
	public final static String CREATE_GROUP_URL = ConfigFactory.getString("chatMessage.client.knowledgeUrl") + "relation/group/create";
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	/**
	 * 聊天类型： 一对一 -> ONE2ONE，多对多（群组） -> MANY2MANY
	 */
	public enum ChatType {
		ONE2ONE, MANY2MANY
	}
	
	/**
	 * 高级查询中，时间范围类型
	 */
	public enum DateType {
		DAY, MONTH, YEAR
	}
	
	/**
	 * contentType的类型
	 */
	public enum ChatMsgContentType {
		ZERO, 					// 0 - 备用
		TEXT, 					// 1 - 文本
		FILE, 					// 2 - 文件
		PICTURE, 				// 3 - 图片
		MEDIA,   				// 4 -多媒体
		FIVE,	 				// 5 - 备用
		SIX, 	 				// 6 - 备用
		SEVEN,   				// 7 - 备用
		EIGHT,	 				// 8 - 备用
		NINE,	 				// 9 - 备用
		CREATE_GROUP, 			// 10 - 创建群组
		INVITE_MEMBER,			// 11 - 邀请成员
		JOIN_GROUP,	  			// 12 - 加入群组
		QUIT_GROUP,   			// 13 - 退出群组
		REMOVE_MEMBER, 			// 14 - 移除成员
		DELETE_GROUP,			// 15 - 解散群组
		MODIFY_GROUPNAME, 		// 16 - 修改群组名称
		MODIFY_GROUP_AVATAR, 	// 17 - 修改群组头像
		MODIFY_NAME_AVATAR; 	//18 - 修改群组名称和头像
	}

	/**
	 * 添加群信息（保存群的基本信息）
	 * @param group 群组
	 * @param translator 翻译者
	 */
	public static Boolean createGroup(Group group,User translator){
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("product",PRDUCT_NAME);
		map.put("id", ChatService.GROUP_PREFIX + group.getId());
		map.put("type", group.getType().ordinal());
		map.put("name", group.getGroupName());
		map.put("owner", group.getOwner() == null ? 0 : group.getOwner().getUserId());
		map.put("translator", translator == null ? "" : translator.getId().toString());
		
		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		logger.debug("createGroup in Log:" + group.getId());
		long beginMillis = System.currentTimeMillis();
		Boolean result = writeDataInvoke("group",jsonStr, CREATE_GROUP_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "createGroup", CREATE_GROUP_URL);
		logger.debug("createGroup out Log:" + result);
		return result;
	}
	
	/**
	 * 查询某用户加入的群组
	 * @param user
	 * @param type 类型
	 * @return
	 * @throws IOException 
	 */
	public static String queryGroups(User user,int type) throws IOException {

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("product", PRDUCT_NAME);
		map.put("user", user.getId().toString());
		map.put("type", type);

		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		logger.debug("queryGroups in Log:" + user.getEmail());
		long beginMillis = System.currentTimeMillis();
		String result = queryInvoke(jsonStr, GROUPS_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "queryGroups", GROUPS_URL);
		logger.debug("queryGroups out Log:" + result);
		return result;
	}
	
	/**
	 * 查询某群组中的成员
	 * @param groupId 群组id
	 * @param memberState 成员状态：0（所有成员，包括所有加过此群的成员，不管目前是否还在群里）、1（目前在此群里面）、2（曾经加过群，但目前已退出），默认为1
	 * @return
	 * @throws IOException 
	 */
	public static String queryMembers(Long groupId,int memberState) throws IOException {

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("product", PRDUCT_NAME);
		map.put("group", ChatService.GROUP_PREFIX + groupId);
		map.put("memberStat", memberState);

		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		logger.debug("queryMembers in Log:" + groupId);
		long beginMillis = System.currentTimeMillis();
		String result = queryInvoke(jsonStr, MEMBERS_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "queryMembers", MEMBERS_URL);
		logger.debug("queryMembers out Log:" + result);
		return result;
	}
	
	/**
	 * 查询某群组中的成员
	 * @param groupIdList G + 群组ID（一个或多个）
	 * @param memberState 成员状态：0（所有成员，包括所有加过此群的成员，不管目前是否还在群里）、1（目前在此群里面）、2（曾经加过群，但目前已退出），默认为1
	 * @return
	 * @throws IOException 
	 */
	public static String queryGroups(List<String> groupIdList) throws IOException {

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("product", PRDUCT_NAME);
		map.put("ids", play.libs.Json.toJson(groupIdList));

		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		logger.debug("queryGroups in Log:" + groupIdList.toString());
		long beginMillis = System.currentTimeMillis();
		String result = queryInvoke(jsonStr, GROUP_LIST_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "queryGroups", GROUP_LIST_URL);
		logger.debug("queryGroups out Log:" + result);
		return result;
	}
	
	
	
	/**
	 * @deprecated 已过时 --》知识库系统不使用这个方法
	 * @param fromUser
	 * @param toUser
	 * @param sendTime
	 * @param content
	 * @param chatType
	 * @return
	 */
	public static Boolean sendChatMessage(User fromUser,User toUser,Date sendTime,String content,ChatType chatType){
		Map<String, Object> map = new HashMap<String, Object>(); 
		
		map.put("product",PRDUCT_NAME);
		map.put("type", chatType == ChatType.ONE2ONE ? 1 : 2);
		// from 和 to 改为存用户的id
		map.put("from", fromUser.getId().toString());
		map.put("to", toUser.getId().toString());
		map.put("sendTime", dateFormat.format(sendTime));
		map.put("content", content);
		
		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		return writeDataInvoke("message",jsonStr,SEND_URL);
	}
	
	/**
	 * 
	 * @param fromUser 发送人
	 * @param toUserOrGroup 接收人或群组
	 * @param sendTime 发送时间  格式为yyyy-MM-dd HH:mm:ss.SSS
	 * @param content 消息内容
	 * @param chatType 1（人对人）、2（群组）
	 * @param contentType 消息内容类型
	 * @return
	 * @throws IOException 
	 */
	public static Boolean sendChatMessage(User fromUser,String toUserOrGroup,Date sendTime,String content,ChatType chatType,Integer contentType) throws IOException{
		Map<String, Object> map = new HashMap<String, Object>(); 
		
		map.put("product",PRDUCT_NAME);
		map.put("type", chatType == ChatType.ONE2ONE ? 1 : 2);
		map.put("contentType", contentType); // *** 新版本多了一个contentType 消息内容类型（必填），值为整形
		// from 和 to 改为存用户的id
		map.put("from", fromUser.getId().toString());
		map.put("to", toUserOrGroup);
		map.put("sendTime", dateFormat.format(sendTime));
		map.put("content", content);
		
		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		logger.debug("sendChatMessage in Log:" + fromUser.getEmail());
		long beginMillis = System.currentTimeMillis();
		Boolean result = writeDataInvoke("message",jsonStr,SEND_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "sendChatMessage", SEND_URL);
		logger.debug("sendChatMessage out Log:" + result);
		return result;
	}
	
	/**
	 * 为群组添加一个成员
	 * @param fromUser 群组成员
	 * @param groupCode 群账户
	 * @param joinTime 当前用户加入群组的时间
	 */
	public static Boolean addGroupMember(User fromUser,String groupCode,Date joinTime){
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("product",PRDUCT_NAME);
		map.put("user", fromUser.getId().toString());
		map.put("group", groupCode);
		map.put("joinTime", dateFormat.format(joinTime));
		
		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		logger.debug("addGroupMember in Log:" + fromUser.getEmail());
		long beginMillis = System.currentTimeMillis();
		Boolean result = writeDataInvoke("relation",jsonStr,RELATION_GROUP_JOIN_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "addGroupMember", RELATION_GROUP_JOIN_URL);
		logger.debug("addGroupMember out Log:" + result);
		return result;
	}
	
	/**
	 * 成员退出群组
	 * @param fromUser 群组成员
	 * @param groupCode 群账户
	 * @param joinTime 当前用户加入群组的时间
	 */
	public static Boolean quitGroupMember(User fromUser,String groupCode,Date quitTime){
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("product",PRDUCT_NAME);
		map.put("user", fromUser.getId().toString());
		map.put("group", groupCode);
		map.put("quitTime", dateFormat.format(quitTime));
		
		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		logger.debug("quitGroupMember in Log:" + fromUser.getEmail());
		long beginMillis = System.currentTimeMillis();
		Boolean result = writeDataInvoke("relation",jsonStr,RELATION_GROUP_QUIT_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "quitGroupMember", RELATION_GROUP_QUIT_URL);
		logger.debug("quitGroupMember out Log:" + result);
		return result;
	}
	
	/**
	 * 查询最后的聊天记录信息，包括所有的聊天的人和最后的聊天记录
	 * <br/>
	 * type：1表示只查与人的聊天；2表示只查群组消息；不传表示不限制；默认为不限制
	 * @throws IOException
	 */
	public static String queryLastMsgData(int start, int rows, User user, ChatType chatType) throws IOException{
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("product", PRDUCT_NAME);
		if (chatType != null) {
			map.put("type", chatType == ChatType.ONE2ONE ? 1 : 2);
		}
		map.put("relevantPerson", user.getId().toString());
		map.put("start", start); // start：起始行数的索引，默认为0
		map.put("rows", rows); // rows：返回的最大记录条数，默认为20，<=0表示不限制

		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		logger.debug("queryLastMsgData in Log:" + user.getEmail());
		long beginMillis = System.currentTimeMillis();
		String result = queryInvoke(jsonStr, QUERY_LATEST_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "queryLastMsgData", QUERY_LATEST_URL);
		logger.debug("queryLastMsgData out Log:" + result);
		return result;
	}
	
	public static String listChatMessage(User fromUser,String toUserOrGroup,Date lastSendTime,int rowCount, Boolean containCurrent,ChatType chatType) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("product", PRDUCT_NAME);
		map.put("type", chatType == ChatType.ONE2ONE ? 1 : 2);
		map.put("relevantPerson",fromUser.getId().toString());
		map.put("preRelevantPerson",toUserOrGroup);
		map.put("sendTime", dateFormat.format(lastSendTime));
		map.put("rows", rowCount);
		map.put("containCurrent", containCurrent.toString());

		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}

		logger.debug("listChatMessage in Log:" + fromUser.getEmail());
		long beginMillis = System.currentTimeMillis();
		String result = queryInvoke(jsonStr, LIST_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "listChatMessage", LIST_URL);
		logger.debug("listChatMessage out Log:" + result);
		return result;
	}
	
	/**
	 * 聊天记录查询，从endTime开始向前取N条记录,最多取到startTime为止
	 * <br/> 替换原来的/chatmessage/list
	 */
	public static String preListChatMessage(User fromUser,String toUserOrGroup,Date lastSendTime,int rowCount, ChatType chatType) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("product", PRDUCT_NAME);
		map.put("type", chatType == ChatType.ONE2ONE ? 1 : 2);
		map.put("relevantPerson",fromUser.getId().toString());
		map.put("preRelevantPerson",toUserOrGroup);
		map.put("endTime", dateFormat.format(lastSendTime));
		map.put("rows", rowCount);

		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}

		logger.debug("preListChatMessage in Log:" + fromUser.getEmail());
		long beginMillis = System.currentTimeMillis();
		String result = queryInvoke(jsonStr, PERV_LIST_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "preListChatMessage", PERV_LIST_URL);
		logger.debug("preListChatMessage out Log:" + result);
		return result;
	}

	/**
	 * 查询最后聊天的人或群组
	 * <br/>
	 * type：1表示只查与人的聊天；2表示只查群组消息；不传表示不限制；默认为不限制
	 * @param user
	 * @param chatType
	 * @return
	 * @throws IOException
	 */
	public static String queryRelationship(User user,ChatType chatType) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("product", PRDUCT_NAME);
		if (chatType != null) {
			map.put("type", chatType == ChatType.ONE2ONE ? 1 : 2);
		}
		map.put("relevantPerson",user.getId().toString());
		map.put("rows", "<=0");
		
		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}

		logger.debug("queryRelationship in Log:" + user.getEmail());
		long beginMillis = System.currentTimeMillis();
		String result = queryInvoke(jsonStr, QUERY_RELATION_SHIP_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "queryRelationship", QUERY_RELATION_SHIP_URL);
		logger.debug("queryRelationship out Log:" + result);
		return result;
	}
	
	public static String queryLastMessage(User fromUser,User toUser,ChatType chatType) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("product", PRDUCT_NAME);
		map.put("type", chatType == ChatType.ONE2ONE ? 1 : 2);
		map.put("relevantPerson",fromUser.getId().toString());
		map.put("preRelevantPerson",toUser.getId().toString());

		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}

		logger.debug("queryLastMessage in Log:" + fromUser.getEmail());
		long beginMillis = System.currentTimeMillis();
		String result = queryInvoke(jsonStr, QUERY_LAST_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "queryLastMessage", QUERY_LAST_URL);
		logger.debug("queryLastMessage out Log:" + result);
		return result;
	}
	
	/**
	 * 简单查询（可以通过条件查询聊天记录，条件包括：参与聊天的人或组、产品名称、聊天类型、关键字及日期范围等）
	 * @return 接口返回的json
	 * @throws IOException 
	 */
	public static String simpleQuery(User fromUser,ChatType chatType,String toUserOrGroup,DateType dateType,int dateNumber,Date endDate,String keywords) throws IOException{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("product", PRDUCT_NAME);
		map.put("type", chatType == ChatType.ONE2ONE ? 1 : 2);
		map.put("relevantPerson", fromUser.getId().toString());
		map.put("preRelevantPerson", toUserOrGroup);
		map.put("dateType", dateType == DateType.DAY ? 1 : (dateType == DateType.MONTH ? 2 : 3)); // dateType：条件中的时间范围1：天为单位；2：月为单位；3：年为单位（必填）
		map.put("dateNumber", dateNumber); // dateNumber：时间范围值，例如dateType=2
		map.put("endDate", dateFormat.format(endDate)); // endDate:结束日期（包括此日期的记录）,查询此日期之前（由dateType和dateNumber决定）的记录（必填）
		map.put("keywords", keywords); // 查询的关键字
		
		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		logger.debug("simpleQuery in Log:" + fromUser.getEmail());
		long beginMillis = System.currentTimeMillis();
		String result = queryInvoke(jsonStr, SIMPLE_QUERY_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "simpleQuery", SIMPLE_QUERY_URL);
		logger.debug("simpleQuery out Log:" + result);
		return result;
	}
	
	/**
	 * 标准查询（可以通过条件查询聊天记录，条件包括：参与聊天的某个人、产品名称、关键字、日期范围等）
	 * @throws IOException 
	 */
	public static String standardQuery(User fromUser,DateType dateType,int dateNumber,Date endDate,String keywords,boolean isincludeGroup,boolean isOnlyIncludeGroup) throws IOException{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("product", PRDUCT_NAME);
		map.put("relevantPerson", fromUser.getId().toString());
		map.put("dateType", dateType == DateType.DAY ? 1 : (dateType == DateType.MONTH ? 2 : 3)); // dateType：条件中的时间范围1：天为单位；2：月为单位；3：年为单位（必填）
		map.put("dateNumber", dateNumber); // dateNumber：时间范围值，例如dateType=2
		map.put("endDate", dateFormat.format(endDate)); // endDate:结束日期（包括此日期的记录）,查询此日期之前（由dateType和dateNumber决定）的记录（必填）
		map.put("keywords", keywords); // 查询的关键字
		map.put("isincludeGroup", isincludeGroup); // isincludeGroup:是否查询群聊记录
		map.put("isOnlyIncludeGroup", isOnlyIncludeGroup); // isOnlyIncludeGroup:是否只包含群聊信息
		
		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		logger.debug("standardQuery in Log:" + fromUser.getEmail());
		long beginMillis = System.currentTimeMillis();
		String result = queryInvoke(jsonStr, STANDARD_QUERY_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "standardQuery", STANDARD_QUERY_URL);
		logger.debug("standardQuery out Log:" + result);
		return result;
	}
	
	/**
	 * 高级查询（可以通过条件查询聊天记录，条件包括：参与聊天的某个人、产品名称、关键字、日期范围等）
	 * @throws IOException
	 * @deprecated
	 */
	public static String seniorQuery(String[] from,String[] to,ChatType chatType,boolean queryAll,Date startDate,Date endDate,String keywords,boolean matchUser,int start,int rows) throws IOException{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("product", PRDUCT_NAME);
		if (chatType != null) {
			map.put("type", chatType == ChatType.ONE2ONE ? 1 : 2);
		}
		map.put("from", from);
		map.put("to", to);
		if (!queryAll) {
			map.put("startTime", dateFormat.format(startDate));
			map.put("endTime", dateFormat.format(endDate));
		}
		if (StringUtils.isNotEmpty(keywords)) {
			map.put("keywords", keywords); // 查询的关键字
		}
		map.put("matchUser", matchUser); // matchUser:是否区分发送方和接收方，默认为false
		map.put("start", start);
		map.put("rows", rows);
		
		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		logger.debug("seniorQuery in Log:");
		long beginMillis = System.currentTimeMillis();
		String result = queryInvoke(jsonStr, SENIOR_QUERY_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "seniorQuery", SENIOR_QUERY_URL);
		logger.debug("seniorQuery out Log:" + result);
		return result;
	}
	
	/**
	 * 高级分页查询（可以通过条件查询聊天记录，条件包括：参与聊天的某个人、关键字、日期范围等）
	 * @param contactList 联系人，可以使群或者人
	 * @param page 当前页码，默认为0
	 * @param rows 每页的记录条数，默认为20
	 * @throws IOException
	 */
	public static String listByPage(String currentUser,List<String> contactList,ChatType chatType,boolean queryAll,Date startDate,Date endDate,String keywords,Integer contentType,int page,int rows) throws IOException{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("product", PRDUCT_NAME);
		map.put("type", chatType == ChatType.ONE2ONE ? 1 : 2);
		map.put("user", currentUser);
		if (CollectionUtils.isNotEmpty(contactList)) {
			map.put("contact", play.libs.Json.toJson(contactList));
		}
		if (!queryAll) {
			map.put("startTime", dateFormat.format(startDate));
			map.put("endTime", dateFormat.format(endDate));
		}
		if (StringUtils.isNotEmpty(keywords)) {
			map.put("keywords", keywords); // 查询的关键字
		}
		if (contentType != null) { // 为空表示不限制
			map.put("contentType", contentType);
		}
		map.put("page", page);
		map.put("rows", rows);
		
		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		logger.debug("listByPage in Log:");
		long beginMillis = System.currentTimeMillis();
		String result = queryInvoke(jsonStr, LIST_BYPAGE_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "listByPage", LIST_BYPAGE_URL);
		logger.debug("listByPage out Log:" + result);
		return result;
	}
	
	
	/**
	 * 上下文查询：查询A与B的某一条聊天记录的前后各Ｎ条记录
	 * @throws IOException
	 */
	public static String queryContext(User fromUser,String toUserOrGroup,ChatType chatType,Date sendTime,int rows,int rowsAfter,boolean isContainCurrent) throws IOException{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("product", PRDUCT_NAME);
		map.put("type", chatType == ChatType.ONE2ONE ? 1 : 2);
		map.put("relevantPerson", fromUser.getId().toString());
		map.put("preRelevantPerson", toUserOrGroup);
		map.put("sendTime", dateFormat.format(sendTime));
		map.put("rows", rows); // 只能大于0，为0的话，默认取5个（包含当前时间的记录）
		map.put("rowsAfter", rowsAfter); // 可以为0，为0的话，默认不取后面的
		map.put("containCurrent", isContainCurrent); // 是否包含sendTime所指向的记录，默认为false。从前面的包含，从后面的没有包含
		
		String jsonStr = null;
		try {
			jsonStr = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		
		logger.debug("queryContext in Log:" + fromUser.getEmail());
		long beginMillis = System.currentTimeMillis();
		String result = queryInvoke(jsonStr, QUERY_CONTEXT_URL);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "queryContext", QUERY_CONTEXT_URL);
		logger.debug("queryContext out Log:" + result);
		return result;
	}
	
	/**
	 * 查询调用的公共方法
	 * @param jsonStr json格式参数值
	 * @param uri 请求的url
	 * @return
	 * @throws IOException 
	 */
	public static String queryInvoke(String jsonStr,String uri) throws IOException{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("conditions",jsonStr));
		
		// 创建一个HttpClient实例
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(uri);
		
		// 创建HttpParams以用来设置HTTP参数
		HttpParams httpParams = new BasicHttpParams();
		// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
        HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, REQUEST_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(httpParams, SOCKET_BUFFER_SIZE);
		httpost.setParams(httpParams);
		
		String result = null;
		HttpResponse response = null;
		InputStream in = null;
		try {
			// 添加请求参数到请求对象
			httpost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 发送请求并等待响应
			response = httpclient.execute(httpost);
			// 如果状态码为200
			if (response.getStatusLine().getStatusCode() == 200) {
				// 读取返回的数据
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					entity = new BufferedHttpEntity(entity);
					in = entity.getContent();
					byte[] read = new byte[1024];
					byte[] all = new byte[0];
					int num;
					while ((num = in.read(read)) > 0) {
						byte[] temp = new byte[all.length + num];
						System.arraycopy(all, 0, temp, 0, all.length);
						System.arraycopy(read, 0, temp, all.length, num);
						all = temp;
					}
					result = new String(all, "UTF-8");
				}
			} else {
				return "Error Response: " + response.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			if(logger.isErrorEnabled()){
				logger.error("调用"+uri+"发生错误", e);
			}
			throw e;
		} catch (IOException e) {
			if(logger.isErrorEnabled()){
				logger.error("调用"+uri+"发生错误", e);
			}
			throw e;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			httpost.abort();
		}
		return result;
	}
	
	/**
	 * 写数据调用的公共方法
	 * @param paramKey 参数名称
	 * @param paramJsonStr json格式参数值
	 * @param uri 请求的url
	 * @return
	 */
	public static Boolean writeDataInvoke(String paramKey,String paramJsonStr,String uri){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(paramKey,paramJsonStr));
		
		// 创建一个HttpClient实例
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(uri);
		// 创建HttpParams以用来设置HTTP参数
		HttpParams httpParams = new BasicHttpParams();
		// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
        HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, REQUEST_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(httpParams, SOCKET_BUFFER_SIZE);
		httpost.setParams(httpParams);
		
		String result = null;
		HttpResponse response = null;
		InputStream in = null;
		try {
			// 添加请求参数到请求对象
			httpost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 发送请求并等待响应
			response = httpclient.execute(httpost);
			// 如果状态码为200
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					entity = new BufferedHttpEntity(entity);
					in = entity.getContent();
					byte[] read = new byte[1024];
					byte[] all = new byte[0];
					int num;
					while ((num = in.read(read)) > 0) {
						byte[] temp = new byte[all.length + num];
						System.arraycopy(all, 0, temp, 0, all.length);
						System.arraycopy(read, 0, temp, all.length, num);
						all = temp;
					}
					result = new String(all, "UTF-8");
				}
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			httpost.abort();
		}
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		JsonNode node = null;
		try {
			node = mapper.readTree(result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (node != null && node.path("responsecode").asText().equals("_200")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 调用接口耗时写日志
	 * @param callTime
	 * @param method
	 * @param url
	 */
	private static void callTimeLog(long callTime, String method, String url) {
        StringBuilder log = new StringBuilder();
        log.append("调用方法=").append(method);
        log.append(",知识库系统接口URL=").append(url);
        log.append(",耗时=").append(callTime).append("ms");
        logger.info(log.toString());
    }
	
}
