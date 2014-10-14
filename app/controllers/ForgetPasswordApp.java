package controllers;

import models.service.ForgetPasswordService;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.base.ObjectNodeResult;

/**
 * 
 * 
 * @ClassName: ForgetPasswordApp
 * @Description: 忘记密码,重置密码
 * @date 2013-10-24 上午9:57:22
 * @author YangXuelin
 * 
 */
public class ForgetPasswordApp extends Controller {

    /**
     * 接收用户输入的邮箱
     * 
     * @return
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public static Result receiveAddress() {
        JsonNode json = request().body().asJson();
        // 获取用户输入的邮箱
        final String email = json.findPath("email").asText();
        // 获取用户输入的验证码
        String captcha = json.findPath("captcha").asText();
        String t = json.findPath("t").asText();
        int resend = json.findPath("reSend").asInt(0);
        boolean isFirstSend = true;
        if(resend == 1) {
        	isFirstSend = false;
        }
        play.Logger.debug("用户输入的邮箱: " + email);
        ObjectNodeResult objectNodeResult = ForgetPasswordService.sendForgetPasswordEmail(t, email, captcha,
                true, isFirstSend);
        return ok(objectNodeResult.getObjectNode());
    }

    /**
     * 重置密码
     * 
     * @return
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public static Result resetPassword() {
        JsonNode json = request().body().asJson();
        // 获取用户的新密码
        String pwd = json.findPath("password").asText();
        // 获取用户的邮箱
        String email = json.findPath("email").asText();
        // 获取用户的加密串
        String code = json.findPath("code").asText();
        ObjectNode result = ForgetPasswordService.resetPassword(session(), pwd, email, code);
        return ok(result);
    }

}
