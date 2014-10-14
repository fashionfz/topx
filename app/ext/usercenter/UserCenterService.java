/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-2-24
 */
package ext.usercenter;

import javax.persistence.EntityManager;

import models.User;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import utils.DeviceUtil;
import utils.jpa.IndieTransactionCall;
import utils.jpa.JPAUtil;
import ext.usercenter.UserAuthService.LoginResult;

/**
 * 
 * 
 * @ClassName: UserCenterService
 * @Description: 用户中心相关服务
 * @date 2014-2-24 下午6:44:01
 * @author ShenTeng
 * 
 */
public class UserCenterService {

    private static final ALogger LOGGER = Logger.of(UserCenterService.class);

    /**
     * 注册用户 ，只要注册成功，就认为用户登录成功了
     * 
     * @param session HTTP Session。必须
     * @param email 邮箱。必须
     * @param userpassword 密码，明文。必须
     * @param userId 用户Id。必须
     * @param realname 姓名。必须
     * @return SUCCESS：成功，USERNAME_DUPLICATE：email重复，UNKNOWN_ERROR：未知错误
     */
    public static RegisterResult registerByEmail(Session session, String email, String userpassword, Long userId,
            String realname) {
        if (null == session || StringUtils.isBlank(email) || StringUtils.isBlank(userpassword) || userId == null
                || StringUtils.isBlank(realname)) {
            throw new IllegalArgumentException("illegal method input param. params: session=" + session + ", email="
                    + email + ", userpassword is " + (userpassword == null ? "" : "not") + " null, userId = " + userId
                    + ", realname = " + realname);
        }

        String device = DeviceUtil.getDeviceByUrl(Context.current().request().path());
        String ip = Context.current().request().remoteAddress();
        UCResult<UCUser> ucResult = UCClient.register(userpassword, null, realname, email, null, device, ip, userId);

        if (ucResult.isSuccess()) {
            LoginResult loginResult = UserAuthService.login(session, email, userpassword);
            if (!loginResult.state.equals(LoginResult.STATE.SUCCESS)) {
                LOGGER.error("register error: register success but login fail.");
                return new RegisterResult(RegisterResult.STATE.UNKNOWN_ERROR, null);
            } else {
                return new RegisterResult(RegisterResult.STATE.SUCCESS, ucResult.data.userpassword);
            }
        } else if (ucResult.duplicateData()) {
            return new RegisterResult(RegisterResult.STATE.USERNAME_DUPLICATE, null);
        } else {
            LOGGER.error("register error: " + ucResult);
        }

        return new RegisterResult(RegisterResult.STATE.UNKNOWN_ERROR, null);
    }

    /**
     * 注册用户 ，只要注册成功，就认为用户登录成功了
     * 
     * @param session HTTP Session。必须
     * @param username 用户名（非邮箱、非手机号）。必须
     * @param userpassword 密码，明文。必须
     * @param userId 用户Id。必须
     * @param realname 姓名。必须
     * @return SUCCESS：成功，USERNAME_DUPLICATE：username重复，UNKNOWN_ERROR：未知错误
     */
    public static RegisterResult registerByUsername(Session session, String username, String userpassword, Long userId,
            String realname) {
        if (null == session || StringUtils.isBlank(username) || StringUtils.isBlank(userpassword) || userId == null
                || StringUtils.isBlank(realname)) {
            throw new IllegalArgumentException("illegal method input param. params: session=" + session + ", username="
                    + username + ", userpassword is " + (userpassword == null ? "" : "not") + " null, userId = "
                    + userId + ", realname = " + realname);
        }

        String device = DeviceUtil.getDeviceByUrl(Context.current().request().path());
        String ip = Context.current().request().remoteAddress();
        UCResult<UCUser> ucResult = UCClient.register(userpassword, username, realname, null, null, device, ip, userId);

        if (ucResult.isSuccess()) {
            LoginResult loginResult = UserAuthService.login(session, username, userpassword);
            if (!loginResult.state.equals(LoginResult.STATE.SUCCESS)) {
                LOGGER.error("register error: register success but login fail.");
                return new RegisterResult(RegisterResult.STATE.UNKNOWN_ERROR, null);
            } else {
                return new RegisterResult(RegisterResult.STATE.SUCCESS, ucResult.data.userpassword);
            }
        } else if (ucResult.duplicateData()) {
            return new RegisterResult(RegisterResult.STATE.USERNAME_DUPLICATE, null);
        } else {
            LOGGER.error("register error: " + ucResult);
        }

        return new RegisterResult(RegisterResult.STATE.UNKNOWN_ERROR, null);
    }

