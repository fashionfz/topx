/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-26
 */
package mobile.controllers;

import java.util.List;

import mobile.service.UserService;
import mobile.util.jsonparam.JsonParam;
import mobile.util.jsonparam.JsonParamConfig;
import mobile.util.jsonparam.config.item.Require;
import mobile.vo.result.CommonVO;
import mobile.vo.result.MobileResult;
import mobile.vo.user.TopUser;
import mobile.vo.user.User;
import mobile.vo.user.UserBasicInfo;
import mobile.vo.user.UserDetailInfo;
import play.db.jpa.Transactional;
import play.mvc.Result;

/**
 * 
 * 
 * @ClassName: UserApp
 * @Description: 用户登录、注册相关
 * @date 2013-12-26 下午5:07:34
 * @author ShenTeng
 * 
 */
public class UserApp extends MobileBaseApp {

    @Transactional
    public static Result getUserBasicInfo(String from) {
        JsonParamConfig jsonConfig = new JsonParamConfig();
        jsonConfig.longField("userId", Require.Yes);

        JsonParam jsonParam = new JsonParam(getJson(), jsonConfig);
        if (jsonParam.isInvalid()) {
            return jsonParam.getErrorResult();
        }

        UserBasicInfo userBasicInfo = UserService.getUserBasicInfo(jsonParam.getLong("userId"));

        MobileResult result = MobileResult.success();
        result.setToField("userBasicInfo", userBasicInfo);
        return result.getResult();
    }

    @Transactional
    public static Result getUserDetailInfo(String from) {
        JsonParamConfig jsonConfig = new JsonParamConfig();
        jsonConfig.longField("userId", Require.Yes);

        JsonParam jsonParam = new JsonParam(getJson(), jsonConfig);
        if (jsonParam.isInvalid()) {
            return jsonParam.getErrorResult();
        }

        UserDetailInfo detail = UserService.getUserDetailInfo(jsonParam.getLong("userId"));

        MobileResult result = MobileResult.success();
        result.setToField("userDetailInfo", detail);
        return result.getResult();
    }

    @Transactional
    public static Result getTopUserList(String from) {
        JsonParamConfig jsonConfig = new JsonParamConfig();
        jsonConfig.intField("size", Require.Yes);

        JsonParam jsonParam = new JsonParam(getJson(), jsonConfig);
        if (jsonParam.isInvalid()) {
            return jsonParam.getErrorResult();
        }

        Integer size = jsonParam.getInt("size");
        if (size < 0 || size > 48) {
            return illegalParameters("size超出范围");
        }

        List<TopUser> topUserList = UserService.getTopUserList(size);

        MobileResult result = MobileResult.success();
        result.setToField("topUserList", topUserList);

        return result.getResult();
    }

    @Transactional
    public static Result getUserList(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.intField("pageIndex", Require.Yes);
        config.intField("pageSize", Require.Yes);
        config.longField("industryId", Require.No);
        config.stringField("skillsTag", Require.No);
        config.stringField("country", Require.No);
        config.intField("payType", Require.No);
        config.intField("gender", Require.No);
        config.stringField("orderBy", Require.No);

        JsonParam param = new JsonParam(getJson(), config);
        if (!param.validate()) {
            return param.getErrorResult();
        }

        int pageIndex = param.getInt("pageIndex");
        int pageSize = param.getInt("pageSize");
        if (pageIndex <= 0 || pageSize < 1 || pageSize > 30) {
            return illegalParameters("pageIndex或pageSize超出范围");
        }

        List<User> userList = UserService.getUserList(pageIndex, pageSize, param.getLong("industryId"),
                param.getString("skillsTag"), param.getString("country"), param.getInt("payType"),
                param.getInt("gender"), param.getString("orderBy"));

        MobileResult result = MobileResult.success();
        result.setToField("userList", userList);

        return result.getResult();
    }

    @Transactional
    public static Result getSkillTagListByKeyword(String from, String keyword) {
        List<String> skillTagList = UserService.getSkillTagListByKeyword(keyword);

        return MobileResult.success().setToField("skillsTags", skillTagList).getResult();
    }

    @Transactional
    public static Result getIndustryTagList(String from) {
        List<CommonVO> industryTagList = UserService.getIndustryTagList();

        return MobileResult.success().setToField("skillsTags", industryTagList).getResult();
    }

    @Transactional
    public static Result getSkillTagListByIndustry(String from, Long i) {
        List<CommonVO> industryTagList = UserService.getSkillTagListByIndustry(i);

        return MobileResult.success().setToField("skillsTags", industryTagList).getResult();
    }

    @Transactional
    public static Result getOnlineTranslatorList(String from) {
        List<User> list = UserService.getOnlineTranslatorList();

        return MobileResult.success().setToField("translatorList", list).getResult();
    }

}
