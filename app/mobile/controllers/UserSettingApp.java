/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package mobile.controllers;

import mobile.service.UserSettingService;
import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.util.jsonparam.JsonParam;
import mobile.util.jsonparam.JsonParamConfig;
import mobile.util.jsonparam.config.item.ArrayItemType;
import mobile.util.jsonparam.config.item.CanEmpty;
import mobile.util.jsonparam.config.item.Require;
import mobile.vo.result.CommonVO;
import mobile.vo.result.MobileResult;
import play.db.jpa.Transactional;
import play.mvc.Result;

/**
 * 
 *
 *
 * @ClassName: UserSettingApp
 * @Description: 个人设置controller
 * @date 2014年7月21日 下午3:27:45
 * @author ShenTeng
 *
 */
public class UserSettingApp extends MobileBaseApp {

    /**
     * 修改密码
     */
    @Transactional
    public static Result changePassword(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("old", Require.Yes);
        config.stringField("new", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = UserSettingService.changePassword(param.getString("old"), param.getString("new"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    /**
     * 获取安全设置
     */
    @Transactional
    public static Result querySafetyReminderCfg(String from) {
        ServiceVOResult<CommonVO> serviceVOResult = UserSettingService.getSafetyReminderCfg();

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    /**
     * 修改安全设置
     */
    @Transactional(readOnly = false)
    public static Result modifySafetyReminder(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.arrayField("changePhoneNum", Require.Yes, CanEmpty.Yes, ArrayItemType.String);
        config.arrayField("changePassword", Require.Yes, CanEmpty.Yes, ArrayItemType.String);
        config.arrayField("differentPlaceLogin", Require.Yes, CanEmpty.Yes, ArrayItemType.String);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = UserSettingService.modifySafetyReminder(param.getRawJson());

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    /**
     * 绑定手机
     */
    @Transactional
    public static Result bindMobilePhone(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("phoneNum", Require.Yes);
        config.stringField("code", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = UserSettingService.bindMobilePhone(param.getString("phoneNum"),
                param.getString("code"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    /**
     * 手机号重复性验证
     */
    public static Result phoneNumExists(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("phoneNum", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = UserSettingService.validatePhoneNumExist(param
                .getString("phoneNum"));

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    /**
     * 发送手机验证码
     */
    public static Result sendPhoneVerificationCode(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("phoneNum", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = UserSettingService.sendPhoneVerifyCode(param.getString("phoneNum"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result queryConnectedSNS(String from) {
        ServiceVOResult<CommonVO> serviceVOResult = UserSettingService.getConnectedSNS();
        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @Transactional
    public static Result toBindSNSPage(String from, String providerName, String referer) {
        return UserSettingService.toBindSNSPage(providerName, referer);
    }

    @Transactional
    public static Result unbindSNS(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("p", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = UserSettingService.unbindSNS(param.getString("p"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

}
