/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-5
 */
package ext.paycenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import common.Global;

import play.Logger;
import play.Logger.ALogger;
import play.Play;
import play.libs.F.Promise;
import play.libs.WS.Response;
import utils.WSUtil;
import ext.config.ConfigFactory;

/**
 * 
 * 
 * @ClassName: PCClient
 * @Description: 支付中心客户端，用于和支付中心交互
 * @date 2013-12-5 下午3:55:35
 * @author ShenTeng
 * 
 */
public class PCClient {

    private static final ALogger LOGGER = Logger.of(PCClient.class);

    /**
     * 请求超时时间，默认10000ms
     */
    private static int REQUEST_TIMEOUT = ConfigFactory.getInt("payCenter.client.timeout");

    /**
     * 禁止实例化
     */
    private PCClient() {
    }

    /**
     * 用户支付
     * 
     * @param token 登录用户token
     * @param email 收款方邮箱地址
     * @param payment 支付金额，精确到小数点后的两位
     * @param serveduration 通话时长，整数
     * @param isIgnoreLocked 是否忽略账户锁定。true - 忽略账户锁定。 false - 账户锁定时无法操作
     * @return
     */
    public static PCResult<Void> transferax(String token, String email, String payment, String serveduration,
            boolean ignoreLocked) {
        String post = postNullIgnore("/pay/payment/transferax", "pay_user.token", token, "incom_user.email", email,
                "payment", payment, "serveduration", serveduration, "ignoreLocked", Boolean.toString(ignoreLocked));

        return PCResult.fromJsonString(post, Void.class, false);
    }
    
    /**
     * 系统转账
     * 
     * @param email 支付方邮箱
     * @param toEmail 收款方邮箱地址
     * @param payment 支付金额，精确到小数点后的两位
     * @param serveduration 通话时长，整数
     * @param isIgnoreLocked 是否忽略账户锁定。true - 忽略账户锁定。 false - 账户锁定时无法操作
     * @return
     */
    public static PCResult<Void> transferByEmail(String email, String toEmail, String payment, String serveduration,
            boolean ignoreLocked) {
        String post = postNullIgnore("/pay/payment/transferByEmail", "pay_user.email", email, "incom_user.email", toEmail,
                "payment", payment, "serveduration", serveduration, "ignoreLocked", Boolean.toString(ignoreLocked));

        return PCResult.fromJsonString(post, Void.class, false);
    }

    /**
     * 获取账户余额
     * 
     * @param token 登录用户token
     * @return PCResult.data - String 账户可用余额，精确到小数点后的两位
     */
    public static PCResult<String> getBalance(String token) {
        String post = postNullIgnore("/pay/account/getBalance", "token", token);

        return PCResult.fromJsonString(post, String.class, true);
    }
    
    /**
     * 系统获取账户余额
     * 
     * @param email 用户注册的email
     * @return PCResult.data - String 账户可用余额，精确到小数点后的两位
     */
    public static PCResult<String> getBalanceBySystem(String email) {
        String post = postNullIgnore("/pay/account/getBalanceBySystem", "email", email);
        
        return PCResult.fromJsonString(post, String.class, true);
    }
    
    /**
     * 系统锁定账户
     * @param email 用户注册的email
     * @return PCResult.data - String 账户可用余额，精确到小数点后的两位
     */
    public static PCResult<String> lock(String email) {
        String post = postNullIgnore("/pay/account/lock", "email", email);
        
        return PCResult.fromJsonString(post, String.class, true);
    }
    
    /**
     * 系统解锁账户
     * @param email 用户注册的email
     * @return PCResult.data - String 账户可用余额，精确到小数点后的两位
     */
    public static PCResult<String> unlock(String email) {
        String post = postNullIgnore("/pay/account/unlock", "email", email);
        
        return PCResult.fromJsonString(post, String.class, true);
    }
    
    /**
     * 获取账户锁定状态
     * @param email 用户注册的email
     * @return PCResult.data - Boolean true - 账户锁定, false - 未锁定
     */
    public static PCResult<Boolean> getLockStatus(String email) {
        String post = postNullIgnore("/pay/account/getLockStatus", "email", email);
        
        return PCResult.fromJsonString(post, Boolean.class, true);
    }

