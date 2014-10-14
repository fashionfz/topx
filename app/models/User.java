/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月21日
 */
package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import models.service.IPBasedLocationService;
import models.service.LoginUserCache;
import models.service.PhoneVerifyCodeService;
import models.service.PhoneVerifyCodeService.PhoneVerifyCodeType;
import models.service.UserInfoCookieService;
import models.service.reminder.Item;
import models.service.reminder.Option;
import models.service.reminder.RemindService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;

import play.Logger;
import play.db.jpa.JPA;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import scala.concurrent.duration.Duration;
import utils.Assets;
import utils.CaptchaUtils;
import utils.HelomeUtil;

import com.fasterxml.jackson.databind.JsonNode;
import common.Constants;

import controllers.base.ObjectNodeResult;
import ext.MessageCenter.utils.MCMessageUtil;
import ext.usercenter.UserAuthService;
import ext.usercenter.UserAuthService.LoginResult;
import ext.usercenter.UserCenterService;
import ext.usercenter.UserCenterService.BindPhoneNumberResult;
import ext.usercenter.UserCenterService.EmailExistResult;
import ext.usercenter.UserCenterService.LoginUsernameExistResult;
import ext.usercenter.UserCenterService.ModifyEmailResult;
import ext.usercenter.UserCenterService.ModifyPasswordResult;
import ext.usercenter.UserCenterService.PasswordSecurityGrade;
import ext.usercenter.UserCenterService.PhoneNumExistResult;
import ext.usercenter.UserCenterService.RegisterResult;
import ext.usercenter.UserCenterService.ValidatePasswordResult;

/**
 * 
 * 
 * @ClassName: User
 * @Description: 注册账号domain
 * @date 2013年10月21日 下午1:34:10
 * @author RenYouchao
 * 
 */
@javax.persistence.Entity
@Table(name = "tb_user")
public class User implements java.io.Serializable {

    private static final long serialVersionUID = -5514537720548098360L;

    /** 主键保存时设置 **/
    @Id
    public Long id;
    /** 用户唯一标识id，由用户中心返回 **/
    @Column(unique = true)
    public String uid;
    /** 请描述 **/
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<Expert> experts;
    /** 邮箱唯一 **/
    @Column(unique = true)
    public String email;
    /** 登录用户名 **/
    @Column(unique = true)
    public String loginUsername;
    /** 姓名 **/
    public String userName;
    /** 手机号 **/
    @Column(unique = true)
    public String phoneNumber;
    /** 加密后的用户密码 **/
    public String encryptedPassword;
    /** 头像地址 **/
    public String avatar;
    /** 性别有默认值 **/
    @Enumerated(EnumType.ORDINAL)
    @Column(length = 1)
    public Gender gender = Gender.MAN;
    /** 注册日期 **/
    public Date registerDate = new java.util.Date();
    /** 是否禁用 **/
    @Column
    public Boolean isEnable = true;
    /** 是否投诉 **/
    @Column
    public Boolean isComplain = false;
    /** 是否是专家 **/
    public Boolean isEx = false;
    /** 提醒设置,不要直接使用该字段，请使用{@link RemindService}里的相关方法 **/
    public String safetyReminderConfig;
    /** 用户登录地址，记录最近一次 */
    public String loginAddress;
    /**
     * 最近一次记住我登录的时间
     */
    public Date lastRMLoginTime;
    
    /**
     * 个人海外简历的发布情况
     */
    public ResumeStatus resumeStatus;
    
    /**
	 * @ClassName: ResumeStatus
	 * @Description 个人海外简历的发布情况： UNPUBLISHED：未发布，PUBLISHED：已发布（翻译中），TRANSLATED：翻译完成
	 */
    public enum ResumeStatus {
    	UNPUBLISHED, PUBLISHED, TRANSLATED
    }
    
    
	public synchronized void save() {
		Long maxId = queryMaxId();
		if (maxId > 0 && maxId - 1000000 < 0) {
			this.id = maxId + 10;
			JPA.em().persist(this);
		} else {
			throw new HibernateException("保存失败！");
		}
	}
    
	/** 查询中文网站用户表对应的最大id */
    public Long queryMaxId(){
    	Long maxId = JPA.em().createQuery("select max(id) from User where id<1000000",Long.class).getSingleResult();
		if (maxId == null) {
			return 0L;
		}
    	return maxId;
    }

    /**
     * 获取安全提醒配置Map
     */
    public Map<Item, List<Option>> getSafetyReminderCfgMap() {
        return RemindService.getCfgMapWithDefault(this, Constants.safetyReminderItems);
    }

    /**
     * 是否配置安全提醒
     * 
     * @return true:配置,false:未配置
     */
    public boolean isCfgSafetyReminder() {
        return !RemindService.isEmpty(this, Constants.safetyReminderItems);
    }

    /**
     * 获取安全提醒Json
     */
    public JsonNode getSafetyReminderCfgJson() {
        return RemindService.getCfgJsonWithDefault(this, Constants.safetyReminderItems);
    }

    /**
     * 获取预约提醒配置Map
     */
    public Map<Item, List<Option>> getBookingReminderCfgMap() {
        return RemindService.getCfgMapWithDefault(this, Constants.bookingReminderItems);
    }

