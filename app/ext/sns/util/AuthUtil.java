/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-26
 */
package ext.sns.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * 
 * @ClassName: AuthUtil
 * @Description: 授权相关的工具类
 * @date 2014-3-26 下午4:09:39
 * @author ShenTeng
 * 
 */
public class AuthUtil {
    /**
     * 编码state参数，使state参数可以携带多个用户自定义的参数
     * 
     * @param params 用户自定义的参数
     * @return 被编码的state参数
     */
    public static String encodeState(Map<String, String> params) {
        String state = null;

        try {
            StringBuilder stateBuilder = new StringBuilder();
            for (Map.Entry<String, String> e : params.entrySet()) {
                stateBuilder.append(URLEncoder.encode(e.getKey(), "utf-8")).append('&');
                stateBuilder.append(URLEncoder.encode(e.getValue(), "utf-8")).append('&');
            }

            if (stateBuilder.length() > 0) {
                stateBuilder.deleteCharAt(stateBuilder.length() - 1);
            }

            state = URLEncoder.encode(stateBuilder.toString(), "utf-8").replace('%', '_');
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return state;
    }

    /**
     * 解码state参数
     * 
     * @param state 被编码的state参数
     * @return 解析失败返回null
     */
    public static Map<String, String> decodeState(String state) {
        if (StringUtils.isBlank(state)) {
            return null;
        }

        Map<String, String> params = null;

        try {
            String decode = URLDecoder.decode(state.replace('_', '%'), "utf-8");
            String[] encodeParamArray = decode.split("&");

            params = new HashMap<String, String>();

            for (int i = 0; i < encodeParamArray.length; i = i + 2) {
                String key = URLDecoder.decode(encodeParamArray[i], "utf-8");
                String val = URLDecoder.decode(encodeParamArray[i + 1], "utf-8");
                params.put(key, val);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return params;
    }
}
