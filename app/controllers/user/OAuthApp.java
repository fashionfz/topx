/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-21
 */
package controllers.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mobile.util.MobileUtil;
import models.Gender;
import models.User;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.cache.Cache;
import play.db.jpa.Transactional;
import play.mvc.Http.Context;
import play.mvc.Result;
import utils.WSUtil;

import com.fasterxml.jackson.databind.JsonNode;
import common.Constants;

import controllers.routes;
import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;
import ext.sns.auth.AccessTokenResponse;
import ext.sns.auth.AuthResponse;
import ext.sns.model.UserOAuth;
import ext.sns.service.OAuth2Service;
import ext.sns.service.ProviderType;
import ext.sns.service.SNSService;
import ext.sns.service.UserOAuthService;
import ext.sns.service.UserOAuthService.UserOAuthResult;
import ext.usercenter.UserAuthService;
import ext.usercenter.UserAuthURLFilter;

/**
 * 
 * 
 * @ClassName: OAuthApp
 * @Description: 获取第三方开放授权
 * @date 2013-11-21 下午3:19:23
 * @author ShenTeng
 * 
 */
public class OAuthApp extends BaseApp {
    private static final ALogger LOGGER = Logger.of(OAuthApp.class);

    /**
     * 请求授权
     */
    public static Result requestAuth(String provider, String referer, String type) {
        ClientType clientType = MobileUtil.isMobileUrlPrefix(request().path()) ? ClientType.MOBILE : ClientType.WEB;
        if (StringUtils.isBlank(referer)) {
            referer = "/";
        }

        try {
            if (!OAuth2Service.checkProviderName(provider) || !ProviderType.validateType(provider, type)) {
                return getResult(ResultType.ILLEGAL_PARAM, clientType, referer);
            }

            if (ProviderType.isNeedLogin(type) && !UserAuthService.isLogin(session())) {
                return getResult(ResultType.NOT_LOGIN, clientType, referer);
            }

            OAuthRequestInfo authRequestInfo = new OAuthRequestInfo();
            authRequestInfo.setReferer(referer);
            authRequestInfo.setClientType(clientType);
            authRequestInfo.setType(type);
            String specialParamKey = null;
            if (ClientType.MOBILE == clientType) {
                authRequestInfo.setFrom(MobileUtil.getFromByUrl(request().path()));
                specialParamKey = "mobileDisplay";
            } else {
                authRequestInfo.setFrom("web");
            }
            return redirect(OAuth2Service.getRequestAuthURI(provider, type, authRequestInfo.toMap(), specialParamKey));
        } catch (Exception e) {
            LOGGER.error("fail to requestAuth.", e);
            return getResult(ResultType.AUTH_FAIL, clientType, referer);
        }
    }