    /**
     * 是否配置预约提醒
     * 
     * @return true:配置,false:未配置
     */
    public boolean isCfgBookingReminder() {
        return !RemindService.isEmpty(this, Constants.bookingReminderItems);
    }

    public String getName() {
        if (!HelomeUtil.isEmpty(userName)) {
            return userName;
        }
        return email.split("@")[0];
    }

    public String getAvatar() {
        if (!HelomeUtil.isEmpty(avatar)) {
            return Assets.at(avatar);
        }
        return Assets.getDefaultAvatar();
    }

    public String getAvatar(int size) {
        if (!HelomeUtil.isEmpty(avatar)) {
            int indexOf = StringUtils.indexOf(avatar, "?");
            String ayata = "?t=0";
            if (indexOf > -1) {
                ayata = StringUtils.substring(avatar, indexOf);
            }
            return Assets.at(getAvatarFileRelativePath(getId(), size) + ayata);
        }
        return Assets.getDefaultAvatar();
    }

    /**
     * 获取email,使用星号隐藏部分内容,例如:ice****@qq.com
     * 
     * @return 例如:ice****@qq.com
     */
    public String getMaskEmail() {
        if (StringUtils.isBlank(email)) {
            return email;
        } else {
            String[] split = email.split("@");

            int prefixLength = split[0].length();
            int starCount = prefixLength < 4 ? prefixLength : 4;

            StringBuilder prefix = new StringBuilder();
            prefix.append(split[0].substring(0, prefixLength - starCount));
            for (int i = 0; i < starCount; i++) {
                prefix.append("*");
            }

            return prefix.toString() + "@" + split[1];
        }
    }

    /**
     * 获取电话号码,使用星号隐藏部分内容,例如:150****0616
     * 
     * @return 例如:150****0616
     */
    public String getMaskPhoneNum() {
        if (StringUtils.isBlank(phoneNumber)) {
            return phoneNumber;
        } else {
            int length = phoneNumber.length();
            final String mask = "****";

            return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(3 + mask.length(), length);
        }
    }

    /**
     * 获取电话号码,例如:150****0616 用于向原手机发送验证码
     * 
     * @return 例如:150****0616
     */
    public String getMaskPhoneNumber() {
        if (StringUtils.isBlank(phoneNumber)) {
            return phoneNumber;
        } else {
            return phoneNumber;
        }
    }

    /**
     * 是否绑定手机
     * 
     * @return true:绑定,false:未绑定
     */
    public boolean isBindMobilePhone() {
        return StringUtils.isNotBlank(phoneNumber);
    }

    public static String getAvatarFileRelativePath(Long id, int size) {
        // Assets.getAssets() 静态资源访问路径
        StringBuilder avatar = new StringBuilder("topx/uploadfile/avatar/");
        avatar.append(id).append("/avatar_").append(size).append(".jpg");
        return avatar.toString();
    }

    /**
     * 通过id获取User对象
     * 
     * @param id 用户ID
     * @return 返回用户实体 如果不存在返回null
     */
    public static User findById(Long id) {
        @SuppressWarnings("unchecked")
        List<User> users = JPA.em().createQuery("from User where id=:id").setParameter("id", id).getResultList();
        if (CollectionUtils.isNotEmpty(users)) {
            return users.get(0);
        }
        return null;
    }

    /**
     * 通过EMAIL 查询用户信息
     * 
     * @param email
     * @return
     */
    public static User findByEmail(String email) {
        @SuppressWarnings("unchecked")
        List<User> users = JPA.em().createQuery("from User where email=:email").setParameter("email", email)
                .getResultList();
        if (CollectionUtils.isNotEmpty(users)) {
            return users.get(0);
        }
        return null;
    }

    /**
     * 通过登录用户名（仅用于第三方登录） 查询用户信息
     * 
     * @param loginUsername
     * @return
     */
    public static User findByLoginUsername(String loginUsername) {
        @SuppressWarnings("unchecked")
        List<User> users = JPA.em().createQuery("from User where loginUsername=:loginUsername")
                .setParameter("loginUsername", loginUsername).getResultList();
        if (CollectionUtils.isNotEmpty(users)) {
            return users.get(0);
        }
        return null;
    }
    
    /**
     * 根据用户名查询用户的集合
     * @param userName
     * @return
     */
	public static List<User> findUserListByUserName(String userName) {
    	List<User> userList = JPA.em().createQuery("from User where userName=:userName",User.class).setParameter("userName", userName).getResultList();
		if (CollectionUtils.isNotEmpty(userList)) {
    		return userList;
    	}
    	return new ArrayList<User>();
    }
	
	/**
     * 根据用户名查询用户Id的集合
     * @param userName
     * @return
     */
	public static List<Long> findUserIdListByUserName(String userName) {
    	List<Long> userIdList = JPA.em().createQuery("select id from User where userName like :userName",Long.class).setParameter("userName", "%" + userName + "%").getResultList();
		if (CollectionUtils.isNotEmpty(userIdList)) {
    		return userIdList;
    	}
    	return new ArrayList<Long>();
    }

