/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package controllers.base;

import java.util.Calendar;

import models.Expert;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.common.error;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author ZhouChun
 * @ClassName: BaseApp
 * @Description: 控制器基类
 * @date 13-10-29 下午5:54
 */
public class BaseApp extends Controller {

    /**
     * 404
     * 
     * @return
     */
    /*
     * public static Result PageNotFound() { return notFound(views.html.common.notFound.render()); }
     */

    /**
     * 
     * @return
     */
    public static Result MissingParameters() {
        String error = "缺少必需的参数!";
        return ok((new ObjectNodeResult(ObjectNodeResult.STATUS_FAILED)).error(error).getObjectNode());
    }

    /**
     * 
     * @return
     */
    public static Result illegalParameters() {
        String error = "参数非法!";
        return ok((new ObjectNodeResult(ObjectNodeResult.STATUS_FAILED)).error(error).getObjectNode());
    }

    /**
     * 获取前端ajax提交上来的json数据
     * 
     * @return
     */
    public static JsonNode getJson() {
        JsonNode json = request().body().asJson();
        return json;
    }

    /**
     * 默认错误信息页面。
     */
    public static Result errorInfo() {
        return errorInfo(null);
    }

    /**
     * 错误信息页面
     * 
     * @param errorInfo 错误信息
     */
    public static Result errorInfo(String errorInfo) {
        return errorInfo(errorInfo, null, null);
    }
    
    /**
     * 错误信息页面
     * 
     * @param errorInfo 错误信息
     * @param backUrl 后退一步地址
     */
    public static Result errorInfo(String errorInfo, String title) {
        return ok(error.render(errorInfo, null, title));
    }

    /**
     * 错误信息页面
     * 
     * @param errorInfo 错误信息
     * @param backUrl 后退一步地址
     */
    public static Result errorInfo(String errorInfo, String backUrl, String title) {
        return ok(error.render(errorInfo, backUrl, title));
    }

    /**
     * 重定向到主页
     * 
     * @return
     */
    public static Result redirectIndex() {
        return redirect(controllers.routes.Application.index());
    }

    /**
     * 获取当前用户的时区偏移量
     * 
     * @return
     */
    public static int getCurrentUserTimezone() {
        User user = User.getFromSession(session());
        if (user != null) {
            Expert expert = Expert.getExpertByUserId(user.getId());
            if (expert != null) {
                return expert.getTimezoneOffset(session());
            }
        }
        return Calendar.getInstance().getTimeZone().getRawOffset() / 1000 / 60;
    }

}
