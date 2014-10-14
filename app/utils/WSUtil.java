/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-4
 */
package utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.Response;
import play.libs.WS.WSRequestHolder;

/**
 * 
 * 
 * @ClassName: WSUtil
 * @Description: play ws工具类
 * @date 2013-12-4 上午11:37:55
 * @author ShenTeng
 * 
 */
public class WSUtil {

    public static String getURIQueryStringPrefix(String authURI) {
        return authURI.contains("?") ? "&" : "?";
    }

    /**
     * 发送post请求。ContentType为application/x-www-form-urlencoded
     * 
     * @param uri 请求URI。可以使用带参数的URI（例如：http://127.0.0.1/test?a=b），方法自动进行处理。
     * @param paramMap 请求参数
     * @return Promise<Response>
     */
    public static Promise<Response> postFormURLEncoded(String uri, Map<String, String> paramMap) {
        if (MapUtils.isEmpty(paramMap)) {
            return postFormURLEncoded(uri);
        } else {
            String[] paramArray = new String[paramMap.size() * 2];
            int index = 0;
            for (Map.Entry<String, String> e : paramMap.entrySet()) {
                paramArray[index++] = e.getKey();
                paramArray[index++] = e.getValue();
            }
            return postFormURLEncoded(uri, paramArray);
        }
    }

    /**
     * 发送post请求。ContentType为application/x-www-form-urlencoded
     * 
     * @param uri 请求URI。可以使用带参数的URI（例如：http://127.0.0.1/test?a=b），方法自动进行处理。
     * @param paramArray 请求参数
     * @return Promise<Response>
     */
    public static Promise<Response> postFormURLEncoded(String uri, String... paramArray) {
        return postFormURLEncodedWithHeader(uri, null, paramArray);
    }

    /**
     * 发送post请求，包含请求头。ContentType为application/x-www-form-urlencoded
     * 
     * @param uri 请求URI。可以使用带参数的URI（例如：http://127.0.0.1/test?a=b），方法自动进行处理。
     * @param header 请求头。为null或空Map则不设置
     * @param paramArray 请求参数
     * @return Promise<Response>
     */
    public static Promise<Response> postFormURLEncodedWithHeader(String uri, Map<String, String> header,
            String... paramArray) {
        if (paramArray != null && paramArray.length % 2 != 0) {
            throw new IllegalArgumentException("请求参数个位不能为奇数");
        }

        String[] convertURI = convertURI(uri);
        String realURI = convertURI[0];

        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = 1; i < convertURI.length; i += 2) {
            bodyBuilder.append(convertURI[i]).append("=").append(convertURI[i + 1]).append('&');
        }
        if (paramArray != null && paramArray.length > 0) {
            for (int i = 0; i < paramArray.length; i += 2) {
                try {
                    bodyBuilder.append(paramArray[i]).append("=").append(URLEncoder.encode(paramArray[i + 1], "utf-8"))
                            .append('&');
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (bodyBuilder.length() > 0) {
            bodyBuilder.deleteCharAt(bodyBuilder.length() - 1);
        }

        WSRequestHolder requestHolder = WS.url(realURI).setContentType("application/x-www-form-urlencoded");
        if (MapUtils.isNotEmpty(header)) {
            for (Map.Entry<String, String> e : header.entrySet()) {
                requestHolder.setHeader(e.getKey(), e.getValue());
            }
        }

        return requestHolder.post(bodyBuilder.toString());
    }
    
    /**
     * 发送post请求，包含请求头。ContentType为application/json
     * 
     * @param uri 请求URI。
     * @param header 请求头。为null或空Map则不设置
     * @param paramArray 请求参数
     * @return Promise<Response>
     */
    public static Promise<Response> postWithJson(String uri, String json) {
        if (StringUtils.isEmpty(json)) {
            throw new IllegalArgumentException("请求参数不能为");
        }

        String[] convertURI = convertURI(uri);
        String realURI = convertURI[0];

        WSRequestHolder requestHolder = WS.url(realURI).setContentType("application/json");

        return requestHolder.post(json);
    }

    /**
     * 发送get请求。
     * 
     * @param uri 请求URI。可以使用带参数的URI（例如：http://127.0.0.1/test?a=b），方法自动进行处理。
     * @param paramMap 请求参数
     * @return Promise<Response>
     */
    public static Promise<Response> get(String uri, Map<String, String> paramMap) {
        if (MapUtils.isEmpty(paramMap)) {
            return get(uri);
        } else {
            String[] paramArray = new String[paramMap.size() * 2];
            int index = 0;
            for (Map.Entry<String, String> e : paramMap.entrySet()) {
                paramArray[index++] = e.getKey();
                paramArray[index++] = e.getValue();
            }
            return get(uri, paramArray);
        }
    }

    /**
     * 发送get请求。
     * 
     * @param uri 请求URI。可以使用带参数的URI（例如：http://127.0.0.1/test?a=b），方法自动进行处理。
     * @param paramArray 请求参数
     * @return Promise<Response>
     */
    public static Promise<Response> get(String uri, String... paramArray) {
        if (paramArray != null && paramArray.length % 2 != 0) {
            throw new IllegalArgumentException("请求参数个位不能为奇数");
        }

        String[] convertURI = convertURI(uri);
        String realURI = convertURI[0];

        WSRequestHolder requestHolder = WS.url(realURI);
        for (int i = 1; i < convertURI.length; i += 2) {
            requestHolder.setQueryParameter(convertURI[i], convertURI[i + 1]);
        }
        if (paramArray != null && paramArray.length > 0) {
            for (int i = 0; i < paramArray.length; i += 2) {
                requestHolder.setQueryParameter(paramArray[i], paramArray[i + 1]);
            }
        }

        return requestHolder.get();
    }

    /**
     * 发送get请求，包含请求头。
     * 
     * @param uri 请求URI。可以使用带参数的URI（例如：http://127.0.0.1/test?a=b），方法自动进行处理。
     * @param header 请求头。为null或空Map则不设置
     * @param paramArray 请求参数
     * @return Promise<Response>
     */
    public static Promise<Response> getWithHeader(String uri, Map<String, String> header, String... paramArray) {
        if (paramArray != null && paramArray.length % 2 != 0) {
            throw new IllegalArgumentException("请求参数个位不能为奇数");
        }

        String[] convertURI = convertURI(uri);
        String realURI = convertURI[0];

        WSRequestHolder requestHolder = WS.url(realURI);
        for (int i = 1; i < convertURI.length; i += 2) {
            requestHolder.setQueryParameter(convertURI[i], convertURI[i + 1]);
        }
        if (paramArray != null && paramArray.length > 0) {
            for (int i = 0; i < paramArray.length; i += 2) {
                requestHolder.setQueryParameter(paramArray[i], paramArray[i + 1]);
            }
        }

        if (MapUtils.isNotEmpty(header)) {
            for (Map.Entry<String, String> e : header.entrySet()) {
                requestHolder.setHeader(e.getKey(), e.getValue());
            }
        }

        return requestHolder.get();
    }

    private static String[] convertURI(String uri) {
        String[] splitURI = uri.split("\\?", 2);
        List<String> paramList = new ArrayList<>();
        paramList.add(splitURI[0]);

        if (splitURI.length > 1) {
            for (String pair : splitURI[1].split("&")) {
                if (StringUtils.isNotBlank(pair)) {
                    String[] param = pair.split("=");

                    String val = param.length == 2 ? param[1] : "";
                    paramList.add(param[0]);
                    paramList.add(val);
                }
            }
        }

        return paramList.toArray(new String[0]);
    }

}