    /**
     * 检查email是否被使用
     * 
     * @param email
     * @return EMAIL_EXIST：email被使用，EMAIL_NOT_EXIST:email未被使用，UNKNOWN_ERROR:未知错误
     */
    public static EmailExistResult validateEmailExist(String email) {
        UCResult<UCUser> ucResult = UCClient.queryUsernameExist(email);
        if (ucResult.isSuccess() && null != ucResult.data) {
            return EmailExistResult.EMAIL_EXIST;
        } else if (ucResult.noMatchData()) {
            return EmailExistResult.EMAIL_NOT_EXIST;
        } else {
            LOGGER.error("queryUsernameExist error: " + ucResult);
        }
        return EmailExistResult.UNKNOWN_ERROR;
    }

    /**
     * 检查手机号是否被使用
     * 
     * @param phoneNum 手机号
     * @return EXIST：手机号被使用，NOT_EXIST:手机号未被使用，UNKNOWN_ERROR:未知错误
     */
    public static PhoneNumExistResult validatePhoneNumExist(String phoneNum) {
        UCResult<UCUser> ucResult = UCClient.queryUsernameExist(phoneNum);

        if (ucResult.isSuccess() && null != ucResult.data) {
            return PhoneNumExistResult.EXIST;
        } else if (ucResult.noMatchData()) {
            return PhoneNumExistResult.NOT_EXIST;
        } else {
            LOGGER.error("queryUsernameExist error: " + ucResult);
        }

        return PhoneNumExistResult.UNKNOWN_ERROR;
    }

    /**
     * 检查登录用户名是否被使用
     * 
     * @param loginUsername 登录用户名
     * @return EXIST：登录用户名被使用，NOT_EXIST:登录用户名未被使用，UNKNOWN_ERROR:未知错误
     */
    public static LoginUsernameExistResult validateLoginUsernameExist(String loginUsername) {
        UCResult<UCUser> ucResult = UCClient.queryUsernameExist(loginUsername);

        if (ucResult.isSuccess() && null != ucResult.data) {
            return LoginUsernameExistResult.EXIST;
        } else if (ucResult.noMatchData()) {
            return LoginUsernameExistResult.NOT_EXIST;
        } else {
            LOGGER.error("queryUsernameExist error: " + ucResult);
        }

        return LoginUsernameExistResult.UNKNOWN_ERROR;
    }

    /**
     * 修改密码
     * 
     * @param username 必须。用户名的形式包括：一般形式的用户名、用户密码、用户邮箱地址
     * @param newPassword 必须。新密码，明文
     * @return SUCCESS：成功，USERNAME_NOT_EXIST：用户名不存在， UNKNOWN_ERROR：未知错误
     */
    public static UserCenterService.ModifyPasswordResult modifyPassword(final Long userId, String username,
            String newPassword) {
        if (null == userId || StringUtils.isBlank(username) || StringUtils.isBlank(newPassword)) {
            throw new IllegalArgumentException("param can't be null or blank.");
        }

        final UCResult<UCUser> ucResult = UCClient.modifyPassword(username, newPassword);

        if (ucResult.isSuccess()) {
            JPAUtil.indieTransaction(new IndieTransactionCall() {
                @Override
                public void call(EntityManager em) {
                    User.updateEncryptedPasswordById(userId, ucResult.data.userpassword);
                }
            });
            return new UserCenterService.ModifyPasswordResult(UserCenterService.ModifyPasswordResult.STATE.SUCCESS,
                    ucResult.data.userpassword);
        } else if (ucResult.noMatchData()) {
            return new UserCenterService.ModifyPasswordResult(
                    UserCenterService.ModifyPasswordResult.STATE.USERNAME_NOT_EXIST, null);
        } else {
            LOGGER.error("modifyPassword error: " + ucResult);
        }

        return new UserCenterService.ModifyPasswordResult(UserCenterService.ModifyPasswordResult.STATE.UNKNOWN_ERROR,
                null);
    }

