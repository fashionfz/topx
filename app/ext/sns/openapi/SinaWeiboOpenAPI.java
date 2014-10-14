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
import play.libs.WS.Response;
import utils.WSUtil;

import com.fasterxml.jackson.databind.JsonNode;

import ext.sns.model.UserOAuth;
import ext.sns.openapi.OpenAPIResult.STATE;
import ext.sns.util.OpenAPIUtil;

/**
 * 
 * 
 * @ClassName: SinaWeiboOpenAPI
 * @Description: 新浪微博开放API
 * @date 2014-3-28 下午5:00:09
 * @author ShenTeng
 * 
 */
public class SinaWeiboOpenAPI implements SNSOpenAPI {

    private static final ALogger LOGGER = Logger.of(SinaWeiboOpenAPI.class);

    @Override
    public OpenAPIResult<Void> revokeAuth(UserOAuth userOAuth) {
        if (!userOAuth.isValid()) {
            return new OpenAPIResult<Void>(STATE.INVALID_TOKEN, null);
        }

        String url = "https://api.weibo.com/oauth2/revokeoauth2";

        Response response = WSUtil.get(url, "access_token", userOAuth.token).get(10000);
        String bodySinaWeibo = response.getBody();
        LOGGER.debug("get " + url + " response = " + bodySinaWeibo);

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(bodySinaWeibo);
        STATE state = validateJsonRet(jsonRet, response.getStatus(), url, bodySinaWeibo);
        if (STATE.SUCCESS != state) {
            return new OpenAPIResult<Void>(state, null);
        }

        return new OpenAPIResult<Void>(state, null);
    }

    @Override
    public OpenAPIResult<UserInfo> getUserInfo(UserOAuth userOAuth) {
        if (!userOAuth.isValid()) {
            return new OpenAPIResult<UserInfo>(STATE.INVALID_TOKEN, null);
        }

        String url = "https://api.weibo.com/2/users/show.json";
        OpenAPIResult<String> openIdResult = getOpenId(userOAuth.token);
        if (!openIdResult.isSuccess()) {
            return new OpenAPIResult<UserInfo>(openIdResult.getState(), null);
        }
        String openId = openIdResult.getResult();

        Response response = WSUtil.get(url, "access_token", userOAuth.token, "uid", openId).get(10000);
        String bodySinaWeibo = response.getBody();
        LOGGER.debug("get " + url + " response = " + bodySinaWeibo);

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(bodySinaWeibo);
        STATE state = validateJsonRet(jsonRet, response.getStatus(), url, bodySinaWeibo);
        if (STATE.SUCCESS != state) {
            return new OpenAPIResult<UserInfo>(state, null);
        }
        OpenAPIUtil.validateJsonRetHasNonNull(jsonRet, url, bodySinaWeibo, "screen_name", "profile_image_url");

        UserInfo userInfo = new UserInfo();
        userInfo.setAvatarUrl(jsonRet.get("profile_image_url").asText());
        userInfo.setNickName(jsonRet.get("screen_name").asText());
        userInfo.setOpenId(openId);

        return new OpenAPIResult<UserInfo>(state, userInfo);
    }

    private OpenAPIResult<String> getOpenId(String token) {
        String url = "https://api.weibo.com/oauth2/get_token_info";

        Response response = WSUtil.postFormURLEncoded(url, "access_token", token).get(10000);
        String body = response.getBody();

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(body);
        STATE state = validateJsonRet(jsonRet, response.getStatus(), url, body);
        if (STATE.SUCCESS != state) {
            return new OpenAPIResult<String>(state, null);
        }
        OpenAPIUtil.validateJsonRetHasNonNull(jsonRet, url, body, "uid");

        return new OpenAPIResult<String>(state, jsonRet.get("uid").asText());
    }

    @Override
    public OpenAPIResult<Void> postMsg(UserOAuth userOAuth, String content) {
        if (!userOAuth.isValid()) {
            return new OpenAPIResult<Void>(STATE.INVALID_TOKEN, null);
        }

        String url = "https://api.weibo.com/2/statuses/update.json";
        Response response = WSUtil.postFormURLEncoded(url, "access_token", userOAuth.token, "status", content).get(
                10000);
        String bodySinaWeibo = response.getBody();
        LOGGER.debug("post " + url + " response = " + bodySinaWeibo);

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(bodySinaWeibo);
        STATE state = validateJsonRet(jsonRet, response.getStatus(), url, bodySinaWeibo);
        if (STATE.SUCCESS != state) {
            return new OpenAPIResult<Void>(state, null);
        }

        return new OpenAPIResult<Void>(state, null);
    }

    private STATE validateJsonRet(JsonNode jsonRet, int status, String urlForLog, String bodyForLog) {
        if (status == 401 || status == 400 || jsonRet.hasNonNull("error_code")) {
            LOGGER.error("call " + urlForLog + " fail. response_body = " + bodyForLog);
            String errorCode = jsonRet.path("error_code").asText();
            if (StringUtils.isNotBlank(errorCode)) {
                if (errorCode.equals("10006") || errorCode.equals("21314") || errorCode.equals("21315")
                        || errorCode.equals("21316") || errorCode.equals("21317") || errorCode.equals("21327")) {
                    return STATE.INVALID_TOKEN;
                }
            }

            return STATE.UNKNOWN_ERROR;
        }

        return STATE.SUCCESS;
    }

}
