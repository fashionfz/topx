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
 * @ClassName: QQOpenAPI
 * @Description: QQ开放API
 * @date 2014-3-28 下午5:00:31
 * @author ShenTeng
 * 
 */
public class QQOpenAPI implements SNSOpenAPI {

    private static final ALogger LOGGER = Logger.of(QQOpenAPI.class);

    @Override
    public OpenAPIResult<Void> revokeAuth(UserOAuth userOAuth) {
        return new OpenAPIResult<Void>(STATE.SUCCESS, null);
    }

    @Override
    public OpenAPIResult<UserInfo> getUserInfo(UserOAuth userOAuth) {
        if (!userOAuth.isValid()) {
            return new OpenAPIResult<UserInfo>(STATE.INVALID_TOKEN, null);
        }

        DefaultProviderConfig providerConfig = (DefaultProviderConfig) ConfigManager
                .getProviderConfigByName(userOAuth.provider);
        String url = "https://graph.qq.com/user/get_user_info";
        OpenAPIResult<String> openIdResult = getOpenId(userOAuth.provider, userOAuth.token);
        if (!openIdResult.isSuccess()) {
            return new OpenAPIResult<UserInfo>(openIdResult.getState(), null);
        }
        String openId = openIdResult.getResult();

        String bodyQQ = WSUtil
                .get(url, "oauth_consumer_key", providerConfig.getClientId(), "access_token", userOAuth.token,
                        "openid", openId).get(10000).getBody();
        LOGGER.debug("get " + url + " response = " + bodyQQ);

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(bodyQQ);
        STATE state = validateJsonRet(jsonRet, url, bodyQQ);
        if (STATE.SUCCESS != state) {
            return new OpenAPIResult<UserInfo>(state, null);
        }
        OpenAPIUtil.validateJsonRetHasNonNull(jsonRet, url, bodyQQ, "nickname", "figureurl_qq_1");

        UserInfo userInfo = new UserInfo();
        userInfo.setAvatarUrl(jsonRet.get("figureurl_qq_1").asText());
        userInfo.setNickName(jsonRet.get("nickname").asText());
        userInfo.setOpenId(openId);

        return new OpenAPIResult<UserInfo>(state, userInfo);
    }

    private OpenAPIResult<String> getOpenId(String provider, String accessToken) {
        String url = "https://graph.qq.com/oauth2.0/me";

        String bodyQQ = WSUtil.get(url, "access_token", accessToken).get(10000).getBody();
        LOGGER.debug("get " + url + " response = " + bodyQQ);
        String content = bodyQQ.substring(bodyQQ.indexOf("{"), bodyQQ.lastIndexOf("}") + 1);

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(content);
        STATE state = validateJsonRet(jsonRet, url, bodyQQ);
        if (STATE.SUCCESS != state) {
            return new OpenAPIResult<String>(state, null);
        }
        OpenAPIUtil.validateJsonRetHasNonNull(jsonRet, url, bodyQQ, "openid");

        return new OpenAPIResult<String>(state, jsonRet.get("openid").asText());
    }

    @Override
    public OpenAPIResult<Void> postMsg(UserOAuth userOAuth, String content) {
        if (!userOAuth.isValid()) {
            return new OpenAPIResult<Void>(STATE.INVALID_TOKEN, null);
        }

        DefaultProviderConfig providerConfig = (DefaultProviderConfig) ConfigManager
                .getProviderConfigByName(userOAuth.provider);
        String url = "https://graph.qq.com/t/add_t";

        String bodyQQ = WSUtil
                .postFormURLEncoded(url, "oauth_consumer_key", providerConfig.getClientId(), "access_token",
                        userOAuth.token, "openid", userOAuth.openId, "content", content).get(10000).getBody();

        LOGGER.debug("post " + url + " response = " + bodyQQ);

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(bodyQQ);
        STATE state = validateJsonRet(jsonRet, url, bodyQQ);
        if (STATE.SUCCESS != state) {
            return new OpenAPIResult<Void>(state, null);
        }

        return new OpenAPIResult<Void>(state, null);
    }

    private STATE validateJsonRet(JsonNode jsonRet, String urlForLog, String bodyForLog) {
        String ret = jsonRet.path("ret").asText();
        if (StringUtils.isNotBlank(ret)) {
            if (!ret.equals("0")) {
                LOGGER.error("call " + urlForLog + " fail. response_body = " + bodyForLog);
                if (ret.equals("-23") || ret.equals("-22") || ret.equals("-71") || ret.equals("100007")
                        || ret.equals("100013") || ret.equals("100014") || ret.equals("100015") || ret.equals("100016")
                        || ret.equals("9016") || ret.equals("9017") || ret.equals("9018") || ret.equals("9094")) {
                    return STATE.INVALID_TOKEN;
                }

                return STATE.UNKNOWN_ERROR;
            }
        }

        return STATE.SUCCESS;
    }
}
