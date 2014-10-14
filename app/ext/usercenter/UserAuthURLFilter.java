/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-11
 */
package ext.usercenter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mobile.util.MobileUtil;
import mobile.vo.result.MobileResult;
import models.User;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.Logger.ALogger;
import play.Play;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Http.Session;
import play.mvc.Results;
import play.mvc.SimpleResult;
import controllers.base.ObjectNodeResult;

/**
 * 
 * 
 * @ClassName: UserAuthURLFilter
 * @Description: 通过URL进行用户鉴权
 * @date 2013-11-11 下午1:55:50
 * @author ShenTeng
 * 
 */
public class UserAuthURLFilter {

    private static final ALogger LOGGER = Logger.of(UserAuthURLFilter.class);

    private static final String commentPrefix = "#";

    private static final String NO_LOGIN_REDIRECT_URI = controllers.routes.Application.login().url() + "?msg=1";

    private static final JsonNode NO_LOGIN_RET_JSON = Json.parse("{\"notLogin\":\"true\"}");

    private static final JsonNode NO_LOGIN_RET_JSON_FOR_MOBILE = Json
            .parse("{\"status\":0,\"errorCode\":\"100002\",\"error\":\"用户未登录\"}");

    private static final JsonNode NO_COMP_INFO_RET_JSON = Json.parse("{\"notCompInfo\":\"true\"}");

    private static final String NO_COMP_INFO_REDIRECT_URI = controllers.user.routes.UserSettingApp
            .thirdaccountsetting().url();

    private static enum RULE {
        MUST_LOGIN("MUST-LOGIN"), MUST_LOGIN_PAGE("MUST-LOGIN-PAGE"), MUST_LOGIN_JSON("MUST-LOGIN-JSON"), ACCEPT(
                "ACCEPT"), MUST_COMP_INFO("MUST-COMP-INFO"), MUST_COMP_INFO_JSON("MUST-COMP-INFO-JSON");

        private String configStr;

        RULE(String configStr) {
            this.configStr = configStr;
        }

        /**
         * 更具配置文件中配置的规则串，获取规则枚举对象
         * 
         * @param config RULE枚举对象，未匹配返回null
         */
        public static RULE getRULEByConfigStr(String config) {
            RULE rule = null;
            for (RULE r : RULE.values()) {
                if (r.configStr.equals(config)) {
                    rule = r;
                    break;
                }
            }

            return rule;
        }
    }

    private static PathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Map<HTTP方法, LinkedHashMap<URL, 规则>>
     */
    private static Map<String, LinkedHashMap<String, RULE>> config = new HashMap<>();

    /**
     * 初始化URL用户鉴权配置
     */
    public static void init() {
        List<String> lines = readAllLines();

        if (CollectionUtils.isEmpty(lines)) {
            LOGGER.warn("userauth.conf is empty.");
        } else {
            parseFile(lines);
        }

        if (MapUtils.isEmpty(config)) {
            LOGGER.warn("userauth.conf is empty.");
        }
    }

    public JsonNode getNoLoginRetAjaxForMobile() {
        return NO_LOGIN_RET_JSON_FOR_MOBILE;
    }

    /**
     * 获取未登录重定向URI
     * 
     * @param referer
     * @return
     */
    public static String getNoLoginRedirectURI(String referer) {
        return getRedirectURI(NO_LOGIN_REDIRECT_URI, referer);
    }

    /**
     * 根据URL进行用户鉴权
     * 
     * @return play的结果对象 null代表不进行拦截，正常进行后续处理
     */
    public static SimpleResult filter(Session session, Request request) {
        String httpMethod = request.method().toUpperCase();
        String path = request.path();
        String referer = request.uri();
        boolean isJsonRequest = isJsonRequest(request);

        RULE r = getLastMatchRule(httpMethod, path);

        if (Logger.isDebugEnabled()) {
            String info = null;
            if (null == r) {
                info = " match no rule";
            } else {
                info = " match rule: [" + r.name() + "]";
            }
            LOGGER.debug("Path: [" + path + "]" + info);
        }

        boolean isLogin = UserAuthService.isLogin(session);
        if (null == r || RULE.ACCEPT.equals(r)) {
            return null;
        } else if (RULE.MUST_LOGIN.equals(r)) {
            return checkLogin(session, path, referer, isJsonRequest, isLogin);
        } else if (RULE.MUST_LOGIN_JSON.equals(r)) {
            return checkLogin(session, path, referer, true, isLogin);
        } else if (RULE.MUST_LOGIN_PAGE.equals(r)) {
            return checkLogin(session, path, referer, false, isLogin);
        } else if (RULE.MUST_COMP_INFO.equals(r)) {
            return checkCompleteInfoAuto(session, path, referer, isJsonRequest, isLogin);
        } else if (RULE.MUST_COMP_INFO_JSON.equals(r)) {
            return checkCompleteInfoAuto(session, path, referer, true, isLogin);
        }

        return null;
    }

