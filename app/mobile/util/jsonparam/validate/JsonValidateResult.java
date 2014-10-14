/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-5-26
 */
package mobile.util.jsonparam.validate;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * 
 * @ClassName: JsonValidateResult
 * @Description: Json校验结果
 * @date 2014-5-26 下午2:28:04
 * @author ShenTeng
 * 
 */
public class JsonValidateResult {
    private JsonNode clean;
    private boolean isValid;
    private String errorMsg;

    private JsonValidateResult() {
    }

    public static JsonValidateResult error(String errorMsg) {
        JsonValidateResult result = new JsonValidateResult();
        result.isValid = false;
        result.errorMsg = errorMsg;

        return result;
    }

    public static JsonValidateResult success(JsonNode clean) {
        JsonValidateResult result = new JsonValidateResult();
        result.isValid = true;
        result.clean = clean;

        return result;
    }

    public JsonNode getClean() {
        return clean;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
