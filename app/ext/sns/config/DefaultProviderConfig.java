/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-25
 */
package ext.sns.config;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import utils.WSUtil;
import ext.sns.auth.AuthResponse;
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
public class DefaultProviderConfig implements ProviderConfig {

    protected static final String PARAM_PROVIDER_NAME = "p";

    /** 服务提供商名称 */
    private String name = "";

    /** 请求授权URI */
    private String authURI = "";

    /** 授权clientId */
    private String clientId = "";

    /** token请求URI */
    private String accessTokenURI = "";

    /** 特殊参数 */
    private Map<String, String> specialParam;

    /** token请求clientSecret */
    private String clientSecret = "";

    /** 授权scope */
    private String scope = "";

    /** 提供业务类型 */
    private String type = "";

    @Override
    public String getRequestAuthFullURI(Map<String, String> callbackParam, String specialParamKey) {
        SNSConfig snsConfig = ConfigManager.getSNSConfig();

        // 拼装URI,标准参数：response_type，client_id，redirect_uri，scope，state。
        StringBuilder requestURI = new StringBuilder(authURI);
        requestURI.append(WSUtil.getURIQueryStringPrefix(authURI));
        requestURI.append("response_type=code");
        requestURI.append("&client_id=").append(clientId);

        String fullRedirectURI = generateRedirectURI(name, snsConfig.getAuthRedirectUri());
        try {
            requestURI.append("&redirect_uri=").append(URLEncoder.encode(fullRedirectURI, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        if (StringUtils.isNotBlank(scope)) {
            requestURI.append("&scope=").append(scope);
        }

        if (MapUtils.isNotEmpty(specialParam) && StringUtils.isNotBlank(specialParamKey)) {
            if (specialParam.containsKey(specialParamKey)) {
                requestURI.append("&").append(specialParam.get(specialParamKey));
            }
        }

        if (MapUtils.isNotEmpty(callbackParam)) {
            requestURI.append("&state=").append(AuthUtil.encodeState(callbackParam));
        }

        return requestURI.toString();
    }

    @Override
    public boolean isMatchAuthResponse(AuthResponse authResponse) {
        String providerName = authResponse.getRaw().get(PARAM_PROVIDER_NAME);
        if (StringUtils.isNotBlank(providerName) && name.equals(providerName)) {
            return true;
        }
        return false;
    }

    @Override
    public Map<String, String> getCallbackParam(AuthResponse authResponse) {
        return AuthUtil.decodeState(authResponse.getState());
    }

    @Override
    public String getAccessTokenFullURI(AuthResponse authResponse) {
        SNSConfig snsConfig = ConfigManager.getSNSConfig();

        StringBuilder requestURI = new StringBuilder(accessTokenURI);
        requestURI.append(WSUtil.getURIQueryStringPrefix(accessTokenURI));
        requestURI.append("client_secret=").append(clientSecret);
        requestURI.append("&client_id=").append(clientId);
        requestURI.append("&grant_type=authorization_code");
        requestURI.append("&code=").append(authResponse.getCode());

        String fullRedirectURI = generateRedirectURI(name, snsConfig.getAuthRedirectUri());
        try {
            requestURI.append("&redirect_uri=").append(URLEncoder.encode(fullRedirectURI, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return requestURI.toString();
    }

    @Override
    public List<String> getTypes() {
        List<String> types = new ArrayList<>();

        if (StringUtils.isNotBlank(type)) {
            String[] split = type.split(",");
            for (String str : split) {
                types.add(str);
            }
        }

        return types;
    }

    protected static String generateRedirectURI(String providerName, String redirectURI) {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(redirectURI);
        uriBuilder.append(WSUtil.getURIQueryStringPrefix(redirectURI)).append(PARAM_PROVIDER_NAME).append('=')
                .append(providerName);

        return uriBuilder.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthURI() {
        return authURI;
    }

    public void setAuthURI(String authURI) {
        this.authURI = authURI;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAccessTokenURI() {
        return accessTokenURI;
    }

    public void setAccessTokenURI(String accessTokenURI) {
        this.accessTokenURI = accessTokenURI;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Map<String, String> getSpecialParam() {
        return specialParam;
    }

    public void setSpecialParam(Map<String, String> specialParam) {
        this.specialParam = specialParam;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
