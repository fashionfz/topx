/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-27
 */
package models;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import models.User.UsernameType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import common.Constants;

import play.Logger;
import play.Logger.ALogger;
import play.db.jpa.JPA;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Http.Cookie;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Http.Session;
import utils.jpa.IndieTransactionCall;
import utils.jpa.JPAUtil;
import controllers.base.ObjectNodeResult;
import ext.config.ConfigFactory;
import ext.usercenter.UserAuthService;

/**
 * 
 * 
 * @ClassName: RememberMeInfo
 * @Description: 记住我的相关信息
 * @date 2013-11-27 下午3:41:44
 * @author ShenTeng
 * 
 */
@javax.persistence.Entity
@Table(name = "tb_remember_me_info")
public class RememberMeInfo {

    private static final ALogger LOGGER = Logger.of(RememberMeInfo.class);

    private static int REMEMBER_ME_EXPIRES = ConfigFactory.getInt("user.rememberMe.expires");

    private static int AUTO_LOGIN_INTERVAL = ConfigFactory.getInt("user.rememberMe.autoLoginInterval");

    private static PathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    /**
     * 记住我的开始时间
     */
    public Date beginTime;

    /**
     * 记住我的cookie value
     */
    public String cookieValue;

    /**
     * 关联用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    public User user;

    /**
     * 记住我
     * 
     * @param userId 用户Id
     * @param encryptedPassword 加密了的密码
     * @param response
     */
    public static void rememberMe(Long userId) {
        if (null == userId) {
            throw new IllegalArgumentException("参数不能为空。");
        }

        forgetMeCurrent(false);

        Context context = Http.Context.current();
        Response response = context.response();
        Session session = context.session();

        RememberMeInfo entity = new RememberMeInfo();
        User user = new User();
        Date now = new Date();
        user.id = userId;
        entity.user = user;

        entity.beginTime = now;
        entity.cookieValue = generateCookieValue();

        JPA.em().persist(entity);

        User.refreshCurrentLastRMLoginTime(session);
        response.setCookie(Constants.COOKIE_REMEMBER_ME, entity.cookieValue, REMEMBER_ME_EXPIRES, "/", null, false, true);
    }

    /**
     * 忘记我，只限于当前浏览器。<br>
     * 方法中会自动处理事务，并不会和外部的事务冲突
     */
    public static void forgetMeCurrent() {
        forgetMeCurrent(true);
    }

    /**
     * 记住我自动登录<br>
     * 触发条件 ：当前未登录，cookie中有记住我的信息<br>
     * 方法中会自动处理事务，并不会和外部的事务冲突
     */
    public static void rememberMeAutoLogin(Request request, final Response response, final Session session) {
        String path = request.path();
        // 消息tell/poll不触发自动登录, forgetMe不触发自动登录
        if (StringUtils.isNotBlank(path) && (isMsgTellPollUrl(path) || path.startsWith("/forgetMe"))) {
            return;
        }

        // 触发条件 ：当前未登录，cookie中有记住我的信息
        if (!UserAuthService.isLogin(session)) {
            Cookie cookie = request.cookie(Constants.COOKIE_REMEMBER_ME);
            if (null != cookie && StringUtils.isNotBlank(cookie.value())) {
                final String cookieValue = cookie.value();

                JPAUtil.indieTransaction(new IndieTransactionCall() {

                    @Override
                    public void call(EntityManager em) {
                        @SuppressWarnings("unchecked")
                        List<RememberMeInfo> entityList = em
                                .createQuery(
                                        "from RememberMeInfo rm left join fetch rm.user where rm.cookieValue = :cookieValue ")
                                .setParameter("cookieValue", cookieValue).getResultList();

                        if (CollectionUtils.isNotEmpty(entityList)) {

                            RememberMeInfo entity = entityList.get(0);
                            User user = entity.user;

                            Calendar expires = Calendar.getInstance();
                            expires.add(Calendar.SECOND, -REMEMBER_ME_EXPIRES);

                            // 已经过期
                            if (entity.beginTime.before(expires.getTime())) {

                                em.remove(entity);
                                response.discardCookie(Constants.COOKIE_REMEMBER_ME);

                            } else if (user.lastRMLoginTime != null
                                    && user.lastRMLoginTime.getTime() + AUTO_LOGIN_INTERVAL * 1000 > System
                                            .currentTimeMillis()) { // 自动登录过于频繁

                                em.remove(entity);
                                response.discardCookie(Constants.COOKIE_REMEMBER_ME);

                            } else { // 未过期
                                ObjectNodeResult loginObjectNodeResult = null;
                                try {
                                    loginObjectNodeResult = User.loginByEncryptedPassword(user.getEmail(),
                                            user.getEncryptedPassword(), false, UsernameType.EMAIL);
                                } catch (Exception e) {
                                    LOGGER.error("fail to loginByEncryptedPassword.", e);
                                }

                                if (null == loginObjectNodeResult || !loginObjectNodeResult.isSuccess()) {
                                    em.remove(entity);
                                    response.discardCookie(Constants.COOKIE_REMEMBER_ME);
                                } else {
                                    User.refreshCurrentLastRMLoginTime(session);
                                }
                            }

                        } else {
                            response.discardCookie(Constants.COOKIE_REMEMBER_ME);
                        }
                    }
                });
            }
        }
    }

    private static boolean isMsgTellPollUrl(String path) {
        return pathMatcher.match("/msg/poll", path) || pathMatcher.match("/msg/tell", path)
                || pathMatcher.match("/mobile/*/msg/poll", path);
    }

    private static String generateCookieValue() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 忘记我，只限于当前浏览器。<br>
     * 方法中会自动处理事务，并不会和外部的事务冲突
     */
    private static void forgetMeCurrent(final boolean isDiscardCookie) {

        JPAUtil.indieTransaction(new IndieTransactionCall() {

            @Override
            public void call(EntityManager em) {
                Context context = Http.Context.current();
                Response response = context.response();
                Request request = context.request();

                Cookie cookie = request.cookie(Constants.COOKIE_REMEMBER_ME);
                if (null != cookie && StringUtils.isNotBlank(cookie.value())) {
                    String cookieValue = cookie.value();
                    JPA.em().createQuery("delete from RememberMeInfo where cookieValue = :cookieValue")
                            .setParameter("cookieValue", cookieValue).executeUpdate();
                }

                if (isDiscardCookie) {
                    response.discardCookie(Constants.COOKIE_REMEMBER_ME);
                }
            }
        });

    }

}
