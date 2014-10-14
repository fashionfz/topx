/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-10-29
 */
package ext.usercenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import models.User;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.libs.F.Promise;
import play.libs.WS.Response;
import utils.WSUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.jackjson.JackJsonUtil;

import ext.config.ConfigFactory;
import ext.usercenter.vo.UCUserVO;

/**
 * 
 * 
 * @ClassName: UCClient
 * @Description: 用户中心客户端，用于直接和用户中心进行交互。
 * @date 2013-10-29 下午2:54:02
 * @author ShenTeng
 * 
 */
public class UCClient {

    private static final ALogger LOGGER = Logger.of(UCClient.class);

    /**
     * 请求超时时间，默认10000ms
     */
    private static int REQUEST_TIMEOUT = ConfigFactory.getInt("userCenter.client.timeout");
    private static String product = ConfigFactory.getString("userCenter.client.product");

    /**
     * 禁止实例化
     */
    private UCClient() {
    }

    /**
     * 查询单个用户信息,利用当前这个用户的登录随机标识
     * 
     * @param token 必须。当前这个用户的登录随机标识
     * @return UCResult.data包含属性：email、isable、phoneNumber、realname、uid、username
     */
    public static UCResult<UCUser> queryUserInfoByToken(String token) {
        long beginMillis = System.currentTimeMillis();

        String post = postNullIgnore("/remote/user/queryUserInfoByToken", "token", token);
        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "queryUserInfoByToken", "/remote/user/queryUserInfoByToken");