    private static SimpleResult checkCompleteInfoAuto(Session session, String path, String referer,
            boolean isJsonRequest, boolean isLogin) {
        SimpleResult result = checkLogin(session, path, referer, isJsonRequest, isLogin);

        if (null != result) {
            return result;
        }

        if (StringUtils.isBlank(User.getFromSession(session).email)) {
            if (MobileUtil.isMobileUrlPrefix(path)) {
                result = (SimpleResult) MobileResult.error("100007", "未完善用户信息").getResult();
            } else {
                if (isJsonRequest) {
                    result = Results.ok(NO_COMP_INFO_RET_JSON);
                } else {
                    result = Results.redirect(getRedirectURI(NO_COMP_INFO_REDIRECT_URI, referer));
                }
            }
        }

        return result;
    }

    private static SimpleResult checkLogin(Session session, String path, String referer, boolean isJsonRequest,
            boolean isLogin) {
        if (isJsonRequest) {
            JsonNode json = null;
            if (MobileUtil.isMobileUrlPrefix(path)) {
                if (UserAuthService.isKicked(session)) {
                    json = new ObjectNodeResult().error("被踢下线", "100003").getObjectNode();
                } else {
                    json = NO_LOGIN_RET_JSON_FOR_MOBILE;
                }
            } else {
                json = NO_LOGIN_RET_JSON;
            }
            return isLogin ? null : Results.ok(json);
        } else {
            return isLogin ? null : Results.redirect(getNoLoginRedirectURI(referer));
        }
    }

    private static boolean isJsonRequest(Request request) {
        String contentType = request.getHeader("Content-Type");
        String accept = request.getHeader("Accept");

        if (StringUtils.contains(contentType, "application/json")) {
            return true;
        }
        if (StringUtils.contains(accept, "application/json")) {
            return true;
        }

        return false;
    }

    /**
     * 获取最后一条匹配的规则
     * 
     * @return RULE枚举,未匹配返回NULL
     */
    private static RULE getLastMatchRule(String httpMethod, String path) {
        LinkedHashMap<String, RULE> urlAndRule = config.get(httpMethod);
        if (null != urlAndRule) {
            for (Map.Entry<String, RULE> e : urlAndRule.entrySet()) {
                boolean isMatch = pathMatcher.match(e.getKey(), path);
                if (isMatch) {
                    return e.getValue();
                }
            }
        }

        return null;
    }

    /**
     * 读取文件的所有行
     * 
     */
    private static List<String> readAllLines() {
        List<String> lines = new ArrayList<>();
        BufferedReader br = null;
        InputStream configFileSteam = Play.application().resourceAsStream("userauth.conf");
        if (null == configFileSteam) {
            throw new RuntimeException("读取conf目录下的userauth.conf文件失败.");
        }

        try {
            br = new BufferedReader(new InputStreamReader(configFileSteam, "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("userauth.conf读取错误.", e);
        } catch (IOException e) {
            throw new RuntimeException("userauth.conf读取错误.", e);
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOGGER.error("关闭BufferedReader失败.", e);
                }
            }
        }

        return lines;
    }

    /**
     * 文件格式：规则 HTTP方法 URL 例如：NO-LOGIN-REDIRECT ALL /user/**
     * 
     * @param line
     */
    private static void parseFile(List<String> lines) {
        for (int i = lines.size() - 1; i > -1; i--) {
            String line = lines.get(i);
            if (line.startsWith(commentPrefix) || StringUtils.isBlank(line)) {
                continue;
            }

            String[] splitedLine = line.split("\\s+");

            if (null == splitedLine || splitedLine.length != 3 || StringUtils.isBlank(splitedLine[0])
                    || StringUtils.isBlank(splitedLine[1]) || StringUtils.isBlank(splitedLine[2])) {
                throw new RuntimeException("userauth.conf第" + (i + 1) + "行非法.内容: " + line);
            }

            RULE rule = RULE.getRULEByConfigStr(splitedLine[0]);
            if (null == rule) {
                throw new RuntimeException("userauth.conf第" + (i + 1) + "行非法.未知的规则: " + splitedLine[0]);
            }

            String method = splitedLine[1].toUpperCase();
            String url = splitedLine[2];

            if (null == config.get(method)) {
                config.put(method, new LinkedHashMap<String, RULE>());
            }

            config.get(method).put(url, rule);
        }
    }

    private static String getURIQueryStringPrefix(String authURI) {
        return authURI.contains("?") ? "&" : "?";
    }

    /**
     * 获取重定向URI
     */
    private static String getRedirectURI(String uri, String referer, String... param) {
        StringBuilder uriBuilder = new StringBuilder(uri);
        uriBuilder.append(getURIQueryStringPrefix(uri));
        String encodeReferer = null;
        try {
            encodeReferer = URLEncoder.encode(StringUtils.defaultString(referer), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        uriBuilder.append("referer=").append(encodeReferer);
        if (ArrayUtils.isNotEmpty(param)) {
            for (String p : param) {
                uriBuilder.append("&").append(p);
            }
        }
        return uriBuilder.toString();
    }

}
