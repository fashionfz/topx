/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-28
 */
package ext.sns.openapi;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import utils.WSUtil;

import com.fasterxml.jackson.databind.JsonNode;

import ext.sns.config.ConfigManager;
import ext.sns.config.DefaultProviderConfig;
import ext.sns.model.UserOAuth;
import ext.sns.openapi.OpenAPIResult.STATE;
import ext.sns.util.OpenAPIUtil;

/**
 * 
 * 
 * @ClassName: TencentWeiboOpenAPI
 * @Description: 腾讯微博开放API
 * @date 2014-3-28 下午4:57:54
 * @author ShenTeng
 * 
 */
public class TencentWeiboOpenAPI implements SNSOpenAPI {

    private static final ALogger LOGGER = Logger.of(TencentWeiboOpenAPI.class);

    @Override
    public OpenAPIResult<Void> revokeAuth(UserOAuth userOAuth) {
        DefaultProviderConfig providerConfig = (DefaultProviderConfig) ConfigManager
                .getProviderConfigByName(userOAuth.provider);
        String url = "https://open.t.qq.com/api/auth/revoke_auth";

        String bodyTencentWeibo = WSUtil
                .get(url, "oauth_consumer_key", providerConfig.getClientId(), "access_token", userOAuth.token,
                        "oauth_version", "2.a", "openid", userOAuth.openId).get(10000).getBody();
        LOGGER.debug("get " + url + " response = " + bodyTencentWeibo);

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(bodyTencentWeibo);
        STATE state = validateJsonRet(jsonRet, url, bodyTencentWeibo);
        if (STATE.SUCCESS != state) {
            return new OpenAPIResult<Void>(state, null);
        }

        return new OpenAPIResult<Void>(state, null);
    }

    /**
     * userOAuth.openId必须有值。腾讯微博只能授权时获得openId，不能通过其他接口获得openId。
     */
    @Override
    public OpenAPIResult<UserInfo> getUserInfo(UserOAuth userOAuth) {
        if (!userOAuth.isValid()) {
            return new OpenAPIResult<UserInfo>(STATE.INVALID_TOKEN, null);
        }
        // 腾讯微博只能授权时获得openId，不能通过其他接口获得openId
        if (StringUtils.isBlank(userOAuth.openId)) {
            throw new IllegalArgumentException("userOAuth.openId 不能为blank。腾讯微博只能授权时获得openId，不能通过其他接口获得openId。");
        }

        DefaultProviderConfig providerConfig = (DefaultProviderConfig) ConfigManager
                .getProviderConfigByName(userOAuth.provider);
        String openId = userOAuth.openId;
        String url = "http://open.t.qq.com/api/user/info";

        String bodyTencentWeibo = WSUtil
                .get(url, "oauth_consumer_key", providerConfig.getClientId(), "access_token", userOAuth.token,
                        "oauth_version", "2.a", "openid", openId, "format", "json").get(10000).getBody();
        LOGGER.debug("get " + url + " response = " + bodyTencentWeibo);

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(bodyTencentWeibo);
        STATE state = validateJsonRet(jsonRet, url, bodyTencentWeibo);
        if (STATE.SUCCESS != state) {
            return new OpenAPIResult<UserInfo>(state, null);
        }
        OpenAPIUtil.validateJsonRetHasNonNull(jsonRet, url, bodyTencentWeibo, "data");

        UserInfo userInfo = new UserInfo();
        userInfo.setAvatarUrl(jsonRet.get("data").get("head").asText() + "/100");
        userInfo.setNickName(jsonRet.get("data").get("nick").asText());
        userInfo.setOpenId(openId);

        return new OpenAPIResult<UserInfo>(state, userInfo);
    }

    @Override
    public OpenAPIResult<Void> postMsg(UserOAuth userOAuth, String content) {
        DefaultProviderConfig providerConfig = (DefaultProviderConfig) ConfigManager
                .getProviderConfigByName(userOAuth.provider);
        String url = "http://open.t.qq.com/api/t/add";

        String bodyTencentWeibo = WSUtil
                .postFormURLEncoded(url, "content", content, "oauth_consumer_key", providerConfig.getClientId(),
                        "access_token", userOAuth.token, "oauth_version", "2.a", "openid", userOAuth.openId, "format",
                        "json").get(10000).getBody();
        LOGGER.debug("post " + url + " response = " + bodyTencentWeibo);

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(bodyTencentWeibo);
        STATE state = validateJsonRet(jsonRet, url, bodyTencentWeibo);
        if (STATE.SUCCESS != state) {
            return new OpenAPIResult<Void>(state, null);
        }

        return new OpenAPIResult<Void>(state, null);
    }

    private STATE validateJsonRet(JsonNode jsonRet, String urlForLog, String bodyForLog) {
        String errcode = jsonRet.path("errcode").asText();
        if (StringUtils.isNotBlank(errcode) && !"0".equals(errcode)) {
            LOGGER.error("call " + urlForLog + " fail. response_body = " + bodyForLog);
            if (errcode.equals("34") || errcode.equals("36") || errcode.equals("37") || errcode.equals("38")
                    || errcode.equals("40")) {
                return STATE.INVALID_TOKEN;
            }

            return STATE.UNKNOWN_ERROR;
        }

        return STATE.SUCCESS;
    }
}