    /**
     * 发生post请求，参数值为null则忽略该参数
     */
    private static String postNullIgnore(String relativeUrl, String... params) {
        if (null == relativeUrl || (params != null && params.length % 2 != 0)) {
            throw new IllegalArgumentException("illegal method input param. param: relativeUrl=" + relativeUrl
                    + ", params=" + Arrays.toString(params));
        }

        String[] paramArray = filterNullParams(params);
        String uri = Play.application().configuration().getString("payCenter.client.serverUrl") + relativeUrl;

        Promise<Response> post = WSUtil.postFormURLEncoded(uri, paramArray);

        LOGGER.debug("payCenter " + relativeUrl + " post : " + Arrays.toString(params));

        Response response;
        try {
            response = post.get(REQUEST_TIMEOUT);
        } catch (Exception e) {
            throw new PayCenterException("接收支付中心的响应时发生异常.URI = " + relativeUrl, e);
        }

        String body = response.getBody();

        LOGGER.debug("payCenter " + relativeUrl + "  post response : " + body);

        return body;
    }

    private static String[] filterNullParams(String[] params) {
        if (ArrayUtils.isNotEmpty(params)) {
            List<String> paramList = new ArrayList<>();
            for (int i = 0; i < params.length; i += 2) {
                if (null == params[i + 1]) {
                    continue;
                }

                paramList.add(params[i]);
                paramList.add(params[i + 1]);
            }
            return paramList.toArray(new String[0]);
        }
        return null;
    }

    /**
     * 发生get请求，参数值为null则忽略该参数
     */
    private static String getNullIgnore(String relativeUrl, String... params) {
        if (null == relativeUrl || (params != null && params.length % 2 != 0)) {
            throw new IllegalArgumentException("illegal method input param. param: relativeUrl=" + relativeUrl
                    + ", params=" + Arrays.toString(params));
        }

        String[] paramArray = filterNullParams(params);
        String uri = Play.application().configuration().getString("payCenter.client.serverUrl") + relativeUrl;

        Promise<Response> get = WSUtil.get(uri, paramArray);

        LOGGER.debug("payCenter " + relativeUrl + " get : " + Arrays.toString(params));

        Response response = null;
        try {
            response = get.get(REQUEST_TIMEOUT);
        } catch (Exception e) {
            throw new PayCenterException("接收支付中心的响应时发生异常.URI = " + relativeUrl, e);
        }

        String body = response.getBody();

        LOGGER.debug("payCenter " + relativeUrl + " get response : " + body);

        return body;

    }
    
    /**
     * 调用关键字统计的结果
      * getKeyWordCountBySystem
     */
    public static String postKeyWordCountBySystem(String relativeUrl,String...params) {
        if (null == relativeUrl || (params != null && params.length % 2 != 0)) {
            throw new IllegalArgumentException("illegal method input param. param: relativeUrl=" + relativeUrl
                    + ", params=" + Arrays.toString(params));
        }
        String[] paramArray = filterNullParams(params);
        String uri = ConfigFactory.getString("keyWordCount.client.serverUrl") + relativeUrl;
        Promise<Response> post = WSUtil.postFormURLEncoded(uri, paramArray);
        LOGGER.debug("keyWordCount " + relativeUrl + " post : " + Arrays.toString(params));
        Response response;
        try {
            response = post.get(REQUEST_TIMEOUT);
        } catch (Exception e) {
            throw new PayCenterException("搜索关键字统计的响应时发生异常.URI = " + relativeUrl, e);
        }
        String body = response.getBody();
        LOGGER.debug("keyWordCount " + relativeUrl + "  post response : " + body);
        return body;
        
        
    }
    
    
    public static String getKeyWordCountBySystem(String relativeUrl,String...params) {
        if (null == relativeUrl || (params != null && params.length % 2 != 0)) {
            throw new IllegalArgumentException("illegal method input param. param: relativeUrl=" + relativeUrl
                    + ", params=" + Arrays.toString(params));
        }
        String[] paramArray = filterNullParams(params);
        String uri = ConfigFactory.getString("keyWordCount.client.serverUrl") + relativeUrl;
        Promise<Response> post = WSUtil.get(uri, paramArray);
        LOGGER.debug("keyWordCount " + relativeUrl + " post : " + Arrays.toString(params));
        Response response;
        try {
            response = post.get(REQUEST_TIMEOUT);
        } catch (Exception e) {
            throw new PayCenterException("搜索关键字统计的响应时发生异常.URI = " + relativeUrl, e);
        }
        String body = response.getBody();
        LOGGER.debug("keyWordCount " + relativeUrl + "  post response : " + body);
        return body;
        
        
    }

}
