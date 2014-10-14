/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-31
 */
package mobile.controllers;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import mobile.service.MobileService;
import mobile.service.core.ClientLogService;
import mobile.service.result.ServiceVOResult;
import mobile.vo.other.IntroImg;
import mobile.vo.result.CommonVO;
import mobile.vo.result.MobileResult;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * @ClassName: MobileApp
 * @Description: 移动端使用的部分接口
 * @date 2013-12-31 下午6:13:53
 * @author ShenTeng
 * 
 */
public class MobileApp extends MobileBaseApp {

    private static final ALogger LOGGER = Logger.of(MobileApp.class);

    /**
     * 获得首页接受图片
     */
    @Transactional
    public static Result getIntroImgs(String from) {
        List<IntroImg> list = MobileService.getIntroImgList(from);

        MobileResult result = MobileResult.success();
        result.setToField("introImgList", list);

        // shenteng 兼容旧接口定义，未来删除
        ObjectNode oldNode = Json.newObject();
        ArrayNode array = Json.newObject().arrayNode();
        for (IntroImg introImg : list) {
            array.add(introImg.getImgUrl());
        }
        oldNode.set("imgs", array);
        result.setObjectNode(oldNode);

        return result.getResult();
    }

    @Transactional
    public static Result getHotKeywords(String from) {
        ServiceVOResult<CommonVO> result = MobileService.getHotKeyword();
        return MobileResult.success().setToRoot(result).getResult();
    }

    /**
     * 获取 APK升级版本信息（包含version,versionName,应用下载地址）
     */
    @Transactional
    public static Result getClientVersion(String from) {
        ServiceVOResult<CommonVO> serviceVOResult = MobileService.getClientVersion();
        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @BodyParser.Of(value = BodyParser.Raw.class, maxLength = 5 * 1024 * 1024)
    public static Result uploadLog(String from, String comment) {
        if (request().body().isMaxSizeExceeded()) {
            return MobileResult.error("290001", "日志文件不能超过5M").getResult();
        }

        if (StringUtils.isNotBlank(comment)) {
            try {
                comment = URLDecoder.decode(comment, "utf-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("url解码异常", e);
            }
        }

        File file = request().body().asRaw().asFile();
        if (null == file || file.length() <= 0) {
            return MobileResult.error("100005", "日志文件大小必须大于0").getResult();
        }

        ClientLogService.uploadLog(from, file, comment);

        return MobileResult.success().getResult();
    }

    @Transactional
    public static Result getHotCountryList(String from) {
        ServiceVOResult<CommonVO> serviceVOResult = MobileService.getHotCountryList();
        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

}
