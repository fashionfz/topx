/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package controllers.user;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;

import models.Expert;
import models.Gender;
import models.RememberMeInfo;
import models.User;
import models.service.UserInfoCookieService;
import play.Logger;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Result;
import utils.DateUtils;
import utils.EmailUtil;
import vo.EmailInfo;
import vo.UserRegisterSuccessNotify;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;
import ext.MessageCenter.utils.MCMessageUtil;
import ext.config.ConfigFactory;
import ext.usercenter.UserAuthService;

/**
 * @author ZhouChun
 * @ClassName: UserController
 * @Description: user detail
 * @date 13-10-29 下午5:53
 */
public class UserApp extends BaseApp {

	/**
	 * 用户基础数据注册保存..
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	@play.db.jpa.Transactional
	public static Result save() {
		JsonNode json = getJson();
		String email = json.findPath("email").asText();
		String password = json.findPath("password").asText();
		String captcha = json.findPath("captcha").asText();
		Gender gender = json.findPath("gender").asInt(0) == 1 ? Gender.WOMAN : Gender.MAN;
		String t = json.findPath("t").asText();
		ObjectNodeResult objectNodeResult = User.register(email, password, gender, captcha, t);
		objectNodeResult.put("token", UserAuthService.getTokenInSession(session()));
		// 注册成功
		if (objectNodeResult.isSuccess()) {
			UserRegisterSuccessNotify obj = new UserRegisterSuccessNotify(objectNodeResult.getUser());
			obj.setPath(ConfigFactory.getString("upload.url"));
			obj.setDate(DateUtils.format(new Date(), DateUtils.FORMAT_DATE));
			EmailInfo emailInfo = new EmailInfo(objectNodeResult.getUser().getEmail(), "欢迎注册helome服务平台");
			//String template = EmailTemplate.getTemplateContent(EmailUtil.USER_REGISTER_SUCCESS_NOTIFY);
			EmailUtil.pushEmail(EmailUtil.USER_REGISTER_SUCCESS_NOTIFY,emailInfo,obj);
			// 注册成功，客服联系用户
			User currentUser = objectNodeResult.getUser();
			try {
				arrangeService(currentUser);
			} catch (Exception e) {
				if (Logger.isErrorEnabled()) {
					Logger.error("用户[" + currentUser.getId() + "]注册成功后给该用户安排客服时出错。", e);
				}
			}
		}
		return ok(objectNodeResult.getObjectNode());
	}
	
	/**
	 * 给用户安排客服联系
	 */
	private static void arrangeService(User currentUser) {
		List<Expert> expertList = Expert.queryCustomerServices("\"嗨啰在线客服\"", true);
		Random r = new Random();
		if (CollectionUtils.isEmpty(expertList)) { // 获取所有的客服
			expertList = Expert.queryCustomerServices("\"嗨啰在线客服\"", null);
		}
		if (CollectionUtils.isNotEmpty(expertList)) {
			int i = r.nextInt(expertList.size());
			Expert expert = expertList.get(i);
			// 给客服发送
			Logger.info("注册成功，安排客服  ----> 客服id：" + expert.userId + "，用户id：" + currentUser.getId() + "，客服用户名：" + expert.userName + "，用户用户名：" + currentUser.getName());
			String content = "你好";
			MCMessageUtil.pustTxtMessage(currentUser.getId(), expert.userId, currentUser.getName(), expert.userName == null ? "" : expert.userName, content);
			MCMessageUtil.pustTxtMessage(currentUser.getId(), expert.userId, currentUser.getName(), expert.userName == null ? "" : expert.userName, "我是新注册用户");
		}
	}

	/**
	 * 登录
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	@play.db.jpa.Transactional
	public static Result login() {
		JsonNode json = request().body().asJson();
		String email = json.findPath("email").asText();
		String password = json.findPath("password").asText();
		String remember = json.findPath("remember").asText();
		ObjectNodeResult loginResult = User.login(email, password, "1".equals(remember));
		loginResult.put("token", UserAuthService.getTokenInSession(session()));
		return ok(loginResult.getObjectNode());
	}

	/**
	 * 退出
	 * 
	 * @return
	 */
	@play.db.jpa.Transactional
	public static Result logout() {
		if (UserAuthService.isLogin(session())) {
			User.logout(session());
		}
		UserAuthService.removeKickedFlag(session());
		return redirectIndex();
	}

	/**
	 * 邮箱地址是否存在
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	@play.db.jpa.Transactional
	public static Result emailexists() {
		JsonNode json = getJson();
		ObjectNodeResult result = new ObjectNodeResult();
		if (User.emailexists(json.findPath("email").asText())){
			 result.errorkey("user.register.emailexist", json.findPath("email").asText());
		}
		return ok(result.getObjectNode());
	}

	public static Result forgetMe() {
		RememberMeInfo.forgetMeCurrent();
		UserInfoCookieService.discardCookie();
		return ok();
	}

}
