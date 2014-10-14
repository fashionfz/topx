/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-26
 */
package mobile.controllers;

import mobile.util.MobileResults;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * 
 * @ClassName: BaseApp
 * @Description: 移动服务端Controller基类
 * @date 2013-12-26 下午6:30:49
 * @author ShenTeng
 * 
 */
public class MobileBaseApp extends Controller {

    public static Result systemError() {
        return MobileResults.systemError().getResult();
    }

    /**
     * 参数非法
     */
    public static Result illegalParameters() {
        return MobileResults.illegalParameters().getResult();
    }

    public static Result illegalParameters(String errorMsg) {
        return MobileResults.illegalParameters(errorMsg).getResult();
    }

    public static JsonNode getJson() {
        JsonNode json = request().body().asJson();
        return json;
    }

}
