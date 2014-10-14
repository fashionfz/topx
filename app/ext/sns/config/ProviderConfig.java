/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-25
 */
package ext.sns.config;

import java.util.List;
import java.util.Map;

import ext.sns.auth.AuthResponse;

/**
 * 
 * 
 * @ClassName: ProviderConfig
 * @Description: SNS服务提供商配置
 * @date 2014-3-25 下午8:07:16
 * @author ShenTeng
 * 
 */
public interface ProviderConfig {

    /** 获取服务提供商名称 */
    String getName();

    /** 获取提供商类型 */
    public List<String> getTypes();

    /**
     * 获取请求授权完整URI
     * 
     * @param callbackParam 回调参数，可为null
     * @param specialParamKey 特殊请求参数Key。对应配置文件中个provider的key，可自定义，以满足不同提供商的特殊参数。可为null
     * @return
     */
    String getRequestAuthFullURI(Map<String, String> callbackParam, String specialParamKey);

    /** 授权请求响应是否匹配当前provider */
    boolean isMatchAuthResponse(AuthResponse authResponse);

    /** 获取回调参数 */
    Map<String, String> getCallbackParam(AuthResponse authResponse);

    /** 获取请求token完整URI */
    String getAccessTokenFullURI(AuthResponse authResponse);

}
