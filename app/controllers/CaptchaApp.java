/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月21日
 */
package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import utils.CaptchaUtils;
import java.io.ByteArrayOutputStream;

/**
 * @ClassName: CaptchaApp
 * @Description: 验证码
 * @date 13-10-22 下午4:18
 */
public class CaptchaApp extends Controller {

    public static Result captcha() {
        String t = request().getQueryString("t");
        ByteArrayOutputStream out = CaptchaUtils.gennerateCaptcha(t);
        response().setContentType("image/jpeg");
        return ok(out.toByteArray());
    }

    /**
     * 验证码校验
     * 
     * @return
     */
    @BodyParser.Of(BodyParser.Json.class)
    @play.db.jpa.Transactional
    public static Result validate() {
        JsonNode json = request().body().asJson();
        String t = null;
        String captcha = null;
        if (json != null) {
            captcha = json.findPath("captcha").asText();
            t = json.findPath("t").asText();
        }
        ObjectNode result = Json.newObject();
        result.put("status", CaptchaUtils.validateCaptcha(t, captcha) ? 1 : 0);
        return ok(result);
    }
}
