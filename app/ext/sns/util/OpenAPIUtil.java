/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-12
 */
package ext.sns.util;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;

/**
 * 
 * 
 * @ClassName: OpenAPIUtil
 * @Description: OpenAPI使用的工具类
 * @date 2014-6-12 下午3:19:44
 * @author ShenTeng
 * 
 */
public class OpenAPIUtil {

    public static JsonNode parseJsonRet(String body) {
        try {
            return Json.parse(body);
        } catch (RuntimeException e) {
            throw new RuntimeException("parse json return error. response = " + body + "\n", e);
        }
    }

    public static void validateJsonRetHasNonNull(JsonNode jsonRet, String urlForLog, String bodyForLog,
            String... fields) {
        for (String f : fields) {
            if (!jsonRet.hasNonNull(f)) {
                throw new RuntimeException("call " + urlForLog + " fail. JsonNode \"" + f
                        + " is not found or is null. body = " + bodyForLog);
            }
        }
    }
}
