/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-10-29
 */
package ext.usercenter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * 
 * @ClassName: UCUser
 * @Description: 用户中心用户实体
 * @date 2013-10-29 下午4:29:02
 * @author ShenTeng
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UCUser {
    public String email;

    public String phoneNumber;

    public String realname;

    public String uid;

    public String username;

    public String userpassword;

    public boolean isable;

    /**
     * 登录成功后的随机码，这个随机码将用户后续用户登录状态的检查
     */
    public String token;

    @Override
    public String toString() {
        return "UCUser [email=" + email + ", phoneNumber=" + phoneNumber + ", realname=" + realname + ", uid=" + uid
                + ", username=" + username + ", userpassword=" + userpassword + ", isable=" + isable + ", token="
                + token + "]";
    }

}