    /**
     * 通过uid 查询用户信息
     * 
     * @param uid
     * @return
     */
    public static User findByUid(String uid) {
        @SuppressWarnings("unchecked")
        List<User> users = JPA.em().createQuery("from User where uid=:uid").setParameter("uid", uid).getResultList();
        if (CollectionUtils.isNotEmpty(users)) {
            return users.get(0);
        }
        return null;
    }

    /**
     * 用户注册(默认使用邮箱，且需要填写验证码)
     * 
     * @param json
     */
    public static ObjectNodeResult register(String email, String password, Gender gender, String captcha, String cpKey) {
        return register(email, password, gender, captcha, true, UsernameType.EMAIL, cpKey);
    }

    /**
     * 移动端用户注册(默认使用邮箱，且需要填写验证码)
     * 
     * @param json
     */
    public static ObjectNodeResult registerFromMobile(String email, String password, Gender gender) {
        return register(email, password, gender, null, false, UsernameType.EMAIL, null);
    }

    /**
     * 用户注册
     */
    public static ObjectNodeResult register(String username, String password, Gender gender, String captcha,
            boolean isValidateCaptcha, UsernameType usernameType, String cpKey) {
        Context context = Http.Context.current();
        Session session = context.session();
        ObjectNodeResult result = new ObjectNodeResult();
        if (StringUtils.isBlank(username)) {
            return result.errorkey("user.register.emailrequire");
        }
        if (UsernameType.EMAIL == usernameType && !HelomeUtil.isEmail(username)) {
            return result.errorkey("user.register.emailerror", username);
        }
        if (HelomeUtil.trim(password).length() < 6) {
            return result.errorkey("user.register.passvalition");
        }

        if (isValidateCaptcha && !CaptchaUtils.validateCaptcha(cpKey, captcha)) {
            return result.errorkey("user.register.captchaerror");
        }

        // 验证重复性
        if (UsernameType.EMAIL == usernameType) {
            EmailExistResult validateEmailExist = UserCenterService.validateEmailExist(username);
            if (EmailExistResult.UNKNOWN_ERROR.equals(validateEmailExist)) {
                Logger.error("调用平台接口注册失败{0}", username);
                return result.errorkey("user.register.error.usercenter");
            }
            // 是否已存在用户名
            if (EmailExistResult.EMAIL_EXIST.equals(validateEmailExist)) {
                return result.errorkey("user.register.emailexist", username);
            }
        } else if (UsernameType.LOGIN_USERNAME == usernameType) {
            LoginUsernameExistResult validateLoginUsernameExist = UserCenterService
                    .validateLoginUsernameExist(username);
            if (LoginUsernameExistResult.UNKNOWN_ERROR.equals(validateLoginUsernameExist)) {
                Logger.error("调用平台接口注册失败{0}", username);
                return result.errorkey("user.register.error.usercenter");
            }
            // 是否是否已存在用户名
            if (LoginUsernameExistResult.EXIST.equals(validateLoginUsernameExist)) {
                return result.errorkey("user.register.emailexist", username);
            }
        }
        
        User user = new User();
        // 注册 保存
        user.gender = gender;
        if (UsernameType.EMAIL == usernameType) {
            user.email = username;
            user.userName = user.email.split("@")[0];
        } else if (UsernameType.LOGIN_USERNAME == usernameType) {
            user.loginUsername = username;
            user.userName = username;
        }
        user.save(); // 保存用户

        // 用户中心注册
        RegisterResult registerResult = null;
        if (UsernameType.EMAIL == usernameType) {
            registerResult = UserCenterService.registerByEmail(session, username, password, user.getId(), user.userName);
        } else if (UsernameType.LOGIN_USERNAME == usernameType) {
            registerResult = UserCenterService.registerByUsername(session, username, password, user.getId(), user.userName);
        }
        if ( registerResult!= null && RegisterResult.STATE.UNKNOWN_ERROR.equals(registerResult.state)) {
            Logger.error("调用平台接口注册失败{0}", username);
            JPA.em().remove(user);
            return result.errorkey("user.register.error.usercenter");
        }
        // 是否注册成功
        if ( registerResult!= null && RegisterResult.STATE.USERNAME_DUPLICATE.equals(registerResult.state)) {
            JPA.em().remove(user);
            return result.errorkey("user.register.emailexist", username);
        }
        
        user.uid = UserAuthService.getLoginUid(session);
        user.encryptedPassword = registerResult.encryptedPassword;
        JPA.em().merge(user);
        
        Expert expert = new Expert();
        expert.saveOrUpateFromUser(user);

        result.setUser(user);
        LoginUserCache.refreshBySession(session);
        CaptchaUtils.invalidCaptcha(cpKey);
        
        UserInfoCookieService.createOrUpdateCookie(true);
        
        return result;
    }

    public enum UsernameType {
        EMAIL, LOGIN_USERNAME
    }

    /**
     * 用户使用加密后的密码登录
     */
    public static ObjectNodeResult loginByEncryptedPassword(String username, String encryptedPassword,
            boolean isRememberMe, UsernameType usernameType) {
        return login(username, encryptedPassword, isRememberMe, true, usernameType);
    }

    /**
     * 用户登录，明文密码
     */
    public static ObjectNodeResult login(String email, String password, boolean isRememberMe) {
        return login(email, password, isRememberMe, false, UsernameType.EMAIL);
    }

