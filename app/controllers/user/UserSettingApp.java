/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package controllers.user;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import models.User;
import models.service.PhoneVerifyCodeService;
import models.service.PhoneVerifyCodeService.PhoneVerifyCodeType;
import models.service.PhoneVerifyCodeService.SendVerifyCodeResult;

import org.apache.commons.lang3.StringUtils;

import play.cache.Cache;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import utils.DateUtils;
import utils.EmailUtil;
import utils.HelomeUtil;
import vo.EmailInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import common.Constants;

import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;
import ext.config.ConfigFactory;
import ext.sns.model.UserOAuth;
import ext.sns.service.UserOAuthService;
import ext.usercenter.UserCenterService;
import ext.usercenter.UserCenterService.PasswordSecurityGrade;
import ext.usercenter.UserCenterService.PhoneNumExistResult;

/**
 * 
 * 
 * 
 * @ClassName: UserSettingApp
 * @Description: 个人设置controller
 * @date 2013-11-5 上午10:46:56
 * @author ShenTeng
 * 
 */
public class UserSettingApp extends BaseApp {

    /**
     * 设置页
     * 
     * @return
     */
    @Transactional(readOnly = true)
    public static Result detail() {
        User user = User.getFromSession(session());
        PasswordSecurityGrade passwordSecurityGrade = User.getPasswordSecurityGrade(session());
        return ok(views.html.usercenter.usersetting.render(user, passwordSecurityGrade));
    }

    /**
     * 第三方登录设置页
     * 
     * @return
     */
    @Transactional(readOnly = true)
    public static Result thirdaccountsetting() {
        return ok(views.html.usercenter.thirdaccountsetting.render());
    }

    /**
     * 修改登录邮箱
     * 
     * @return
     */
    @Transactional(readOnly = false)
    public static Result changeEmail() {
        JsonNode json = getJson();

        // 校验参数
        boolean isValidParams = json.hasNonNull("old") && json.hasNonNull("new") && json.hasNonNull("psw");
        if (!isValidParams) {
            return illegalParameters();
        }

        User user = User.getFromSession(session());

        // 修改email
        ObjectNodeResult result = User.changeEmail(user, json.get("old").asText(), json.get("new").asText(),
                json.get("psw").asText(), session());

        return ok(result.getObjectNode());
    }

    /**
     * 修改密码
     * 
     * @return
     */
    @Transactional(readOnly = false)
    public static Result changePassword() {
        JsonNode json = getJson();

        // 校验参数
        boolean isValidParams = json.hasNonNull("old") && json.hasNonNull("new");
        if (!isValidParams) {
            return illegalParameters();
        }

        User user = User.getFromSession(session());

        ObjectNodeResult result = User.changePassword(user, json.get("old").asText(), json.get("new").asText(),
                session());

        return ok(result.getObjectNode());
    }

    /**
     * 修改安全设置
     * 
     * @return
     */
    @Transactional(readOnly = false)
    public static Result modifySafetyReminder() {
        JsonNode newCfg = getJson();

        User user = User.getFromSession(session());

        ObjectNodeResult result = User.modifySafetyReminder(user, newCfg, session());

        return ok(result.getObjectNode());
    }
    
    /**
     * 修改预约提醒设置
     * 
     * @return
     */
    @Transactional(readOnly = false)
    public static Result modifyBookingReminder() {
        JsonNode newCfg = getJson();
        
        User user = User.getFromSession(session());
        
        ObjectNodeResult result = User.modifyBookingReminder(user, newCfg, session());
        
        return ok(result.getObjectNode());
    }

    /**
     * 绑定手机
     * 
     * @return
     */
    @Transactional(readOnly = false)
    public static Result bindMobilePhone() {
        JsonNode json = getJson();
        ObjectNodeResult result =new ObjectNodeResult();
        User user = User.getFromSession(session());
         if(StringUtils.isNotBlank(user.getPhoneNumber())){
        	 result.error("您已经绑定了手机","500006");
        	 
         }      
        // 校验参数
        boolean isValidParams = json.hasNonNull("phoneNum") && json.hasNonNull("code");
        if (!isValidParams) {
            return illegalParameters();
        }

        result = User.bindMobilePhone(user,json.findPath("phoneNum").asText(), json.findPath("phoneNum").asText(), json.findPath("code").asText(),
                session());

        return ok(result.getObjectNode());
    }
    
