/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-5-26
 */
package mobile.util.jsonparam.validate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import mobile.util.jsonparam.JsonParamConfig;
import mobile.util.jsonparam.config.FieldConfig;
import mobile.util.jsonparam.config.item.ArrayItemType;
import mobile.util.jsonparam.config.item.CanEmpty;
import mobile.util.jsonparam.config.item.Require;
import mobile.util.jsonparam.config.item.Type;

import org.springframework.util.CollectionUtils;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * @ClassName: JsonValidator
 * @Description: 根据JsonConfig对Json进行校验
 * @date 2014-5-26 下午2:27:31
 * @author ShenTeng
 * 
 */
public class JsonValidator {

    public static JsonValidateResult validateAndClean(JsonNode raw, JsonParamConfig config) {
        if (null == raw || !raw.isObject()) {
            return JsonValidateResult.error("Json参数不是Json对象。请确保Json参数合法，并且正确设置请求头的Content-Type为application/json");
        }

        // 检查原始Json并生成clean Json
        ObjectNode clean = Json.newObject();
        int existCount = 0;
        for (Entry<String, FieldConfig> e : config.getParamConfig().entrySet()) {
            String fieldName = e.getKey();
            FieldConfig fieldConfig = e.getValue();

            ValidateResult validateResult = validateAndCleanField(raw, clean, fieldName, fieldConfig);

            if (!validateResult.isValid) {
                return JsonValidateResult.error(validateResult.getErrorMsg());
            } else if (validateResult.getIsExist()) {
                existCount++;
            }
        }

        Integer lessRequired = config.getLessRequired();
        if (null != lessRequired && existCount < lessRequired) {
            return JsonValidateResult.error("必填字段不足。至少有" + lessRequired + "个字段必填");
        }

        return JsonValidateResult.success(clean);
    }

    private static ValidateResult validateAndCleanField(JsonNode raw, ObjectNode clean, String fieldName,
            FieldConfig fieldConfig) {
        Type type = fieldConfig.get(Type.class);
        Require require = fieldConfig.get(Require.class);

        // 获取所有直接父级
        List<JsonNode> directPararentList = getDirectPararentsByFieldName(raw, fieldName);
        List<JsonNode> directCleanPararentList = getDirectPararentsByFieldName(clean, fieldName);
        String currentFieldName = getCurrentFieldName(fieldName);

        if (CollectionUtils.isEmpty(directPararentList)) {
            return ValidateResult.success().setIsExist(false);
        }

        boolean isExist = false;
        for (int i = 0; i < directPararentList.size(); ++i) {
            JsonNode parentNode = directPararentList.get(i);
            JsonNode parentCleanNode = directCleanPararentList.get(i);

            if (require == Require.Yes && !parentNode.hasNonNull(currentFieldName)) {
                return ValidateResult.error("未找到字段：" + fieldName + "，字段定义为必填");
            }
            if (parentNode.hasNonNull(currentFieldName)) {
                isExist = true;
                JsonNode currentNode = parentNode.get(currentFieldName);

                // 类型校验
                ValidateResult fieldTypeResult = validateFieldType(fieldName, type, currentNode);
                if (!fieldTypeResult.isValid()) {
                    return fieldTypeResult;
                }
                cleanField(currentNode, (ObjectNode) parentCleanNode, currentFieldName, type);

                // 特殊校验
                if (type == Type.JsonArray) {
                    ArrayNode currentArrayNode = (ArrayNode) currentNode;
                    ArrayNode currentCleanArrayNode = (ArrayNode) parentCleanNode.get(currentFieldName);
                    CanEmpty canEmpty = fieldConfig.get(CanEmpty.class);
                    ArrayItemType arrayItemType = fieldConfig.get(ArrayItemType.class);
                    // 数组非空校验
                    if (canEmpty == CanEmpty.No && currentArrayNode.size() <= 0) {
                        return ValidateResult.error("数组字段：" + fieldName + " 不能为空数组");
                    }
                    // 数组元素类型校验
                    Iterator<JsonNode> elements = currentArrayNode.elements();
                    while (elements.hasNext()) {
                        JsonNode item = elements.next();
                        // 类型校验
                        ValidateResult arrayItemTypeResult = validateArrayItemType(fieldName, arrayItemType, item);
                        if (!arrayItemTypeResult.isValid()) {
                            return arrayItemTypeResult;
                        }
                        cleanArrayItem(item, currentCleanArrayNode, arrayItemType);
                    }
                }
            }
        }

        return ValidateResult.success().setIsExist(isExist);
    }

    /**
     * 根据fieldName获取直接父级
     * 
     * @param root 根JsonNode
     * @param fieldName
     */
    private static List<JsonNode> getDirectPararentsByFieldName(JsonNode root, String fieldName) {
        List<JsonNode> directPararentList = new LinkedList<>();
        directPararentList.add(root);

        // 规范化处理fieldName
        fieldName = normalizeFieldName(fieldName);

        String[] levels = fieldName.split("\\.");
        if (levels.length == 1 && !levels[0].equals("[?]")) {
            return directPararentList;
        }

        String parentFieldname = "";
        // 取全部直接父级
        for (int i = 0; i < levels.length - 1; ++i) {
            String level = levels[i];

            ListIterator<JsonNode> parentIterator = directPararentList.listIterator();
            while (parentIterator.hasNext()) {
                JsonNode parentNode = parentIterator.next();

                if (level.equals("[?]")) {
                    if (!parentNode.isArray()) {
                        throw new RuntimeException("参数配置错误。字段:" + fieldName + " 中的字段：" + parentFieldname
                                + " 不是jsonArray");
                    }

                    // 取出该层字段，删除父层字段
                    parentIterator.remove();
                    Iterator<JsonNode> elements = parentNode.elements();
                    while (elements.hasNext()) {
                        parentIterator.add(elements.next());
                    }
                } else {
                    if (!parentNode.isObject()) {
                        throw new RuntimeException("参数配置错误。字段:" + fieldName + " 中的字段：" + parentFieldname
                                + " 不是jsonObject");
                    }
                    if (parentNode.hasNonNull(level)) {
                        parentIterator.set(parentNode.get(level));
                    } else {
                        parentIterator.remove();
                    }
                }
            }

            if (CollectionUtils.isEmpty(directPararentList)) {
                return directPararentList;
            }
            parentFieldname += level.equals("[?]") ? level : (parentFieldname.equals("") ? level : ("." + level));
        }

        return directPararentList;
    }

