/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-19
 */
package mobile.controllers;

import mobile.service.AuthService;
import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.util.jsonparam.JsonParam;
import mobile.util.jsonparam.JsonParamConfig;
import mobile.util.jsonparam.config.item.Require;
import mobile.vo.result.CommonVO;
import mobile.vo.result.MobileResult;
import play.db.jpa.Transactional;
import play.mvc.Result;

/**
 * 
 * 
 * @ClassName: AuthApp
 * @Description: 用户授权（登录、注册等）相关APP
 * @date 2014-6-19 下午2:48:28
 * @author ShenTeng
 * 
 */
public class AuthApp extends MobileBaseApp {

    @Transactional
    public static Result login(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("email", Require.Yes);
        config.stringField("password", Require.Yes);
        config.intField("timeZoneOffset", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = AuthService.login(param.getString("email"),
                param.getString("password"), param.getInt("timeZoneOffset"));

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @Transactional
    public static Result toThirdPartyLoginPage(String from, String providerName, String referer) {
        return AuthService.toThirdPartyLoginPagetoThirdPartyLoginPage(providerName, referer);
    }

    @Transactional
    public static Result logout(String from) {
        ServiceResult serviceResult = AuthService.logout();
        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result register(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("email", Require.Yes);
        config.stringField("password", Require.Yes);
        config.intField("timeZoneOffset", Require.Yes);
        config.intField("gender", Require.No);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = AuthService.register(param.getString("email"),
                param.getString("password"), param.getInt("timeZoneOffset"), param.getInt("gender"));

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @Transactional
    public static Result forgetPassword(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("email", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = AuthService.sendForgetPasswordEmail(param.getString("email"));

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @Transactional
    public static Result validateEmailExist(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("email", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = AuthService.validateEmailExist(param.getString("email"));

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

}
