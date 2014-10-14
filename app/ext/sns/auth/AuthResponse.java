/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-26
 */
package ext.sns.auth;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;

import ext.sns.config.ConfigManager;
import ext.sns.config.ProviderConfig;

/**
 * 
 * 
 * @ClassName: AuthResponse
 * @Description: 授权请求响应
 * @date 2014-3-26 下午2:36:51
 * @author ShenTeng
 * 
 */
public class AuthResponse {

    private static final ALogger LOGGER = Logger.of(AuthResponse.class);

    /**
     * 必须。授权码。
     */
    private String code;

    /**
     * 可选。发送请求是附带的参数，用于对response进行校验。
     */
    private String state;

    private String error;

    private String type;

    private Map<String, String> backExtParam;

    private String providerName;

    /**
     * 必须，原始回调参数。
     */
    private Map<String, String> raw;

    private AuthResponse() {
    }

    /**
     * 创建AuthResonpse对象
     * 
     * @param params 回调参数
     * @return AuthResonpse对象， null - 参数不合法
     */
    public static AuthResponse create(Map<String, String> params) {
        if (MapUtils.isEmpty(params)) {
            return null;
        }
        String code = params.get("code");
        String error = params.get("error");
        if (StringUtils.isBlank(code) && StringUtils.isBlank(error)) {
            LOGGER.error("code and error is blank.");
            return null;
        }
        String state = params.get("state");

        AuthResponse authResponse = new AuthResponse();
        authResponse.code = code;
        authResponse.state = state;
        authResponse.raw = params;
        authResponse.error = error;

        ProviderConfig providerConfig = ConfigManager.getProviderConfigByAuthResponse(authResponse);
        if (null == providerConfig) {
            LOGGER.error("No match ProviderConfig by " + authResponse.toString() + ".");
            return null;
        }
        Map<String, String> callbackParam = providerConfig.getCallbackParam(authResponse);
        if (MapUtils.isEmpty(callbackParam)) {
            LOGGER.error("callbackParam is null.");
            return null;
        }

        String type = callbackParam.get(ConfigManager.TYPE_KEY);
        if (StringUtils.isBlank(type)) {
            LOGGER.error("type is blank.");
            return null;
        }

        authResponse.type = type;
        authResponse.providerName = providerConfig.getName();

        callbackParam.remove(ConfigManager.TYPE_KEY);
        authResponse.backExtParam = callbackParam;

        return authResponse;
    }

    /**
     * 是否为错误返回
     * 
     * @return
     */
    public boolean isError() {
        return StringUtils.isNotBlank(error);
    }

    /**
     * 是否为access_denied错误返回
     * 
     * @return
     */
    public boolean isAccessDeniedError() {
        return isError() && "access_denied".equalsIgnoreCase(error);
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getBackExtInfo() {
        return backExtParam;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getCode() {
        return code;
    }

    public String getState() {
        return state;
    }

    public Map<String, String> getRaw() {
        return raw;
    }

    public String getError() {
        return error;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRaw(Map<String, String> raw) {
        this.raw = raw;
    }

}
