/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年8月28日
 */
package mobile.controllers;

import java.io.File;

import mobile.service.RNSService;
import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.util.jsonparam.JsonParam;
import mobile.util.jsonparam.JsonParamConfig;
import mobile.util.jsonparam.config.item.ArrayItemType;
import mobile.util.jsonparam.config.item.CanEmpty;
import mobile.util.jsonparam.config.item.Require;
import mobile.vo.result.CommonVO;
import mobile.vo.result.MobilePage;
import mobile.vo.result.MobileResult;
import mobile.vo.rns.RequireDetailVO;
import mobile.vo.rns.RequireVO;
import mobile.vo.rns.ServiceDetailVO;
import mobile.vo.rns.ServiceVO;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Result;

/**
 *
 *
 * @ClassName: RNSApp
 * @Description: 需求服务App
 * @date 2014年8月28日 下午6:24:57
 * @author ShenTeng
 * 
 */
public class RNSApp extends MobileBaseApp {

    @Transactional
    public static Result createRequire(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("title", Require.Yes);
        config.longField("industryId", Require.Yes);
        config.stringField("info", Require.No);
        config.stringField("budget", Require.Yes);
        config.arrayField("skillsTags", Require.Yes, CanEmpty.No, ArrayItemType.String);
        config.arrayField("attachs", Require.No, CanEmpty.No, ArrayItemType.Long);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = RNSService.createRequire(param.getString("title"),
                param.getLong("industryId"), param.getString("info"), param.getString("budget"),
                param.getStringList("skillsTags"), param.getLongList("attachs"));

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @Transactional
    public static Result createService(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("title", Require.Yes);
        config.longField("industryId", Require.Yes);
        config.stringField("info", Require.No);
        config.stringField("price", Require.Yes);
        config.arrayField("skillsTags", Require.Yes, CanEmpty.No, ArrayItemType.String);
        config.arrayField("attachs", Require.No, CanEmpty.No, ArrayItemType.Long);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = RNSService.createService(param.getString("title"),
                param.getLong("industryId"), param.getString("info"), param.getString("price"),
                param.getStringList("skillsTags"), param.getLongList("attachs"));

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @Transactional
    public static Result updateRequire(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("requireId", Require.Yes);
        config.stringField("title", Require.No);
        config.longField("industryId", Require.No);
        config.stringField("info", Require.No);
        config.stringField("budget", Require.No);
        config.arrayField("skillsTags", Require.No, CanEmpty.No, ArrayItemType.String);
        config.arrayField("attachs", Require.No, CanEmpty.No, ArrayItemType.Long);
        config.lessRequired(2);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = RNSService.updateRequire(param.getLong("requireId"), param.getString("title"),
                param.getLong("industryId"), param.getString("info"), param.getString("budget"),
                param.getStringList("skillsTags"), param.getLongList("attachs"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result updateService(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("serviceId", Require.Yes);
        config.stringField("title", Require.No);
        config.longField("industryId", Require.No);
        config.stringField("info", Require.No);
        config.stringField("price", Require.No);
        config.arrayField("skillsTags", Require.No, CanEmpty.No, ArrayItemType.String);
        config.arrayField("attachs", Require.No, CanEmpty.No, ArrayItemType.Long);
        config.lessRequired(2);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = RNSService.updateService(param.getLong("serviceId"), param.getString("title"),
                param.getLong("industryId"), param.getString("info"), param.getString("price"),
                param.getStringList("skillsTags"), param.getLongList("attachs"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    @BodyParser.Of(value = BodyParser.Raw.class, maxLength = 2 * 1024 * 1024)
    public static Result uploadServicePic(String from, Long serviceId) {
        if (serviceId == -1) {
            serviceId = null;
        }
        // 文件大小不能超过2M
        if (request().body().isMaxSizeExceeded()) {
            return MobileResult.error("100005", "图片大小不能超过2M").getResult();
        }

        File file = request().body().asRaw().asFile();
        if (null == file || file.length() <= 0) {
            return MobileResult.error("100005", "图片大小必须大于0").getResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = RNSService.uploadServicePic(serviceId, file);

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @Transactional
    @BodyParser.Of(value = BodyParser.Raw.class, maxLength = 2 * 1024 * 1024)
    public static Result updateRequireAttachment(String from, Long requireId, String filename) {
        if (requireId == -1) {
            requireId = null;
        }
        // 文件大小不能超过2M
        if (request().body().isMaxSizeExceeded()) {
            return MobileResult.error("100005", "文件大小不能超过2M").getResult();
        }

        File file = request().body().asRaw().asFile();
        if (null == file || file.length() <= 0) {
            return MobileResult.error("100005", "文件大小必须大于0").getResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = RNSService.updateRequireAttachment(requireId, file, filename);

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @Transactional
    public static Result deleteMyRequire(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("requireId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = RNSService.deleteMyRequire(param.getLong("requireId"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result deleteMyService(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("serviceId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = RNSService.deleteMyService(param.getLong("serviceId"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result getRequireDetail(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("requireId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<RequireDetailVO> serviceVOResult = RNSService.getRequireDetail(param.getLong("requireId"));

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @Transactional
    public static Result getServiceDetail(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("serviceId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<ServiceDetailVO> serviceVOResult = RNSService.getServiceDetail(param.getLong("serviceId"));

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @Transactional
    public static Result getServiceList(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.intField("pageIndex", Require.Yes);
        config.intField("pageSize", Require.Yes);
        config.longField("industryId", Require.No);
        config.stringField("skillsTags", Require.No);
        config.longField("ownerUserId", Require.No);
        config.stringField("searchText", Require.No);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        int pageIndex = param.getInt("pageIndex");
        int pageSize = param.getInt("pageSize");
        if (pageIndex <= 0 || pageSize < 1 || pageSize > 30) {
            return illegalParameters("pageIndex或pageSize超出范围");
        }

        ServiceVOResult<MobilePage<ServiceVO>> serviceVOResult = RNSService.getServicePage(pageIndex, pageSize,
                param.getLong("ownerUserId"), param.getString("searchText"), param.getLong("industryId"),
                param.getString("skillsTags"));

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @Transactional
    public static Result getRequireList(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.intField("pageIndex", Require.Yes);
        config.intField("pageSize", Require.Yes);
        config.longField("industryId", Require.No);
        config.stringField("skillsTags", Require.No);
        config.longField("ownerUserId", Require.No);
        config.stringField("searchText", Require.No);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        int pageIndex = param.getInt("pageIndex");
        int pageSize = param.getInt("pageSize");
        if (pageIndex <= 0 || pageSize < 1 || pageSize > 30) {
            return illegalParameters("pageIndex或pageSize超出范围");
        }

        ServiceVOResult<MobilePage<RequireVO>> serviceVOResult = RNSService.getRequirePage(pageIndex, pageSize,
                param.getLong("ownerUserId"), param.getString("searchText"), param.getLong("industryId"),
                param.getString("skillsTags"));

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

}