    /**
     * 授权回调
     */
    @Transactional
    public static Result authBack() {
        try {
            // 获取授权请求返回，并进行校验

            AuthResponse authResponse = OAuth2Service.getAuthResponseByURI(request().uri());
            if (null == authResponse) {
                LOGGER.error("authResponse为空。");
                return getResult(ResultType.AUTH_FAIL, null, null);
            }

            OAuthRequestInfo authRequestInfo = OAuthRequestInfo.create(authResponse.getBackExtInfo());
            String provider = authResponse.getProviderName();
            if (authResponse.isError() || null == authRequestInfo || StringUtils.isBlank(provider)) {
                LOGGER.error("authRequestInfo、provider为空、authResponse为Error。 uri = " + request().uri());
                if (null == authRequestInfo) {
                    return getResult(ResultType.AUTH_FAIL, null, null);
                } else {
                    return getResult(authResponse.isAccessDeniedError() ? ResultType.ACCESS_DENIED
                            : ResultType.AUTH_FAIL, authRequestInfo.getClientType(), authRequestInfo.getReferer());
                }
            }

            ClientType clientType = authRequestInfo.getClientType();
            String referer = authRequestInfo.getReferer();
            String type = authRequestInfo.getType();
            String from = authRequestInfo.getFrom();

            if (!OAuth2Service.checkProviderName(provider) || !ProviderType.validateType(provider, type)) {
                LOGGER.error("provider或type非法。 uri = " + request().uri());
                return getResult(ResultType.AUTH_FAIL, clientType, referer);
            }

            if (ProviderType.isNeedLogin(type) && !UserAuthService.isLogin(session())) {
                return getResult(ResultType.NOT_LOGIN, clientType, referer);
            }

            // 获取token，并进行校验

            AccessTokenResponse accessTokenResponse = OAuth2Service.accessToken(authResponse);

            if (null == accessTokenResponse) {
                LOGGER.error("accessTokenResponse为 null。");
                return getResult(ResultType.AUTH_FAIL, clientType, referer);
            }

            // 处理登录或绑定流程

            UserOAuthResult userOAuthResult = UserOAuthService.saveOrUpdate(accessTokenResponse, provider);
            if (!userOAuthResult.isSuccess()) {
                LOGGER.error("UserOAuthService.saveOrUpdate失败。userOAuthResult.getState = " + userOAuthResult.getState());
                return getResult(ResultType.AUTH_FAIL, clientType, referer);
            }
            UserOAuth userOAuth = userOAuthResult.getResult();

            // 处理绑定SNS流程
            if (type.equals(ProviderType.SNS)) {
                if (userOAuth.isBindUser()) {
                    return getResult(ResultType.OTHER_USER_BIND, clientType, referer);
                } else {
                    int ret = UserOAuthService.bindToUser(userOAuth, User.getFromSession(session()));
                    if (ret != 1) {
                        LOGGER.error("ALL:SNS:UserOAuthService.bindToUser失败");
                        return getResult(ResultType.AUTH_FAIL, clientType, referer);
                    }
                }
                return redirect(referer);
            }

            // 处理登录流程
            if (ClientType.MOBILE == clientType) {
                // 已关联用户，直接登录
                if (userOAuth.isBindUser()) {
                    int ret = UserOAuthService.loginByBindUser(userOAuth);
                    if (ret != 1) {
                        if (ret == -1) {
                            return getResult(ResultType.ACCOUNT_DISABLE, clientType, referer);
                        }
                        LOGGER.error("MOBILE:LOGIN:UserOAuthService.loginByBindUser失败");
                        return getResult(ResultType.AUTH_FAIL, clientType, referer);
                    }
                } else { // 未关联用户，注册个随机用户，再进行关联
                    User newUser = UserOAuthService.registerRandomUser(userOAuth.nickname);
                    if (null == newUser) {
                        LOGGER.error("MOBILE:LOGIN:UserOAuthService.registerRandomUser失败");
                        return getResult(ResultType.AUTH_FAIL, clientType, referer);
                    }
                    int ret = UserOAuthService.bindToUser(userOAuth, newUser);
                    if (ret != 1) {
                        LOGGER.error("MOBILE:LOGIN:UserOAuthService.bindToUser失败");
                        User.logout(Context.current().session());
                        return getResult(ResultType.AUTH_FAIL, clientType, referer);
                    }

                    if (MobileUtil.isFromAndroid(from)) {
                        SNSService.postMsg(userOAuth, "seagull客户端可以帮你找到你需要的服务者");
                    }
                }
                return redirect(referer);
            } else if (ClientType.WEB == clientType) {
                // 已关联用户，直接登录
                if (userOAuth.isBindUser()) {
                    int ret = UserOAuthService.loginByBindUser(userOAuth);
                    if (ret != 1) {
                        if (ret == -1) {
                            return getResult(ResultType.ACCOUNT_DISABLE, clientType, referer);
                        }
                        LOGGER.error("MOBILE:LOGIN:UserOAuthService.loginByBindUser失败");
                        return getResult(ResultType.AUTH_FAIL, clientType, referer);
                    }

                    return ok(views.html.user.thirdtransfer.render("1", "", "", ""));
                } else { // 未关联用户
                    String key = UUID.randomUUID().toString().replace("-", "");
                    Cache.set(Constants.CACHE_USEROAUTH_ID + key, userOAuth.id, 600);
                    return ok(views.html.user.thirdtransfer.render("0", key, userOAuth.avatarUrl, userOAuth.nickname));
                }
            }

            return redirect(referer);
        } catch (Exception e) {
            LOGGER.error("authBack异常", e);
            return getResult(ResultType.AUTH_FAIL, null, null);
        }
    }

    /**
     * 直接登录：创建随机帐号登录
     */
    @Transactional
    public static Result directLogin() {
        JsonNode param = getJson();
        if (null == param) {
            return illegalParameters();
        }
        String key = param.path("key").asText();
        if (StringUtils.isBlank(key)) {
            return illegalParameters();
        }

        ObjectNodeResult result = new ObjectNodeResult();
        Long userOAuthId = (Long) Cache.get(Constants.CACHE_USEROAUTH_ID + key);
        UserOAuth userOAuth = UserOAuthService.getById(userOAuthId);
        if (null == userOAuth) {
            return ok(result.errorkey("useroauth.login.notfoundauth").getObjectNode());
        }
        Cache.remove(Constants.CACHE_USEROAUTH_ID + key);

        // 创建随机帐号登录
        User newUser = UserOAuthService.registerRandomUser(userOAuth.nickname);
        if (null == newUser) {
            LOGGER.error("directLogin:UserOAuthService.registerRandomUser失败");
            return ok(result.errorkey("useroauth.login.fail").getObjectNode());
        }
        int ret = UserOAuthService.bindToUser(userOAuth, newUser);
        if (ret != 1) {
            LOGGER.error("directLogin:UserOAuthService.bindToUser失败");
            User.logout(Context.current().session());
            return ok(result.errorkey("useroauth.login.fail").getObjectNode());
        }

        return ok(result.getObjectNode());
    }