    /**
     * 登出
     */
    public static void logout(Session session) {
        if (UserAuthService.isLogin(Context.current().session())) {
            User user = User.getFromSession(session);
            LoginUserCache.removeBySession(session);
            UserAuthService.loginout(session);
            RememberMeInfo.forgetMeCurrent();
            UserInfoCookieService.discardCookie();
            MCMessageUtil.pushNormalOfflineMessage(user);// 推送正常下线消息到mc
        }
    }

    /**
     * 根据session中的信息取用户对象<br>
     * null代表用户未登录
     * 
     * @param session Http Session
     * @return 用户对象，null代表用户未登录
     */
    public static User getFromSession(Session session) {
        User user = LoginUserCache.getBySession(session);
        return user;
    }

    /**
     * 邮件地址是否已经存在
     * 
     * @param email
     * @return
     */
    public static boolean emailexists(String email) {
        EmailExistResult emailExistResult = UserCenterService.validateEmailExist(email);
        if (emailExistResult == null || EmailExistResult.EMAIL_EXIST.equals(emailExistResult))
            return true;
        return false;
    }

    /**
     * 更新用户头像地址
     * 
     * @param avatar
     * @param userId
     * @param session
     */
    public static void updateAvatarById(String avatar, Long userId, Session session) {
        JPA.em().createQuery("update User set avatar=:avatar where id=:userId").setParameter("avatar", avatar)
                .setParameter("userId", userId).executeUpdate();
        LoginUserCache.refreshBySession(session);
    }

    /**
     * 修改邮箱
     * 
     * @param user 登录用户对象
     * @param oldEmail 旧邮箱
     * @param newEmail 新邮箱
     * @param loginPassword 登录密码
     * @param session HTTP Session
     * @return 执行结果
     */
    public static ObjectNodeResult changeEmail(User user, String oldEmail, String newEmail, String loginPassword,
            Session session) {
        ObjectNodeResult result = new ObjectNodeResult();
        if (StringUtils.isBlank(oldEmail) || !user.email.equals(oldEmail)) {
            return result.error("原邮箱地址错误", "263001");
        }

        if (StringUtils.isBlank(newEmail) || !HelomeUtil.isEmail(newEmail)) {
            return result.error("新邮箱地址格式错误", "263002");
        }

        if (StringUtils.isBlank(loginPassword)) {
            return result.error("密码错误", "263003");
        }

        // 验证密码
        ValidatePasswordResult validatePasswordResult = UserCenterService.validatePasswordForModifyUsername(session,
                loginPassword);
        if (ValidatePasswordResult.CORRECT != validatePasswordResult) {
            if (ValidatePasswordResult.INCORRECT == validatePasswordResult) {
                return result.error("密码错误", "263003");
            } else if (ValidatePasswordResult.NOT_LOGIN == validatePasswordResult) {
                return result.error("未登录，请登录", "100002");
            } else {
                return result.error("系统错误", "100001");
            }
        }

        // 更新邮箱
        ModifyEmailResult modifyEmailResult = UserCenterService.modifyEmail(session, newEmail);
        if (ModifyEmailResult.SUCCESS != modifyEmailResult) {
            if (ModifyEmailResult.EMAIL_EXIST == modifyEmailResult) {
                return result.error("邮箱已被占用", "263004");
            } else if (ModifyEmailResult.NOT_LOGIN == modifyEmailResult) {
                return result.error("未登录，请登录", "100002");
            } else {
                return result.error("系统错误", "100001");
            }
        }

        JPA.em().createQuery("update User set email=:email where id=:id").setParameter("email", newEmail)
                .setParameter("id", user.id).executeUpdate();

        LoginUserCache.refreshBySession(session);

        return result;
    }

    /**
     * 修改密码
     * 
     * @param oldPsw 旧密码
     * @param newPsw 新密码
     * @param session HTTP SESSION
     * @return 执行结果
     */
    public static ObjectNodeResult changePassword(User user, String oldPsw, String newPsw, Session session) {
        ObjectNodeResult result = new ObjectNodeResult();
        if (StringUtils.isBlank(oldPsw)) {
            return result.error("原密码错误", "244001");
        }

        if (HelomeUtil.trim(newPsw).length() < 6) {
            return result.error("新密码不能小于6位", "244002");
        }

        // 验证原密码
        ValidatePasswordResult validatePasswordResult = UserCenterService.validatePasswordForModifyPassword(session,
                oldPsw);
        if (ValidatePasswordResult.CORRECT != validatePasswordResult) {
            if (ValidatePasswordResult.INCORRECT == validatePasswordResult) {
                return result.error("原密码错误", "244001");
            } else if (ValidatePasswordResult.NOT_LOGIN == validatePasswordResult) {
                return result.error("未登录，请登录", "100002");
            } else {
                return result.error("系统错误", "100001");
            }
        }

        // 更新密码
        ModifyPasswordResult modifyResult = UserCenterService.modifyPassword(user.getId(), user.email, newPsw);
        if (ModifyPasswordResult.STATE.SUCCESS != modifyResult.state) {
            if (ModifyPasswordResult.STATE.USERNAME_NOT_EXIST == modifyResult.state) {
                return result.error("用户邮箱不存在", "100001");
            } else {
                return result.error("系统错误，修改密码失败", "100001");
            }
        }

        LoginUserCache.refreshBySession(session);

        RemindService.remind(Item.CHANGE_PASSWORD, user, user, null);

        return result;
    }

