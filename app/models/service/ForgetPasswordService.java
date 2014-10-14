/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-30
 */
package models.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import models.RandomCode;
import models.User;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.mvc.Http;
import utils.CaptchaUtils;
import utils.DateUtils;
import utils.EmailUtil;
import utils.HelomeUtil;
import utils.MD5Util;
import vo.EmailInfo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.base.ObjectNodeResult;
import ext.config.ConfigFactory;
import ext.usercenter.UserCenterService;
import ext.usercenter.UserCenterService.EmailExistResult;
import ext.usercenter.UserCenterService.ModifyPasswordResult;

/**
 * 
 * 
 * @ClassName: ForgetPasswordService
 * @Description: 忘记密码服务类
 * @date 2013-12-30 下午6:03:07
 * @author ShenTeng
 * 
 */
public class ForgetPasswordService {

	private static final ALogger LOGGER = Logger.of(ForgetPasswordService.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	/**
	 * 发送找回密码邮件
	 * 
	 * @param session
	 *            Http.Session
	 * @param email
	 *            目标邮箱
	 * @param captcha
	 *            输入的验证码
	 * @param isVarifyCaptcha
	 *            是否校验验证码
	 * @return
	 */
	public static ObjectNodeResult sendForgetPasswordEmail(String cpKey, final String email, String captcha, boolean isVarifyCaptcha,
			boolean isFirstSend) {
		final ObjectNodeResult result = new ObjectNodeResult();
		if (!HelomeUtil.isEmail(email)) {
			return result.errorkey("forgetpasswd.error.formatemail");
		}
		// 验证码是否正确
		if (isFirstSend && isVarifyCaptcha && !CaptchaUtils.validateCaptcha(cpKey, captcha)) {
			return result.errorkey("forgetpasswd.error.captcha");
		}
		// 调用平台接口
		EmailExistResult emailExistResult = UserCenterService.validateEmailExist(email);
		if (emailExistResult == null || EmailExistResult.UNKNOWN_ERROR.equals(emailExistResult)) {
			LOGGER.error("调用平台接口验证邮箱是否存在失败{0}", email);
			return result.errorkey("forgetpasswd.error.usercenter", email);
		}
		// 若邮箱不存在
		if (EmailExistResult.EMAIL_NOT_EXIST.equals(emailExistResult)) {
			return result.errorkey("forgetpasswd.error.notexist", email);
		}
		// 清除验证码信息
		final String now = sdf.format(new Date());
		// 将时间戳字符串持久化
		new RandomCode().saveOrUpdateRandomCode(email, now);
		
		SendMail(email, now);
		
		result.successkey("forgetpasswd.success");
		
		CaptchaUtils.invalidCaptcha(cpKey);
		
		return result;
	}

	/**
	 * 重置密码
	 * 
	 * @param session
	 *            Http.Session
	 * @param pwd
	 *            用户的新密码
	 * @param email
	 *            用户的邮箱
	 * @param code
	 *            用户的加密串
	 * @return
	 */
	public static ObjectNode resetPassword(Http.Session session, String pwd, String email, String code) {

		// 判断链接是否合法，有效
		ObjectNode result = isLinkValid(email, code);
		if ("false".equals(result.get("result").asText())) {
			result.remove("result");
			return result;
		} else {
			result.remove("result");
		}
		User user = User.findByEmail(email);
		if (null == user) {
		    result.put("error", "对不起，该链接是无效的链接！");
		    return result;
		}

		if (HelomeUtil.trim(pwd).length() < 6) {
			result.put("error", "密码长度不符合要求！");
			return result;
		}
		
		ModifyPasswordResult modifyPasswordResult = UserCenterService.modifyPassword(user.getId(), email, pwd);

		if (!ModifyPasswordResult.STATE.SUCCESS.equals(modifyPasswordResult.state)) {
			if (ModifyPasswordResult.STATE.USERNAME_NOT_EXIST.equals(modifyPasswordResult.state)) {
				result.put("error", "邮箱不存在！");
				return result;
			} else {
				result.put("error", "密码修改失败，请稍后再试！");
				return result;
			}
		}

		LoginUserCache.refreshBySession(session);
		result.put("success", "新密码设置成功！");

		RandomCode.removeByEmail(email);

		return result;
	}

	/**
	 * 判断加密串是否有效，是否合法
	 * 
	 * @return
	 */
	public static ObjectNode isLinkValid(String email, String code) {
		// 得到随机码
		String randomCode = new RandomCode().getRandomCode(email);
		String encryptStr = MD5Util.MD5(email + "#" + randomCode);
		ObjectNode result = Json.newObject();
		// 若加密串相等
		if (encryptStr.equals(code)) {
			Date sendTime = null;
			try {
				sendTime = sdf.parse(randomCode);
			} catch (Exception e) {
				result.put("result", "false");
				result.put("error", "系统错误！");
				return result;
			}
			// 获取配置的超时时间
			long timeout = ConfigFactory.getInt("mail.timeout") * 60 * 1000;
			long expireTime = sendTime.getTime() + timeout;
			long now = new Date().getTime();
			// 是否超时
			if (now > expireTime) {
				result.put("result", "false");
				result.put("error", "对不起，该链接已失效！");
				return result;
			}
			return result.put("result", "true");
		} else {
			result.put("result", "false");
			result.put("error", "对不起，该链接是无效的链接！");
			return result;
		}
	}

	private static void SendMail(String email, String now) {
		// 需要加密的字符串
		String str = email + "#" + now;
		// 加密后的字符串
		String encryptStr = MD5Util.MD5(str);
		// 生成邮件内容
		String content = getBody(encryptStr, email);
		String title = "嗨啰·觅——密码找回";
		
        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setSubject(title);
        emailInfo.setBody(content, "text/html;charset=utf-8");
        emailInfo.setTo(email);
        EmailUtil.pushEmail(emailInfo);
	}

	/**
	 * 生成邮件内容
	 * 
	 * @param link
	 * @return
	 */
	private static String getBody(String encryptStr, String email) {
		String webContextUrl = ConfigFactory.getString("web.context.url");
		String link = webContextUrl + "/resetpwd/" + email + "/" + encryptStr;
		String a = webContextUrl + "/resetpwd/" + email + "/" + encryptStr;
		String date = DateUtils.format(new Date(), DateUtils.FORMAT_DATE);
		StringBuffer sb = new StringBuffer();
		String path = ConfigFactory.getString("upload.url");
		sb.append("<div style=\"width:980px; background: url(")
				.append(path)
				.append("topx/assets/misc/skin/v1.0.0/i/mail-bg.png);height: 470px; margin: 100px auto;padding-top: 28px;\"><div style=\"width:478px; height: 430px;border: 1px solid #c2dbff;margin: 0 auto;border-radius: 5px;-moz-box-shadow:0 2px 0 rgba(224, 237, 255, 0.6); -webkit-box-shadow:0 2px 0 rgba(224, 237, 255, 0.6); box-shadow:0 2px 0 rgba(224, 237, 255, 0.6);\"><div style=\"width: 458px;height: 69px;background: #5c93e1;FILTER: progid:DXImageTransform.Microsoft.gradient(gradientType=0,startColorStr=#5c93e1,endColorStr=#2f69c9);background: linear-gradient(top, #5c93e1, #2f69c9);background: -moz-linear-gradient(top, #5c93e1, #2f69c9);background: -ms-linear-gradient(top,  #5c93e1 0%,#2f69c9 100%);background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#5c93e1), to(#2f69c9));border-radius: 3px 3px 0 0;-webkit-border-radius: 3px 3px 0 0;padding-left: 20px;\"><ul style=\"margin:0; padding: 0;list-style-type: none;width: 458px;height: 69px;line-height: 79px;overflow: hidden;\">")
				.append("<li style=\"list-style-type:none;float:left;height:69px;padding-right:20px;font-family:Arial,Helvetica,sans-serif;font-size: 16px;color: #8fb4ec;font-weight: bold;\"><a href=\"http://www.helome.com\" target=\"_blank\"><img style=\"border: 0;\" src=\"")
				.append(path)
				.append("topx/assets/misc/images/logo.png\"></a></li><li style=\"list-style-type: none;float: left;height: 69px;padding-right: 20px;font-family: Arial, Helvetica, sans-serif;font-size: 16px;color: #8fb4ec;font-weight: bold;\">|</li><li style=\"list-style-type: none;float:left;height: 69px;padding-right: 20px;font-family: Arial, Helvetica, sans-serif;font-size: 16px;color: #8fb4ec;font-weight: bold;\">密码重置</li></ul>")
				.append("</div><div style=\"width:458px;padding: 20px 0 0 20px;\"><div style=\"font-size: 16px;color: #40526a;font-weight: bold;\">")
				.append("亲爱的")
				.append(email)
				.append(":</div><div style=\"margin: 27px 0 0 25px;line-height: 20px;color: #40526a;font-family: Arial, Helvetica, sans-serif;font-size: 12px;\">")
				.append("欢迎使用嗨啰·觅平台。<br /><br />").append("我们收到了您的密码重置请求，请点击该链接重置您的密码：<br />")
				.append("<a style=\"width:458px;font-weight: bold;color: #3a75d7;text-decoration: none;word-break: break-all;\" href=\"")
				.append(link).append("\">").append(a).append("</a><br /><br />").append("如果您要放弃以上修改，请忽略本邮件。<br />")
				.append("如果您未曾申请密码重置，请登录嗨啰·觅服务交易平台检查您的帐号安全性。<br /><br />").append("此致<br />").append("嗨啰·觅团队敬上<br />").append(date)
				.append("<br /><br />").append("<span style=\"color: #a9b2bf;\">请注意，该电子邮件地址不接受回复邮件</span></div></div></div></div>");
		return sb.toString();
	}

}
