/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-26
 */
package ext.sns.auth;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * 
 * @ClassName: AccessTokenResponse
 * @Description: 获取token响应
 * @date 2014-3-26 下午5:05:04
 * @author ShenTeng
 * 
 */
public class AccessTokenResponse {
    private static final ALogger LOGGER = Logger.of(AccessTokenResponse.class);

    /**
     * 必须。获得的凭证。
     */
    private String accessToken;

    /**
     * 必须。token生命周期，秒为单位。
     */
    private Long expiresIn;

    /**
     * 可选。用于刷新凭证的凭证。
     */
    private String refreshToken;

    /**
     * 可选。
     */
    private String scope;

    /**
     * 必须。
     */
    private String tokenType;

    /**
     * 必须。原始内容
     */
    private String raw;

    /** 1:form 2:json */
    private int resultType;

    private Object resultObject;

    private AccessTokenResponse() {
    }

    /**
     * 获取AccessTokenResponse对象。类型不匹配或者responseString无效则返回null。
     * 
     * @param responseString 返回内容
     * @param responseType 返回类型
     * @return AccessTokenResponse，null - 类型不匹配或者responseString无效
     */
    public static AccessTokenResponse create(String responseString) {
        if (StringUtils.isBlank(responseString)) {
            return null;
        }

        AccessTokenResponse response = new AccessTokenResponse();
        response.setResultTypeByResponseStr(responseString);

        if (response.resultType == 1) {
            Map<String, String> responseMap = null;
            try {
                responseMap = parseResponseParam(responseString);
            } catch (RuntimeException e) {
                LOGGER.error("AccessTokenResponse解析失败", e);
            }

            response.raw = responseString;
            response.accessToken = responseMap.get("access_token");

            String expiresInString = responseMap.get("expires_in");
            response.expiresIn = StringUtils.isNumeric(expiresInString) ? Long.valueOf(expiresInString) : null;

            response.refreshToken = responseMap.get("refresh_token");
            response.scope = responseMap.get("scope");
            response.tokenType = responseMap.get("token_type");

            response.resultObject = responseMap;
        } else if (response.resultType == 2) {
            JsonNode responseJSON = null;
            try {
                responseJSON = Json.parse(responseString);
            } catch (RuntimeException e) {
                LOGGER.error("AccessTokenResponse解析失败", e);
            }

            response.raw = responseString;
            response.accessToken = responseJSON.has("access_token") ? responseJSON.get("access_token").asText() : null;

            String expiresInString = responseJSON.has("expires_in") ? responseJSON.get("expires_in").asText() : null;
            response.expiresIn = StringUtils.isNumeric(expiresInString) ? Long.valueOf(expiresInString) : null;

            response.refreshToken = responseJSON.has("refresh_token") ? responseJSON.get("refresh_token").asText()
                    : null;
            response.scope = responseJSON.has("scope") ? responseJSON.get("scope").asText() : null;
            response.tokenType = responseJSON.has("token_type") ? responseJSON.get("token_type").asText() : null;

            response.resultObject = responseJSON;
        } else {
            return null;
        }

        return response;
    }

    /**
     * 获取返回值。
     * 
     * @param key 参数key
     * @return 返回参数Map
     */
    public String getResponseValue(String key) {
        if (resultType == 1) {
            @SuppressWarnings("unchecked")
            Map<String, String> resultMap = (Map<String, String>) resultObject;
            return resultMap.get(key);
        } else if (resultType == 2) {
            JsonNode resultJson = (JsonNode) resultObject;

            if (!resultJson.hasNonNull(key)) {
                return null;
            }

            JsonNode valueNode = resultJson.get(key);
            if (valueNode.isContainerNode()) {
                return valueNode.toString();
            } else {
                return valueNode.asText();
            }

        } else {
            return null;
        }
    }

    private static Map<String, String> parseResponseParam(String responseString) {
        Map<String, String> paramMap = new HashMap<String, String>();

        String[] pairArray = responseString.split("&");
        for (String pair : pairArray) {
            String[] paramArray = pair.split("=");

            String val = paramArray.length == 2 ? paramArray[1] : "";
            paramMap.put(paramArray[0], val);
        }

        return paramMap;
    }

    private void setResultTypeByResponseStr(String responseString) {
        String trim = responseString.trim();
        if (trim.startsWith("{") && trim.endsWith("}")) {
            this.resultType = 2;
        } else {
            this.resultType = 1;
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
    
}