    /**
     * 修改手机号
     * 
     * @return
     */
    @Transactional(readOnly = false)
    public static Result updateMobilePhone() {
        JsonNode json = getJson();
        
        // 校验参数
        boolean isValidParams = json.hasNonNull("phoneNum") && json.hasNonNull("code");
        if (!isValidParams) {
            return illegalParameters();
        }

        User user = User.getFromSession(session());
         String phoneNum=user.getMaskPhoneNumber();
        ObjectNodeResult result = User.bindMobilePhone(user,phoneNum, json.get("phoneNum").asText(), json.get("code").asText(),
                session());

        return ok(result.getObjectNode());
    }

    
    /**
     * 修改手机号,原手机不能接收短信的情况
     * 
     * @return
     */
    @Transactional(readOnly = false)
    public static Result bindNewPhone() {
        JsonNode json = getJson();
        
        // 校验参数
        if (!json.hasNonNull("code") || !json.hasNonNull("newPhoneNum") || !json.hasNonNull("key")) {
            return illegalParameters();
        }
        
        User user = User.getFromSession(session());
        ObjectNodeResult result = new ObjectNodeResult();
        
        Long userId = (Long) Cache.get(Constants.CACHE_CHANGE_PHONE_BY_EMAIL_KEY_CU + json.get("key").asText());
        if (null == userId) {
            return ok(result.error("对不起，验证邮件已失效或者过期，请重新发送验证邮件").getObjectNode());
        }
        if (!user.id.equals(userId)) {
            return ok(result.error("对不起，验证邮件不属于当前用户，请使用发送验证邮件的帐号登录").getObjectNode());
        }
        
        result = User.bindNewPhone(user, json.findPath("newPhoneNum").asText(), json.findPath("code").asText(),
                session());
        if (result.isSuccess()) {
            //user-code key
            String ucKey = Constants.CACHE_CHANGE_PHONE_BY_EMAIL_KEY_UC + user.id;
            
            String oldCode = (String) Cache.get(ucKey);
            if (StringUtils.isNotBlank(oldCode)) {
                String oldCUKey = Constants.CACHE_CHANGE_PHONE_BY_EMAIL_KEY_CU + oldCode;
                Cache.remove(ucKey);
                Cache.remove(oldCUKey);
            }
        }
        
        return ok(result.getObjectNode());
    }
    /**
     * 手机号重复性验证
     * 
     * @return
     */
    public static Result phoneNumExists() {
        JsonNode json = getJson();

        // 校验参数
        boolean isValidParams = json.hasNonNull("phoneNum");
        if (!isValidParams) {
            return illegalParameters();
        }

        ObjectNodeResult result = new ObjectNodeResult();
        String phoneNum = json.get("phoneNum").asText();

        if (HelomeUtil.trim(phoneNum).length() != 11) {
            result.error("手机号错误");
        } else {
            PhoneNumExistResult validateResult = UserCenterService.validatePhoneNumExist(phoneNum);
            if (PhoneNumExistResult.EXIST == validateResult) {
                result.put("exists", true);
            } else if (PhoneNumExistResult.NOT_EXIST == validateResult) {
                result.put("exists", false);
            } else {
                result.error("系统错误");
            }
        }

        return ok(result.getObjectNode());
    }
    
    
    @Transactional
    public static Result queryConnectedSNS() {
        User user = User.getFromSession(session());
        Map<String, UserOAuth> userOAuthMap = UserOAuthService.getValidByUserId(user.id);
        ArrayNode sns = Json.newObject().arrayNode();
        for (Map.Entry<String, UserOAuth> e : userOAuthMap.entrySet()) {
            sns.add(e.getKey());
        }
        ObjectNodeResult result = new ObjectNodeResult();
        result.put("sns", sns);
        return ok(result.getObjectNode());
    }


    /**
     * 发送手机验证码
     * 
     * @return
     */
    public static Result sendPhoneVerificationCode() {
        JsonNode json = getJson();

        // 校验参数
        boolean isValidParams = json.hasNonNull("phoneNum");
        if (!isValidParams) {
            return illegalParameters();
        }

        User user = User.getFromSession(session());
        ObjectNodeResult result = new ObjectNodeResult();
        String phoneNum = json.get("phoneNum").asText();

        if (HelomeUtil.trim(phoneNum).length() != 11) {
            result.error("手机号错误");
        } else {
            SendVerifyCodeResult sendResult = PhoneVerifyCodeService.sendVerifyCode(
                    PhoneVerifyCodeType.BIND_MOBILE_PHONE, String.valueOf(user.id), phoneNum);

            if (SendVerifyCodeResult.TOO_MANY == sendResult) {
                result.error("验证码发送太过频繁,请稍候再发送");
            } else if (SendVerifyCodeResult.FAIL == sendResult) {
                result.error("验证码发送失败");
            }
        }

        return ok(result.getObjectNode());
    }
    
