/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年7月21日
 */
package mobile.service;

import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.vo.result.CommonVO;
import models.Gender;
import models.User;
import models.service.ForgetPasswordService;

import org.apache.commons.lang3.StringUtils;

import play.libs.Json;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import play.mvc.Result;
import utils.HelomeUtil;
import utils.TimeZoneUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.base.ObjectNodeResult;
import controllers.user.OAuthApp;
import ext.sns.service.ProviderType;
import ext.usercenter.UserAuthService;

/**
 *
 *
 * @ClassName: AuthService
 * @Description: 鉴权相关的服务，例如：登录、注册、找回密码
 * @date 2014年7月21日 下午5:21:38
 * @author ShenTeng
 * 
 */
public class AuthService {

    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码明文
     * @param timeZoneOffset 用户时区偏移量，分钟为单位。例如+8区为480。
     */
    public static ServiceVOResult<CommonVO> login(String username, String password, Integer timeZoneOffset) {
        Session session = Context.current().session();
        TimeZoneUtils.setTimeZoneOffset2Session(session, timeZoneOffset);
        ObjectNodeResult loginResult = User.login(username, password, false);

        ServiceVOResult<CommonVO> serviceVOResult = ServiceVOResult.create(loginResult);
        if (serviceVOResult.isSuccess()) {
            User user = loginResult.getUser();
            CommonVO vo = CommonVO.create();

            ObjectNode userNode = Json.newObject();
            userNode.put("id", user.getId());
            userNode.put("name", user.getName());
            userNode.put("avatar_190", user.getAvatar(190));
            userNode.put("avatar_70", user.getAvatar(70));
            userNode.put("avatar_22", user.getAvatar(22));
            userNode.put("phoneNumber", StringUtils.defaultIfBlank(loginResult.getUser().phoneNumber, null));

            vo.set("user", userNode);
            vo.set("token", UserAuthService.getTokenInSession(session));
            serviceVOResult.setVo(vo);
        }

        return serviceVOResult;
    }

    /**
     * 第三方登录页面
     */
    public static Result toThirdPartyLoginPagetoThirdPartyLoginPage(String providerName, String referer) {
        return OAuthApp.requestAuth(providerName, referer, ProviderType.LOGIN);
    }

    /**
     * 当前用户登出
     */
    public static ServiceResult logout() {
        Session session = Context.current().session();
        if (UserAuthService.isLogin(session)) {
            User.logout(session);
        } else {
            return ServiceResult.error("221001", "用户已经注销");
        }
        return ServiceResult.success();
    }

    /**
     * 用户注册
     * 
     * @param username 用户名。必须
     * @param password 密码明文。必须
     * @param timeZoneOffset 用户时区偏移量，分钟为单位。例如+8区为480
     * @param gender 性别，非必须。0:男，1:女，默认：男
     * @return
     */
    public static ServiceVOResult<CommonVO> register(String username, String password, Integer timeZoneOffset,
            Integer gender) {
        Gender genderObj = gender == Integer.valueOf(1) ? Gender.WOMAN : Gender.MAN;

        Session session = Context.current().session();
        TimeZoneUtils.setTimeZoneOffset2Session(session, timeZoneOffset);
        ObjectNodeResult objectNodeResult = User.registerFromMobile(username, password, genderObj);

        ServiceVOResult<CommonVO> serviceVOResult = ServiceVOResult.create(objectNodeResult);
        if (serviceVOResult.isSuccess()) {
            User user = objectNodeResult.getUser();
            CommonVO vo = CommonVO.create();

            ObjectNode userNode = Json.newObject();
            userNode.put("id", user.getId());
            userNode.put("name", user.getName());
            userNode.put("avatar_190", user.getAvatar(190));
            userNode.put("avatar_70", user.getAvatar(70));
            userNode.put("avatar_22", user.getAvatar(22));

            vo.set("user", userNode);
            serviceVOResult.setVo(vo);
        }

        return serviceVOResult;
    }

    /**
     * 发送忘记密码邮件
     * 
     * @param email 邮箱地址。必须
     * @return
     */
    public static ServiceVOResult<CommonVO> sendForgetPasswordEmail(String email) {
        ObjectNodeResult objectNodeResult = ForgetPasswordService.sendForgetPasswordEmail("", email, null, false, true);

        ServiceVOResult<CommonVO> serviceVOResult = ServiceVOResult.create(objectNodeResult);
        if (serviceVOResult.isSuccess()) {
            CommonVO vo = CommonVO.create();
            vo.set("success", objectNodeResult.getObjectNode().path("success").asText());
            serviceVOResult.setVo(vo);
        }

        return serviceVOResult;
    }

    /**
     * 校验邮箱存在性
     */
    public static ServiceVOResult<CommonVO> validateEmailExist(String email) {
        if (!HelomeUtil.isEmail(email)) {
            return ServiceVOResult.error("100005", "传入email不合法");
        }

        ServiceVOResult<CommonVO> serviceVOResult = ServiceVOResult.success();
        CommonVO vo = CommonVO.create();
        vo.set("exists", User.emailexists(email));
        serviceVOResult.setVo(vo);

        return serviceVOResult;
    }

}
