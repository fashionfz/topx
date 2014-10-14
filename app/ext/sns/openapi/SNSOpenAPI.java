package ext.sns.openapi;

import ext.sns.model.UserOAuth;

/**
 * 
 * 
 * 
 * @ClassName: SNSOpenAPI
 * @Description: 第三方SNS提供的开放API
 * @date 2014-3-28 下午4:58:06
 * @author ShenTeng
 * 
 */
public interface SNSOpenAPI {

    /**
     * 取消授权
     */
    OpenAPIResult<Void> revokeAuth(UserOAuth userOAuth);

    /**
     * 获取用户信息
     */
    OpenAPIResult<UserInfo> getUserInfo(UserOAuth userOAuth);

    /**
     * 发布信息。不同的供应商可能会有不同。比如腾讯微博就是发送一条微博。
     * 
     * @param content 内容，不要超过140字符。
     * @return true - 发生成功， false - 发生失败
     */
    OpenAPIResult<Void> postMsg(UserOAuth userOAuth, String content);

}
