/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-5-20
 */
package mobile.util.jsonparam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import play.libs.Akka;
import play.libs.Json;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import mobile.util.MobileResults;
import mobile.util.jsonparam.validate.JsonValidateResult;
import mobile.util.jsonparam.validate.JsonValidator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ext.config.ConfigFactory;

/**
 * 
 * 
 * @ClassName: JsonParam
 * @Description: Json参数工具类
 * @date 2014-5-20 下午6:05:38
 * @author ShenTeng
 * 
 */
public class JsonParam {
    private static Boolean isShowErrorDetail = ConfigFactory.getBoolean("mobile.jsonparam.validate.showErrorDetail");
    // 定时刷新配置
    static {
        Akka.system()
                .scheduler()
                .schedule(Duration.create(300000, TimeUnit.MILLISECONDS),
                        Duration.create(300000, TimeUnit.MILLISECONDS), new Runnable() {
                            @Override
                            public void run() {
                                isShowErrorDetail = ConfigFactory
                                        .getBoolean("mobile.jsonparam.validate.showErrorDetail");
                            }
                        }, Akka.system().dispatcher());
    }

    private JsonParamConfig config;
    private JsonNode raw;
    private JsonNode clean;
    private Boolean validateResult;
    private String errorMsg;

    public JsonParam(JsonNode jsonNode, JsonParamConfig config) {
        if (null == config) {
            throw new IllegalArgumentException("config can't be null.");
        }
        this.raw = jsonNode;
        this.config = config;
    }

    public boolean validate() {
        validateAndClean();
        return validateResult;
    }

    public boolean isInvalid() {
        validateAndClean();
        return !validateResult;
    }

    public String getErrorMsg() {
        validateAndClean();
        if (isShowErrorDetail()) {
            return errorMsg;
        } else {
            return "参数非法";
        }
    }

    public Result getErrorResult() {
        validateAndClean();
        if (validate()) {
            return null;
        }
        return MobileResults.illegalParameters(getErrorMsg()).getResult();
    }

    /**
     * 获取干净Json。所有未在配置中定义的项均会被过滤。
     * 
     * @return null - 原始Json未通过校验
     */
    public JsonNode getCleanJson() {
        if (!validate()) {
            return null;
        }

        return Json.newObject().setAll((ObjectNode) clean);
    }

    /**
     * 获取原始Json<br>
     * 如果其中含有多余的参数不会被过滤，在直接保存Json到数据库的场景中需要慎用。<br>
     * 另外，如果原始Json未通过校验，不会影响该接口的返回，这与获取干净Json不同。<br>
     * 通常情况下应该使用获取干净Json接口{@link JsonParam#getCleanJson()}
     * 
     * @return
     */
    public JsonNode getRawJson() {
        return raw;
    }

    public ArrayNode getJsonArray(String fieldName) {
        return getJsonArray(fieldName, null);
    }

    public ArrayNode getJsonArray(String fieldName, ArrayNode defaultValue) {
        if (!validate() || !clean.hasNonNull(fieldName) || !clean.get(fieldName).isArray()) {
            return defaultValue;
        }
        return (ArrayNode) clean.get(fieldName);
    }

    public Boolean getBoolean(String fieldName) {
        return getBoolean(fieldName, null);
    }

    public Boolean getBoolean(String fieldName, Boolean defaultValue) {
        if (!validate() || !clean.hasNonNull(fieldName) || !clean.get(fieldName).isBoolean()) {
            return defaultValue;
        }
        return clean.get(fieldName).booleanValue();
    }

    public ObjectNode getJsonObject(String fieldName) {
        return getJsonObject(fieldName, null);
    }

    public ObjectNode getJsonObject(String fieldName, ObjectNode defaultValue) {
        if (!validate() || !clean.hasNonNull(fieldName) || !clean.get(fieldName).isObject()) {
            return defaultValue;
        }
        return (ObjectNode) clean.get(fieldName);
    }

    public String getString(String fieldName) {
        return getString(fieldName, null);
    }

    public String getString(String fieldName, String defaultValue) {
        if (!validate() || !clean.hasNonNull(fieldName) || !clean.get(fieldName).isTextual()) {
            return defaultValue;
        }
        return clean.get(fieldName).textValue();
    }

    public Integer getInt(String fieldName) {
        return getInt(fieldName, null);
    }