     /**
      * 发送修改手机号链接页面到用户邮箱，用于当用户原手机无法接受短信的情况
      * @param user 
      * @return
      */
    public static void sendUdpByEmail(User user){
        String webContextUrl = ConfigFactory.getString("web.context.url");
        String code = UUID.randomUUID().toString().replace("-", "");
        //user-code key
        String ucKey = Constants.CACHE_CHANGE_PHONE_BY_EMAIL_KEY_UC + user.id;
        //code-user key
        String cuKey = Constants.CACHE_CHANGE_PHONE_BY_EMAIL_KEY_CU + code;
        String oldCode = (String) Cache.get(ucKey);
        if (StringUtils.isNotBlank(oldCode)) {
            String oldCUKey = Constants.CACHE_CHANGE_PHONE_BY_EMAIL_KEY_CU + oldCode;
            Cache.remove(ucKey);
            Cache.remove(oldCUKey);
        }
        
        Cache.set(ucKey, code, 10*60);
        Cache.set(cuKey, user.id, 10*60);
        
        String locatin=webContextUrl+"/user/usersetting/phonechange/" + code;
        String path = ConfigFactory.getString("upload.url");
        String date = DateUtils.format(new Date(), DateUtils.FORMAT_DATE);
    	String email = user.getEmail()==null?"用户":user.getEmail().trim();
    	
        StringBuffer sb = new StringBuffer();
        sb.append("<div style=\"width:980px; background: url(").append(path).append("topx/assets/misc/skin/v1.0.0/i/mail-bg.png);height: 470px; margin: 100px auto;padding-top: 28px;\"><div style=\"width:478px; height: 430px;border: 1px solid #c2dbff;margin: 0 auto;border-radius: 5px;-moz-box-shadow:0 2px 0 rgba(224, 237, 255, 0.6); -webkit-box-shadow:0 2px 0 rgba(224, 237, 255, 0.6); box-shadow:0 2px 0 rgba(224, 237, 255, 0.6);\"><div style=\"width: 458px;height: 69px;FILTER: progid:DXImageTransform.Microsoft.gradient(gradientType=0,startColorStr=#5c93e1,endColorStr=#2f69c9);background: linear-gradient(top, #5c93e1, #2f69c9);background: -moz-linear-gradient(top, #5c93e1, #2f69c9);background: -ms-linear-gradient(top,  #5c93e1 0%,#2f69c9 100%);background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#5c93e1), to(#2f69c9));border-radius: 3px 3px 0 0;-webkit-border-radius: 3px 3px 0 0;padding-left: 20px;\"><ul style=\"margin:0; padding: 0;list-style-type: none;width: 458px;height: 69px;line-height: 79px;overflow: hidden;\">")
		.append("<li style=\"list-style-type:none;float:left;height:69px;padding-right:20px;font-family:Arial,Helvetica,sans-serif;font-size: 16px;color: #8fb4ec;font-weight: bold;\"><a href=\"http://www.helome.com\" target=\"_blank\"><img style=\"padding-top: 11px;border: 0;\" src=\"").append(path).append("topx/assets/misc/images/logo.png\" width=\"157\" height=\"47\"></a></li><li style=\"list-style-type: none;float: left;height: 69px;padding-right: 20px;font-family: Arial, Helvetica, sans-serif;font-size: 16px;color: #8fb4ec;font-weight: bold;\">|</li><li style=\"list-style-type: none;float:left;height: 69px;padding-right: 20px;font-family: Arial, Helvetica, sans-serif;font-size: 16px;color: #8fb4ec;font-weight: bold;\">手机重置</li></ul>")
		.append("</div><div style=\"width:458px;padding: 20px 0 0 20px;\"><div style=\"font-size: 16px;color: #40526a;font-weight: bold;\">")
        .append("亲爱的").append(email).append(":</div><div style=\"margin: 27px 0 0 25px;line-height: 20px;color: #40526a;font-family: Arial, Helvetica, sans-serif;font-size: 12px;\">")
        .append("欢迎使用嗨啰·觅平台。<br /><br />")
          .append("我们收到了您的手机号码重置请求，请点击该链接重置您的手机号：<br />")
           .append("<a style=\"width:458px;font-weight: bold;color: #3a75d7;text-decoration: none;word-break: break-all;\" href=\"").append(locatin).append("\">").append(locatin).append("</a><br /><br />")
           .append("如果您要放弃以上修改，请忽略本邮件。<br />")
           .append("如果您未曾申请手机号码重置，请登录嗨啰·觅服务交易平台检查您的帐号安全性。<br /><br />")
           .append("此致<br />")
           .append("嗨啰·觅团队敬上<br />")
           .append(date).append("<br /><br />")
           .append("<span style=\"color: #a9b2bf;\">请注意，该电子邮件地址不接受回复邮件</span></div></div></div></div>");
    	EmailInfo emailInfo = new EmailInfo();
        emailInfo.setSubject("嗨啰·觅——修改手机");
        emailInfo.setBody(sb.toString(), "text/html;charset=utf-8");
        emailInfo.setTo(email);
        EmailUtil.pushEmail(emailInfo);
    
    	
    
    }
    
    
    