    private static String getCurrentFieldName(String fieldName) {
        // 规范化处理fieldName
        fieldName = normalizeFieldName(fieldName);
        String[] levels = fieldName.split("\\.");

        return levels[levels.length - 1];
    }

    private static ValidateResult validateFieldType(String fieldName, Type type, JsonNode currentNode) {
        switch (type) {
        case JsonArray:
            if (!currentNode.isArray()) {
                return ValidateResult.error("字段：" + fieldName + " 类型错误，字段类型定义为jsonArray");
            }
            break;
        case Boolean:
            if (!currentNode.isBoolean()) {
                return ValidateResult.error("字段：" + fieldName + " 类型错误，字段类型定义为boolean");
            }
            break;
        case Int:
            if (!currentNode.isInt()) {
                return ValidateResult.error("字段：" + fieldName + " 类型错误，字段类型定义为int");
            }
            break;
        case Long:
            if (!currentNode.isLong() && !currentNode.isInt()) {
                return ValidateResult.error("字段：" + fieldName + " 类型错误，字段类型定义为long");
            }
            break;
        case JsonObject:
            if (!currentNode.isObject()) {
                return ValidateResult.error("字段：" + fieldName + " 类型错误，字段类型定义为jsonObject");
            }
            break;
        case String:
            if (!currentNode.isTextual()) {
                return ValidateResult.error("字段：" + fieldName + " 类型错误，字段类型定义为string");
            }
            break;
        default:
            return ValidateResult.error("未知字段类型。字段：" + fieldName);
        }

        return ValidateResult.success();
    }

    private static void cleanField(JsonNode currentNode, ObjectNode parentCleanNode, String currentFieldName, Type type) {
        switch (type) {
        case JsonArray:
            parentCleanNode.set(currentFieldName, Json.newObject().arrayNode());
            break;
        case JsonObject:
            parentCleanNode.set(currentFieldName, Json.newObject());
            break;
        case Boolean:
        case Int:
        case Long:
        case String:
            parentCleanNode.set(currentFieldName, currentNode.deepCopy());
            break;
        }
    }

    private static ValidateResult validateArrayItemType(String fieldName, ArrayItemType type, JsonNode jsonNode) {
        switch (type) {
        case Boolean:
            if (!jsonNode.isBoolean()) {
                return ValidateResult.error("数组字段：" + fieldName + " 包含的元素类型错误，元素类型定义为boolean");
            }
            break;
        case Int:
            if (!jsonNode.isInt()) {
                return ValidateResult.error("数组字段：" + fieldName + " 包含的元素类型错误，元素类型定义为int");
            }
            break;
        case Long:
            if (!jsonNode.isLong() && !jsonNode.isInt()) {
                return ValidateResult.error("数组字段：" + fieldName + " 包含的元素类型错误，元素类型定义为long");
            }
            break;
        case JsonObject:
            if (!jsonNode.isObject()) {
                return ValidateResult.error("数组字段：" + fieldName + " 包含的元素类型错误，元素类型定义为jsonObject");
            }
            break;
        case String:
            if (!jsonNode.isTextual()) {
                return ValidateResult.error("数组字段：" + fieldName + " 包含的元素类型错误，元素类型定义为string");
            }
            break;
        default:
            return ValidateResult.error("数组字段：" + fieldName + " 元素类型未知");
        }

        return ValidateResult.success();
    }

    private static void cleanArrayItem(JsonNode item, ArrayNode currentCleanArrayNode, ArrayItemType arrayItemType) {
        switch (arrayItemType) {
        case Boolean:
        case Int:
        case Long:
        case String:
            currentCleanArrayNode.add(item.deepCopy());
            break;
        case JsonObject:
            currentCleanArrayNode.add(Json.newObject());
            break;

        }
    }

    private static String normalizeFieldName(String fieldName) {
        return fieldName.replace(".[?]", "[?]").replace("[?]", ".[?]");
    }

    /**
     * 字段校验结果
     */
    private static class ValidateResult {
        private boolean isValid;
        private String errorMsg;
        private Boolean isExist;

        private ValidateResult() {
        }

        public static ValidateResult success() {
            ValidateResult result = new ValidateResult();
            result.isValid = true;
            return result;
        }

        public static ValidateResult error(String errorMsg) {
            ValidateResult result = new ValidateResult();
            result.isValid = false;
            result.errorMsg = errorMsg;
            return result;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public Boolean getIsExist() {
            return isExist;
        }

        public ValidateResult setIsExist(Boolean isExist) {
            this.isExist = isExist;
            return this;
        }

    }

}
