package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Expert;
import models.Group;
import models.User;
import models.service.ChatService.CreateTranslateGroupResult;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.cache.Cache;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Result;
import vo.ExpertDetail;
import vo.GroupMemberVO;
import vo.page.Page;
import common.Constants;
import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;
import ext.MessageCenter.utils.MCMessageUtil;
import ext.config.ConfigFactory;
import ext.msg.model.Message;
import ext.usercenter.UCClient;

public class ChatApp extends BaseApp {
	static ALogger logger = Logger.of(ChatApp.class);
	private static String HOST = ConfigFactory.getString(Constants.MC_WEBSOCKET_HOST);
	private static String PORT = ConfigFactory.getString(Constants.MC_WEBSOCKET_PORT);
	private static String URL = "ws://"+HOST+":"+PORT;
	
	@Transactional
	public static Result chat(Long userId) {
		User me = User.getFromSession(session());
		User you = User.findById(userId);
		if(you == null) { // 从用户中心取
			you = UCClient.queryUserById(userId);
		}
		if (you == null) {
			you = new User(userId,"");
		}
		if(me.id == you.id){
			return badRequest();
		}
		Expert e = Expert.getExpertByUserId(me.id);		
		return ok(views.html.chat.render(me, you, URL,e.expenses));
	}
	
	@Transactional
	public static Result chatMsgQuery(){
		DynamicForm requestData = Form.form().bindFromRequest();
		// 最后查询时间
		String lastQueryDate = requestData.get("lastQueryDate");
		// 对方用户id
		Long userId = 0L;
		try {
			userId = Long.parseLong(StringUtils.isBlank(requestData.get("userId")) ? "0" : requestData.get("userId"));
		} catch(Exception e) {
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
				Logger.error("请求的参数groupId" + requestData.get("groupId")
						+ "装换为Long类型出错。");
			}
		}
		
		// 查询行数
		int rows = Integer.parseInt(StringUtils.isEmpty(requestData.get("pageSize")) ? "20" : requestData.get("pageSize"));
		
		java.util.Date queryDate = null;
		// 是否包含最后查询时间的这条记录
		Boolean containCurrent = Boolean.TRUE;
		if (StringUtils.isEmpty(lastQueryDate)) {
			queryDate = new java.util.Date();
		} else {
			queryDate = new java.util.Date(Long.parseLong(lastQueryDate));
			containCurrent = Boolean.FALSE;
		}
		// 当前登录用户
		User me = User.getFromSession(session());
		// 对方用户
		User you = User.findById(userId);
		if (you == null) {
			you = new User();
			you.setId(0L);
		}
		