    /**
     * 绑定已有账号登录：使用已有帐号登录，并和授权绑定
     */
    @Transactional
    public static Result bindAccountLogin() {
        JsonNode param = getJson();
        if (null == param) {
            return illegalParameters();
        }
        String key = param.path("key").asText();
        String email = param.path("email").asText();
        String password = param.path("password").asText();
        if (StringUtils.isBlank(key) || StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
            return illegalParameters();
        }

        ObjectNodeResult result = new ObjectNodeResult();
        Long userOAuthId = (Long) Cache.get(Constants.CACHE_USEROAUTH_ID + key);
        UserOAuth userOAuth = UserOAuthService.getById(userOAuthId);
        if (null == userOAuth) {
            return ok(result.errorkey("useroauth.login.notfoundauth").getObjectNode());
        }

        // 使用已有帐号登录，并和授权绑定
        result = User.login(email, password, false);
        if (!result.isSuccess()) {
            return ok(result.getObjectNode());
        }
        Cache.remove(Constants.CACHE_USEROAUTH_ID + key);

        int ret = UserOAuthService.bindToUser(userOAuth, result.getUser());
        if (ret != 1) {
            LOGGER.error("bindAccountLogin:UserOAuthService.bindToUser失败");
            User.logout(Context.current().session());
            return ok(result.errorkey("useroauth.login.fail").getObjectNode());
        }

        return ok(result.getObjectNode());
    }

    /**
     * 完善帐号登录：根据用户填写信息注册帐号并绑定
     */
    @Transactional
    public static Result newAccountLogin() {
        JsonNode param = getJson();
        if (null == param) {
            return illegalParameters();
        }
        String key = param.path("key").asText();
        String email = param.path("email").asText();
        String password = param.path("password").asText();
        String captcha = param.path("captcha").asText();
        String cpKey = param.path("t").asText();
        if (StringUtils.isBlank(key) || StringUtils.isBlank(email) || StringUtils.isBlank(password)
                || StringUtils.isBlank(captcha) || StringUtils.isBlank(cpKey)) {
            return illegalParameters();
        }

        ObjectNodeResult result = new ObjectNodeResult();
        Long userOAuthId = (Long) Cache.get(Constants.CACHE_USEROAUTH_ID + key);
        UserOAuth userOAuth = UserOAuthService.getById(userOAuthId);
        if (null == userOAuth) {
            return ok(result.errorkey("useroauth.login.notfoundauth").getObjectNode());
        }

        // 根据用户填写信息注册帐号并绑定
        result = User.register(email, password, Gender.MAN, captcha, cpKey);
        if (!result.isSuccess()) {
            return ok(result.getObjectNode());
        }
        Cache.remove(Constants.CACHE_USEROAUTH_ID + key);

        int ret = UserOAuthService.bindToUser(userOAuth, result.getUser());
        if (ret != 1) {
            LOGGER.error("newAccountLogin:UserOAuthService.bindToUser失败");
            User.logout(Context.current().session());
            return ok(result.errorkey("useroauth.login.fail").getObjectNode());
        }

        return ok(result.getObjectNode());
    }

    /**
     * 取消授权
     */
    @Transactional
    public static Result revokeAuth(String providerName, String referer) {
        if (!OAuth2Service.checkProviderName(providerName)) {
            return errorInfo("参数非法");
        }

        User user = User.getFromSession(session());

        UserOAuthService.revokeAuthorize(user, providerName);

        return redirect(referer);
    }

