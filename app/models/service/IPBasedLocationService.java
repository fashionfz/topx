/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-19
 */
package models.service;

import org.apache.commons.lang3.StringEscapeUtils;

import play.Logger;
import play.Logger.ALogger;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.WS.Response;
import utils.WSUtil;

import com.fasterxml.jackson.databind.JsonNode;

import ext.config.ConfigFactory;

/**
 * 
 * 
 * @ClassName: IPBasedLocationService
 * @Description: 基于IP的定位服务
 * @date 2013-11-19 下午2:23:26
 * @author ShenTeng
 * 
 */
public class IPBasedLocationService {

    private static final ALogger LOGGER = Logger.of(IPBasedLocationService.class);

    private static String baiduServiceURL = "http://api.map.baidu.com/location/ip";
    private static String baiduServiceAK = ConfigFactory.getString("baidu.ipBasedLocation.ak");
    private static int REQUEST_TIMEOUT = 10000;

    /**
     * 简要地址，例如：北京市、吉林省长春市
     * 
     * @param ip ip地址
     * @return 简要地址,null - 获取地址时发生错误
     */
    public static String getSimpleAddress(String ip) {
        Promise<Response> get = WSUtil.get(baiduServiceURL, "ak", baiduServiceAK, "ip", ip);

        Response response;
        try {
            response = get.get(REQUEST_TIMEOUT);
        } catch (RuntimeException e) {
            LOGGER.error("请求百度IP定位API超时", e);
            return null;
        }

        String body = response.getBody();
        JsonNode locationJsonNode = null;

        try {
            body = StringEscapeUtils.unescapeJava(body);
            locationJsonNode = Json.parse(body);
        } catch (RuntimeException e) {
            LOGGER.error("百度IP定位API返回JSON解析错误。返回的JSON：" + body, e);
            return null;
        }

        String simpleAddress = null;

        if (locationJsonNode.has("content") && locationJsonNode.get("content").has("address")) {
            simpleAddress = locationJsonNode.get("content").get("address").asText();
        } else {
            LOGGER.error("百度IP定位API定位失败。返回的JSON：" + body);
        }

        return simpleAddress;
    }

}