    /**
     * 绑定手机号
     * 
     * @param user 登录用户
     * @param phoneNum 手机号码
     * @param vCode 验证码
     */
    public static ObjectNodeResult bindMobilePhone(User user, String codePhone, String PhoneNum, String vCode,
            Session session) {
        ObjectNodeResult result = new ObjectNodeResult();

        if (HelomeUtil.trim(PhoneNum).length() != 11) {
            return result.error("手机号无效", "247002");
        }

        if (StringUtils.isBlank(vCode)) {
            return result.error("验证码错误", "247003");
        }

        PhoneNumExistResult validateResult = UserCenterService.validatePhoneNumExist(PhoneNum);
        if (PhoneNumExistResult.EXIST == validateResult) {
            return result.error("手机号已被绑定", "247004");
        } else if (PhoneNumExistResult.UNKNOWN_ERROR == validateResult) {
            return result.error("系统错误", "100001");
        }

        // 校验验证码
        boolean isValidVCode = PhoneVerifyCodeService.validateVerifyCode(PhoneVerifyCodeType.BIND_MOBILE_PHONE,
                String.valueOf(user.id), vCode, codePhone);
        if (!isValidVCode) {
            return result.error("验证码错误", "247003");
        }

        // 更新用户中心手机号
        BindPhoneNumberResult bindPhoneNumberResult = UserCenterService.bindPhoneNumber(session, PhoneNum);
        if (BindPhoneNumberResult.SUCCESS != bindPhoneNumberResult) {
            if (BindPhoneNumberResult.NOT_LOGIN == bindPhoneNumberResult) {
                return result.error("未登录，请登录", "100002");
            } else if (BindPhoneNumberResult.PHONE_NUM_EXIST == bindPhoneNumberResult) {
                return result.error("手机号已被绑定", "247004");
            } else if (BindPhoneNumberResult.ILLEGAL_PHONE_NUM == bindPhoneNumberResult) {
                return result.error("手机号无效", "247002");
            } else {
                return result.error("系统错误", "100001");
            }
        }

        // 更新手机号
        JPA.em().createQuery("update User set phoneNumber=:phoneNum where id=:id").setParameter("phoneNum", PhoneNum)
                .setParameter("id", user.id).executeUpdate();

        LoginUserCache.refreshBySession(session);

        User newUser = User.getFromSession(session);
        if (StringUtils.isEmpty(user.getPhoneNumber())) {
            newUser.phoneNumber = PhoneNum;
        }
        RemindService.remind(Item.CHANGE_PHONE_NUM, user, newUser, null);

        PhoneVerifyCodeService.invalidVerifyCode(PhoneVerifyCodeType.BIND_MOBILE_PHONE, String.valueOf(user.id));

        return result;
    }

    /**
     * 绑定手机号,用于原手机不能接收短信绑定新手机的情况
     * 
     * @param user 登录用户
     * @param phoneNum 手机号码
     * @param vCode 验证码
     */
    public static ObjectNodeResult bindNewPhone(User user, String codePhone, String vCode, Session session) {
        ObjectNodeResult result = new ObjectNodeResult();
        if (HelomeUtil.trim(codePhone).length() != 11) {
            return result.error("手机号错误");
        }

        if (StringUtils.isBlank(vCode)) {
            return result.error("验证码错误");
        }

        PhoneNumExistResult validateResult = UserCenterService.validatePhoneNumExist(codePhone);
        if (PhoneNumExistResult.EXIST == validateResult) {
            return result.error("手机号已存在");
        } else if (PhoneNumExistResult.UNKNOWN_ERROR == validateResult) {
            return result.error("系统错误");
        }

        // 校验验证码
        boolean isValidVCode = PhoneVerifyCodeService.validateVerifyCode(PhoneVerifyCodeType.BIND_MOBILE_PHONE,
                String.valueOf(user.id), vCode, codePhone);
        if (!isValidVCode) {
            return result.error("验证码错误");
        }

        // 更新用户中心手机号
        BindPhoneNumberResult bindPhoneNumberResult = UserCenterService.bindPhoneNumber(session, codePhone);
        if (BindPhoneNumberResult.SUCCESS != bindPhoneNumberResult) {
            if (BindPhoneNumberResult.NOT_LOGIN == bindPhoneNumberResult) {
                return result.error("未登录，请登录");
            } else if (BindPhoneNumberResult.PHONE_NUM_EXIST == bindPhoneNumberResult) {
                return result.error("手机号已经被使用");
            } else if (BindPhoneNumberResult.ILLEGAL_PHONE_NUM == bindPhoneNumberResult) {
                return result.error("手机号非法");
            } else {
                return result.error("系统错误");
            }
        }

        // 更新手机号
        JPA.em().createQuery("update User set phoneNumber=:phoneNum where id=:id").setParameter("phoneNum", codePhone)
                .setParameter("id", user.id).executeUpdate();

        LoginUserCache.refreshBySession(session);

        RemindService.remind(Item.CHANGE_PHONE_NUM, user, User.getFromSession(session), null);

        PhoneVerifyCodeService.invalidVerifyCode(PhoneVerifyCodeType.BIND_MOBILE_PHONE, String.valueOf(user.id));

        return result;
    }

