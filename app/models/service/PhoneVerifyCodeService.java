/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-15
 */
package models.service;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import common.Constants;
import play.Logger;
import play.cache.Cache;
import utils.MessageUtil;

/**
 * 
 * 
 * @ClassName: PhoneVedifyCodeService
 * @Description: 手机验证码服务
 * @date 2013-11-15 下午5:39:27
 * @author ShenTeng
 * 
 */
public class PhoneVerifyCodeService {

    /**
     * 发送间隔秒数
     */
    private static final int SEND_INTERVAL_SEC = 55;

    /**
     * 验证码失效时间
     */
    private static final int CODE_INVALID_SEC = 300;

    private static Random r = new Random();

    public enum PhoneVerifyCodeType {
        /** 绑定手机号 **/
        BIND_MOBILE_PHONE("_bindMobilePhone_");

        private String prefix;

        PhoneVerifyCodeType(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    /**
     * 发送手机验证码
     * 
     * @param type 验证码类型
     * @param key 验证码的key,要保证在该类型手机验证码中唯一.使用该key来匹配验证码
     * @param phoneNum 手机号
     * @return SUCCESS:发送成功(只代表通过校验,不代表手机已经接收), TOO_MANY:发送频率太快
     */
    public static SendVerifyCodeResult sendVerifyCode(PhoneVerifyCodeType type, String key, String phoneNum) {
        // 发送间隔验证
        long lastSendTimeMillis = getLastSendTimeMillis(type, key);
        if (lastSendTimeMillis != -1 && System.currentTimeMillis() - lastSendTimeMillis < SEND_INTERVAL_SEC * 1000) {
            return SendVerifyCodeResult.TOO_MANY;
        }

        String code = generateVerifyCode();

        String content = "您正在进行手机绑定，校验码：" + code + "，5分钟内有效。【嗨啰科技】";
        if(play.Logger.isInfoEnabled()){
        	play.Logger.info("进行手机号码"+phoneNum+"绑定,发送到手机的信息：" + content);
        }

        int result = MessageUtil.batchSend(phoneNum, content);
        if (result < 0) {
            return SendVerifyCodeResult.FAIL;
        }

        // 发送成功写入缓存
        setToCache(type, key, code, phoneNum);

        return SendVerifyCodeResult.SUCCESS;
    }

    public enum SendVerifyCodeResult {
        SUCCESS, FAIL, TOO_MANY
    }

    /**
     * 校验用户输入的验证码
     * 
     * @param type 验证码类型
     * @param key 验证码key,要保证在该类型手机验证码中唯一.使用该key来匹配验证码
     * @param userCode 用户输入验证码
     * @return true:验证码正确, false:验证码错误
     */
    public static boolean validateVerifyCode(PhoneVerifyCodeType type, String key, String userCode, String phoneNum) {
        if (StringUtils.isBlank(userCode) || StringUtils.isBlank(phoneNum)) {
            return false;
        }

        String sendPhoneNum = getSendPhoneNum(type, key);
        if (!phoneNum.equals(sendPhoneNum)) {
            return false;
        }

        String code = getCodeFromCache(type, key);

        return userCode.equals(code);
    }

    /**
     * 作废验证码
     * 
     * @param type 验证码类型
     * @param key 验证码key,要保证在该类型手机验证码中唯一.使用该key来匹配验证码
     */
    public static void invalidVerifyCode(PhoneVerifyCodeType type, String key) {
        removeFromCache(type, key);
    }

    /**
     * 生成随机手机验证码
     * 
     * @return 验证码
     */
    private static String generateVerifyCode() {
        int code = (int) (r.nextFloat() * 1000000);
        if (code > 0) {
            code--;
        }
        return String.valueOf(code);
    }

    private static void setToCache(PhoneVerifyCodeType type, String key, String code, String phoneNum) {
        Cache.set(Constants.CACHE_PHONE_VERIFY_CODE + type.getPrefix() + key, System.currentTimeMillis() + "," + code
                + "," + phoneNum, CODE_INVALID_SEC);
    }

    private static String getCodeFromCache(PhoneVerifyCodeType type, String key) {
        String val = (String) Cache.get(Constants.CACHE_PHONE_VERIFY_CODE + type.getPrefix() + key);
        return StringUtils.isBlank(val) ? val : val.split(",")[1];
    }

    /**
     * 获取该key最近一次发送验证码的时间
     * 
     * @param key 验证码key,见{@link PhoneVerifyCodeService#sendVerifyCode(String, String)}
     * @return 该key最近一次发送验证码的时间, -1:未发送或者发送的验证码已经失效
     */
    private static long getLastSendTimeMillis(PhoneVerifyCodeType type, String key) {
        String val = (String) Cache.get(Constants.CACHE_PHONE_VERIFY_CODE + type.getPrefix() + key);
        return StringUtils.isBlank(val) ? -1 : Long.valueOf(val.split(",")[0]);
    }

    /**
     * 获取发送验证码的手机号
     */
    private static String getSendPhoneNum(PhoneVerifyCodeType type, String key) {
        String val = (String) Cache.get(Constants.CACHE_PHONE_VERIFY_CODE + type.getPrefix() + key);
        return StringUtils.isBlank(val) ? val : val.split(",")[2];
    }

    private static void removeFromCache(PhoneVerifyCodeType type, String key) {
        Cache.remove(Constants.CACHE_PHONE_VERIFY_CODE + type.getPrefix() + key);
    }

}
