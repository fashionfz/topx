/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-11
 */
package ext.sns.service;

import org.apache.commons.lang3.StringUtils;

import ext.sns.config.ConfigManager;
import ext.sns.config.ProviderConfig;

/**
 * 
 * 
 * @ClassName: ProviderType
 * @Description: SNS提供商类型
 * @date 2014-6-11 下午6:33:28
 * @author ShenTeng
 * 
 */
public class ProviderType {
    public static final String SNS = "SNS";
    public static final String LOGIN = "LOGIN";

    public static boolean validateType(String type) {
        if (StringUtils.isBlank(type)) {
            return false;
        }

        if (type.equals(SNS) || type.equals(LOGIN)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validateType(String provider, String type) {
        if (StringUtils.isBlank(provider) || !validateType(type)) {
            return false;
        }

        ProviderConfig providerConfig = ConfigManager.getProviderConfigByName(provider);
        if (null == providerConfig) {
            return false;
        }

        return providerConfig.getTypes().contains(type);
    }
    
    public static boolean isNeedLogin(String type) {
        return type.equals(SNS);
    }
}
