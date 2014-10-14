/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-13
 */
package models.service;

import static play.Play.application;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;

import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Http.Session;

import common.Constants;

import ext.config.ConfigFactory;
import ext.usercenter.UserAuthService;

/**
 * 
 * 
 * @ClassName: UserCache
 * @Description: 登录用户的缓存
 * @date 2013-11-13 下午4:37:59
 * @author ShenTeng
 * 
 */
public class LoginUserCache {

    private static ALogger LOGGER = Logger.of(LoginUserCache.class);

    /**
     * User对象缓存超时时间，秒为单位
     */
    private static final int userCacheTimeout = ConfigFactory.getInt("userCache.timeout") == null ? 30 * 60
            : ConfigFactory.getInt("userCache.timeout");

    private static final String userCacheVersion = "v1.1";

    /**
     * 根据session中的信息取用户对象<br>
     * null代表用户未登录
     * 
     * @param session Http Session
     * @return 用户对象，null代表用户未登录
     */
    public static User getBySession(Session session) {
        String uid = UserAuthService.getLoginUid(session);
        if (UserAuthService.isLogin(session)) {
            User user = LoginUserCache.getUser(uid);
            if (null == user) {
                synchronized (uid.intern()) {
                    user = LoginUserCache.getUser(uid);
                    if (null == user) {
                        user = queryUserByUidWithAutoTrasaction(uid);
                        LOGGER.debug("query login user from db. uid = " + uid);
                        if (null != user) {
                            LoginUserCache.setUser(uid, user);
                        }
                    } else {
                        LOGGER.debug("get login user from cache. uid = " + uid);
                    }
                }
            } else {
                LOGGER.debug("get login user from cache. uid = " + uid);
            }
            return user;
        }

        return null;
    }

    public static void refreshBySession(Session session) {
        String uid = UserAuthService.getLoginUid(session);
        if (StringUtils.isNotBlank(uid)) {
            removeUser(uid);
        }
    }

    public static void refreshByUid(String uid) {
        if (StringUtils.isNotBlank(uid)) {
            removeUser(uid);
        }
    }

    public static void removeBySession(Session session) {
        String uid = UserAuthService.getLoginUid(session);
        if (StringUtils.isNotBlank(uid)) {
            removeUser(uid);
        }
    }

    private static User queryUserByUidWithAutoTrasaction(final String uid) {
        EntityManager em = (EntityManager) Http.Context.current().args.get("currentEntityManager");
        if (null != em) {
            User user = queryUserByUid(uid);
            if (null != user) {
                try {
                    JPA.em().flush();
                } catch (TransactionRequiredException e) {
                    LOGGER.info("no transaction, needn't to flush.");
                }
                JPA.em().detach(user);
                if (user.id == null) {
                    user = queryUserByUid(uid);
                    JPA.em().detach(user);
                }
            }
            return user;
        } else {
            try {
                return JPA.withTransaction(new F.Function0<User>() {

                    @Override
                    public User apply() throws Throwable {
                        return queryUserByUid(uid);
                    }
                });
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static User queryUserByUid(String uid) {
        @SuppressWarnings("unchecked")
        List<User> users = JPA.em().createQuery("from User u where u.uid=:uid").setParameter("uid", uid)
                .getResultList();
        if (CollectionUtils.isNotEmpty(users)) {
            return users.get(0);
        }
        return null;
    }

    private static User getUser(String uid) {
        return (User) Cache.get(Constants.CACHE_LOGIN_USER + uid + "_" + userCacheVersion);
    }

    private static void setUser(String uid, User user) {
        Cache.set(Constants.CACHE_LOGIN_USER + uid + "_" + userCacheVersion, user, userCacheTimeout);
    }

    private static void removeUser(String uid) {
        Cache.remove(Constants.CACHE_LOGIN_USER + uid + "_" + userCacheVersion);
    }

}
