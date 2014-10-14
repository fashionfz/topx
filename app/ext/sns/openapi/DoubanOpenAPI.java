/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-28
 */
package ext.sns.openapi;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.libs.WS.Response;
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
 * @ClassName: DoubanOpenAPI
 * @Description: 豆瓣开放API
 * @date 2014-3-28 下午5:00:49
 * @author ShenTeng
 * 
 */
public class DoubanOpenAPI implements SNSOpenAPI {

    private static final ALogger LOGGER = Logger.of(DoubanOpenAPI.class);

    @Override
    public OpenAPIResult<Void> revokeAuth(UserOAuth userOAuth) {
        return new OpenAPIResult<Void>(STATE.SUCCESS, null);
    }

    @Override
    public OpenAPIResult<UserInfo> getUserInfo(UserOAuth userOAuth) {
        if (!userOAuth.isValid()) {
            return new OpenAPIResult<UserInfo>(STATE.INVALID_TOKEN, null);
        }

        String url = "https://api.douban.com/v2/user/~me";
        Map<String, String> header = new HashMap<String, String>();
        header.put("Authorization", "Bearer " + userOAuth.token);

        Response response = WSUtil.getWithHeader(url, header).get(10000);
        String bodyDouban = response.getBody();
        LOGGER.debug("get " + url + " response = " + bodyDouban);

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(bodyDouban);
        STATE state = validateJsonRet(response.getStatus(), jsonRet, url, bodyDouban);
        if (STATE.SUCCESS != state) {
            return new OpenAPIResult<UserInfo>(state, null);
        }
        OpenAPIUtil.validateJsonRetHasNonNull(jsonRet, url, bodyDouban, "name", "avatar", "id");

        UserInfo userInfo = new UserInfo();
        userInfo.setAvatarUrl(jsonRet.get("avatar").asText());
        userInfo.setNickName(jsonRet.get("name").asText());
        userInfo.setOpenId(jsonRet.get("id").asText());

        return new OpenAPIResult<UserInfo>(state, userInfo);
    }

    @Override
    public OpenAPIResult<Void> postMsg(UserOAuth userOAuth, String content) {
        if (!userOAuth.isValid()) {
            return new OpenAPIResult<Void>(STATE.INVALID_TOKEN, null);
        }

        DefaultProviderConfig providerConfig = (DefaultProviderConfig) ConfigManager
                .getProviderConfigByName(userOAuth.provider);
        Map<String, String> header = new HashMap<String, String>();
        header.put("Authorization", "Bearer " + userOAuth.token);
        String url = "https://api.douban.com/shuo/v2/statuses/";

        Response response = WSUtil.postFormURLEncodedWithHeader(url, header, "source", providerConfig.getClientId(),
                "text", content).get(10000);
        String bodyDouban = response.getBody();
        LOGGER.debug("post " + url + " response = " + bodyDouban);

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(bodyDouban);
        STATE state = validateJsonRet(response.getStatus(), jsonRet, url, bodyDouban);

        return new OpenAPIResult<Void>(state, null);
    }

    private STATE validateJsonRet(int status, JsonNode jsonRet, String urlForLog, String bodyForLog) {
        if (status < 200 || status > 299) {
            LOGGER.error("call " + urlForLog + " fail. status = " + status + ". response_body = " + bodyForLog);

            String errorCode = jsonRet.path("code").asText();
            if (StringUtils.isNotBlank(errorCode)) {
                if (errorCode.equals("102") || errorCode.equals("103") || errorCode.equals("106")
                        || errorCode.equals("123")) {
                    return STATE.INVALID_TOKEN;
                }
            }
            return STATE.UNKNOWN_ERROR;
        }
        return STATE.SUCCESS;
    }
}
