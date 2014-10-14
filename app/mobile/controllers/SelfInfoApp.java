/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-4-9
 */
package mobile.controllers;

import java.io.File;
import java.util.List;

import mobile.service.SelfInfoService;
import mobile.service.UserService;
import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.util.MobileResults;
import mobile.util.jsonparam.JsonParam;
import mobile.util.jsonparam.JsonParamConfig;
import mobile.util.jsonparam.config.item.ArrayItemType;
import mobile.util.jsonparam.config.item.CanEmpty;
import mobile.util.jsonparam.config.item.Require;
import mobile.vo.result.CommonVO;
import mobile.vo.result.MobileResult;
import mobile.vo.user.EducationExp;
import mobile.vo.user.JobExp;
import mobile.vo.user.SelfInfo;
import mobile.vo.user.UserBasicInfo;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Result;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * @ClassName: SelfInfoApp
 * @Description: 个人资料接口
 * @date 2014-4-9 上午11:07:24
 * @author ShenTeng
 * 
 */
public class SelfInfoApp extends MobileBaseApp {

    @Transactional
    public static Result getSelfInfo(String from) {
        SelfInfo selfInfo = SelfInfoService.getSelfInfo();
        UserBasicInfo userBasicInfo = UserService.getUserBasicInfo();

        MobileResult result = MobileResult.success();
        result.setToField("selfInfo", selfInfo);
        result.setToField("userBasicInfo", userBasicInfo);

        return result.getResult();
    }

