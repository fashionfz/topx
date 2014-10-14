/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-2-12
 */
package mobile.util;

import models.User;

import org.apache.commons.lang3.StringUtils;

import play.mvc.Http.Context;
import play.mvc.Http.Request;

/**
 * 
 * 
 * @ClassName: MobileUtil
 * @Description: 移动端使用的工具类
 * @date 2014-2-12 下午2:43:09
 * @author ShenTeng
 * 
 */
public class MobileUtil {

    public static final String FROM_ANDROID = "android";
    public static final String FROM_IPAD = "ipad";
    public static final String FROM_IPHONE = "iphone";

    /**
     * 是否是手机移动客户端请求的URL，会验证1级目录
     * 
     * @return true：是，false：不是
     */
    public static boolean isMobileUrlPrefix(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        } else {
            return url.startsWith("/mobile/");
        }
    }

    /**
     * 是否是手机移动客户端请求的URL，会验证1级目录和2级目录
     * 
     * @return true：是，false：不是
     */
    public static boolean isMobileUrlPrefixAndDevice(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        } else {
            return url.startsWith("/mobile/android/") || url.startsWith("/mobile/ipad/")
                    || url.startsWith("/mobile/iphone/");
        }
    }

    public static boolean isFromAndroid(String from) {
        return FROM_ANDROID.equals(from);
    }

    public static boolean isFromIpad(String from) {
        return FROM_IPAD.equals(from);
    }

    public static boolean isFromIphone(String from) {
        return FROM_IPHONE.equals(from);
    }

    /**
     * 根据Url获取from
     * 
     * @param url 相对路径
     * @return null代表未知来源
     */
    public static String getFromByUrl(String url) {
        if (url.startsWith("/mobile/android/")) {
            return FROM_ANDROID;
        } else if (url.startsWith("/mobile/ipad/")) {
            return FROM_IPAD;
        } else if (url.startsWith("/mobile/iphone/")) {
            return FROM_IPHONE;
        }

        return null;
    }

    public static boolean JudgeIsMoblie(Request request) {
        boolean isMoblie = false;
        String[] mobileAgents = { "iphone", "ipad", "ipod", "android", "phone", "mobile", "wap", "netfront", "java",
                "opera mobi", "opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry",
                "dopod", "nokia", "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola",
                "foma", "docomo", "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad",
                "webos", "techfaith", "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips",
                "sagem", "wellcom", "bunjalloo", "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos",
                "pantech", "gionee", "portalmmm", "jig browser", "hiptop", "benq", "haier", "Googlebot-Mobile" };
        if (request.getHeader("User-Agent") != null) {
            for (String mobileAgent : mobileAgents) {
                if (request.getHeader("User-Agent").toLowerCase().indexOf(mobileAgent) >= 0) {
                    isMoblie = true;
                    break;
                }
            }
        }
        return isMoblie;
    }

    /**
     * 获取当前登录用户，未登录返回null
     * 
     * @return
     */
    public static User getCurrentUser() {
        return User.getFromSession(Context.current().session());
    }

}
