/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-11
 */
package ext.sns.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import models.Expert;
import models.Gender;
import models.GroupMember;
import models.User;
import models.User.UsernameType;
import models.service.LoginUserCache;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import play.Logger;
import play.Logger.ALogger;
import play.db.jpa.JPA;
import play.mvc.Http.Context;
import controllers.base.ObjectNodeResult;
import ext.sns.auth.AccessTokenResponse;
import ext.sns.model.UserOAuth;
import ext.sns.openapi.OpenAPIResult;
import ext.sns.openapi.OpenAPIResult.STATE;
import ext.sns.openapi.UserInfo;
import ext.sns.service.UserOAuthService.UserOAuthResult.UserOAuthSTATE;
import ext.usercenter.UserAuthService;
import ext.usercenter.UserCenterService;

/**
 * 
 * 
 * @ClassName: UserOAuth2Service
 * @Description: 用户OAuth2授权服务类,提供登录、绑定等具体授权相关业务的服务
 * @date 2014-6-11 下午2:05:47
 * @author ShenTeng
 * 
 */
public class UserOAuthService {

    private static final ALogger LOGGER = Logger.of(UserOAuthService.class);

    /**
     * 根据UserOAuthId获取UserOAuth对象
     * 
     * @param id UserOAuthId
     * @return UserOAuth对象
     */
    public static UserOAuth getById(Long id) {
        if (null == id) {
            return null;
        }
        List<UserOAuth> resultList = JPA.em().createQuery("from UserOAuth where id=:id", UserOAuth.class)
                .setParameter("id", id).getResultList();

        if (CollectionUtils.isEmpty(resultList)) {
            return null;
        }

        return resultList.get(0);
    }

    /**
     * 根据用户Id获取UserOAuth Map
     * 
     * @param userId 用户Id
     * @return UserOAuth Map: key - 提供商名字，value - UserOAuth对象
     */
    public static Map<String, UserOAuth> getByUserId(Long userId) {
        List<UserOAuth> userOAuthList = JPA.em().createQuery("from UserOAuth where user.id=:userId", UserOAuth.class)
                .setParameter("userId", userId).getResultList();

        if (CollectionUtils.isEmpty(userOAuthList)) {
            return new HashMap<String, UserOAuth>(0);
        }

        Map<String, UserOAuth> userOAuthMap = new HashMap<>();
        for (UserOAuth userOAuth : userOAuthList) {
            userOAuthMap.put(userOAuth.provider, userOAuth);
        }

        return userOAuthMap;
    }

    /**
     * 根据用户Id获取有效的UserOAuth Map
     * 
     * @param userId 用户Id
     * @return UserOAuth Map: key - 提供商名字，value - UserOAuth对象
     */
    public static Map<String, UserOAuth> getValidByUserId(Long userId) {
        List<UserOAuth> userOAuthList = JPA.em().createQuery("from UserOAuth where user.id=:userId", UserOAuth.class)
                .setParameter("userId", userId).getResultList();

        if (CollectionUtils.isEmpty(userOAuthList)) {
            return new HashMap<String, UserOAuth>(0);
        }

        Map<String, UserOAuth> userOAuthMap = new HashMap<>();
        for (UserOAuth userOAuth : userOAuthList) {
            if (userOAuth.isValid()) {
                userOAuthMap.put(userOAuth.provider, userOAuth);
            }
        }

        return userOAuthMap;
    }

    /**
     * 取消授权，注意：调用该接口前应该要求用户完善邮箱，否则可能导致用户没有登录的途径
     * 
     * @param userId 用户Id
     * @param provider Provider枚举
     */
    public static void revokeAuthorize(User user, String provider) {
        if (null == user || null == provider) {
            throw new IllegalArgumentException("参数不能为null。");
        }

        UserOAuth userOAuth = getByUserIdAndProvider(user.id, provider);
        if (null != userOAuth) {
            SNSService.revokeAuth(userOAuth);
            JPA.em().remove(userOAuth);
        }
    }

    /**
     * 创建并保存UserOAuth对象，不包括其中的user属性。
     */
    public static UserOAuthResult saveOrUpdate(AccessTokenResponse accessTokenResponse, String providerName) {
        return saveOrUpdate(providerName, accessTokenResponse.getAccessToken(), accessTokenResponse.getExpiresIn(),
                accessTokenResponse.getRefreshToken(), accessTokenResponse.getResponseValue("openid"));
    }

