/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年7月21日
 */
package mobile.service;

import java.util.Map;

import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.vo.result.CommonVO;
import models.User;
import models.service.PhoneVerifyCodeService;
import models.service.PhoneVerifyCodeService.PhoneVerifyCodeType;
import models.service.PhoneVerifyCodeService.SendVerifyCodeResult;

import org.apache.commons.lang3.StringUtils;

import play.libs.Json;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import play.mvc.Result;
import utils.HelomeUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import controllers.base.ObjectNodeResult;
import controllers.user.OAuthApp;
import ext.sns.model.UserOAuth;
import ext.sns.service.OAuth2Service;
import ext.sns.service.ProviderType;
import ext.sns.service.UserOAuthService;
import ext.usercenter.UserCenterService;
import ext.usercenter.UserCenterService.PhoneNumExistResult;

/**
 *
 *
 * @ClassName: UserSettingService
 * @Description: 用户设置服务
 * @date 2014年7月21日 下午3:30:26
 * @author ShenTeng
 * 
 */
public class UserSettingService {

    /**
     * 修改密码
     * 
     * @param oldPsw 旧密码
     * @param newPsw 新密码
     * @return
     */
    public static ServiceResult changePassword(String oldPsw, String newPsw) {
        Session session = Context.current().session();
        User user = User.getFromSession(session);

        ObjectNodeResult result = User.changePassword(user, oldPsw, newPsw, session);

        return ServiceResult.create(result);
    }

    /**
     * 获取安全设置
     */
    public static ServiceVOResult<CommonVO> getSafetyReminderCfg() {
        User user = User.getFromSession(Context.current().session());
        JsonNode cfg = user.getSafetyReminderCfgJson();

        CommonVO vo = CommonVO.create();
        vo.set("cfg", cfg);

        ServiceVOResult<CommonVO> result = ServiceVOResult.success();
        result.setVo(vo);
        return result;
    }

    /**
     * 修改安全提醒设置
     * 
     * @param newCfg 新的设置
     * @return
     */
    public static ServiceResult modifySafetyReminder(JsonNode newCfg) {
        Session session = Context.current().session();
        User user = User.getFromSession(session);

        ObjectNodeResult result = User.modifySafetyReminder(user, newCfg, session);

        return ServiceResult.create(result);
    }

    /**
     * 绑定手机
     * 
     * @param phoneNum 手机号
     * @param verifyCode 验证码
     * @return
     */
    public static ServiceResult bindMobilePhone(String phoneNum, String verifyCode) {
        Session session = Context.current().session();
        User user = User.getFromSession(session);
        if (StringUtils.isNotBlank(user.getPhoneNumber())) {
            return ServiceResult.error("247001", "已经绑定过手机");
        }

        ObjectNodeResult result = User.bindMobilePhone(user, phoneNum, phoneNum, verifyCode, session);

        return ServiceResult.create(result);
    }

    /**
     * 验证手机号是否重复
     * 
     * @param phoneNum 待验证手机号
     * @return
     */
    public static ServiceVOResult<CommonVO> validatePhoneNumExist(String phoneNum) {
        if (HelomeUtil.trim(phoneNum).length() != 11) {
            return ServiceVOResult.error("245001", "手机号无效");
        }

        PhoneNumExistResult validateResult = UserCenterService.validatePhoneNumExist(phoneNum);
        CommonVO vo = CommonVO.create();
        if (PhoneNumExistResult.EXIST == validateResult) {
            vo.set("exists", true);
        } else if (PhoneNumExistResult.NOT_EXIST == validateResult) {
            vo.set("exists", false);
        } else {
            return ServiceVOResult.error("100001", "系统错误");
        }

        ServiceVOResult<CommonVO> result = ServiceVOResult.success();
        result.setVo(vo);
        return result;
    }

    /**
     * 发送手机验证码
     * 
     * @param phoneNum 手机号码
     * @return
     */
    public static ServiceResult sendPhoneVerifyCode(String phoneNum) {
        Session session = Context.current().session();
        User user = User.getFromSession(session);

        ObjectNodeResult result = new ObjectNodeResult();

        if (HelomeUtil.trim(phoneNum).length() != 11) {
            return ServiceResult.error("246001", "手机号无效");
        }

        SendVerifyCodeResult sendResult = PhoneVerifyCodeService.sendVerifyCode(PhoneVerifyCodeType.BIND_MOBILE_PHONE,
                String.valueOf(user.id), phoneNum);

        if (SendVerifyCodeResult.TOO_MANY == sendResult) {
            return ServiceResult.error("246002", "验证码发送太过频繁,请稍候再发送");
        } else if (SendVerifyCodeResult.FAIL == sendResult) {
            return ServiceResult.error("246003", "验证码发送失败");
        }

        return ServiceResult.create(result);
    }

    /**
     * 获取已绑定的SNS
     * 
     * @return
     */
    public static ServiceVOResult<CommonVO> getConnectedSNS() {
        User user = User.getFromSession(Context.current().session());
        Map<String, UserOAuth> userOAuthMap = UserOAuthService.getValidByUserId(user.id);
        ArrayNode sns = Json.newObject().arrayNode();
        for (Map.Entry<String, UserOAuth> e : userOAuthMap.entrySet()) {
            sns.add(e.getKey());
        }

        CommonVO vo = CommonVO.create();
        vo.set("sns", sns);

        ServiceVOResult<CommonVO> result = ServiceVOResult.success();
        result.setVo(vo);
        return result;
    }

    /**
     * SNS绑定页面
     * 
     * @param providerName sns名称
     * @param referer 授权成功后的重定向URI
     * @return
     */
    public static Result toBindSNSPage(String providerName, String referer) {
        return OAuthApp.requestAuth(providerName, referer, ProviderType.SNS);
    }

    /**
     * 解绑SNS
     * 
     * @param providerName sns名称
     * @return
     */
    public static ServiceResult unbindSNS(String providerName) {
        if (!OAuth2Service.checkProviderName(providerName)) {
            return ServiceResult.error("100005", "无效的SNS名称：" + providerName);
        }

        User user = User.getFromSession(Context.current().session());
        UserOAuthService.revokeAuthorize(user, providerName);

        return ServiceResult.success();
    }

}
