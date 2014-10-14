/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-5-20
 */
package mobile.util.jsonparam;

import java.util.LinkedHashMap;
import java.util.Map;

import mobile.util.jsonparam.config.FieldConfig;
import mobile.util.jsonparam.config.item.ArrayItemType;
import mobile.util.jsonparam.config.item.CanEmpty;
import mobile.util.jsonparam.config.item.Require;
import mobile.util.jsonparam.config.item.Type;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * 
 * @ClassName: JsonParam
 * @Description: Json参数规则配置类，定义顺序必须是先父级后子级，且类型必须匹配
 * @date 2014-5-20 下午6:05:38
 * @author ShenTeng
 * 
 */
public class JsonParamConfig {

    private Map<String, FieldConfig> paramConfig = new LinkedHashMap<String, FieldConfig>();
    private Integer lessRequired;

    public JsonParamConfig intField(String fieldName, Require require) {
        checkFieldName(fieldName, Type.Int);
        if (null == require) {
            throw new IllegalArgumentException("配置参数不能为空");
        }

        paramConfig.put(fieldName, new FieldConfig(require, Type.Int));

        return this;
    }

    public JsonParamConfig longField(String name, Require require) {
        checkFieldName(name, Type.Long);
        if (null == require) {
            throw new IllegalArgumentException("配置参数不能为空");
        }

        paramConfig.put(name, new FieldConfig(require, Type.Long));

        return this;
    }

    public JsonParamConfig stringField(String fieldName, Require require) {
        checkFieldName(fieldName, Type.String);
        if (null == require) {
            throw new IllegalArgumentException("配置参数不能为空");
        }

        paramConfig.put(fieldName, new FieldConfig(require, Type.String));

        return this;
    }

    /**
     * Json数组
     * 
     * @param fieldName 属性名
     * @param require 是否必须
     * @param canEmpty 是否可为空
     * @param arrayItemType 数组元素类型（所有元素只能是一种类型）
     * @return JsonParam对象
     */
    public JsonParamConfig arrayField(String fieldName, Require require, CanEmpty canEmpty, ArrayItemType arrayItemType) {
        checkFieldName(fieldName, Type.JsonArray);
        if (null == require || null == canEmpty || null == arrayItemType) {
            throw new IllegalArgumentException("配置参数不能为空");
        }

        paramConfig.put(fieldName, new FieldConfig(require, canEmpty, arrayItemType, Type.JsonArray));

        return this;
    }

    public JsonParamConfig objectField(String fieldName, Require require) {
        checkFieldName(fieldName, Type.JsonObject);
        if (null == require) {
            throw new IllegalArgumentException("配置参数不能为空");
        }

        paramConfig.put(fieldName, new FieldConfig(require, Type.JsonObject));

        return this;
    }

    public JsonParamConfig booleanField(String fieldName, Require require) {
        checkFieldName(fieldName, Type.Boolean);
        if (null == require) {
            throw new IllegalArgumentException("配置参数不能为空");
        }

        paramConfig.put(fieldName, new FieldConfig(require, Type.Boolean));

        return this;
    }

    /**
     * 最少必填字段个数。适用场景举例：参数都是非必填但是至少有一个是必填。
     * 
     * @param num 最少必填字段个数，>=0
     */
    public JsonParamConfig lessRequired(int num) {
        if (num < 0) {
            throw new IllegalArgumentException("lessRequired num must >= 1");
        }

        lessRequired = num;

        return this;
    }

    public Map<String, FieldConfig> getParamConfig() {
        return paramConfig;
    }

    public Integer getLessRequired() {
        return lessRequired;
    }

    private void checkFieldName(String fieldName, Type f) {
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("配置参数 name 不能为blank");
        }
        if (fieldName.contains("[?][?]")) {
            throw new IllegalArgumentException("字段：" + fieldName + " 定义错误，不支持多级[?]，如：a[?][?]");
        }
        if (fieldName.startsWith("[?]") || fieldName.contains(".[?]")) {
            throw new IllegalArgumentException("字段：" + fieldName + " 定义错误，不能以[?]开头或者包含.[?]，如：[?].b、a.[?]");
        }
        if (paramConfig.containsKey(fieldName)) {
            throw new IllegalArgumentException("字段：" + fieldName + " 重复定义");
        }
        if (fieldName.endsWith("[?]")) {
            throw new IllegalArgumentException("字段：" + fieldName + " 定义错误，不能以[?]结尾.使用arrayField方法定义");
        }

        // 校验父级，定义顺序必须是先父级后子级，且类型必须匹配
        String[] levels = fieldName.split("\\.");
        String currentFieldname = "";
        for (String level : levels) {
            currentFieldname += currentFieldname.equals("") ? level : ("." + level);

            if (level.contains("[?]")) {
                String rootFieldName = currentFieldname.substring(0, currentFieldname.lastIndexOf("[?]"));
                if (!paramConfig.containsKey(rootFieldName)) {
                    throw new IllegalArgumentException("字段：" + fieldName + " 定义错误，未找到字段：" + rootFieldName);
                }
                Type type = paramConfig.get(rootFieldName).get(Type.class);
                if (type != Type.JsonArray) {
                    throw new IllegalArgumentException("字段：" + fieldName + " 定义错误，字段：" + rootFieldName
                            + " 类型不为jsonArray");
                }
            } else {
                String rootFieldName = currentFieldname;
                if (!currentFieldname.equals(fieldName) && !paramConfig.containsKey(rootFieldName)) {
                    throw new IllegalArgumentException("字段：" + fieldName + " 定义错误，未找到字段：" + rootFieldName);
                }
                // 非最后一级，类型必须为JsonObject
                if (!currentFieldname.equals(fieldName)) {
                    Type type = paramConfig.get(rootFieldName).get(Type.class);
                    if (type != Type.JsonObject) {
                        throw new IllegalArgumentException("字段：" + fieldName + " 定义错误，字段：" + rootFieldName
                                + " 类型不为jsonObject");
                    }
                }
            }
        }
    }
}