    /**
     * 原手机号不能接受短信调用发邮件接口
     * 
     * @return
     */
    public static Result sendEmail() {
       
        ObjectNodeResult result = new ObjectNodeResult();
        sendUdpByEmail(User.getFromSession(session()));
        return ok(result.getObjectNode());
    }
    
    
    /**
     * 完善用户信息，用于第三方登录的用户
     * 
     * @return
     */
    @Transactional
    public static Result completeUserInfo() {
        JsonNode json = getJson();

        if (!json.hasNonNull("email") || !json.hasNonNull("pwd")) {
            return illegalParameters();
        }

        String email = json.get("email").asText();
        String pwd = json.get("pwd").asText();

        ObjectNodeResult result = User.completeUserInfo(session(), email, pwd);

        return ok(result.getObjectNode());
    }

    /**
     * 修改手机页面
     * 
     * @return
     */
    @Transactional(readOnly = true)
    public static Result phoneChange(String code) {
        User user = User.getFromSession(session());
        Long userId = (Long) Cache.get(Constants.CACHE_CHANGE_PHONE_BY_EMAIL_KEY_CU + code);
        if (null == userId) {
            return errorInfo("对不起，验证邮件已失效或者过期，请重新发送验证邮件", "请重新发送验证邮件");
        }
        if (!user.id.equals(userId)) {
            return errorInfo("对不起，验证邮件不属于当前用户，请使用发送验证邮件的帐号登录", "请更换帐号登录");
        }
        
        return ok(views.html.usercenter.phonechange.render(code));
    }

    /**
     * 修改手机号，有原手机的情况，直接向原手机发送验证码
     * 
     * @return
     */
    public static Result sendVerificationCodeByPhone() {
        User user = User.getFromSession(session());
        String phoneNum =user.getMaskPhoneNumber();
        ObjectNodeResult result = new ObjectNodeResult();
        SendVerifyCodeResult sendResult = PhoneVerifyCodeService.sendVerifyCode(
                    PhoneVerifyCodeType.BIND_MOBILE_PHONE, String.valueOf(user.id), phoneNum);

            if (SendVerifyCodeResult.TOO_MANY == sendResult) {
                result.error("验证码发送太过频繁,请稍候再发送");
            } else if (SendVerifyCodeResult.FAIL == sendResult) {
                result.error("验证码发送失败");
            }else{
            	
            	result.put("phoneNum", user.getMaskPhoneNum());
            }

        return ok(result.getObjectNode());
    }

    
    /**
     * 修改手机号，没有手机的情况，向新手机发送验证码
     * 
     * @return
     */
    public static Result sendVerificationCodeByNewPhone() {
        JsonNode json = getJson();

        User user = User.getFromSession(session());
        if (!json.hasNonNull("newPhoneNum")){
        	
        	return illegalParameters();
        } 
       String newPhoneNum = json.findPath("newPhoneNum").asText();
        ObjectNodeResult result = new ObjectNodeResult();
        SendVerifyCodeResult sendResult = PhoneVerifyCodeService.sendVerifyCode(
                    PhoneVerifyCodeType.BIND_MOBILE_PHONE, String.valueOf(user.id), newPhoneNum);

            if (SendVerifyCodeResult.TOO_MANY == sendResult) {
                result.error("验证码发送太过频繁,请稍候再发送");
            } else if (SendVerifyCodeResult.FAIL == sendResult) {
                result.error("验证码发送失败");
            }

        return ok(result.getObjectNode());
    }
    
    /**
     * 修改手机，如果手机收不到短信，发送邮箱页面
     * 
     * @return
     */
    @Transactional(readOnly = true)
    public static Result phoneMailSuccess() {
        return ok(views.html.usercenter.phonemailsuccess.render());
    }

    /**
     * 修改手机，如果手机收不到短信，发送邮箱页面
     * 
     * @return
     */
    @Transactional(readOnly = true)
    public static Result phoneResetSuccess() {
        return ok(views.html.usercenter.phoneresetsuccess.render());
    }
}
