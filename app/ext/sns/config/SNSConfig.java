/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-25
 */
package ext.sns.config;

import java.util.List;

/**
 * 
 * 
 * @ClassName: SNSConfig
 * @Description: SNS的配置文件
 * @date 2014-3-25 下午7:07:34
 * @author ShenTeng
 * 
 */
public class SNSConfig {

    /** 授权重定向URI */
    private String authRedirectUri = "";

    /** SNS服务提供商配置集合 */
    private List<ProviderConfig> providerConfigList;

    public String getAuthRedirectUri() {
        return authRedirectUri;
    }

    public void setAuthRedirectUri(String authRedirectUri) {
        this.authRedirectUri = authRedirectUri;
    }

    public List<ProviderConfig> getProviderConfigList() {
        return providerConfigList;
    }

    public void setProviderConfigList(List<ProviderConfig> providerConfigList) {
        this.providerConfigList = providerConfigList;
    }

}