    /**
     * 创建UserOAuth对象，不包括其中的user属性。
     * 
     * @param openId 只有tencentWeibo需要传递该参数
     */
    public static UserOAuthResult saveOrUpdate(String providerName, String token, Long expiredIn, String refreshToken,
            String openId) {
        Date now = new Date();
        UserOAuth userOAuth = new UserOAuth();

        userOAuth.createTime = now;
        userOAuth.refreshTime = now;
        userOAuth.expiredIn = expiredIn;
        userOAuth.provider = providerName;
        userOAuth.token = token;
        userOAuth.refreshToken = refreshToken;

        if (providerName.equals("tencentWeibo")) {
            userOAuth.openId = openId;
        }

        if (null == userOAuth.expiredIn || StringUtils.isBlank(userOAuth.provider)
                || StringUtils.isBlank(userOAuth.token)) {
            LOGGER.error("initAuth失败1。某些必须字段为空。UserOAuth = " + userOAuth);
            return new UserOAuthResult(UserOAuthSTATE.INVALID_TOKEN, null);
        }

        OpenAPIResult<UserInfo> result = SNSService.getUserInfo(userOAuth);
        if (!result.isSuccess()) {
            if (result.getState() == STATE.INVALID_TOKEN) {
                return new UserOAuthResult(UserOAuthSTATE.INVALID_TOKEN, null);
            } else {
                return new UserOAuthResult(UserOAuthSTATE.UNKNOWN_ERROR, null);
            }
        } else {
            UserInfo userInfo = result.getResult();
            userOAuth.openId = userInfo.getOpenId();
            userOAuth.nickname = userInfo.getNickName();
            userOAuth.avatarUrl = userInfo.getAvatarUrl();
        }

        if (null == userOAuth.expiredIn || StringUtils.isBlank(userOAuth.provider)
                || StringUtils.isBlank(userOAuth.token) || StringUtils.isBlank(userOAuth.openId)
                || StringUtils.isBlank(userOAuth.nickname)) {
            LOGGER.error("initAuth失败2。某些必须字段为空。UserOAuth = " + userOAuth);
            return new UserOAuthResult(UserOAuthSTATE.INVALID_TOKEN, null);
        }

        // 插入或更新数据库
        UserOAuth newUserOAuth = null;
        UserOAuth oldUserOAuth = getByProviderAndOpenId(userOAuth.provider, userOAuth.openId);
        if (null != oldUserOAuth) {
            oldUserOAuth.avatarUrl = userOAuth.avatarUrl;
            oldUserOAuth.createTime = userOAuth.createTime;
            oldUserOAuth.expiredIn = userOAuth.expiredIn;
            oldUserOAuth.nickname = userOAuth.nickname;
            oldUserOAuth.refreshTime = userOAuth.refreshTime;
            oldUserOAuth.refreshToken = userOAuth.refreshToken;
            oldUserOAuth.token = userOAuth.token;

            JPA.em().merge(oldUserOAuth);
            newUserOAuth = oldUserOAuth;
        } else {
            userOAuth = JPA.em().merge(userOAuth);
            newUserOAuth = userOAuth;
        }

        return new UserOAuthResult(UserOAuthSTATE.SUCCESS, newUserOAuth);
    }

    /**
     * 绑定到用户
     * 
     * @param userOAuth UserOAuth对象
     * @param user 目标用户
     * @return 1 - 成功。 <br>
     *         -1 - 失败。已经被其他用户绑定。 <br>
     */
    public static int bindToUser(UserOAuth userOAuth, User user) {
        if (userOAuth == null || user == null || userOAuth.id == null || user.id == null) {
            throw new IllegalArgumentException("参数非法. userOAuth = " + userOAuth + " ,user = " + user);
        }

        // 其他用户已经绑定该第三方帐号
        if (userOAuth.user != null && !user.id.equals(userOAuth.user.getId())) {
            return -1;
        }

        userOAuth.user = user;
        JPA.em().merge(userOAuth);

        return 1;
    }

