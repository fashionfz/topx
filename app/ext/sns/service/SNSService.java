/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-28
 */
package ext.sns.service;

import java.util.HashMap;
import java.util.Map;

import ext.sns.model.UserOAuth;
import ext.sns.openapi.DoubanOpenAPI;
import ext.sns.openapi.OpenAPIResult;
import ext.sns.openapi.QQOpenAPI;
import ext.sns.openapi.RenrenOpenAPI;
import ext.sns.openapi.SNSOpenAPI;
import ext.sns.openapi.SinaWeiboOpenAPI;
import ext.sns.openapi.TencentWeiboOpenAPI;
import ext.sns.openapi.UserInfo;

/**
 * 
 * 
 * @ClassName: SNSService
 * @Description: SNS开放接口相关服务
 * @date 2014-3-28 下午5:06:53
 * @author ShenTeng
 * 
 */
public class SNSService {

    /**
     * 开发API Map，Map<提供商名,SNSOpenAPI实现>
     */
    private static Map<String, SNSOpenAPI> openAPIMap;

    static {
        // 初始化
        openAPIMap = new HashMap<String, SNSOpenAPI>();
        openAPIMap.put("tencentWeibo", new TencentWeiboOpenAPI());
        openAPIMap.put("sinaWeibo", new SinaWeiboOpenAPI());
        openAPIMap.put("qq", new QQOpenAPI());
        openAPIMap.put("douban", new DoubanOpenAPI());
        openAPIMap.put("renren", new RenrenOpenAPI());
    }

    /**
     * 取消授权
     */
    public static void revokeAuth(UserOAuth userOAuth) {
        SNSOpenAPI snsOpenAPI = getAndValidateProvider(userOAuth.provider);
        snsOpenAPI.revokeAuth(userOAuth);
    }

    /**
     * 获取用户昵称
     */
    public static OpenAPIResult<UserInfo> getUserInfo(UserOAuth userOAuth) {
        SNSOpenAPI snsOpenAPI = getAndValidateProvider(userOAuth.provider);
        return snsOpenAPI.getUserInfo(userOAuth);
    }

    /**
     * 发布信息。不同的供应商可能会有不同。比如腾讯微博就是发送一条微博。
     * 
     * @param content 内容，不要超过140字符。
     * @return true - 发生成功， false - 发生失败
     */
    public static OpenAPIResult<Void> postMsg(UserOAuth userOAuth, String content) {
        SNSOpenAPI snsOpenAPI = getAndValidateProvider(userOAuth.provider);
        return snsOpenAPI.postMsg(userOAuth, content);
    }

    private static SNSOpenAPI getAndValidateProvider(String provider) {
        if (!openAPIMap.containsKey(provider)) {
            throw new RuntimeException("不支持该提供商。provider=" + provider);
        } else {
            return openAPIMap.get(provider);
        }
    }

}
