/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-28
 */
package ext.sns.openapi;

import java.util.Iterator;

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
 * @ClassName: RenrenOpenAPI
 * @Description: 人人网开放API
 * @date 2014-3-28 下午5:01:03
 * @author ShenTeng
 * 
 */
public class RenrenOpenAPI implements SNSOpenAPI {

    private static final ALogger LOGGER = Logger.of(RenrenOpenAPI.class);

    @Override
    public OpenAPIResult<Void> revokeAuth(UserOAuth userOAuth) {
        return new OpenAPIResult<Void>(STATE.SUCCESS, null);
    }

    @Override
    public OpenAPIResult<UserInfo> getUserInfo(UserOAuth userOAuth) {
        if (!userOAuth.isValid()) {
            return new OpenAPIResult<UserInfo>(STATE.INVALID_TOKEN, null);
        }

        String url = "https://api.renren.com/v2/user/get";
        Response response = WSUtil.get(url, "access_token", userOAuth.token).get(10000);
        String bodyRenRen = response.getBody();
        LOGGER.debug("get " + url + " response = " + bodyRenRen);

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(bodyRenRen);
        STATE state = validateJsonRet(jsonRet, response.getStatus(), url, bodyRenRen);
        if (STATE.SUCCESS != state) {
            return new OpenAPIResult<UserInfo>(state, null);
        }
        OpenAPIUtil.validateJsonRetHasNonNull(jsonRet, url, bodyRenRen, "response");

        UserInfo userInfo = new UserInfo();
        Iterator<JsonNode> elements = jsonRet.get("response").get("avatar").elements();
        while (elements.hasNext()) {
            JsonNode child = elements.next();
            if ("MAIN".equals(child.path("size").asText())) {
                userInfo.setAvatarUrl(child.path("url").asText());
                break;
            }
        }
        userInfo.setNickName(jsonRet.get("response").get("name").asText());
        userInfo.setOpenId(jsonRet.get("response").get("id").asText());

        return new OpenAPIResult<UserInfo>(state, userInfo);
    }

    @Override
    public OpenAPIResult<Void> postMsg(UserOAuth userOAuth, String content) {
        if (!userOAuth.isValid()) {
            return new OpenAPIResult<Void>(STATE.INVALID_TOKEN, null);
        }

        String url = "https://api.renren.com/v2/status/put";
        Response response = WSUtil.postFormURLEncoded(url, "access_token", userOAuth.token, "content", content).get(
                10000);
        String bodyRenRen = response.getBody();
        LOGGER.debug("post " + url + " response = " + bodyRenRen);

        JsonNode jsonRet = OpenAPIUtil.parseJsonRet(bodyRenRen);
        STATE state = validateJsonRet(jsonRet, response.getStatus(), url, bodyRenRen);
        if (STATE.SUCCESS != state) {
            return new OpenAPIResult<Void>(state, null);
        }

        return new OpenAPIResult<Void>(state, null);
    }

    private STATE validateJsonRet(JsonNode jsonRet, int status, String urlForLog, String bodyForLog) {
        if (status == 400 || status == 401) {
            LOGGER.error("call " + urlForLog + " fail. response_body = " + bodyForLog);
            String code = jsonRet.path("error").path("code").asText();
            if (StringUtils.isNotBlank(code)) {
                if (code.contains("INVALID-TOKEN") || code.contains("EXPIRED-TOKEN")) {
                    return STATE.INVALID_TOKEN;
                }
            }

            return STATE.UNKNOWN_ERROR;
        }
        return STATE.SUCCESS;
    }
}