        return UCResult.fromJsonString(post, UCUser.class, true);
    }

    /**
     * 查询给定的用户名是否已存在于用户数据中。用户名的形式包括 一般用户名、电话或者邮箱。<br>
     * 如果在UCResult.data中存在数据，说明该用户信息已经被使用了，反之说明没有被使用。
     * 
     * @param username 必须。用户名的形式包括 一般用户名、电话或者邮箱。
     * @return UCResult.data包含属性：email、phoneNumber、username
     */
    public static UCResult<UCUser> queryUsernameExist(String username) {
        long beginMillis = System.currentTimeMillis();

        String post = getNullIgnore("/remote/user/queryUsernameExist", "username", username, "product", product);
        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "queryUsernameExist", "/remote/user/queryUsernameExist");
        return UCResult.fromJsonString(post, UCUser.class, true);
    }
    
    /**
     * 获取helome后台用户的id对应的用户中心用户信息
     * @param userId 用户的id
     * @return
     */
	public static User queryUserById(Long userId) {
    	long beginMillis = System.currentTimeMillis();
    	
    	String post = postNullIgnore("/remote/security/findUserInfoByPrivateId", "privateId", userId.toString(), "product", product);
    	long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "queryUserById", "/remote/security/findUserInfoByPrivateId");
        return parseJsonForQueryUserById(post);
    }
    
    /**
     * 解析queryUserById方法调用接口返回的json
     * @param json
     * @return
     */
	public static User parseJsonForQueryUserById(String json) {
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		User user = new User();
		if (StringUtils.isNotEmpty(json)) {
			JsonNode node = null;
			try {
				node = mapper.readTree(json);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			JsonNode jsonNode = node.path("data");
			Long privateId = jsonNode.path("privateId").asLong();
			String email = jsonNode.path("email").asText();
			String realname = jsonNode.path("realname").asText();
			String phoneNumber = jsonNode.path("phoneNumber").asText();
			user.setId(privateId);
			user.setUserName(realname);
			user.setEmail(email);
			user.setPhoneNumber(phoneNumber);
		}
		return user;
	}
	
	 /**
     * 获取helome后台用户的id的集合对应的用户中心用户信息的集合
     * @param userIdList 用户id的集合
     * @return
     */
	public static List<UCUserVO> queryUserListByIds(List<Long> userIdList) {
    	long beginMillis = System.currentTimeMillis();
    	
    	String json = play.libs.Json.toJson(userIdList).toString();
    	String post = postWithJson("/remote/security/findNamesByPrivateIds", json);
    	long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "queryUserListByIds", "/remote/security/findNamesByPrivateIds");
        return parseJsonForQueryUserListByIds(post);
    }
	
	/**
     * 解析queryUserById方法调用接口返回的json
     * @param json
     * @return
     */
	public static List<UCUserVO> parseJsonForQueryUserListByIds(String json) {
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		List<UCUserVO> ucUserVOList = new ArrayList<UCUserVO>();
		if (StringUtils.isNotEmpty(json)) {
			JsonNode node = null;
			try {
				node = mapper.readTree(json);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (node != null && node.path("responsecode").asText().equals("_200")) {
				JsonNode jsonNode = node.path("data");
				Iterator<Entry<String, JsonNode>> fieldIte = jsonNode.fields(); // 2053={"englishName":null,"realname":null}
				while (fieldIte.hasNext()) {
					Entry<String, JsonNode> entry = fieldIte.next();
					Long userId = new Long(entry.getKey());
					JsonNode valueNode = entry.getValue();
					String englishName = valueNode.path("englishName").asText();
					String realname = valueNode.path("realname").asText();
					UCUserVO vo = new UCUserVO(userId, englishName, realname);
					ucUserVOList.add(vo);
				}
			}
		}
		return ucUserVOList;
	}

    /**
     * 用户登录
     * 
     * @param username 必须。用户名或者邮箱，或者11位电话号码
     * @param userpassword 必须。密码，直接传入密码原文
     * @param device 必须。android/iphone/ipad/web
     * @param ip 必须。在为移动端做代理登陆时，需要传入移动设备实际的ip地址
     * @return UCResult.data包含属性：UCUser全部属性<br>
     *         如果用户名、密码错误，返回没有匹配数据错误
     */
    public static UCResult<UCUser> login(String username, String userpassword, String device, String ip) {
        long beginMillis = System.currentTimeMillis();

        String post = postNullIgnore("/remote/security/login", "username", username, "userpassword", userpassword,
                "product", product, "device", device, "ip", ip);
        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "login", "/remote/security/login");

        UCResult<UCUser> result = UCResult.fromJsonString(post, UCUser.class, true);
        if (result.isSuccess() && (result.data == null || StringUtils.isBlank(result.data.userpassword))) {
            LOGGER.error("response error : null param or response. path = /remote/security/login, response = " + post);
        }
        return result;
    }

    /**
     * 检查用户状态
     * 
     * @param token 必须。这个随机码是用户登陆后生成的一个随机码
     * @return UCResult.data不包含任何数据，为null
     */
    public static UCResult<Void> checkUserLoginStatus(String token) {
        long beginMillis = System.currentTimeMillis();

        String post = postNullIgnore("/remote/security/checkUserLoginStatus", "token", token);
        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "checkUserLoginStatus", "/remote/security/checkUserLoginStatus");
        return UCResult.fromJsonString(post, Void.class, false);
    }

    /**
     * 注销指定token对应的用户的登录信息
     * 
     * @param token 必须。这个随机码是用户登陆后生成的一个随机码
     * @return UCResult.data不包含任何数据，为null<br>
     *         如果已经登出，返回没有匹配数据错误
     */
    public static UCResult<Void> logout(String token) {
        long beginMillis = System.currentTimeMillis();
        String post = postNullIgnore("/remote/security/logout", "token", token);

        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "logout", "/remote/security/logout");

        return UCResult.fromJsonString(post, Void.class, false);
    }

    /**
     * 注册用户 ，只要注册成功，就认为用户登录成功了<br>
     * 用户名、邮箱、电话号码必须填一项，服务端会进行重复性校验。
     * 
     * @param userpassword 必须。密码，明文
     * @param username 非必须，传入null代表不使用该参数。用户名
     * @param realname 非必须，传入null代表不使用该参数。姓名
     * @param email 非必须，传入null代表不使用该参数。邮箱
     * @param phoneNumber 非必须，传入null代表不使用该参数。电话号码
     * @param device 必须。android/iphone/ipad/web
     * @param ip 必须。在为移动端做代理登陆时，需要传入移动设备实际的ip地址
     * @param privateId 用户Id
     * @return UCResult.data包含属性：email、isable、phoneNumber、realname、token、uid、username、userpassword<br>
     *         用户名、邮箱、电话不唯一，返回查询到重复数据错误。
     */
    public static UCResult<UCUser> register(String userpassword, String username, String realname, String email,
            String phoneNumber, String device, String ip, Long privateId) {

        long beginMillis = System.currentTimeMillis();
        String post = postNullIgnore("/remote/security/register", "userpassword", userpassword, "username", username,
                "realname", realname, "email", email, "phoneNumber", phoneNumber, "product", product, "device", device,
                "ip", ip, "privateId", privateId.toString());
        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "register", "/remote/security/register");

        UCResult<UCUser> result = UCResult.fromJsonString(post, UCUser.class, true);
        if (result.data == null || StringUtils.isBlank(result.data.userpassword)) {
            LOGGER.error("response error : null param or response. path = /remote/security/register, response = "
                    + post);
        }
        return result;
    }

    /**
     * 使用新的明文密码的方式，对指定用户进行修改。
     * 
     * @param username 必须。用户名的形式包括：一般形式的用户名、用户密码、用户邮箱地址
     * @param newPassword 必须。新密码，明文
     * @return UCResult.data属性包含：userpassword
     */
    public static UCResult<UCUser> modifyPassword(String username, String newPassword) {
        long beginMillis = System.currentTimeMillis();
        String post = postNullIgnore("/remote/security/modifyPassword", "username", username, "newPassword",
                newPassword);
        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "modifyPassword", "/remote/security/modifyPassword");

        UCResult<UCUser> result = UCResult.fromJsonString(post, UCUser.class, true);
        if (result.data == null || StringUtils.isBlank(result.data.userpassword)) {
            LOGGER.error("response error : null param or response. path = /remote/security/modifyPassword, response = "
                    + post);
        }
        return result;
    }

    /**
     * 修改用户名时输入密码进行密码验证<br>
     * 用户名的形式包括：一般形式的用户名、用户密码、用户邮箱地址
     * 
     * @param token 必须。用户登陆后生成的一个随机凭证
     * @param password 必须。密码
     * @return UCResult.data不包含任何数据，为null<br>
     */
    public static UCResult<Void> checkPassword4ModifyUsername(String token, String password) {
        long beginMillis = System.currentTimeMillis();
        String post = postNullIgnore("/remote/security/checkPassword4ModifyUsername", "token", token, "password",
                password);
        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "checkPassword4ModifyUsername", "/remote/security/checkPassword4ModifyUsername");
        return UCResult.fromJsonString(post, Void.class, false);
    }

    /**
     * 修改用户密码时输入密码进行密码验证
     * 
     * @param token 必须。用户登陆后生成的一个随机凭证
     * @param password 必须。密码
     * @return UCResult.data不包含任何数据，为null<br>
     */
    public static UCResult<Void> checkPassword4ModifyPassword(String token, String password) {
        long beginMillis = System.currentTimeMillis();
        String post = postNullIgnore("/remote/security/checkPassword4ModifyPassword", "token", token, "password",
                password);

        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "checkPassword4ModifyPassword", "/remote/security/checkPassword4ModifyPassword");
        return UCResult.fromJsonString(post, Void.class, false);
    }

    /**
     * 返回当前用户密码安全级别,密码安全级别分为:强,中,弱。<br>
     * 返安全级别：<br>
     * strong--强(包含数字、字符、特殊字符)<br>
     * medium--中(包含数字和字符)<br>
     * weak--弱(只包含数字,密码长度小于8)
     * 
     * @param token 必须。用户登陆后生成的一个随机凭证
     * @return UCResult.data String类型，strong - 强， medium - 中， weak - 弱<br>
     */
    public static UCResult<String> passwordSecurityGrade(String token) {
        long beginMillis = System.currentTimeMillis();
        String post = postNullIgnore("/remote/security/passwordSecurityGrade", "token", token);
        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "passwordSecurityGrade", "/remote/security/passwordSecurityGrade");
        return UCResult.fromJsonString(post, String.class, true);
    }

    /**
     * 在用户已经登录的情况下，修改用户email。
     * 
     * @param token 必须。用户登陆后生成的一个随机凭证
     * @param email 新邮箱
     * @return UCResult.data不包含任何数据，为null<br>
     *         会进行登录校验、重复性校验
     */
    public static UCResult<Void> modifyEmail(String token, String email) {
        long beginMillis = System.currentTimeMillis();
        String post = postNullIgnore("/remote/security/modifyEmail", "token", token, "email", email);
        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "modifyEmail", "/remote/security/modifyEmail");
        return UCResult.fromJsonString(post, Void.class, false);
    }

    /**
     * 在用户登录的情况下绑定(修改)手机号。
     * 
     * @param token 必须。用户登陆后生成的一个随机凭证
     * @param phoneNumber 新手机号
     * @return UCResult.data不包含任何数据，为null<br>
     *         会进行登录校验、重复性校验
     */
    public static UCResult<Void> bindingPhoneNumber(String token, String phoneNumber) {
        long beginMillis = System.currentTimeMillis();
        String post = postNullIgnore("/remote/security/bindingPhoneNumber", "token", token, "phoneNumber", phoneNumber);
        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "bindingPhoneNumber", "/remote/security/bindingPhoneNumber");
        return UCResult.fromJsonString(post, Void.class, false);
    }

    /**
     * 使用加密的密码登录。加密的密码调用登录、注册接口可以获得。
     * 
     * @param username 必须。用户名或者邮箱，或者11位电话号码
     * @param userpassword 必须。密码，直接传入密码原文
     * @param device 必须。android/iphone/ipad/web
     * @param ip 必须。在为移动端做代理登陆时，需要传入移动设备实际的ip地址
     * @return UCResult.data包含全UCUser全部属性<br>
     *         如果用户名、密码错误，返回没有匹配数据错误
     */
    public static UCResult<UCUser> encryptLogin(String username, String userpassword, String device, String ip) {
        long beginMillis = System.currentTimeMillis();
        String post = postNullIgnore("/remote/security/encryptLogin", "username", username, "userpassword",
                userpassword, "product", product, "device", device, "ip", ip);
        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "encryptLogin", "/remote/security/encryptLogin");
        UCResult<UCUser> ucResult = UCResult.fromJsonString(post, UCUser.class, true);

        if (ucResult.isSuccess()) {
            UCResult<UCUser> oldUCResult = ucResult;
            ucResult = queryUserInfoByToken(ucResult.data.token);
            if (ucResult.isSuccess()) {
                ucResult.data.token = oldUCResult.data.token;
                ucResult.data.userpassword = userpassword;
            }
        }

        return ucResult;
    }

    public static UCResult<Void> modifyRealname(String token, String realname) {
        long beginMillis = System.currentTimeMillis();
        String post = postNullIgnore("/remote/security/modifyRealname", "token", token, "realname", realname);
        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        callTimeLog(callTime, "modifyRealname", "/remote/security/modifyRealname");
        return UCResult.fromJsonString(post, Void.class, false);
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
        String uri = ConfigFactory.getString("userCenter.client.serverUrl") + relativeUrl;

        Promise<Response> post = WSUtil.postFormURLEncoded(uri, paramArray);

        LOGGER.debug("usercenter request uri : " + uri + ", post : " + Arrays.toString(params));

        Response response;
        try {
            response = post.get(REQUEST_TIMEOUT);
        } catch (Exception e) {
            throw new UserCenterException("接收用户中心的响应时发生异常.METHOD = post, URI = " + uri + ", params = "
                    + Arrays.toString(params), e);
        }

        String body = response.getBody();

        LOGGER.debug("usercenter request uri : " + uri + ", post response : " + body);

        return body;
    }
    
    /**
     * 发生post请求，参数值为json字符串
     */
    private static String postWithJson(String relativeUrl, String json) {
        if (StringUtils.isEmpty(json)) {
            throw new IllegalArgumentException("Parameters cannot be empty");
        }

        String uri = ConfigFactory.getString("userCenter.client.serverUrl") + relativeUrl;

        Promise<Response> post = WSUtil.postWithJson(uri, json);

        LOGGER.debug("usercenter request uri : " + uri + ", post : " + json);

        Response response;
        try {
            response = post.get(REQUEST_TIMEOUT);
        } catch (Exception e) {
            throw new UserCenterException("接收用户中心的响应时发生异常.METHOD = post, URI = " + uri + ", params = " + json, e);
        }

        String body = response.getBody();

        LOGGER.debug("usercenter request uri : " + uri + ", post response : " + body);

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
        String uri = ConfigFactory.getString("userCenter.client.serverUrl") + relativeUrl;

        Promise<Response> get = WSUtil.get(uri, paramArray);

        LOGGER.debug("usercenter request uri : " + uri + ", get : " + Arrays.toString(params));

        Response response = null;
        try {
            response = get.get(REQUEST_TIMEOUT);
        } catch (Exception e) {
            throw new UserCenterException("接收用户中心的响应时发生异常.METHOD = get, URI = " + uri + ", params = "
                    + Arrays.toString(params), e);
        }

        String body = response.getBody();

        LOGGER.debug("usercenter request uri : " + uri + ", get response : " + body);

        return body;

    }

    private static void callTimeLog(long callTime, String method, String url) {
        StringBuilder log = new StringBuilder();
        log.append("调用方法=").append(method);
        log.append(",用户中心URL=").append(url);
        log.append(",耗时=").append(callTime).append("ms");
        LOGGER.info(log.toString());
    }

}
