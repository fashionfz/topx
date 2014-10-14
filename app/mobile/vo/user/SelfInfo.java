/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-4-22
 */
package mobile.vo.user;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

import mobile.vo.MobileVO;
import models.User;

/**
 * 
 * 
 * @ClassName: SelfInfo
 * @Description: 个人信息
 * @date 2014-4-22 下午5:12:06
 * @author ShenTeng
 * 
 */
public class SelfInfo implements MobileVO {

    /** 用户邮箱 */
    private String email;
    /** 手机号 **/
    private String phoneNumber;
    /** 用户token **/
    private String token;

    public static SelfInfo create(User user, String token) {

        SelfInfo selfInfo = new SelfInfo();

        selfInfo.setEmail(user.email);
        selfInfo.setPhoneNumber(user.phoneNumber);
        selfInfo.setToken(token);

        return selfInfo;
    }

    @Override
    public JsonNode toJson() {
        return Json.toJson(this);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
