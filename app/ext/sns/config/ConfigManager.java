/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-25
 */
package ext.sns.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import play.libs.Yaml;
import ext.sns.auth.AuthResponse;

/**
 * 
 * 
 * @ClassName: ConfigManager
 * @Description: SNS配置管理器
 * @date 2014-3-25 下午7:32:25
 * @author ShenTeng
 * 
 */
public class ConfigManager {

    /** SNS配置对象 */
    private static SNSConfig snsConfig;

    /** Map<提供商名,提供商配置对象> */
    private static Map<String, ProviderConfig> providerConfigMap;

    public static final String TYPE_KEY = "!type!";

    /**
     * 加载配置
     */
    public static void loadConfig() {
        snsConfig = (SNSConfig) Yaml.load("snsConfig.yml");
        // 加载特殊的配置类，目前还没有

        providerConfigMap = new HashMap<String, ProviderConfig>();
        for (ProviderConfig providerConfig : snsConfig.getProviderConfigList()) {
            providerConfigMap.put(providerConfig.getName(), providerConfig);
        }
    }

    /**
     * 获取指定类型的提供商名字
     * 
     * @param typeList 提供商类型集合
     * 
     * @return 提供商名字集合
     */
    public static List<String> getProviderNameByTypes(List<String> typeList) {
        return getProviderNameByTypes(typeList.toArray(new String[0]));
    }

    /**
     * 检查提供商名字是否存在
     * 
     * @param providerName 提供商名字
     * @return
     */
    public static boolean checkProviderName(String providerName) {
        return providerConfigMap.containsKey(providerName);
    }

    /**
     * 获取指定类型的提供商名字
     * 
     * @param types 提供商类型集合
     * 
     * @return 提供商名字集合
     */
    public static List<String> getProviderNameByTypes(String... types) {
        Set<String> keySet = new HashSet<>();
        for (Entry<String, ProviderConfig> e : providerConfigMap.entrySet()) {
            for (String type : e.getValue().getTypes()) {
                if (ArrayUtils.contains(types, type)) {
                    keySet.add(e.getKey());
                    break;
                }
            }
        }
        return new ArrayList<>(keySet);
    }

    /**
     * SNS配置对象
     * 
     * @return SNS配置对象对象
     */
    public static SNSConfig getSNSConfig() {
        return snsConfig;
    }

    /**
     * 根据提供商名称获取提供商配置
     * 
     * @param providerName 提供商名称
     * @return 提供商配置对象
     */
    public static ProviderConfig getProviderConfigByName(String providerName) {
        return providerConfigMap.get(providerName);
    }

    /**
     * 根据授权请求响应获取提供商配置
     * 
     * @param authResponse 授权请求响应
     * @return 提供商配置对象。null - 无对应提供商
     */
    public static ProviderConfig getProviderConfigByAuthResponse(AuthResponse authResponse) {
        for (ProviderConfig cfg : providerConfigMap.values()) {
            if (cfg.isMatchAuthResponse(authResponse)) {
                return cfg;
            }
        }

        return null;
    }

}