    /**
     * 修改安全提醒设置
     * 
     * @param user 登录用户实体
     * @param newCfg 新的安全提醒设置Json
     * @param session HTTP session
     * @return
     */
    public static ObjectNodeResult modifySafetyReminder(User user, JsonNode newCfg, Session session) {
        ObjectNodeResult result = new ObjectNodeResult();

        boolean isValidParams = RemindService.verifyCfg(newCfg, Constants.safetyReminderItems);
        if (!isValidParams) {
            return result.error("传入参数不符合规范", "100005");
        }

        RemindService.saveCfgJson(session, user, newCfg, Constants.safetyReminderItems);

        return result;
    }

    /**
     * 修改预约提醒设置
     * 
     * @param user 登录用户实体
     * @param newCfg 新的预约提醒设置Json
     * @param session HTTP session
     * @return
     */
    public static ObjectNodeResult modifyBookingReminder(User user, JsonNode newCfg, Session session) {
        ObjectNodeResult result = new ObjectNodeResult();

        boolean isValidParams = RemindService.verifyCfg(newCfg, Constants.bookingReminderItems);
        if (!isValidParams) {
            return result.error("传入参数不符合规范", "100005");
        }

        RemindService.saveCfgJson(session, user, newCfg, Constants.bookingReminderItems);

        return result;
    }

    /**
     * 获取当前登录用户的密码安全级别：1-3级
     * 
     * @return
     */
    public static PasswordSecurityGrade getPasswordSecurityGrade(Session session) {
        PasswordSecurityGrade securityGrade = UserCenterService.getPasswordSecurityGrade(session);
        return securityGrade == null ? PasswordSecurityGrade.WEAK : securityGrade;
    }

    /**
     * 根据用户id设置加密的密码<br>
     * 
     * @param id 用户Id
     * @param encryptedPassword 加密了的密码
     */
    public static void setEncryptedPasswordById(Long id, String encryptedPassword) {
        JPA.em().createQuery("update User set encryptedPassword=:encryptedPassword where id=:id")
                .setParameter("encryptedPassword", encryptedPassword).setParameter("id", id).executeUpdate();

        LoginUserCache.refreshBySession(Http.Context.current().session());
    }

    /**
     * 根据用户Id更新加密的密码<br>
     * 
     * @param userId userId
     * @param encryptedPassword 加密了的密码
     */
    public static void updateEncryptedPasswordById(Long userId, String encryptedPassword) {
        JPA.em()
                .createQuery(
                        "update User set encryptedPassword=:encryptedPassword where id=:userId")
                .setParameter("encryptedPassword", encryptedPassword).setParameter("userId", userId).executeUpdate();

        LoginUserCache.refreshBySession(Http.Context.current().session());
    }

