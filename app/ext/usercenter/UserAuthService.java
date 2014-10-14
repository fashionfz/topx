/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-10-29
 */
package ext.usercenter;

import static play.Play.application;
import models.service.UserInfoCookieService;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.cache.Cache;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Http.Session;
import utils.DeviceUtil;
import common.Constants;
import ext.config.ConfigFactory;

/**
 * 
 * 
 * @ClassName: UserAuthService
 * @Description: 用户鉴权服务类。提供登入登出等用户鉴权相关的服务
 * @date 2013-10-29 下午2:41:45
 * @author ShenTeng
 * 
 */
public class UserAuthService {

    private static final ALogger LOGGER = Logger.of(UserAuthService.class);

    private static String KICKED_FLAG_KEY = "_kicked";

    private static String sessionDomain = application().configuration().getString("session.domain");

    /**
     * 禁止实例化
     */
    private UserAuthService() {
    }

    /**
     * 判断当前用户是否登录
     * 
     * @param session 不能为null。HTTP Session
     * @return true:已登录
     */
    public static boolean isLogin(Session session) {
        if (null == session) {
            throw new IllegalArgumentException("illegal method input param. params: session=" + session);
        }

        return null != getTokenInSession(session);
    }

    /**
     * 获取当前登录用户UId<br>
     * 返回null代表用户未登录
     * 
     * @param session 不能为null。HTTP Session
     * @return uid，返回null代表用户未登录
     */
    public static String getLoginUid(Session session) {
        if (null == session) {
            throw new IllegalArgumentException("illegal method input param. params: session=" + session);
        }

        String token = session.get(LOGIN_TOKEN_SESSION_KEY);
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        String uid = getTokenUidLocalCache(token);

        // 缓存中无数据则从远端服务器取。
        if (null == uid) {
            UCResult<UCUser> ucResult = UCClient.queryUserInfoByToken(token);
            if (ucResult.isSuccess()) {
                uid = ucResult.data.uid;
                setTokenUidLocalCache(uid, token);
            } else if (!ucResult.noMatchData()) {
                LOGGER.error("queryUserInfoByToken error: " + ucResult);
            }
        }

        return uid;
    }

    /**
     * 登录
     * 
     * @param session 不能为null。HTTP Session
     * @param username 必须。用户名的形式包括：一般形式的用户名、用户密码、用户邮箱地址
     * @param userpassword 必须。密码，直接传入密码原文
     * @return SUCCESS：成功，USERNAME_PASSWORD_ERROR：用户名或密码错误， UNKNOWN_ERROR：未知错误
     */
    public static LoginResult login(Session session, String username, String userpassword) {
        if (null == session || StringUtils.isBlank(username) || StringUtils.isBlank(userpassword)) {
            throw new IllegalArgumentException("illegal method input param. params: session=" + session + ", username="
                    + username + ", userpassword is " + (userpassword == null ? "" : "not") + " null ");
        }

        String device = DeviceUtil.getDeviceByUrl(Context.current().request().path());
        String ip = Context.current().request().remoteAddress();
        UCResult<UCUser> ucResult = UCClient.login(username, userpassword, device, ip);

        if (ucResult.isSuccess()) {
            setTokenToSession(session, ucResult.data.token);
            setTokenUidLocalCache(ucResult.data.uid, ucResult.data.token);

            LoginResult loginResult = new LoginResult(LoginResult.STATE.SUCCESS, ucResult.data.userpassword);
            loginResult.ucUser = ucResult.data;
            return loginResult;
        } else if (ucResult.noMatchData()) {
            return new LoginResult(LoginResult.STATE.USERNAME_PASSWORD_ERROR, null);
        } else {
            LOGGER.error("login error: " + ucResult);
        }

        return new LoginResult(LoginResult.STATE.UNKNOWN_ERROR, null);
    }

    /**
     * 使用加密的密码登录。加密的密码调用登录、注册接口可以获得。
     * 
     * @param session 不能为null。HTTP Session
     * @param username 必须。用户名的形式包括：一般形式的用户名、用户密码、用户邮箱地址
     * @param encryptedPassword 必须。加密的密码
     * @return SUCCESS：成功，USERNAME_PASSWORD_ERROR：用户名或密码错误， UNKNOWN_ERROR：未知错误
     */
    public static LoginResult encryptLogin(Session session, String username, String encryptedPassword) {
        if (null == session || StringUtils.isBlank(username) || StringUtils.isBlank(encryptedPassword)) {
            throw new IllegalArgumentException("illegal method input param. params: session=" + session + ", username="
                    + username + ", encryptedPassword is " + (encryptedPassword == null ? "" : "not") + " null ");
        }

        String device = DeviceUtil.getDeviceByUrl(Context.current().request().path());
        String ip = Context.current().request().remoteAddress();
        UCResult<UCUser> ucResult = UCClient.encryptLogin(username, encryptedPassword, device, ip);

        if (ucResult.isSuccess()) {
            setTokenToSession(session, ucResult.data.token);
            setTokenUidLocalCache(ucResult.data.uid, ucResult.data.token);

            LoginResult loginResult = new LoginResult(LoginResult.STATE.SUCCESS, ucResult.data.userpassword);
            loginResult.ucUser = ucResult.data;
            return loginResult;
        } else if (ucResult.noMatchData()) {
            return new LoginResult(LoginResult.STATE.USERNAME_PASSWORD_ERROR, null);
        } else {
            LOGGER.error("login error: " + ucResult);
        }

        return new LoginResult(LoginResult.STATE.UNKNOWN_ERROR, null);
    }

