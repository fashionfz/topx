/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-25
 */
package ext.sns.config.special;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import play.mvc.Http.Context;
import play.mvc.Http.Cookie;

import utils.WSUtil;
import ext.sns.auth.AuthResponse;
import ext.sns.config.ConfigManager;
import ext.sns.config.DefaultProviderConfig;
import ext.sns.config.SNSConfig;
import ext.sns.util.AuthUtil;

/**
 * 
 * 
 * @ClassName: DefaultProviderConfig
 * @Description: 通用SNS服务提供商配置
 * @date 2014-3-25 下午7:09:37
 * @author ShenTeng
 * 
 */
public class DoubanConfig extends DefaultProviderConfig {
    private static final String CALLBACK_PARAM_COOKIENAME = "_snsOAuth";

    @Override
    public String getRequestAuthFullURI(Map<String, String> callbackParam, String specialParamKey) {
        SNSConfig snsConfig = ConfigManager.getSNSConfig();

        // 拼装URI,标准参数：response_type，client_id，redirect_uri，scope，state。
        StringBuilder requestURI = new StringBuilder(getAuthURI());
        requestURI.append(WSUtil.getURIQueryStringPrefix(getAuthURI()));
        requestURI.append("response_type=code");
        requestURI.append("&client_id=").append(getClientId());

        String fullRedirectURI = generateRedirectURI(getName(), snsConfig.getAuthRedirectUri());

        // 放置callbackParam在cookie中
        if (MapUtils.isNotEmpty(callbackParam)) {
            Context.current()
                    .response()
                    .setCookie(CALLBACK_PARAM_COOKIENAME, AuthUtil.encodeState(callbackParam), 15 * 60, "/", null,
                            false, true);
        }

        try {
            requestURI.append("&redirect_uri=").append(URLEncoder.encode(fullRedirectURI, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        if (StringUtils.isNotBlank(getScope())) {
            requestURI.append("&scope=").append(getScope());
        }

        if (MapUtils.isNotEmpty(getSpecialParam()) && StringUtils.isNotBlank(specialParamKey)) {
            if (getSpecialParam().containsKey(specialParamKey)) {
                requestURI.append("&").append(getSpecialParam().get(specialParamKey));
            }
        }

        return requestURI.toString();
    }

    @Override
    public Map<String, String> getCallbackParam(AuthResponse authResponse) {
        String value = null;
        Context.current().response().discardCookie(CALLBACK_PARAM_COOKIENAME);
        Cookie cookie = Context.current().request().cookie(CALLBACK_PARAM_COOKIENAME);
        if (null != cookie) {
            value = cookie.value();
        }
        return AuthUtil.decodeState(value);
    }

}
