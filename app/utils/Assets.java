/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package utils;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.attachment.AttachmentApp;
import ext.config.ConfigFactory;
import play.Logger;
import play.Play;
import play.Logger.ALogger;
import play.libs.Json;

/**
 * @author ZhouChun
 * @ClassName: Assets
 * @Description: 静态资源访问地址
 * @date 13-11-28 下午12:32
 */
public class Assets {

    private static final ALogger LOGGER = Logger.of(Assets.class);

    private static final String ASSETS;

    static {
        ASSETS = Play.application().configuration().getString(AttachmentApp.UPLOAD_URL);
    }

    public static String at(String src) {
        if (StringUtils.isBlank(src)) {
            return src;
        }
        return ASSETS + src;
    }

    public static String getAssets() {
        return ASSETS;
    }

    public static String getDefaultAvatar() {
        return at("topx/assets/misc/images/dev-head-default1.png");
    }

    /**
     * @param isRelation 非全路径，关系路径
     * @return
     */
    public static String getDefaultGroupHeadUrl(boolean isRelation) {
		if (isRelation) {
    		return "topx/assets/misc/images/group-default190x190.png";
    	}
        return at("topx/assets/misc/images/group-default190x190.png");
    }
    
    public static String getServiceDefaultAvatar() {
        return at("topx/assets/misc/images/default-services-171x129.jpg");
    }

    /**
     * 获取群组背景
     * 
     * @param industryId 行业Id
     * @param isRelation 非全路径，关系路径
     * @return
     */
    public static String getDefaultGroupBackgroundUrl(Long industryId,boolean isRelation) {
        String cfg = ConfigFactory.getString("group.background.default");
        if (null == industryId || StringUtils.isBlank(cfg)) {
            return null;
        }

        JsonNode cfgJson = null;
        try {
            cfgJson = Json.parse(cfg);
        } catch (Exception e) {
            LOGGER.error("解析group.background.default配置JSON错误", e);
            return null;
        }

        String cfgPath = cfgJson.path(industryId.toString()).asText();
        if (StringUtils.isBlank(cfgPath)) {
            return null;
        } else {
			if (isRelation) {
				return cfgPath;
			}
            return at(cfgPath);
        }
    }
}
