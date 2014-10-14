/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-20
 */
package mobile.service;

import java.util.ArrayList;
import java.util.List;

import mobile.service.core.IntroImgService;
import mobile.service.result.ServiceVOResult;
import mobile.vo.other.IntroImg;
import mobile.vo.result.CommonVO;
import models.SkillTag;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import play.cache.Cache;
import play.libs.Json;

import common.Constants;

import controllers.ExpertApp;
import controllers.base.ObjectNodeResult;
import ext.config.ConfigFactory;

/**
 * 
 * 
 * @ClassName: MobileService
 * @Description: 移动端服务
 * @date 2014-3-20 下午4:25:39
 * @author ShenTeng
 * 
 */
public class MobileService {

    private static final int INTRO_IMG_CACHE_EXPIRATION = 24 * 60 * 60;

    /**
     * 获取介绍图CDN路径
     * 
     * @param from 请求来源
     * @return 介绍图CDN路径集合，不为null
     */
    public static List<IntroImg> getIntroImgList(String from) {
        List<IntroImg> list = new ArrayList<IntroImg>();

        String cacheKey = Constants.CACHE_INDUSTRY_IMG + from;
        @SuppressWarnings("unchecked")
        List<IntroImg> imgList = (List<IntroImg>) Cache.get(cacheKey);

        if (CollectionUtils.isEmpty(imgList)) {
            List<mobile.model.IntroImg> listPo = IntroImgService.getByFrom(from);
            imgList = new ArrayList<>();
            for (mobile.model.IntroImg po : listPo) {
                imgList.add(IntroImg.create(po));
            }
            Cache.set(cacheKey, imgList, INTRO_IMG_CACHE_EXPIRATION);
        }
        list.addAll(imgList);

        return list;
    }

    public static void removeIntroImgCache(String from) {
        String cacheKey = Constants.CACHE_INDUSTRY_IMG + from;
        Cache.remove(cacheKey);
    }

    /**
     * 获取热门搜索关键字
     * 
     * @return
     */
    public static ServiceVOResult<CommonVO> getHotKeyword() {
        String[] keywords = ExpertApp.queryKeywords();

        CommonVO vo = CommonVO.create();
        vo.set("keywords", Json.toJson(keywords));

        ServiceVOResult<CommonVO> success = ServiceVOResult.success();
        success.setVo(vo);

        return success;
    }

    /**
     * 获取客户端的版本
     * 
     * @return
     */
    public static ServiceVOResult<CommonVO> getClientVersion() {
        String value = ConfigFactory.getString(Constants.APK_VERSION_INFO);
        if (StringUtils.isEmpty(value)) {
            ObjectNodeResult result = new ObjectNodeResult();
            result.errorkey("mobile.apk.version.nofounddata");
            return ServiceVOResult.create(result);
        }
        String[] args = value.split(",");
        if (args.length >= 3) {
            String version = args[0].trim();
            String versionName = args[1].trim();
            String downloadURL = args[2].trim();

            CommonVO vo = CommonVO.create();
            vo.set("version", version);
            vo.set("versionName", versionName);
            vo.set("downloadURL", downloadURL);

            return ServiceVOResult.success(vo);
        } else {
            ObjectNodeResult result = new ObjectNodeResult();
            result.errorkey("mobile.apk.version.nofounddata");
            return ServiceVOResult.create(result);
        }
    }

    /**
     * 获得热门国家列表
     */
    public static ServiceVOResult<CommonVO> getHotCountryList() {
        List<String> countryList = SkillTag.getCountryNameWithCache();

        CommonVO vo = CommonVO.create();
        vo.set("list", Json.toJson(countryList));

        return ServiceVOResult.success(vo);
    }

}