		ext.MessageCenter.Message.chatMessage.page.Page<vo.ChatMessageRecordVO> pageMsg = Message.queryMsessageList(me,you,groupId,rows,queryDate,containCurrent);
		if (logger.isDebugEnabled()) {
			logger.debug("调用chatMsgQuery()方法返回给聊天的界面的json --->" + play.libs.Json.toJson(pageMsg));
		}
		return ok(play.libs.Json.toJson(pageMsg));
	}
	/**
	 * @description 验证用户是否在聊天室
	 * @author beyond.zhang
	 */
	@Transactional
	public static Result whetherInChat(Long sendId,Long receiveId) {
		boolean flag = MCMessageUtil.whetherInChat(sendId, receiveId);
		 ObjectNodeResult result = null;
		if(flag){
			  result = new ObjectNodeResult(ObjectNodeResult.STATUS_SUCCESS);
		}else{
			  result = new ObjectNodeResult(ObjectNodeResult.STATUS_FAILED);
		}
		return ok(result.getObjectNode());
	}
	
	/**
	 * 添加成员和翻译者到群组
	 * <br/> 创建翻译群
	 * @param userId 群组新成员用户id
	 * @param translatorId 翻译者成员用户id
	 * @throws IOException 
	 */
	@Transactional
	public static Result createTranslateGroup(Long userId,Long translatorId) throws IOException {
		if (userId == null || translatorId == null) {
			throw new NullPointerException("参数不能为空。");
		}
		User me = User.getFromSession(session());
		me = User.findById(me.getId());
		User you = User.findById(userId);
		User translator = User.findById(translatorId);
		if(you == null) { // 从用户中心获取
			you = UCClient.queryUserById(userId);
		}
		if(translator == null) { // 从用户中心获取
			translator = UCClient.queryUserById(translatorId);
		}
		if (you == null || translator == null) {
			return ok("{\"status\":\"0\",\"error\":\"添加的用户或翻译者不存在。\"}");
		}
		if(me.getId() - translator.getId() == 0) {
			return ok("{\"status\":\"0\",\"error\":\"不能邀请自己作为翻译者。\"}");
		}
		if (you.getId() - translator.getId() == 0) {
			return ok("{\"status\":\"0\",\"error\":\"与自己聊天的用户和翻译者不能是同一个人。\"}");
		}
		
		CreateTranslateGroupResult result = models.service.ChatService.createTranslateGroup(me, you, translator);
		Group group = result.getGroup();
		if (group != null) {
			return ok("{\"status\":\"1\",\"groupId\":\"" + group.getId() + "\",\"groupName\":\"" + group.getGroupName() + "\",\"groupType\":\"" + group.getType().toString().toLowerCase() + "\"}");
		} else {
			return ok("{\"status\":\"0\",\"error\":\"已经添加用户到同一个组了，请不要重复添加。\"}");
		}
		
	}
	
	/**
	 * 群组 - 新增成员
	 * @return
	 * @throws IOException 
	 */
	@Transactional
	public static Result appendMemberToGroup() throws IOException{
		DynamicForm requestData = Form.form().bindFromRequest();
		// 群组的id
		Long groupId = 0L;
		try {
			groupId = Long.parseLong(StringUtils.isBlank(requestData.get("groupId")) ? "0" : requestData.get("groupId"));
		} catch (Exception e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("请求的参数groupId" + requestData.get("groupId") + "装换为Long类型出错。");
			}
		}
	 	String userId = requestData.get("userId");
		
		ObjectNodeResult result = models.service.ChatService.appendMemberToGroup(groupId,Long.parseLong(userId));
		return ok(result.getObjectNode());
	}
	
	/**
	 * 查询群组下面的成员
	 */
	@Transactional(readOnly = true)
	public static Result queryUserUnderGroup(Long groupId) {
		if (groupId == null) {
			throw new NullPointerException("参数不能为空。");
		}
		User me = User.getFromSession(session());
		List<GroupMemberVO> gmVOList = models.service.ChatService.queryUserUnderGroup(groupId,me);
		if (Logger.isDebugEnabled()) {
			Logger.debug("查询群组下面的成员，返回给前端的json    ----> " + play.libs.Json.toJson(gmVOList));
		}
		return ok(play.libs.Json.toJson(gmVOList));
	}
	
	/**
	 * 查询群组下面的成员
	 * <br/>
	 * 提供分页
	 */
	@Transactional(readOnly = true)
	public static Result queryMemberUnderGroup() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String pageStr = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "10" : requestData.get("pageSize");
		String groupId = StringUtils.isBlank(requestData.get("groupId")) ? "0" : requestData.get("groupId");
		Integer page = Integer.parseInt(pageStr);
		if (page != null && page < 0) {
			page = 0;
		}
		Page<ExpertDetail> gmVOPage = models.service.ChatService.queryUserUnderGroup(Long.parseLong(groupId), page, Integer.parseInt(pageSize));
		if (Logger.isDebugEnabled()) {
			Logger.debug("查询群组下面的成员，返回给前端的json    ----> " + play.libs.Json.toJson(gmVOPage));
		}
		return ok(play.libs.Json.toJson(gmVOPage));
	}
	/**
	 * @description 验证用户是否在线
	 * @author beyond.zhang
	 */
	@Transactional
	public static Result whetherOnline(Long userId) {
		boolean flag =MCMessageUtil.whetherOnline( userId) ;
		 ObjectNodeResult result = null;
		if(flag){
			  result = new ObjectNodeResult(ObjectNodeResult.STATUS_SUCCESS);
		}else{
			  result = new ObjectNodeResult(ObjectNodeResult.STATUS_FAILED);
		}
		return ok(result.getObjectNode());
	}
	
	/**
	 * @description 清除聊天数目
	 * @author beyond.zhang
	 */
	@Transactional
	public static Result cleanChatNum(Long userId,String receiveId) {
		String key = "communicateNum"+String.valueOf(userId)+"to"+receiveId;
		try {
			Object obj = Cache.get(key);
			if(obj!=null){
				Cache.remove(key);
			}
		} catch (Exception e) {
			Logger.debug("清除聊天数目出错{}",e);
			return ok(new ObjectNodeResult(ObjectNodeResult.STATUS_FAILED).getObjectNode());
		}
		return ok(new ObjectNodeResult(ObjectNodeResult.STATUS_SUCCESS).getObjectNode());
	}
	@Transactional
	public static Result cleanGroupChatNum(String groupId,String receiveId) {
		String key = "communicateNum"+"G"+String.valueOf(groupId)+"to"+String.valueOf(receiveId);
		try {
			Object obj = Cache.get(key);
			if(obj!=null){
				Cache.remove(key);
			}
		} catch (Exception e) {
			Logger.debug("清除聊天数目出错{}",e);
			return ok(new ObjectNodeResult(ObjectNodeResult.STATUS_FAILED).getObjectNode());
		}
		return ok(new ObjectNodeResult(ObjectNodeResult.STATUS_SUCCESS).getObjectNode());
	}
	
	/**
	 * 查询当前用户的最近联系人
	 * @return
	 */
	@Transactional
	public static Result queryRelationship(){
		User currentUser = User.getFromSession(session());
		List<vo.ChatMsgRelationshipVO> voList = models.service.ChatService.queryRelationship(currentUser);
		if (logger.isDebugEnabled()) {
			logger.debug("调用queryRelationship()方法查询当前用户的最近联系人，返回给前端的json    ----> " + play.libs.Json.toJson(voList));
		}
		return ok(play.libs.Json.toJson(voList));
	}
	
	/**
	 * 查询在线翻译
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result queryTranslatorServices() {
		List<Expert> expertList = Expert.queryCustomerServices("\"嗨啰在线翻译\"", true);
		List<ExpertDetail> expertDetailList = new ArrayList<ExpertDetail>();
		if (CollectionUtils.isNotEmpty(expertList)) {
			for (Expert e : expertList) {
				ExpertDetail expertDetail = new ExpertDetail();
				expertDetail.convert(e);
				expertDetail.setEducationExp(null);
				expertDetail.setJobExp(null);
				expertDetail.setIsOnline(true); // 设置在线
				expertDetailList.add(expertDetail);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("调用queryCustomerServices()方法查询客服，返回给前端的json    ----> " + play.libs.Json.toJson(expertDetailList));
		}
		return ok(play.libs.Json.toJson(expertDetailList));
	}
	
}