    /**
     * 完善用户信息，用于第三方登录的用户
     * 
     * @return
     */
    @Transactional
    public static Result completeUserInfo(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("email", Require.Yes);
        config.stringField("pwd", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult result = SelfInfoService.completeUserInfo(param.getString("email"), param.getString("pwd"));

        return MobileResult.success().setToRoot(result).getResult();
    }

    @Transactional
    public static Result saveJobExp(String from) {
        JsonParamConfig jsonConfig = new JsonParamConfig();
        jsonConfig.arrayField("jobExpList", Require.Yes, CanEmpty.Yes, ArrayItemType.JsonObject);
        jsonConfig.stringField("jobExpList[?].beginYear", Require.Yes);
        jsonConfig.stringField("jobExpList[?].beginMonth", Require.Yes);
        jsonConfig.stringField("jobExpList[?].endYear", Require.Yes);
        jsonConfig.stringField("jobExpList[?].endMonth", Require.Yes);
        jsonConfig.stringField("jobExpList[?].company", Require.Yes);
        jsonConfig.stringField("jobExpList[?].duty", Require.Yes);
        jsonConfig.stringField("jobExpList[?].workInfo", Require.Yes);

        JsonParam jsonParam = new JsonParam(getJson(), jsonConfig);
        if (jsonParam.isInvalid()) {
            return jsonParam.getErrorResult();
        }

        ServiceResult serviceResult = SelfInfoService.saveJobExp(jsonParam.getJsonArray("jobExpList"));

        List<JobExp> jobExpList = UserService.getJobExpList();

        MobileResult result = MobileResult.success();
        result.setToRoot(serviceResult);
        result.setToField("jobExpList", jobExpList);

        return result.getResult();
    }

    @Transactional
    public static Result saveEducationExp(String from) {
        JsonParamConfig jsonConfig = new JsonParamConfig();
        jsonConfig.arrayField("educationExpList", Require.Yes, CanEmpty.Yes, ArrayItemType.JsonObject);
        jsonConfig.stringField("educationExpList[?].year", Require.Yes);
        jsonConfig.stringField("educationExpList[?].yearEnd", Require.Yes);
        jsonConfig.stringField("educationExpList[?].school", Require.Yes);
        jsonConfig.stringField("educationExpList[?].major", Require.Yes);
        jsonConfig.stringField("educationExpList[?].academicDegree", Require.Yes);
        jsonConfig.stringField("educationExpList[?].eduInfo", Require.Yes);

        JsonParam jsonParam = new JsonParam(getJson(), jsonConfig);
        if (jsonParam.isInvalid()) {
            return jsonParam.getErrorResult();
        }

        ServiceResult serviceResult = SelfInfoService.saveEducationExp(jsonParam.getJsonArray("educationExpList"));

        List<EducationExp> educationExpList = UserService.getEducationExpList();

        MobileResult result = MobileResult.success();
        result.setToRoot(serviceResult);
        result.setToField("educationExpList", educationExpList);

        return result.getResult();
    }

    @Transactional
    public static Result addEduExp(String from) {
        JsonParamConfig jsonConfig = new JsonParamConfig();
        jsonConfig.stringField("year", Require.Yes);
        jsonConfig.stringField("yearEnd", Require.Yes);
        jsonConfig.stringField("school", Require.Yes);
        jsonConfig.stringField("major", Require.Yes);
        jsonConfig.stringField("academicDegree", Require.Yes);
        jsonConfig.stringField("eduInfo", Require.Yes);

        JsonParam jsonParam = new JsonParam(getJson(), jsonConfig);
        if (jsonParam.isInvalid()) {
            return jsonParam.getErrorResult();
        }

        ServiceResult serviceResult = SelfInfoService.addEducationExp(jsonParam.getCleanJson());

        List<EducationExp> educationExpList = UserService.getEducationExpList();

        MobileResult result = MobileResult.success();
        result.setToRoot(serviceResult);
        result.setToField("educationExpList", educationExpList);

        return result.getResult();
    }

    @Transactional
    public static Result deleteEduExp(String from) {
        JsonParamConfig jsonConfig = new JsonParamConfig();
        jsonConfig.intField("i", Require.Yes);

        JsonParam jsonParam = new JsonParam(getJson(), jsonConfig);
        if (jsonParam.isInvalid()) {
            return jsonParam.getErrorResult();
        }

        ServiceResult serviceResult = SelfInfoService.deleteEduExpByIndex(jsonParam.getInt("i"));

        List<EducationExp> educationExpList = UserService.getEducationExpList();

        MobileResult result = MobileResult.success();
        result.setToRoot(serviceResult);
        result.setToField("educationExpList", educationExpList);

        return result.getResult();
    }

    @Transactional
    public static Result addJobExp(String from) {
        JsonParamConfig jsonConfig = new JsonParamConfig();
        jsonConfig.stringField("beginYear", Require.Yes);
        jsonConfig.stringField("endYear", Require.Yes);
        jsonConfig.stringField("beginMonth", Require.Yes);
        jsonConfig.stringField("endMonth", Require.Yes);
        jsonConfig.stringField("company", Require.Yes);
        jsonConfig.stringField("duty", Require.Yes);
        jsonConfig.stringField("workInfo", Require.Yes);

        JsonParam jsonParam = new JsonParam(getJson(), jsonConfig);
        if (jsonParam.isInvalid()) {
            return jsonParam.getErrorResult();
        }

        ServiceResult serviceResult = SelfInfoService.addJobExp(jsonParam.getCleanJson());

        List<JobExp> jobExpList = UserService.getJobExpList();

        MobileResult result = MobileResult.success();
        result.setToRoot(serviceResult);
        result.setToField("jobExpList", jobExpList);

        return result.getResult();
    }

    @Transactional
    public static Result deleteJobExp(String from) {
        JsonParamConfig jsonConfig = new JsonParamConfig();
        jsonConfig.intField("i", Require.Yes);

        JsonParam jsonParam = new JsonParam(getJson(), jsonConfig);
        if (jsonParam.isInvalid()) {
            return jsonParam.getErrorResult();
        }

        ServiceResult serviceResult = SelfInfoService.deleteJobExpByIndex(jsonParam.getInt("i"));

        List<JobExp> jobExpList = UserService.getJobExpList();

        MobileResult result = MobileResult.success();
        result.setToRoot(serviceResult);
        result.setToField("jobExpList", jobExpList);

        return result.getResult();
    }

    @Transactional
    public static Result saveUserInfo(String from) {
        JsonParamConfig jsonConfig = new JsonParamConfig();
        jsonConfig.stringField("name", Require.No);
        jsonConfig.intField("gender", Require.No);
        jsonConfig.stringField("job", Require.No);
        jsonConfig.stringField("country", Require.No);
        jsonConfig.longField("expenses", Require.No);
        jsonConfig.stringField("personalInfo", Require.No);
        jsonConfig.stringField("timezoneUid", Require.No);
        jsonConfig.intField("payType", Require.No);
        jsonConfig.stringField("language", Require.No);
        jsonConfig.lessRequired(1);

        JsonParam jsonParam = new JsonParam(getJson(), jsonConfig);
        if (jsonParam.isInvalid()) {
            return jsonParam.getErrorResult();
        }

        ObjectNode cleanJson = (ObjectNode) jsonParam.getCleanJson();
        if (cleanJson.hasNonNull("timezoneUid")) {
            cleanJson.put("timeZone", cleanJson.get("timezoneUid").asText());
            cleanJson.remove("timezoneUid");
        }

        ServiceResult serviceResult = SelfInfoService.saveUserInfo(cleanJson);

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result saveServiceTag(String from) {
        JsonParamConfig jsonConfig = new JsonParamConfig();
        jsonConfig.arrayField("industryIds", Require.No, CanEmpty.Yes, ArrayItemType.Long);
        jsonConfig.arrayField("skillsTags", Require.No, CanEmpty.Yes, ArrayItemType.String);
        jsonConfig.lessRequired(1);

        JsonParam jsonParam = new JsonParam(getJson(), jsonConfig);
        if (jsonParam.isInvalid()) {
            return jsonParam.getErrorResult();
        }

        ServiceResult serviceResult = SelfInfoService.saveUserInfo(jsonParam.getCleanJson());

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result savePersonalInfo(String from) {
        JsonParamConfig jsonConfig = new JsonParamConfig();
        jsonConfig.stringField("personalInfo", Require.Yes);

        JsonParam jsonParam = new JsonParam(getJson(), jsonConfig);
        if (jsonParam.isInvalid()) {
            return jsonParam.getErrorResult();
        }

        ServiceResult serviceResult = SelfInfoService.saveUserInfo(jsonParam.getCleanJson());

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    @BodyParser.Of(value = BodyParser.Raw.class, maxLength = 10 * 1024 * 1024)
    public static Result uploadAvatar(String from) {
        if (request().body().isMaxSizeExceeded()) {
            return MobileResult.error("215002", "头像文件大小不能超过10M").getResult();
        }

        File uploadFile = request().body().asRaw().asFile();
        if (null == uploadFile || uploadFile.length() <= 0) {
            return MobileResults.illegalParameters("上传文件尺寸为0").getResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = SelfInfoService.uploadAvatar(uploadFile);

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }
}
