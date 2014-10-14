package ext.msg.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import models.Group;
import models.SkillTag;
import models.User;
import models.User.ResumeStatus;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import vo.msg.MessageJson;
import vo.page.Page;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.jackjson.JackJsonUtil;
import controllers.base.ObjectNodeResult;
import ext.MessageCenter.Message.chatMessage.ChatMessageHttpClient.ChatType;
import ext.MessageCenter.utils.MCMessageUtil;
import ext.config.ConfigFactory;
import ext.msg.model.Message;
import ext.msg.model.Message.MsgType;
import ext.msg.model.service.MessageService;
import ext.msg.vo.ChatMsgCount;
import ext.msg.vo.GroupInfoVO;
import ext.msg.vo.MsgCount;
import ext.usercenter.UserAuthService;
import ext.usercenter.UserAuthService.ValidateTokenResult;

public class MessageApp extends Controller {

	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static ALogger LOGGER = Logger.of(MessageService.class);
	
	public final static String HELOMEUS_URL = ConfigFactory.getString("resume.client.helomeusUrl");

	/**
	 * 系统消息
	 * 
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@Transactional
	public static Result queryMessage() throws JsonParseException, JsonMappingException, IOException {
		User currentUser = User.getFromSession(session());
		String userId = String.valueOf(currentUser.id);
		DynamicForm requestData = Form.form().bindFromRequest();
		String page = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "10" : requestData.get("pageSize");
		String msgType = requestData.get("msgType");
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		Page pageMsg = null;
		Set<MsgType> msgTypeSet = new HashSet<>();
		if (StringUtils.equals(msgType, "system")) {
			List<MessageJson> mjs = new ArrayList<MessageJson>();
			msgTypeSet.add(MsgType.COMMENT);
			msgTypeSet.add(MsgType.REPLY);
			msgTypeSet.add(MsgType.TRANSFER_IN);
			msgTypeSet.add(MsgType.TRANSFER_OUT);
			msgTypeSet.add(MsgType.RESUME_FINISH);
			pageMsg = Message.queryMessageByRead(Integer.parseInt(page), Integer.parseInt(pageSize), userId, msgTypeSet, false);
			List<Message> pms = pageMsg.getList();
			for (Message message : pms) {
				if (StringUtils.isNotBlank(message.content)&&message.msgType != Message.MsgType.RESUME_FINISH) {
					JsonNode jn = play.libs.Json.parse(message.content);
					if (message.msgType.equals(Message.MsgType.COMMENT) || message.msgType.equals(Message.MsgType.REPLY)) {
						MessageJson mjson = new MessageJson(message.id, message.senderOnly, message.senderName, message.consumeOnly,
								message.consumeName, message.msgType, dateFormat.format(message.msgTime), message.isRead);

						mjson.refId = jn.findPath("refId").asLong();
						mjson.content = jn.findPath("content").asText();
						mjs.add(mjson);
					} else if (message.msgType.equals(Message.MsgType.TRANSFER_IN) || message.msgType.equals(Message.MsgType.TRANSFER_OUT)) {
						MessageJson mjson = new MessageJson(message.id, message.senderOnly, message.senderName, message.consumeOnly,
								message.consumeName, message.msgType, dateFormat.format(message.msgTime), message.isRead);
						mjson.amount = String.format("%.2f", jn.findPath("amount").asDouble()).toString();
						mjson.currency = jn.findPath("currency").asInt();
						mjs.add(mjson);
					}
				}
				if (message.msgType.equals(Message.MsgType.RESUME_FINISH)) {
					MessageJson mjson = new MessageJson(message.id, message.senderOnly, message.senderName, message.consumeOnly,
							message.consumeName, message.msgType, dateFormat.format(message.msgTime), message.isRead);
					if (StringUtils.isNotBlank(message.content)) {
						JsonNode jn = play.libs.Json.parse(message.content);
						String content = jn.findPath("content").asText();
						mjson.content = (StringUtils.isNotEmpty(HELOMEUS_URL) ? HELOMEUS_URL : "http://www.helome.us/") + content;
					}
					mjs.add(mjson);
				}
			}
			pageMsg.setList(mjs);
			Message.markReaded(userId, msgTypeSet);
		} else if (StringUtils.equals(msgType, "friends")) {
			List<MessageJson> mjs = new ArrayList<MessageJson>();
			msgTypeSet.add(MsgType.ADD_FRIENDS);
			msgTypeSet.add(MsgType.AGREE_FRIENDS);
			pageMsg = Message.queryMessageByRead(Integer.parseInt(page), Integer.parseInt(pageSize), userId, msgTypeSet, false);
			List<Message> pms = pageMsg.getList();
			for (Message message : pms) {
				MessageJson mjfd = new MessageJson();
				if (StringUtils.isNotBlank(message.content)) {
					JsonNode jn = play.libs.Json.parse(message.content);
					mjfd.content = jn.findPath("content").asText();
				}
				mjfd.messageId = message.id;
				mjfd.isRead = message.isRead;
				mjfd.senderId = message.senderOnly;
				mjfd.senderName = message.senderName;
				mjfd.recevierId = message.consumeOnly;
				mjfd.recevierName = message.consumeName;
				mjfd.msgType = message.msgType;
				mjfd.isHandler = message.isHandler;
				mjfd.msgTime = dateFormat.format(message.msgTime);
				mjs.add(mjfd);
			}
			pageMsg.setList(mjs);
			Message.markReaded(userId, msgTypeSet);
		} else if (StringUtils.equals(msgType, "group")) {
			List<MessageJson> mjs = new ArrayList<MessageJson>();
			msgTypeSet.add(MsgType.INVIT_GROUP_MEMBER);
			msgTypeSet.add(MsgType.AGREE_GROUP_INVIT);
			msgTypeSet.add(MsgType.REJECT_GROUP_INTVIT);
			msgTypeSet.add(MsgType.REMOVE_GROUP_MEMBER);
			msgTypeSet.add(MsgType.QUIT_GROUP);
			msgTypeSet.add(MsgType.APPLY_GROUP);
			msgTypeSet.add(MsgType.AGREE_GROUP_APPLY);
			msgTypeSet.add(MsgType.DISMISS_GROUP);
			pageMsg = Message.queryMessageByRead(Integer.parseInt(page), Integer.parseInt(pageSize), userId, msgTypeSet, false);
			List<Message> pms = pageMsg.getList();
			for (Message message : pms) {
				MessageJson mjfd = new MessageJson();
				if (StringUtils.isNotBlank(message.content)) {
					JsonNode jn = play.libs.Json.parse(message.content);
					mjfd.content = jn.findPath("content").asText();
					mjfd.groupId = jn.findPath("groupId").asLong(0);
					mjfd.groupName = jn.findPath("groupName").asText();
					mjfd.hostsId = jn.findPath("hostsId").asLong(0);
					mjfd.hostsName = jn.findPath("hostsName").asText();
				}
				mjfd.messageId = message.id;
				mjfd.senderId = message.senderOnly;
				mjfd.senderName = message.senderName;
				mjfd.recevierId = message.consumeOnly;
				mjfd.recevierName = message.consumeName;
				mjfd.isHandler = message.isHandler;
				mjfd.msgType = message.msgType;
				mjfd.isRead = message.isRead;
				mjfd.msgTime = dateFormat.format(message.msgTime);
				mjs.add(mjfd);

			}
			pageMsg.setList(mjs);
			Message.markReaded(userId, msgTypeSet);
		}
		String resultJson = play.libs.Json.toJson(pageMsg).toString();
		return ok(resultJson);
	}

	@Transactional(readOnly = true)
	public static Result queryMessageUnReadAll() {
		User currentUser = User.getFromSession(session());
		String userId = String.valueOf(currentUser.id);
		List<Message> data = Message.queryMessage(userId);
		List<MessageJson> mjs = new ArrayList<MessageJson>();
		for (Message message : data) {
			if (message.msgType.equals(Message.MsgType.COMMENT) 
					|| message.msgType.equals(Message.MsgType.REPLY)) {
				MessageJson mjson = new MessageJson(message.id, message.senderOnly, message.senderName, message.consumeOnly,
						message.consumeName, message.msgType, dateFormat.format(message.msgTime), message.isRead);
				if (StringUtils.isNotBlank(message.content)) {
					JsonNode jn = play.libs.Json.parse(message.content);
					mjson.refId = jn.findPath("refId").asLong();
					mjson.content = jn.findPath("content").asText();
					mjs.add(mjson);
				}
			} else if (message.msgType.equals(Message.MsgType.TRANSFER_IN) 
					|| message.msgType.equals(Message.MsgType.TRANSFER_OUT)) {
				MessageJson mjson = new MessageJson(message.id, message.senderOnly, message.senderName, message.consumeOnly,
						message.consumeName, message.msgType, dateFormat.format(message.msgTime), message.isRead);
				if (StringUtils.isNotBlank(message.content)) {
					JsonNode jn = play.libs.Json.parse(message.content);
					mjson.amount = String.format("%.2f", jn.findPath("amount").asDouble()).toString();
					mjson.currency = jn.findPath("currency").asInt();
					mjs.add(mjson);
				}
			} else if (message.msgType.equals(Message.MsgType.RESUME_FINISH)) {
				MessageJson mjson = new MessageJson(message.id, message.senderOnly, message.senderName, message.consumeOnly,
						message.consumeName, message.msgType, dateFormat.format(message.msgTime), message.isRead);
				mjs.add(mjson);
			} else if (message.msgType.equals(Message.MsgType.ADD_FRIENDS) 
					|| message.msgType.equals(Message.MsgType.AGREE_FRIENDS)){
				MessageJson mjfd = new MessageJson();
				if (StringUtils.isNotBlank(message.content)) {
					JsonNode jn = play.libs.Json.parse(message.content);
					mjfd.content = jn.findPath("content").asText();
				}
				mjfd.messageId = message.id;
				mjfd.isRead = message.isRead;
				mjfd.senderId = message.senderOnly;
				mjfd.senderName = message.senderName;
				mjfd.isHandler = message.isHandler;
				mjfd.recevierId = message.consumeOnly;
				mjfd.recevierName = message.consumeName;
				mjfd.msgType = message.msgType;
				mjfd.msgTime = dateFormat.format(message.msgTime);
				mjs.add(mjfd);
			} else if (message.msgType.equals(MsgType.INVIT_GROUP_MEMBER) 
					|| message.msgType.equals(MsgType.AGREE_GROUP_INVIT)
					|| message.msgType.equals(MsgType.REJECT_GROUP_INTVIT)
					|| message.msgType.equals(MsgType.REMOVE_GROUP_MEMBER)
					|| message.msgType.equals(MsgType.QUIT_GROUP)
					|| message.msgType.equals(MsgType.APPLY_GROUP)
					|| message.msgType.equals(MsgType.AGREE_GROUP_APPLY)
					|| message.msgType.equals(MsgType.DISMISS_GROUP)){
				MessageJson mjfd = new MessageJson();
				if (StringUtils.isNotBlank(message.content)) {
					JsonNode jn = play.libs.Json.parse(message.content);
					mjfd.content = jn.findPath("content").asText();
					mjfd.groupId = jn.findPath("groupId").asLong(0);
					mjfd.groupName = jn.findPath("groupName").asText();
					mjfd.hostsId = jn.findPath("hostsId").asLong(0);
					mjfd.hostsName = jn.findPath("hostsName").asText();
				}
				mjfd.messageId = message.id;
				mjfd.senderId = message.senderOnly;
				mjfd.senderName = message.senderName;
				mjfd.recevierId = message.consumeOnly;
				mjfd.isHandler = message.isHandler;
				mjfd.recevierName = message.consumeName;
				mjfd.msgType = message.msgType;
				mjfd.isRead = message.isRead;
				mjfd.msgTime = dateFormat.format(message.msgTime);
				mjs.add(mjfd);
				
			}
		}
		String resultJson = play.libs.Json.toJson(mjs).toString();
		return ok(resultJson);
	}

	/**
	 * 聊天记录
	 * 
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@Transactional
	public static Result queryChatMessage() {
		User currentUser = User.getFromSession(session());
		DynamicForm requestData = Form.form().bindFromRequest();
		String page = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "10" : requestData.get("pageSize");
		String msgType = requestData.get("msgType");
		String chatTypeStr = requestData.get("chatType"); // 没有这个参数查询所有的，
															// chatType：1，人与人聊天
															// ; chatType：2，群组聊天
		ChatType chatType = null;
		if (StringUtils.isNotBlank(chatTypeStr)) {
			chatType = ChatType.ONE2ONE;
			if (StringUtils.equals(chatTypeStr, "2")) {
				chatType = ChatType.MANY2MANY;
			}
		}

		if (StringUtils.equals(msgType, "no_system")) {
			try {
				Page<vo.ChatMessageRecordVO> pageMsg = Message.queryChatMsg(Integer.parseInt(page), Integer.parseInt(pageSize),
						currentUser, chatType);
				if (Logger.isDebugEnabled()) {
					Logger.debug("调用queryChatMessage()方法返回给聊天记录界面的json    ----> " + play.libs.Json.toJson(pageMsg));
				}
				return ok(play.libs.Json.toJson(pageMsg));
			} catch (IOException e) {
				if (Logger.isErrorEnabled()) {
					Logger.error("调用queryChatMessage()方法查询出错啦。", e);
				}
				return ok("{\"status\":\"0\",\"error\":\"获取服务端连接超时\"}");
			}
		}
		return ok("{\"status\":\"0\",\"error\":\"请检查参数msgType的值\"}");
	}

	/**
	 * 聊天记录内容根据条件查询
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result queryChatMessageByCondition() {
		User currentUser = User.getFromSession(session());
		DynamicForm requestData = Form.form().bindFromRequest();
		String page = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "10" : requestData.get("pageSize");
		String startDateStr = requestData.get("startDate");
		String endDateStr = requestData.get("endDate");
		String keyWords = requestData.get("keyWords"); // 查询关键词允许为空
		String type = StringUtils.isBlank(requestData.get("type")) ? "3" : requestData.get("type"); // type:
																									// 1-图片
																									// 2-文件
																									// 3-文本
																									// 4-多媒体
		String userName = requestData.get("userName"); // 用户名
		String groupName = requestData.get("groupName"); // 群组名称
		String bindingStr = requestData.get("binding"); // 绑定用户名或群组名称 和 keyWords
														// --> binding : 1
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String chatTypeStr = requestData.get("chatType"); // chatType：1，人与人聊天 ;
															// chatType：2，群组聊天
		ChatType chatType = ChatType.ONE2ONE;
		if (StringUtils.equals(chatTypeStr, "2")) {
			chatType = ChatType.MANY2MANY;
		}
		String binding = "";
		if (StringUtils.isNotBlank(bindingStr)) {
			if (StringUtils.equals(bindingStr, "1")) {
				binding = bindingStr;
				if (chatType == ChatType.ONE2ONE) {
					userName = keyWords;
				} else {
					groupName = keyWords;
				}
			}
		}

		boolean queryAll = Boolean.TRUE; // 默认查询所有的
		Date startDate = new Date();
		Date endDate = new Date();
		if (StringUtils.isNotBlank(startDateStr) && StringUtils.isNotBlank(endDateStr)) {
			queryAll = Boolean.FALSE;
			try {
				startDate = dateFormat.parse(startDateStr);
				endDate = dateFormat.parse(endDateStr);
			} catch (ParseException e) {
				if (Logger.isErrorEnabled()) {
					Logger.error("queryChatMessageByCondition()方法解析日期字符串出现异常。", e);
				}
				return ok("{\"status\":\"0\",\"error\":\"解析传入的日期字符串出现异常，请检查\"}");
			}
		}

		List<Long> userIdList = new ArrayList<Long>();
		List<Long> groupIdList = new ArrayList<Long>();
		if (StringUtils.isNotBlank(userName)) {
			userIdList = User.findUserIdListByUserName(userName);
		}
		if (StringUtils.isNotBlank(groupName)) {
			groupIdList = Group.findGroupIdListByGroupNameAndUserId(groupName,currentUser.getId());
		}

		try {
			ext.MessageCenter.Message.chatMessage.page.Page<vo.ChatMessageRecordVO> pageMsg = Message.queryChatMessageByCondition(
					Integer.parseInt(page), Integer.parseInt(pageSize), currentUser, keyWords, type, userIdList, groupIdList, userName,
					groupName, startDate, endDate, queryAll, chatType, binding);
			if (Logger.isDebugEnabled()) {
				Logger.debug("调用queryChatMessageByCondition()方法返回给聊天记录查询界面的json    ----> " + play.libs.Json.toJson(pageMsg));
			}
			return ok(play.libs.Json.toJson(pageMsg));
		} catch (IOException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("调用queryChatMessageByCondition()方法查询出错啦。", e);
			}
			return ok("{\"status\":\"0\",\"error\":\"获取服务端连接超时\"}");
		}
	}

	/**
	 * 聊天记录上下文查询
	 */
	@Transactional(readOnly = true)
	public static Result queryChatMessageContext() {
		DynamicForm requestData = Form.form().bindFromRequest();
		// 发送时间
		String sendTime = requestData.get("sendTime");

		java.util.Date queryDate = null;
		queryDate = new java.util.Date(Long.parseLong(StringUtils.isEmpty(sendTime) ? "0" : sendTime));
		// 对方用户id
		Long userId = 0L;
		try {
			userId = Long.parseLong(StringUtils.isBlank(requestData.get("userId")) ? "0" : requestData.get("userId"));
		} catch (Exception e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("请求的参数userId" + requestData.get("userId") + "装换为Long类型出错。");
			}
			userId = 0L;
		}
		// 群组的id
		Long groupId = 0L;
		try {
			groupId = Long.parseLong(StringUtils.isBlank(requestData.get("groupId")) ? "0" : requestData.get("groupId"));
		} catch (Exception e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("请求的参数groupId" + requestData.get("groupId") + "装换为Long类型出错。");
			}
			groupId = 0L;
		}
		// 对方用户
		User you = User.findById(userId);
		if (you == null) {
			you = new User();
			you.setId(0L);
		}
		// 向前查询行数
		int preRows = Integer.parseInt(StringUtils.isEmpty(requestData.get("preSize")) ? "5" : requestData.get("preSize"));
		// 向后查询行数
		int nextRows = Integer.parseInt(StringUtils.isEmpty(requestData.get("nextSize")) ? "5" : requestData.get("nextSize"));
		// 当前登录用户
		User currentUser = User.getFromSession(session());
		// 是否包含查询时间的这条记录
		String isContainCurrent = requestData.get("containCurrent");
		Boolean containCurrent = Boolean.TRUE;
		if (StringUtils.equals(isContainCurrent, "false")) {
			containCurrent = Boolean.FALSE;
		}

		ext.MessageCenter.Message.chatMessage.page.Page<vo.ChatMessageRecordVO> pageMsg = Message.queryChatMessageContext(currentUser, you,
				groupId, preRows, nextRows, queryDate, containCurrent);
		if (Logger.isDebugEnabled()) {
			Logger.debug("调用queryChatMessageContext()方法返回给前端界面的json --->" + play.libs.Json.toJson(pageMsg));
		}
		return ok(play.libs.Json.toJson(pageMsg));
	}

	@Transactional
	public static Result noticeMessage() {
		User user = User.getFromSession(session());
		MCMessageUtil.pustTxtMessage(0L, user.id, user.getName(), user.getName(), "通知消息");
		MsgCount msgCountVO = new MsgCount();
		msgCountVO.setStatus("200");
		return ok(play.libs.Json.toJson(msgCountVO));
	}

	@Transactional(readOnly = true)
	public static Result newMessage() {
		User user = User.getFromSession(session());
		MsgCount msgCountVO = Message.newsMessageNum(user);
		msgCountVO.setStatus("200");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("调用newMessage()方法返回的json   --->" + play.libs.Json.toJson(msgCountVO));
		}
		return ok(play.libs.Json.toJson(msgCountVO));
	}
	
	@Transactional(readOnly = true)
	public static Result chatMsgUnReadInfo() {
		User user = User.getFromSession(session());
		ChatMsgCount msgCountVO = Message.chatMsgUnReadInfo(user);
		msgCountVO.setStatus("200");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("调用chatMsgUnReadInfo()方法返回的json   --->" + play.libs.Json.toJson(msgCountVO));
		}
		return ok(play.libs.Json.toJson(msgCountVO));
	}

	@BodyParser.Of(BodyParser.Json.class)
	@Transactional
	public static Result deleteMessage() {
		JsonNode json = request().body().asJson();
		Long id = json.findPath("id").asLong();
		User user = User.getFromSession(session());

		boolean result = true;
		try {
			result = Message.deleteMessageById(id, user.id);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}

		if (result) {
			return ok("{\"status\":\"200\",\"message\":\"success\"}");
		} else {
			return ok("{\"status\":\"0\",\"message\":\"删除失败\"}");
		}
	}

	@BodyParser.Of(BodyParser.Json.class)
	@Transactional
	public static Result pushTS() {
		ObjectNodeResult result = new ObjectNodeResult();
		JsonNode json = request().body().asJson();
		String sender = json.findPath("sender").asText(), recevier = json.findPath("recevier").asText(), amount = json.findPath("amount")
				.asText(), token = json.findPath("token").asText();
		int currency = json.findPath("currency").asInt(0);

		LOGGER.info("pushts request:" + sender + ":" + recevier + ":" + amount + ":" + token);
		if (StringUtils.isBlank(sender) || StringUtils.isBlank(recevier) || StringUtils.isBlank(amount) || StringUtils.isBlank(token)) {
			result.errorkey("msg.pushts.error.notenoughparam");
			return ok(result.getObjectNode());
		}
		ValidateTokenResult va = UserAuthService.validateToken(token);
		if (!va.equals(ValidateTokenResult.VALID)) {
			result.errorkey("msg.pushts.error.token", token);
			return ok(result.getObjectNode());
		}

		User senderUser = User.findByEmail(sender), recevierUser = User.findByEmail(recevier);
		if (senderUser == null) {
			result.errorkey("msg.pushts.error.nofounduser", sender);
			return ok(result.getObjectNode());
		}
		if (recevierUser == null) {
			result.errorkey("msg.pushts.error.nofounduser", recevier);
			return ok(result.getObjectNode());
		}
		MessageService.pushMsgTransfer(senderUser, recevierUser, amount, currency);
		return ok(result.getObjectNode());
	}

	@Transactional
	public static Result pushRS() {
		ObjectNodeResult result = new ObjectNodeResult();
		DynamicForm requestData = Form.form().bindFromRequest();
		String jsonStr = requestData.get("conditions");
		JsonNode json = null;
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		try {
			json = mapper.readTree(jsonStr);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (Logger.isInfoEnabled()) {
			Logger.info("OverseasResumeApp - create() ->传入的json：" + json);
		}
		if (json == null) {
			result = result.error("Json can't be empty.", "501"); // Json can't
																	// be empty.
																	// ---
																	// 传入的json不能为空。
			return ok(result.getObjectNode());
		}
		String userId = json.findPath("userId").asText();
		User user = User.findById(new Long(userId));
		if (user == null) {
			// result.errorkey("msg.pushts.error.nofounduser",userId);
			result = result.error("this user can't find.", "502"); // this user
																	// can't
																	// find. ---
																	// 用户没有找到
			return ok(result.getObjectNode());
		}
		
		try { // 增加try-catch，保证后续操作完成
			MessageService.pushResumeComplete(user, user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 成功后要做的所有的后续操作...
		if (Logger.isInfoEnabled()) {
			Logger.info("收到客户端海外简历发布通知，更新User的resumeStatus的值为TRANSLATED。");
		}
//		user.setIsForbidAddResumeTask(Boolean.FALSE); // 是否禁止发布海外简历 --> 否
		user.setResumeStatus(ResumeStatus.TRANSLATED); // 已翻译
		User.merge(user); // 更新信息，刷新缓存
		if (Logger.isInfoEnabled()) {
			Logger.info("更新User的resumeStatus的值成功。");
		}
		return ok(result.getObjectNode());
	}

	@BodyParser.Of(BodyParser.Json.class)
	@Transactional
	public static Result updateRead() {
		ObjectNodeResult result = new ObjectNodeResult();
		JsonNode json = request().body().asJson();
		Iterator<JsonNode> iter = json.get("messageId").elements();
		List<Long> mids = new ArrayList<Long>();
		while (iter.hasNext()) {
			mids.add(iter.next().asLong());
		}		
		String userId = User.getFromSession(session()).id.toString();
		Message.markReadedIds(userId,mids);
		return ok(result.getObjectNode());
	}

	/**
	 * 返回所有的群成员信息 <br/>
	 * 参数 page 页号，默认从0开始
	 * 
	 * @return
	 */
	@Transactional
	public static Result allGroupInfo() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String pageParam = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		Integer page = Integer.parseInt(pageParam);
		int pageSize = 20; // 默认每次20个
		// String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ?
		// "20" : requestData.get("pageSize");
		List<GroupInfoVO> giVOList = models.service.ChatService.queryAllGroupInfo(page, pageSize);
		Long total = Group.queryTotalCount();
		String resultJson = "";
		if ((page * pageSize + pageSize) <= total) {
			resultJson = "{\"total\":" + total + ",\"hasNext\":true,\"data\":" + play.libs.Json.toJson(giVOList).toString() + "}";
		} else {
			resultJson = "{\"total\":" + total + ",\"hasNext\":false,\"data\":" + play.libs.Json.toJson(giVOList).toString() + "}";
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("调用allGroupInfo()方法返回的json ----> " + resultJson);
		}
		return ok(resultJson);
	}

}