    /**
     * 验证当前用户的密码<br>
     * 用于修改用户名（包括：一般形式的用户名、用户密码、用户邮箱地址）时的密码验证<br>
     * 当前用户登录才能修改
     * 
     * @param session 必须。HTTP session
     * @param password 必须。密码
     * @return CORRECT：密码匹配，INCORRECT：密码不匹配，NOT_LOGIN：用户未登录，UNKNOWN_ERROR：未知错误
     */
    public static ValidatePasswordResult validatePasswordForModifyUsername(Session session, String password) {

        if (!UserAuthService.isLogin(session)) {
            return ValidatePasswordResult.NOT_LOGIN;
        }

        String token = UserAuthService.getTokenInSession(session);

        UCResult<Void> ucResult = UCClient.checkPassword4ModifyUsername(token, password);

        if (ucResult.isSuccess()) {
            return ValidatePasswordResult.CORRECT;
        } else if (ucResult.noMatchData()) {
            return ValidatePasswordResult.INCORRECT;
        } else {
            LOGGER.error("checkPassword4ModifyUsername error: " + ucResult);
        }

        return ValidatePasswordResult.UNKNOWN_ERROR;
    }

    /**
     * 验证当前用户的密码<br>
     * 用于修改密码时的原密码验证<br>
     * 当前用户登录才能修改
     * 
     * @param session 必须。HTTP session
     * @param password 必须。密码
     * @return CORRECT：密码匹配，INCORRECT：密码不匹配，NOT_LOGIN：用户未登录，UNKNOWN_ERROR：未知错误
     */
    public static ValidatePasswordResult validatePasswordForModifyPassword(Session session, String password) {

        if (!UserAuthService.isLogin(session)) {
            return ValidatePasswordResult.NOT_LOGIN;
        }

        String token = UserAuthService.getTokenInSession(session);
        UCResult<Void> ucResult = UCClient.checkPassword4ModifyPassword(token, password);

        if (ucResult.isSuccess()) {
            return ValidatePasswordResult.CORRECT;
        } else if (ucResult.noMatchData()) {
            return ValidatePasswordResult.INCORRECT;
        } else {
            LOGGER.error("checkPassword4ModifyPassword error: " + ucResult);
        }

        return ValidatePasswordResult.UNKNOWN_ERROR;
    }

    /**
     * 获取当前登录用户的密码安全级别<br>
     * 当前用户必须登录
     * 
     * @param session 必须。HTTP session
     * @return 密码安全级别， null - 未登录或者系统错误
     */
    public static PasswordSecurityGrade getPasswordSecurityGrade(Session session) {
        if (!UserAuthService.isLogin(session)) {
            return null;
        }

        String token = UserAuthService.getTokenInSession(session);
        UCResult<String> ucResult = UCClient.passwordSecurityGrade(token);

        if (ucResult.isSuccess()) {
            return PasswordSecurityGrade.getFromKey(ucResult.data);
        } else if (ucResult.noMatchData()) {
            return null;
        } else {
            LOGGER.error("passwordSecurityGrade error: " + ucResult);
        }

        return null;
    }

    /**
     * 修改邮箱<br>
     * 用户登录才能修改
     * 
     * @param session 必须。HTTP session
     * @param newEmail 必须。新邮箱
     * @return ModifyEmailResult
     */
    public static ModifyEmailResult modifyEmail(Session session, String newEmail) {
        if (!UserAuthService.isLogin(session)) {
            return ModifyEmailResult.NOT_LOGIN;
        }

        String token = UserAuthService.getTokenInSession(session);
        UCResult<Void> ucResult = UCClient.modifyEmail(token, newEmail);

        if (ucResult.isSuccess()) {
            return ModifyEmailResult.SUCCESS;
        } else if (ucResult.duplicateData()) {
            return ModifyEmailResult.EMAIL_EXIST;
        } else {
            LOGGER.error("modifyEmail error: " + ucResult);
        }

        return ModifyEmailResult.UNKNOWN_ERROR;
    }

