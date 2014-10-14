/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年7月14日
 */
package models.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Objects;

import mobile.util.MobileUtil;
import models.User;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.mvc.Http.Context;
import play.mvc.Http.Cookie;
import utils.HelomeUtil;
import common.Constants;
import ext.usercenter.UserAuthService;

/**
 *
 *
 * @ClassName: UserInfoCookieService
 * @Description: 当前用户信息的Cookie相关服务，用于存储部分用户信息在cookie中，供前端使用
 * @date 2014年7月14日 下午3:35:19
 * @author ShenTeng
 * 
 */
public class UserInfoCookieService {

    private static final ALogger LOGGER = Logger.of(UserInfoCookieService.class);

    /**
     * 创建或者更新用户信息COOKIE
     * 
     * @param isForce 是否强制更新。通常如果请求中附带该COOKIE且值正确则不更新。强制更新忽略上述判断每次调用该方法都进行更新。
     */
    public static void createOrUpdateCookie(boolean isForce) {
        Context ct = Context.current();
        if (MobileUtil.isMobileUrlPrefix(ct.request().path())) {
            return;
        }

        User user = User.getFromSession(ct.session());
        if (null != user) {
            Long userId = user.getId();
            String username = user.getName();
            String token = UserAuthService.getTokenInSession(ct.session());

            Long userIdInCookie = cookieValueToLong(Constants.COOKIE_USERINFO_ID);
            String usernameInCookie = cookieValueToDecodedString(Constants.COOKIE_USERINFO_NAME);
            String tokenInCookie = cookieValueToDecodedString(Constants.COOKIE_USERINFO_TOKEN);

            if (isForce || !Objects.equals(userIdInCookie, userId)) {
                ct.response().setCookie(Constants.COOKIE_USERINFO_ID, String.valueOf(userId));
            }

            if (isForce || !Objects.equals(usernameInCookie, username)) {
                try {
                    ct.response().setCookie(Constants.COOKIE_USERINFO_NAME, URLEncoder.encode(username, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error("URL编码异常", e);
                }
            }

            if (isForce || !Objects.equals(tokenInCookie, token)) {
                try {
                    ct.response().setCookie(Constants.COOKIE_USERINFO_TOKEN, URLEncoder.encode(token, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error("URL编码异常", e);
                }
            }
        }
    }

    public static void discardCookie() {
        Context ct = Context.current();
        if (MobileUtil.isMobileUrlPrefix(ct.request().path())) {
            return;
        }

        ct.response().discardCookie(Constants.COOKIE_USERINFO_ID);
        ct.response().discardCookie(Constants.COOKIE_USERINFO_NAME);
        ct.response().discardCookie(Constants.COOKIE_USERINFO_TOKEN);
    }

    private static Long cookieValueToLong(String cookieName) {
        Cookie cookie = Context.current().request().cookie(cookieName);
        return cookie == null ? null : HelomeUtil.toLong(cookie.value(), null);
    }

    private static String cookieValueToDecodedString(String cookieName) {
        Cookie cookie = Context.current().request().cookie(cookieName);
        if (null == cookie) {
            return null;
        }
        String cookieValue = cookie.value();
        if (StringUtils.isNotBlank(cookieValue)) {
            try {
                cookieValue = URLDecoder.decode(cookieValue, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("URL解码异常", e);
            }
        }
        return cookieValue;
    }

}
