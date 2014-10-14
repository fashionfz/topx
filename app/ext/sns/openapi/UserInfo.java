/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-11
 */
package ext.sns.openapi;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * 
 * @ClassName: UserInfo
 * @Description: SNS通用用户信息对象
 * @date 2014-6-11 下午3:27:26
 * @author ShenTeng
 * 
 */
public class UserInfo {

    /**
     * 用户昵称
     */
    private String nickName;
    private String openId;

    /**
     * 用户头像URL，取能取到的最大头像
     */
    private String avatarUrl;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        String replace = StringUtils.replace(avatarUrl, "\\/", "/");
        this.avatarUrl = replace;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

}
