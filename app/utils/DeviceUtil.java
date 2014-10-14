/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-17
 */
package utils;

import mobile.util.MobileUtil;

import org.apache.commons.lang3.StringUtils;

/**
 *
 *
 * @ClassName: DeviceUtil
 * @Description: 获取设备来源的工具类
 * @date 2014-3-17 下午6:03:09
 * @author ShenTeng
 * 
 */
public class DeviceUtil {
    
    /**
     * 根据url获取请求来源设备
     * @param url 相对路径
     * @return 来源设备：android/iphone/ipad/web
     */
    public static String getDeviceByUrl(String url) {
        String from = MobileUtil.getFromByUrl(url);
        if (StringUtils.isBlank(from)) {
            return "web";
        } else if (from.toLowerCase().contains("android")) {
            return "android";
        } else if (from.toLowerCase().contains("ipad")) {
            return "ipad";
        } else if (from.toLowerCase().contains("iphone")) {
            return "iphone";
        }
        
        return "web";
    }
    
}
