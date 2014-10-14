/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年8月1日
 */
package system.controllers;

import mobile.service.core.ClientLogService;
import mobile.vo.other.ClientLog;
import mobile.vo.result.MobilePage;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import system.vo.Page;

/**
 *
 *
 * @ClassName: MobileClientLogApp
 * @Description: 移动端客户端日志
 * @date 2014年8月1日 上午11:43:24
 * @author ShenTeng
 * 
 */
public class MobileClientLogApp extends Controller {

    @Transactional
    public static Result getByPage() {
        int pageIndex = NumberUtils.toInt(request().getQueryString("page"), 1);
        int pageSize = NumberUtils.toInt(request().getQueryString("limit"), 20);
        String device = StringUtils.defaultIfBlank(request().getQueryString("device"), "android");

        MobilePage<ClientLog> page = ClientLogService.getPage(pageIndex, pageSize, device);
        Page<ClientLog> sysPage = new Page<ClientLog>(page.getTotalRowCount(), page.getList());

        return ok(Json.toJson(sysPage));
    }

}