    /**
     * 使用绑定的用户登录
     * 
     * @param userOAuth 已经绑定过用户的UserOAuth对象
     * @return 1 - 成功。 <br>
     *         0 - 失败。<br>
     *         -1 - 账户被禁用。
     */
    public static int loginByBindUser(UserOAuth userOAuth) {
        if (userOAuth == null || userOAuth.id == null) {
            throw new IllegalArgumentException("参数非法. userOAuth = " + userOAuth);
        }

        // 已经登录先注销
        if (UserAuthService.isLogin(Context.current().session())) {
            User.logout(Context.current().session());
        }

        // 如果已经绑定过用户，则直接用该用户登录
        if (!userOAuth.isBindUser()) {
            LOGGER.error("userOAuth未关联用户。 userOAuth.id = " + userOAuth.id);
            return 0;
        }

        User authUser = userOAuth.user;

        ObjectNodeResult loginObjectNodeResult = null;
        if (StringUtils.isNotBlank(authUser.getLoginUsername())) {
            loginObjectNodeResult = User.loginByEncryptedPassword(authUser.getLoginUsername(),
                    authUser.getEncryptedPassword(), false, UsernameType.LOGIN_USERNAME);
        } else if (StringUtils.isNotBlank(authUser.getEmail())) {
            loginObjectNodeResult = User.loginByEncryptedPassword(authUser.getEmail(), authUser.getEncryptedPassword(),
                    false, UsernameType.EMAIL);
        } else {
            LOGGER.error("无法自动登录。loginUsername和email均为空。");
            return 0;
        }

        if (!loginObjectNodeResult.isSuccess()) {
            if ("200002".equals(loginObjectNodeResult.getErrorCode())) {
                return -1;
            } else {
                LOGGER.error("登录失败。UserOAuth.id = " + userOAuth.id + ",loginResultError = "
                        + loginObjectNodeResult.getError());
                return 0;
            }
        }

        return 1;
    }

    /**
     * 注册随机用户，注册时会自动使用该用户登录
     * 
     * @param name 用户姓名，如果为blank,则使用默认姓名
     * @return
     */
    public static User registerRandomUser(String name) {
        // 注册帐号。用户名为helome_[时间毫秒数]_[随机数]，密码UUID随机生成。
        String username = "helome_" + System.currentTimeMillis() + "_" + Math.abs(new Random().nextInt());
        String password = UUID.randomUUID().toString();

        ObjectNodeResult registerObjectNodeResult = User.register(username, password, Gender.MAN, null, false,
                UsernameType.LOGIN_USERNAME, null);
        if (!registerObjectNodeResult.isSuccess()) {
            LOGGER.error("注册失败。");
            return null;
        }
        User user = registerObjectNodeResult.getUser();

        if (StringUtils.isNotBlank(name)) {
            user.userName = name;
            JPA.em().merge(user);

            Expert expert = Expert.findByUserId(user.id);
            if (null != expert) {
                expert.userName = name;
                expert.saveOrUpate();
                GroupMember.updateUserNameByUserId(expert.userName, user.getId());
            }

            UserCenterService.modifyRealname(Context.current().session(), user.userName);
            LoginUserCache.refreshBySession(Context.current().session());
        }

        return user;
    }

    public static class UserOAuthResult {
        public enum UserOAuthSTATE {
            SUCCESS, INVALID_TOKEN, UNKNOWN_ERROR;
        }

        private UserOAuthSTATE state;
        private UserOAuth result;

        public UserOAuthResult(UserOAuthSTATE state, UserOAuth result) {
            this.state = state;
            this.result = result;
        }

        public UserOAuthSTATE getState() {
            return state;
        }

        public UserOAuth getResult() {
            return result;
        }

        public boolean isSuccess() {
            return state == UserOAuthSTATE.SUCCESS;
        }
    }

    /**
     * 根据用户Id和Provider枚举获取UserOAuth实体
     * 
     * @param userId 用户Id
     * @param provider Provider枚举
     * @return UserOAuth实体
     */
    private static UserOAuth getByUserIdAndProvider(Long userId, String provider) {
        List<UserOAuth> userOAuthList = JPA.em()
                .createQuery("from UserOAuth where user.id=:userId and provider=:provider", UserOAuth.class)
                .setParameter("userId", userId).setParameter("provider", provider).getResultList();

        return CollectionUtils.isEmpty(userOAuthList) ? null : userOAuthList.get(0);
    }

    /**
     * 根据openId获取UserOAuth实体
     * 
     * @param openId openId
     * @return UserOAuth实体
     */
    private static UserOAuth getByProviderAndOpenId(String provider, String openId) {
        List<UserOAuth> userOAuthList = JPA.em()
                .createQuery("from UserOAuth where openId=:openId and provider=:provider", UserOAuth.class)
                .setParameter("openId", openId).setParameter("provider", provider).getResultList();

        return CollectionUtils.isEmpty(userOAuthList) ? null : userOAuthList.get(0);
    }
}