    private static Result getResult(ResultType resultType, ClientType clientType, String referer) {

        if (resultType == ResultType.ILLEGAL_PARAM) {

            if (clientType == ClientType.MOBILE) {
                return mobileErrorRedirect(referer, "传入参数不符合规范", "100005");
            } else if (clientType == ClientType.WEB) {
                return errorInfo("参数非法");
            }

        } else if (resultType == ResultType.NOT_LOGIN) {

            if (clientType == ClientType.MOBILE) {
                return mobileErrorRedirect(referer, "用户未登录", "100002");
            } else if (clientType == ClientType.WEB) {
                return redirect(UserAuthURLFilter.getNoLoginRedirectURI(referer));
            }

        } else if (resultType == ResultType.AUTH_FAIL) {

            if (clientType == ClientType.MOBILE) {
                return mobileErrorRedirect(referer, "授权失败", "267001");
            } else if (clientType == ClientType.WEB) {
                return errorInfo("授权失败", referer, "授权失败");
            } else {
                return internalServerError("授权失败，请尝试重新授权");
            }

        } else if (resultType == ResultType.ACCESS_DENIED) {
            return getAccessDeniedResult(clientType, referer);
        } else if (resultType == ResultType.OTHER_USER_BIND) {

            if (clientType == ClientType.MOBILE) {
                return mobileErrorRedirect(referer, "授权失败。已经被其他账户绑定", "267002");
            } else if (clientType == ClientType.WEB) {
                return errorInfo("授权失败。已经被其他账户绑定", referer, "授权失败");
            }

        } else if (resultType == ResultType.ACCOUNT_DISABLE) {
            if (clientType == ClientType.MOBILE) {
                return mobileErrorRedirect(referer, "第三方登录失败。账户被禁用", "267004");
            } else if (clientType == ClientType.WEB) {
                String url = routes.Application.loginskip().url();
                return jumpForWeb(referer, url);
            }
        }

        return null;
    }

    private static Result jumpForWeb(String referer, String url) {
        if (!referer.startsWith("window:")) {
            return redirect(referer);
        } else {
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder
                    .append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            contentBuilder.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" >");
            contentBuilder.append("<head>");
            contentBuilder.append("<title></title>");
            contentBuilder.append("</head>");
            contentBuilder.append("<body>");
            contentBuilder.append("<script type=\"text/javascript\">");
            contentBuilder.append("window.onload = function(){");
            contentBuilder.append("window.close();");
            if (StringUtils.isNotBlank(url)) {
                contentBuilder.append("window.opener.location.href = \"" + url + "\"");
            }
            contentBuilder.append("};");
            contentBuilder.append("</script>");
            contentBuilder.append("</body>");
            contentBuilder.append("</html>");

            return ok(contentBuilder.toString()).as("text/html");
        }
    }

    private static Result getAccessDeniedResult(ClientType clientType, String referer) {
        if (clientType == ClientType.MOBILE) {
            return mobileErrorRedirect(referer, "授权失败，授权被拒绝", "267003");
        } else if (clientType == ClientType.WEB) {
            return jumpForWeb(referer, null);
        } else {
            return internalServerError("access_denied");
        }
    }

    private static Result mobileErrorRedirect(String referer, String error, String errorCode) {
        try {
            return redirect(referer + WSUtil.getURIQueryStringPrefix(referer) + "errorCode=" + errorCode + "&error="
                    + URLEncoder.encode(error, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回结果类型
     */
    private enum ResultType {
        /** 参数非法 */
        ILLEGAL_PARAM,
        /** 未登录 */
        NOT_LOGIN,
        /** 授权失败 */
        AUTH_FAIL,
        /** 授权被拒绝 */
        ACCESS_DENIED,
        /** 账户已被其他用户绑定 */
        OTHER_USER_BIND,
        /** 帐号禁用 **/
        ACCOUNT_DISABLE
    }

    private enum ClientType {
        WEB, MOBILE;
        public static ClientType getByName(String name) {
            for (ClientType c : ClientType.values()) {
                if (c.name().equals(name)) {
                    return c;
                }
            }
            return null;
        }
    }

    private static class OAuthRequestInfo {

        private String referer;
        private ClientType clientType;
        private String from;
        private String type;

        public static OAuthRequestInfo create(Map<String, String> map) {
            if (MapUtils.isEmpty(map)) {
                return null;
            }

            String referer = map.get("r");
            ClientType clientType = ClientType.getByName(map.get("c"));
            String from = map.get("f");
            String type = map.get("t");

            if (StringUtils.isBlank(referer) || null == clientType || StringUtils.isBlank(from)
                    || StringUtils.isBlank(type)) {
                return null;
            }

            OAuthRequestInfo info = new OAuthRequestInfo();
            info.setReferer(referer);
            info.setClientType(clientType);
            info.setFrom(from);
            info.setType(type);

            return info;
        }

        public Map<String, String> toMap() {
            Map<String, String> param = new HashMap<String, String>();
            param.put("r", referer);
            param.put("c", clientType.name());
            param.put("f", from);
            param.put("t", type);

            return param;
        }

        public String getReferer() {
            return referer;
        }

        public void setReferer(String referer) {
            this.referer = referer;
        }

        public ClientType getClientType() {
            return clientType;
        }

        public void setClientType(ClientType clientType) {
            this.clientType = clientType;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

}
