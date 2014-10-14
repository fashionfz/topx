/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-5-21
 */
package mobile.util.jsonparam.config;

import java.util.HashMap;
import java.util.Map;

import mobile.util.jsonparam.config.item.ConfigItem;

import org.apache.commons.lang3.ArrayUtils;

/**
 * 
 * 
 * @ClassName: Config
 * @Description: 一个字段的配置
 * @date 2014-5-21 下午2:48:55
 * @author ShenTeng
 * 
 */
public class FieldConfig {

    private Map<Class<? extends ConfigItem>, ConfigItem> config = new HashMap<Class<? extends ConfigItem>, ConfigItem>();

    public FieldConfig(ConfigItem... values) {
        if (ArrayUtils.isNotEmpty(values)) {
            for (ConfigItem t : values) {
                set(t);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ConfigItem> T get(Class<T> clz) {
        return (T) config.get(clz);
    }

    public <T extends ConfigItem> void set(T value) {
        if (null == value) {
            throw new IllegalArgumentException("value can't be null.");
        }
        config.put(value.getClass(), value);
    }

}
