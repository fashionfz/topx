/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-27
 */
package ext.sns.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import utils.WSUtil;
import ext.sns.config.ConfigManager;
import ext.sns.config.ProviderConfig;

/**
 * 
 * 
 * @ClassName: OAuthClient
 * @Description: 获取第三方开放授权，目前只支持Authorization Code Grant模式
 * @date 2014-3-27 下午3:34:58
 * @author ShenTeng
 * 
 */
public class OAuth2Client {

    private static final ALogger LOGGER = Logger.of(OAuth2Client.class);

    /**
     * 获取授权请求的URI。
     * 
     * @param providerName 服务提供者名称
     * @param type 提供商类型，不能为null或empty
     * @param callbackParam 回调参数，可为null
     * @param specialParamKey 特殊请求参数Key。对应配置文件中个provider的key，可自定义，以满足不同提供商的特殊参数。可为null
     * @return 授权请求URI
     */
    public static String getAuthRequestURI(String providerName, String type, Map<String, String> callbackParam,
            String specialParamKey) {
        if (StringUtils.isBlank(providerName)) {
            throw new IllegalArgumentException("参数不能为空。providerString=" + providerName);
        }

        List<String> providerList = ConfigManager.getProviderNameByTypes(type);
        if (!providerList.contains(providerName)) {
            throw new IllegalArgumentException("没有找到匹配的providerName和type。providerName = " + providerName + " ,type = "
                    + type + ".");
        }
        callbackParam.put(ConfigManager.TYPE_KEY, type);

        ProviderConfig providerConfig = ConfigManager.getProviderConfigByName(providerName);
        String requestAuthFullURI = providerConfig.getRequestAuthFullURI(callbackParam, specialParamKey);

        LOGGER.debug("requestAuthFullURI:" + requestAuthFullURI);
        return requestAuthFullURI;
    }

    /**
     * 根据授权请求回调URI获得回调参数Map
     * 
     * @param uri 授权请求回调URI
     * @return 回调参数Map
     */
    public static Map<String, String> getAuthBackParamByURI(String uri) {
        // 处理多个问号的URI，将第1个以后的问号替换成&
        int countMatches = StringUtils.countMatches(uri, "?");
        if (countMatches > 1) {
            int firstIndex = uri.indexOf("?");

            StringBuilder uriBuilder = new StringBuilder(uri);
            for (int i = 0; i < countMatches - 1; ++i) {
                int start = uriBuilder.indexOf("?", firstIndex + 1);
                uriBuilder.replace(start, start + 1, "&");
            }
            uri = uriBuilder.toString();
        }

        String paramStr = uri.substring(uri.indexOf("?") + 1);

        Map<String, String> paramMap = new HashMap<String, String>();

        String[] pairArray = paramStr.split("&");
        for (String pair : pairArray) {
            String[] paramArray = pair.split("=");

            String val = paramArray.length == 2 ? paramArray[1] : "";
            paramMap.put(paramArray[0], val);
        }

        return paramMap;
    }

    /**
     * 从授权请求回调的参数中获取AuthResponse对象。
     * 
     * @param params 必须。授权请求回调的参数
     * @return AuthResponse对象, null - 参数不合法
     */
    public static AuthResponse getAuthResponse(Map<String, String> params) {
        return AuthResponse.create(params);
    }

    /**
     * 获取accessToken
     * 
     * @param authResponse 必须。AuthResponse对象
     * @return 请求的原始返回值，不同的第三方返回的格式可能有差别。 null - 请求失败。
     */
    public static AccessTokenResponse accessToken(AuthResponse authResponse) {
        if (null == authResponse) {
            throw new IllegalArgumentException("参数不能为空。authResponse=" + authResponse);
        }

        ProviderConfig providerConfig = ConfigManager.getProviderConfigByAuthResponse(authResponse);

        if (null == providerConfig) {
            throw new IllegalArgumentException("无对应提供商。authResponse：" + authResponse.toString());
        }

        String accessTokenFullURI = providerConfig.getAccessTokenFullURI(authResponse);

        LOGGER.debug("accessTokenFullURI:" + accessTokenFullURI);

        String body = WSUtil.postFormURLEncoded(accessTokenFullURI).get(30000).getBody();

        LOGGER.debug("accessTokenResponse:" + body);

        AccessTokenResponse response = AccessTokenResponse.create(body);

        if (null == response) {
            LOGGER.error("无效的accessToken响应。body：" + body);
        }

        return response;
    }
}