    /**
     * 修改姓名
     * 
     * @param session 必须。HTTP session
     * @param realname 新的姓名
     * @return
     */
    public static ModifyRealnameResult modifyRealname(Session session, String realname) {
        if (!UserAuthService.isLogin(session)) {
            return ModifyRealnameResult.NOT_LOGIN;
        }

        String token = UserAuthService.getTokenInSession(session);
        UCResult<Void> ucResult = UCClient.modifyRealname(token, realname);

        if (ucResult.isSuccess()) {
            return ModifyRealnameResult.SUCCESS;
        } else {
            LOGGER.error("modifyRealname error: " + ucResult);
        }

        return ModifyRealnameResult.UNKNOWN_ERROR;
    }

    /**
     * 修改手机号<br>
     * 用户登录才能修改
     * 
     * @param session 必须。HTTP session
     * @param newPhoneNumber 必须。新手机号
     * @return BindPhoneNumberResult
     */
    public static BindPhoneNumberResult bindPhoneNumber(Session session, String newPhoneNumber) {
        if (!UserAuthService.isLogin(session)) {
            return BindPhoneNumberResult.NOT_LOGIN;
        }

        String token = UserAuthService.getTokenInSession(session);
        UCResult<Void> ucResult = UCClient.bindingPhoneNumber(token, newPhoneNumber);

        if (ucResult.isSuccess()) {
            return BindPhoneNumberResult.SUCCESS;
        } else if (ucResult.duplicateData()) {
            return BindPhoneNumberResult.PHONE_NUM_EXIST;
        } else if (ucResult.illegalParam()) {
            return BindPhoneNumberResult.ILLEGAL_PHONE_NUM;
        } else {
            LOGGER.error("bindingPhoneNumber error: " + ucResult);
        }

        return BindPhoneNumberResult.UNKNOWN_ERROR;
    }

    public enum EmailExistResult {
        EMAIL_EXIST, EMAIL_NOT_EXIST, UNKNOWN_ERROR
    }

    public enum PhoneNumExistResult {
        EXIST, NOT_EXIST, UNKNOWN_ERROR
    }

    public enum LoginUsernameExistResult {
        EXIST, NOT_EXIST, UNKNOWN_ERROR
    }

    public enum ModifyEmailResult {
        SUCCESS, EMAIL_EXIST, NOT_LOGIN, UNKNOWN_ERROR
    }

    public enum ModifyRealnameResult {
        SUCCESS, NOT_LOGIN, UNKNOWN_ERROR
    }

    public enum BindPhoneNumberResult {
        SUCCESS, PHONE_NUM_EXIST, NOT_LOGIN, ILLEGAL_PHONE_NUM, UNKNOWN_ERROR
    }

    public enum ValidatePasswordResult {
        CORRECT, INCORRECT, NOT_LOGIN, UNKNOWN_ERROR
    }

    public enum PasswordSecurityGrade {
        WEAK("weak"), MEDIUM("medium"), STRONG("strong");

        private String key;

        PasswordSecurityGrade(String key) {
            this.key = key;
        }

        public static PasswordSecurityGrade getFromKey(String key) {
            PasswordSecurityGrade[] gradeArray = PasswordSecurityGrade.values();
            for (PasswordSecurityGrade grade : gradeArray) {
                if (null != grade.key && grade.key.equals(key)) {
                    return grade;
                }
            }

            return null;
        }
    }

    public static class ModifyPasswordResult {
        public STATE state;
        /**
         * 加密后的密码
         */
        public String encryptedPassword;

        private ModifyPasswordResult(STATE state, String encryptedPassword) {
            this.state = state;
            this.encryptedPassword = encryptedPassword;
        }

        public enum STATE {
            SUCCESS, USERNAME_NOT_EXIST, UNKNOWN_ERROR
        }
    }

    public static class RegisterResult {
        public STATE state;
        /**
         * 加密后的密码
         */
        public String encryptedPassword;

        private RegisterResult(STATE state, String encryptedPassword) {
            this.state = state;
            this.encryptedPassword = encryptedPassword;
        }

        public enum STATE {
            SUCCESS, USERNAME_DUPLICATE, UNKNOWN_ERROR
        }
    }
}