    /**
     * 登出
     * 
     * @param session 不能为null。HTTP Session
     * @return SUCCESS:成功，ALREADY_LOGOUT：已经是登出状态，UNKNOWN_ERROR：未知错误
     */
    public static LoginoutResult loginout(Session session) {
        if (null == session) {
            throw new IllegalArgumentException("illegal method input param. params: session=" + session);
        }

        if (isLogin(session)) {
            String token = getTokenInSession(session);

            UCResult<Void> ucResult = UCClient.logout(token);

            removeTokenUidLocalCache(token);
            removeTokenInSession(session);

            if (ucResult.isSuccess()) {
                return LoginoutResult.SUCCESS;
            } else if (ucResult.noMatchData()) {
                return LoginoutResult.ALREADY_LOGOUT;
            } else {
                LOGGER.error("logout error: " + ucResult);
            }
        }

        return LoginoutResult.UNKNOWN_ERROR;
    }

    /**
     * 刷新登录信息。<br>
     * 用户每次请求的开始都需要和用户中心同步用户的登录状态。该方法应该在过滤器中调用，且比该服务中任何方法都先调用<br>
     * 该方法会保证session中的token始终是被验证的登录凭证。
     * 
     * @param response
     * @param request
     * 
     * @param session
     * @return validateTokenResult
     */
    public static void refreshLoginInfo(Request request, Response response, Session session) {
        if (null == session) {
            throw new IllegalArgumentException("illegal method input param. params: session=" + session);
        }

        // 清理无效session，以免造成无法登录的问题
        if (null != sessionDomain && !sessionDomain.equals("www.helome.com")
                && StringUtils.countMatches(request.getHeader("Cookie"), "PLAY_SESSION") > 1) {
            response.setCookie("PLAY_SESSION", "", -86400, "/", null, false, true);
        }

        String token = session.get(LOGIN_TOKEN_SESSION_KEY);

        if (StringUtils.isNotBlank(token)) {
            ValidateTokenResult validateTokenResult = validateToken(token);
            if (validateTokenResult != ValidateTokenResult.VALID) {
                removeTokenInSession(session);
                UserInfoCookieService.discardCookie();
            }
            // 被踢下线，在session中放置一个标识。
            if (ValidateTokenResult.KICKED == validateTokenResult) {
                session.put(KICKED_FLAG_KEY, "1");
            }
            
            if (ValidateTokenResult.VALID == validateTokenResult) {
                UserInfoCookieService.createOrUpdateCookie(false);
            }
        }

    }

    /**
     * 用户是否被踢下线
     * 
     * @param session HTTP SESSION
     * @return true - 被踢下线 ，false - 未被踢下线（可能登录，也可能未登录）
     */
    public static boolean isKicked(Session session) {
        return "1".equals(session.get(KICKED_FLAG_KEY));
    }

    /**
     * 移除踢下线flag
     * 
     * @param session
     */
    public static void removeKickedFlag(Session session) {
        session.remove(KICKED_FLAG_KEY);
    }

    /**
     * 向用户中心确认token是否有效
     * 
     * @param token
     * @return validateTokenResult
     */
    public static ValidateTokenResult validateToken(String token) {
        if (StringUtils.isBlank(token)) {
            return ValidateTokenResult.INVALID;
        }

        UCResult<Void> ucResult = null;
        try {
            ucResult = UCClient.checkUserLoginStatus(token);
        } catch (UserCenterException e) {
            LOGGER.error("UserCenterException", e);
        }

        if (null == ucResult) {
            return ValidateTokenResult.INVALID;
        }

        if (!ucResult.isSuccess()) {
            if (ucResult.duplicateData()) {
                return ValidateTokenResult.KICKED;
            }

            if (!ucResult.noMatchData()) {
                LOGGER.error("checkUserLoginStatus error: " + ucResult);
            }

            return ValidateTokenResult.INVALID;
        }

        return ValidateTokenResult.VALID;
    }

    public static String getTokenInSession(Session session) {
        return session.get(LOGIN_TOKEN_SESSION_KEY);
    }

    public static void removeTokenInSession(Session session) {
        session.remove(LOGIN_TOKEN_SESSION_KEY);
    }

    public static class LoginResult {
        public STATE state;
        /**
         * 加密后的密码
         */
        public String encryptedPassword;

        public UCUser ucUser;

        private LoginResult(STATE state, String encryptedPassword) {
            this.state = state;
            this.encryptedPassword = encryptedPassword;
        }

        public enum STATE {
            SUCCESS, USERNAME_PASSWORD_ERROR, UNKNOWN_ERROR
        }
    }

    public enum LoginoutResult {
        SUCCESS, ALREADY_LOGOUT, UNKNOWN_ERROR
    }

    public enum ValidateTokenResult {
        VALID, INVALID,
        /** 被踢下线 **/
        KICKED
    }

    /**
     * 存储在session中的key，存储登录token
     */
    private static final String LOGIN_TOKEN_SESSION_KEY = "_ls_tk";

    private static void setTokenToSession(Session session, String token) {
        session.put(LOGIN_TOKEN_SESSION_KEY, token);
    }

    /**
     * token-uid缓存超时时间，秒为单位
     */
    private static final int tokenUidCacheTimeout = ConfigFactory.getInt("userCenter.cache.tokenUidCache.timeout");

    private static String getTokenUidLocalCache(String token) {
        return (String) Cache.get(Constants.CACHE_LOGIN_TOKEN_UID + token);
    }

    private static void setTokenUidLocalCache(String uid, String token) {
        Cache.set(Constants.CACHE_LOGIN_TOKEN_UID + token, uid, tokenUidCacheTimeout);
    }

    private static void removeTokenUidLocalCache(String token) {
        Cache.remove(Constants.CACHE_LOGIN_TOKEN_UID + token);
    }

}