    public Integer getInt(String fieldName, Integer defaultValue) {
        if (!validate() || !clean.hasNonNull(fieldName) || !clean.get(fieldName).isInt()) {
            return defaultValue;
        }
        return clean.get(fieldName).intValue();
    }

    public Long getLong(String fieldName) {
        return getLong(fieldName, null);
    }

    public Long getLong(String fieldName, Long defaultValue) {
        if (!validate() || !clean.hasNonNull(fieldName)
                || (!clean.get(fieldName).isLong() && !clean.get(fieldName).isInt())) {
            return defaultValue;
        }
        return clean.get(fieldName).longValue();
    }

    public List<Boolean> getBooleanList(String fieldName) {
        return getBooleanList(fieldName, null);
    }

    public List<Boolean> getBooleanList(String fieldName, List<Boolean> defaultValue) {
        if (!validate() || !clean.hasNonNull(fieldName) || !clean.get(fieldName).isArray()) {
            return defaultValue;
        }

        Iterator<JsonNode> elements = clean.get(fieldName).elements();

        List<Boolean> result = new ArrayList<>();
        while (elements.hasNext()) {
            JsonNode next = elements.next();
            if (!next.isBoolean()) {
                return defaultValue;
            }
            result.add(next.booleanValue());
        }

        return result;
    }

    public List<ObjectNode> getJsonObjectList(String fieldName) {
        return getJsonObjectList(fieldName, null);
    }

    public List<ObjectNode> getJsonObjectList(String fieldName, List<ObjectNode> defaultValue) {
        if (!validate() || !clean.hasNonNull(fieldName) || !clean.get(fieldName).isArray()) {
            return defaultValue;
        }

        Iterator<JsonNode> elements = clean.get(fieldName).elements();

        List<ObjectNode> result = new ArrayList<>();
        while (elements.hasNext()) {
            JsonNode next = elements.next();
            if (!next.isObject()) {
                return defaultValue;
            }
            result.add((ObjectNode) next);
        }

        return result;
    }

    public List<String> getStringList(String fieldName) {
        return getStringList(fieldName, null);
    }

    public List<String> getStringList(String fieldName, List<String> defaultValue) {
        if (!validate() || !clean.hasNonNull(fieldName) || !clean.get(fieldName).isArray()) {
            return defaultValue;
        }

        Iterator<JsonNode> elements = clean.get(fieldName).elements();

        List<String> result = new ArrayList<>();
        while (elements.hasNext()) {
            JsonNode next = elements.next();
            if (!next.isTextual()) {
                return defaultValue;
            }
            result.add(next.textValue());
        }

        return result;
    }

    public List<Integer> getIntList(String fieldName) {
        return getIntList(fieldName, null);
    }

    public List<Integer> getIntList(String fieldName, List<Integer> defaultValue) {
        if (!validate() || !clean.hasNonNull(fieldName) || !clean.get(fieldName).isArray()) {
            return defaultValue;
        }

        Iterator<JsonNode> elements = clean.get(fieldName).elements();

        List<Integer> result = new ArrayList<>();
        while (elements.hasNext()) {
            JsonNode next = elements.next();
            if (!next.isInt()) {
                return defaultValue;
            }
            result.add(next.intValue());
        }

        return result;
    }

    public List<Long> getLongList(String fieldName) {
        return getLongList(fieldName, null);
    }

    public List<Long> getLongList(String fieldName, List<Long> defaultValue) {
        if (!validate() || !clean.hasNonNull(fieldName) || !clean.get(fieldName).isArray()) {
            return defaultValue;
        }

        Iterator<JsonNode> elements = clean.get(fieldName).elements();

        List<Long> result = new ArrayList<>();
        while (elements.hasNext()) {
            JsonNode next = elements.next();
            if (!next.isLong() && !next.isInt()) {
                return defaultValue;
            }
            result.add(next.longValue());
        }

        return result;
    }

    /**
     * 校验并生成经过处理的干净Json（cleanJson）
     */
    private void validateAndClean() {
        if (null != validateResult) {
            return;
        }

        JsonValidateResult jsonValidateResult = JsonValidator.validateAndClean(raw, config);

        this.validateResult = jsonValidateResult.isValid();
        this.clean = jsonValidateResult.getClean();
        this.errorMsg = jsonValidateResult.getErrorMsg();
    }

    private boolean isShowErrorDetail() {
        return isShowErrorDetail == null || isShowErrorDetail;
    }

}
