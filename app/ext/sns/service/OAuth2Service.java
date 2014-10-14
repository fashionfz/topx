/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-27
 */
package ext.sns.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import ext.sns.auth.AccessTokenResponse;
import ext.sns.auth.AuthResponse;
import ext.sns.auth.OAuth2Client;
import ext.sns.config.ConfigManager;

/**
 * 
 * 
 * @ClassName: OAuthService
 * @Description: 第三方开放授权服务
 * @date 2014-3-27 下午5:32:01
 * @author ShenTeng
 * 
 */
public class OAuth2Service {

    /**
     * 根据提供商名称，获取提供商类型
     * 
     * @return 提供商类型集合
     */
    public static List<String> getTypesByProviderName(String providerName) {
        return ConfigManager.getProviderConfigByName(providerName).getTypes();
    }

    /**
     * 获取指定类型的提供商名称
     * 
     * @param typeList 提供商类型集合
     * 
     * @return 提供商名称集合
     */
    public static List<String> getProviderNameByTypes(List<String> typeList) {
        return ConfigManager.getProviderNameByTypes(typeList);
    }

    /**
     * 获取指定类型的提供商名称
     * 
     * @param types 提供商类型集合
     * 
     * @return 提供商名称集合
     */
    public static List<String> getProviderNameByTypes(String... types) {
        return ConfigManager.getProviderNameByTypes(types);
    }

    /**
     * 检查提供商名字是否存在
     * 
     * @param providerName 提供商名字
     * @return 提供商名称集合
     */
    public static boolean checkProviderName(String providerName) {
        return ConfigManager.checkProviderName(providerName);
    }

    /**
     * 获取请求授权URI
     * 
     * @param providerName 提供商名称
     * @param type 提供商类型，不能为null或empty
     * @param backExtParam 回调额外参数，授权成功后会返回这些信息，可以为null或empty
     * @param specialParamKey 特殊请求参数Key。对应配置文件中个provider的key，可自定义，以满足不同提供商的特殊参数。可为null
     * @return 请求授权URI
     */
    public static String getRequestAuthURI(String providerName, String type, Map<String, String> backExtParam,
            String specialParamKey) {
        if (!ConfigManager.getProviderConfigByName(providerName).getTypes().contains(type)) {
            throw new IllegalArgumentException(providerName + "不能进行" + type + "类型授权.");
        }
        if (StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("type can't be blank.");
        }

        Map<String, String> callbackParam = new HashMap<String, String>();
        if (MapUtils.isNotEmpty(backExtParam)) {
            callbackParam.putAll(backExtParam);
        }

        return OAuth2Client.getAuthRequestURI(providerName, type, callbackParam, specialParamKey);
    }

    /**
     * 根据请求授权的回调参数，获取AuthResponse对象
     * 
     * @param params 请求授权的回调参数
     * @return AuthResponse对象
     */
    public static AuthResponse getAuthResponseByURI(String uri) {
        Map<String, String> authBackParam = OAuth2Client.getAuthBackParamByURI(uri);
        return OAuth2Client.getAuthResponse(authBackParam);
    }

    /**
     * 获取accessToken
     */
    public static AccessTokenResponse accessToken(AuthResponse authResponse) {
        if (null == authResponse) {
            throw new IllegalArgumentException("参数不能为空。authResponse=" + authResponse);
        }

        return OAuth2Client.accessToken(authResponse);
    }

}
