/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-5-23
 */
package models.service;

import models.User;

import org.apache.commons.lang3.StringUtils;

import play.mvc.Http.Request;
import play.mvc.Http.Session;
import play.mvc.Results;
import play.mvc.SimpleResult;
import controllers.base.ObjectNodeResult;

/**
 * 
 * 
 * @ClassName: CurrentUserFilter
 * @Description: 确保当前请求属于当前用户的过滤器
 * @date 2014-5-23 下午5:23:04
 * @author ShenTeng
 * 
 */
public class CurrentUserFilter {

    /**
     * 确保当前请求属于当前用户的过滤器
     * 
     * @return play的结果对象 null代表不进行拦截，正常进行后续处理
     */
    public static SimpleResult filter(Session session, Request request) {
        User user = User.getFromSession(session);
        if (null != user) {
            String currentUid = request.getHeader("currentUid");
            if (StringUtils.isNotBlank(currentUid) && !currentUid.equals(user.id.toString())) {
                return Results.ok(new ObjectNodeResult().error("页面已过期，请重新刷新页面。").getObjectNode());
            }
        }

        return null;
    }

}