    /**
     * 
     * 用户登录
     * 
     * @param username 可能是登录用户名（第三方登录使用）也可能是邮箱
     * @param password 密码，可能是未加密的，也可能是加密的（第三方登录、记住我登录使用）
     * @param isRememberMe 是否记住我。true - 记住我，false - 不记住我
     * @param isEncryptedPassword 密码是否为加密的密码。true - 加密密码，false - 明文密码
     * @param usernameType 用户名类型
     * @return ObjectNodeResult
     */
    private static ObjectNodeResult login(String username, String password, boolean isRememberMe,
            boolean isEncryptedPassword, final UsernameType usernameType) {
        ObjectNodeResult result = new ObjectNodeResult();
        Context context = Http.Context.current();
        Session session = context.session();

        if (HelomeUtil.isEmpty(username) || HelomeUtil.isEmpty(password)) {
            return result.errorkey("user.login.uperror");
        }
        /**
         * 通过用户输入的email查询本系统中的数据信息
         */
        User user = null;
        if (UsernameType.LOGIN_USERNAME == usernameType) {
            user = User.findByLoginUsername(username);
        } else if (UsernameType.EMAIL == usernameType) {
            user = User.findByEmail(username);
        }
        if (user != null && user.isEnable == false) {
            return result.errorkey("user.login.forbidden");
        }
        // 用户中心验证
        LoginResult loginResult = null;
        if (isEncryptedPassword) {
            loginResult = UserAuthService.encryptLogin(session, username, password);
        } else {
            loginResult = UserAuthService.login(session, username, password);
        }

        if (LoginResult.STATE.USERNAME_PASSWORD_ERROR.equals(loginResult.state)) {
            return result.errorkey("user.login.uperror");
        } else if (LoginResult.STATE.UNKNOWN_ERROR.equals(loginResult.state)) {
            return result.errorkey("user.login.error.forbidden");
        }

        String loginUid = UserAuthService.getLoginUid(session);
        // 清理用户缓存
        LoginUserCache.refreshBySession(session);

        if (user == null) {
            user = new User();
            if (UsernameType.LOGIN_USERNAME == usernameType) {
                user.loginUsername = username;
                user.email = StringUtils.defaultIfBlank(loginResult.ucUser.email, null);
            } else if (UsernameType.EMAIL == usernameType) {
                user.email = username;
                user.loginUsername = StringUtils.defaultIfBlank(loginResult.ucUser.username, null);
            }
            user.phoneNumber = StringUtils.defaultIfBlank(loginResult.ucUser.phoneNumber, null);
            user.uid = loginUid;
            user.userName = loginResult.ucUser.realname;
            user.encryptedPassword = loginResult.encryptedPassword;

            user.save();
            Expert expert = new Expert();
            expert.saveOrUpateFromUser(user);
        } else {
            if (UsernameType.LOGIN_USERNAME == usernameType) {
                user.loginUsername = username;
                user.email = StringUtils.defaultIfBlank(loginResult.ucUser.email, null);
            } else if (UsernameType.EMAIL == usernameType) {
                user.email = username;
                user.loginUsername = StringUtils.defaultIfBlank(loginResult.ucUser.username, null);
            }
            if (StringUtils.isNotBlank(loginResult.encryptedPassword)) {
                user.encryptedPassword = loginResult.encryptedPassword;
            }
            user.phoneNumber = StringUtils.defaultIfBlank(loginResult.ucUser.phoneNumber, null);
            if (StringUtils.isNotBlank(loginUid)) {
                user.uid = loginUid;
            }
            
            if (StringUtils.isNotBlank(loginResult.ucUser.realname)) {
                user.userName = loginResult.ucUser.realname;
                Expert expert = Expert.getExpertByUserId(user.id);
                if (null != expert) {
                    expert.userName = loginResult.ucUser.realname;
                    GroupMember.updateUserNameByUserId(expert.userName, user.getId());
                    expert.saveOrUpate();
                }
            } else { // 处理旧数据，把用户姓名向用户中心同步
                Expert expert = Expert.getExpertByUserId(user.id);
                if (null != expert && StringUtils.isNotBlank(expert.userName)) {
                    UserCenterService.modifyRealname(session, expert.userName);
                }
            }
            
            JPA.em().merge(user);
        }

        // 记住我
        if (isRememberMe) {
            RememberMeInfo.rememberMe(user.id);
        }
        
        UserInfoCookieService.createOrUpdateCookie(true);

        // 异步触发异地登录安全提醒
        final User loginUser = user;
        final String ip = context.request().remoteAddress();
        final String previousLoginAddress = user.loginAddress;
        Akka.system().scheduler().scheduleOnce(Duration.create(1000, TimeUnit.MILLISECONDS), new Runnable() {
            public void run() {
                JPA.withTransaction(new F.Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        String currentLoginAddress = IPBasedLocationService.getSimpleAddress(ip);
                        // 触发异地登录安全提醒
                        if (UsernameType.EMAIL == usernameType && StringUtils.isNotBlank(previousLoginAddress)
                                && StringUtils.isNotBlank(currentLoginAddress)
                                && !previousLoginAddress.equals(currentLoginAddress)) {
                            RemindService.remind(Item.DIFFERENT_PLACE_LOGIN, loginUser, loginUser, null);
                        }
                        // 更新用户登录地址
                        if (StringUtils.isNotBlank(currentLoginAddress)
                                && !currentLoginAddress.equals(previousLoginAddress)) {
                            JPA.em().createQuery("UPDATE User u set u.loginAddress = :loginAddress where u.id = :id")
                                    .setParameter("loginAddress", currentLoginAddress).setParameter("id", loginUser.id)
                                    .executeUpdate();
                        }
                    }
                });
            }
        }, Akka.system().dispatcher());
        result.setUser(user);
        UserAuthService.removeKickedFlag(session);
        return result;
    }

    public static void merge(User user) {
        JPA.em().merge(user);
        LoginUserCache.refreshByUid(user.uid);
    }

    public static ObjectNodeResult completeUserInfo(Session session, String email, String pwd) {
        ObjectNodeResult result = new ObjectNodeResult();

        if (StringUtils.isBlank(email) || StringUtils.isBlank(pwd)) {
            return result.error("参数非法。", "100005");
        }

        User user = User.getFromSession(session);
        if (StringUtils.isNotBlank(user.getEmail())) {
            return result.error("不能重复完善用户信息。", "273001");
        }
        if (HelomeUtil.trim(pwd).length() < 6) {
            return result.error("新密码不能小于6位", "273002");
        }

        // 更新用户中心邮箱
        ModifyEmailResult modifyEmailResult = UserCenterService.modifyEmail(session, email);
        if (ModifyEmailResult.SUCCESS != modifyEmailResult) {
            if (ModifyEmailResult.EMAIL_EXIST == modifyEmailResult) {
                return result.error("邮箱已被占用", "273004");
            } else if (ModifyEmailResult.NOT_LOGIN == modifyEmailResult) {
                return result.error("未登录，请登录", "100002");
            } else {
                return result.error("系统错误", "100001");
            }
        }

        // 更新用户中心密码
        ModifyPasswordResult modifyResult = UserCenterService.modifyPassword(user.getId(), email, pwd);
        if (ModifyPasswordResult.STATE.SUCCESS != modifyResult.state) {
            if (ModifyPasswordResult.STATE.USERNAME_NOT_EXIST == modifyResult.state) {
                return result.error("系统错误，用户邮箱不存在", "100001");
            } else {
                return result.error("系统错误，修改密码失败", "100001");
            }
        }

        // 更新用户信息
        JPA.em().createQuery("update User set email=:email where id=:id").setParameter("email", email)
                .setParameter("id", user.id).executeUpdate();

        PhoneVerifyCodeService.invalidVerifyCode(PhoneVerifyCodeType.BIND_MOBILE_PHONE, String.valueOf(user.id));
        LoginUserCache.refreshBySession(session);

        return result;
    }

    /**
     * 刷新当前用户的记住我最后登录时间
     * 
     * @param session
     */
    public static void refreshCurrentLastRMLoginTime(Session session) {
        User user = LoginUserCache.getBySession(session);
        if (null != user) {
            JPA.em().createQuery("update User set lastRMLoginTime=:lastRMLoginTime where id=:id")
                    .setParameter("lastRMLoginTime", new Date()).setParameter("id", user.id).executeUpdate();

            LoginUserCache.refreshBySession(session);
        }
    }

    /**
     * 根据邮箱获取对应用户id
     */
    public static List<User> queryIdByEmail(List<String> s) {
        List<User> users = JPA.em()
                .createQuery(" select new User(t.id,t.email) from User t where t.email in (:emails)", User.class)
                .setParameter("emails", s).getResultList();

        if (CollectionUtils.isNotEmpty(users)) {
            return users;
        }
        return new ArrayList<User>();
    }

    /**
     * 根据邮箱获取对应用户
     */
    public static List<User> queryUserNameByEmail(List<String> s) {
        if (s != null && s.size() > 0) {
            List<User> userList = JPA.em().createQuery(" from User t where t.email in (:emails)", User.class)
                    .setParameter("emails", s).getResultList();

            if (CollectionUtils.isNotEmpty(userList)) {
                return userList;
            }
        }

        return new ArrayList<User>();
    }

    /**
     * 根据用户id获取对应用户
     */
    public static List<User> queryUserByIds(List<Long> s) {
        if (s != null && s.size() > 0) {
            List<User> userList = JPA.em().createQuery(" from User t where t.id in (:ids)", User.class)
                    .setParameter("ids", s).getResultList();

            if (CollectionUtils.isNotEmpty(userList)) {
                return userList;
            }
        }

        return new ArrayList<User>();
    }

    public User() {
        super();
    }

    public User(Long id, String email) {
        super();
        this.id = id;
        this.email = email;
    }

    public User(Long id, String userName, String email) {
        super();
        this.id = id;
        this.userName = userName;
        this.email = email;
    }

    /**
     * 获得用户的用户名， 若用户名为空，则取邮箱(邮箱经过*号处理)
     * 
     * @param user
     * @return
     */
    public String getUserNameOrEmail() {
        if (null == this.userName || "".equals(this.userName)) {
            return HelomeUtil.getMaskEmail(this.email);
        }
        return this.userName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Gender getGender() {
        return gender;
    }
    
    public Gender getGenderWithDefault() {
        return gender == null ? Gender.MAN : gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

	public ResumeStatus getResumeStatus() {
		return resumeStatus == null ? ResumeStatus.UNPUBLISHED : resumeStatus;
	}

	public void setResumeStatus(ResumeStatus resumeStatus) {
		this.resumeStatus = resumeStatus;
	}

	/**
	 * @return the experts
	 */
	public Set<Expert> getExperts() {
		return experts;
	}

	/**
	 * @param experts the experts to set
	 */
	public void setExperts(Set<Expert> experts) {
		this.experts = experts;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the registerDate
	 */
	public Date getRegisterDate() {
		return registerDate;
	}

	/**
	 * @param registerDate the registerDate to set
	 */
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	/**
	 * @return the isEnable
	 */
	public Boolean getIsEnable() {
		return isEnable;
	}

	/**
	 * @param isEnable the isEnable to set
	 */
	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	/**
	 * @return the isComplain
	 */
	public Boolean getIsComplain() {
		return isComplain;
	}

	/**
	 * @param isComplain the isComplain to set
	 */
	public void setIsComplain(Boolean isComplain) {
		this.isComplain = isComplain;
	}

	/**
	 * @return the isEx
	 */
	public Boolean getIsEx() {
		return isEx;
	}

	/**
	 * @param isEx the isEx to set
	 */
	public void setIsEx(Boolean isEx) {
		this.isEx = isEx;
	}

	/**
	 * @return the safetyReminderConfig
	 */
	public String getSafetyReminderConfig() {
		return safetyReminderConfig;
	}

	/**
	 * @param safetyReminderConfig the safetyReminderConfig to set
	 */
	public void setSafetyReminderConfig(String safetyReminderConfig) {
		this.safetyReminderConfig = safetyReminderConfig;
	}

	/**
	 * @return the loginAddress
	 */
	public String getLoginAddress() {
		return loginAddress;
	}

	/**
	 * @param loginAddress the loginAddress to set
	 */
	public void setLoginAddress(String loginAddress) {
		this.loginAddress = loginAddress;
	}

	/**
	 * @return the lastRMLoginTime
	 */
	public Date getLastRMLoginTime() {
		return lastRMLoginTime;
	}

	/**
	 * @param lastRMLoginTime the lastRMLoginTime to set
	 */
	public void setLastRMLoginTime(Date lastRMLoginTime) {
		this.lastRMLoginTime = lastRMLoginTime;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	
}