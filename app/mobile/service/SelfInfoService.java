/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-5-26
 */
package mobile.service;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.util.ServiceResults;
import mobile.vo.result.CommonVO;
import mobile.vo.user.SelfInfo;
import models.Expert;
import models.User;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.mvc.Http.Context;
import play.mvc.Http.Session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.attachment.AttachmentApp;
import controllers.base.ObjectNodeResult;
import controllers.user.UserAvatarApp;
import exception.AvatarException;
import ext.usercenter.UserAuthService;

/**
 * 
 * 
 * @ClassName: SelfInfoService
 * @Description: 个人资料服务
 * @date 2014-5-26 下午6:06:55
 * @author ShenTeng
 * 
 */
public class SelfInfoService {

    private static final ALogger LOGGER = Logger.of(SelfInfoService.class);

    /**
     * 获取当前用户的个人信息
     * 
     * @return SelfInfo VO
     */
    public static SelfInfo getSelfInfo() {
        Session session = Context.current().session();
        User user = User.getFromSession(session);
        String token = UserAuthService.getTokenInSession(session);

        return SelfInfo.create(user, token);
    }

    /**
     * 保存当前用户的职业经历
     * 
     * @param jobExpArray
     * @return
     */
    public static ServiceResult saveJobExp(ArrayNode jobExpArray) {
        ArrayNode newJobExpArray = jobExpArray.deepCopy();
        Iterator<JsonNode> elements = newJobExpArray.elements();
        while (elements.hasNext()) {
            ObjectNode next = (ObjectNode) elements.next();
            if (next.hasNonNull("endYear") && next.get("endYear").asText().equals("-1")) {
                next.put("endYear", "至今");
            }
        }

        ObjectNode jobExpNode = Json.newObject();
        jobExpNode.set("jobExp", newJobExpArray);
        ObjectNodeResult objectNodeResult = Expert.saveExpertByJson(Context.current().session(), jobExpNode);

        return ServiceResult.create(objectNodeResult);
    }

    /**
     * 添加当前用户职业经历
     * 
     * @param newJobExp 新增的职业经历
     */
    public static ServiceResult addJobExp(JsonNode newJobExp) {
        User user = User.getFromSession(Context.current().session());
        Expert expert = Expert.findByUserId(user.id);

        ArrayNode jobExpArrayNode = null;
        if (null == expert || StringUtils.isBlank(expert.jobExp)) {
            jobExpArrayNode = Json.newObject().arrayNode();
        } else {
            jobExpArrayNode = (ArrayNode) Json.parse(expert.jobExp);
        }
        if (newJobExp.hasNonNull("endYear") && newJobExp.get("endYear").asText().equals("-1")) {
            ((ObjectNode) newJobExp).put("endYear", "至今");
        }
        jobExpArrayNode.add(newJobExp);

        ObjectNode jobExpNode = Json.newObject();
        jobExpNode.set("jobExp", jobExpArrayNode);

        ObjectNodeResult objectNodeResult = Expert.saveExpertByJson(Context.current().session(), jobExpNode);

        return ServiceResult.create(objectNodeResult);
    }

    /**
     * 根据序号删除当前用户职业经历
     * 
     * @param index 职业经历序号,从0开始
     */
    public static ServiceResult deleteJobExpByIndex(int index) {
        User user = User.getFromSession(Context.current().session());
        Expert expert = Expert.findByUserId(user.id);

        ArrayNode jobExpArrayNode = null;
        if (null == expert || StringUtils.isBlank(expert.jobExp) || index < 0) {
            return ServiceResult.error("234001", "删除职业经历失败，序号无效");
        } else {
            jobExpArrayNode = (ArrayNode) Json.parse(expert.jobExp);
            if (index >= jobExpArrayNode.size()) {
                return ServiceResult.error("234001", "删除职业经历失败，序号无效");
            } else {
                jobExpArrayNode.remove(index);
            }
        }

        ObjectNode jobExpNode = Json.newObject();
        jobExpNode.set("jobExp", jobExpArrayNode);
        ObjectNodeResult objectNodeResult = Expert.saveExpertByJson(Context.current().session(), jobExpNode);

        return ServiceResult.create(objectNodeResult);
    }

    /**
     * 保存当前用户的教育经历
     * 
     * @param eduExpArray
     * @return
     */
    public static ServiceResult saveEducationExp(ArrayNode eduExpArray) {
        ObjectNode eduExpNode = Json.newObject();
        eduExpNode.set("educationExp", eduExpArray);
        ObjectNodeResult objectNodeResult = Expert.saveExpertByJson(Context.current().session(), eduExpNode);

        return ServiceResult.create(objectNodeResult);
    }

    /**
     * 添加当前用户的教育经历
     * 
     * @param newEduExp 新增的教育经历Json对象
     */
    public static ServiceResult addEducationExp(JsonNode newEduExp) {
        User user = User.getFromSession(Context.current().session());
        Expert expert = Expert.findByUserId(user.id);

        ArrayNode eduExpArrayNode = null;
        if (null == expert || StringUtils.isBlank(expert.educationExp)) {
            eduExpArrayNode = Json.newObject().arrayNode();
        } else {
            eduExpArrayNode = (ArrayNode) Json.parse(expert.educationExp);
        }
        eduExpArrayNode.add(newEduExp);

        ObjectNode eduExpNode = Json.newObject();
        eduExpNode.set("educationExp", eduExpArrayNode);
        ObjectNodeResult objectNodeResult = Expert.saveExpertByJson(Context.current().session(), eduExpNode);

        return ServiceResult.create(objectNodeResult);
    }

    /**
     * 根据序号删除当前用户教育经历
     * 
     * @param index 教育经历序号,从0开始
     */
    public static ServiceResult deleteEduExpByIndex(int index) {
        User user = User.getFromSession(Context.current().session());
        Expert expert = Expert.findByUserId(user.id);

        ArrayNode eduExpArrayNode = null;
        if (null == expert || StringUtils.isBlank(expert.educationExp) || index < 0) {
            return ServiceResult.error("232001", "删除教育经历失败，序号无效");
        } else {
            eduExpArrayNode = (ArrayNode) Json.parse(expert.educationExp);
            if (index >= eduExpArrayNode.size()) {
                return ServiceResult.error("232001", "删除教育经历失败，序号无效");
            } else {
                eduExpArrayNode.remove(index);
            }
        }

        ObjectNode eduExpNode = Json.newObject();
        eduExpNode.set("educationExp", eduExpArrayNode);
        ObjectNodeResult objectNodeResult = Expert.saveExpertByJson(Context.current().session(), eduExpNode);

        return ServiceResult.create(objectNodeResult);
    }

    /**
     * 完善当前用户的用户资料
     * 
     * @param email 用户填写的邮箱
     * @param pwd 用户填写的登录密码
     * @return
     */
    public static ServiceResult completeUserInfo(String email, String pwd) {
        ObjectNodeResult result = User.completeUserInfo(Context.current().session(), email, pwd);

        return ServiceResult.create(result);
    }

    /**
     * 保存用户信息,只会保存用户信息Json中存在的属性
     * 
     * @param node 用户信息Json
     * @return
     */
    public static ServiceResult saveUserInfo(JsonNode node) {
        if (node.hasNonNull("language")) {
            String language = node.get("language").asText();
            List<String> list = AttachmentApp.getLanguage();

            if (!list.contains(language)) {
                return ServiceResults.illegalParameters("非法的language：" + language);
            }
        }

        ObjectNodeResult result = Expert.saveExpertByJson(Context.current().session(), node);

        return ServiceResult.create(result);
    }

    /**
     * 上传头像
     * 
     * @param avatarFile 头像文件
     * @return
     */
    public static ServiceVOResult<CommonVO> uploadAvatar(File avatarFile) {
        User current = User.getFromSession(Context.current().session());

        ObjectNodeResult result = new ObjectNodeResult();
        try {
            UserAvatarApp.save(avatarFile, result, current);
        } catch (AvatarException e) {
            LOGGER.error("保存用户头像失败", e);
            return ServiceVOResult.error("215001", "头像文件上传失败");
        }

        ServiceVOResult<CommonVO> serviceVOResult = ServiceVOResult.create(result);
        if (serviceVOResult.isSuccess()) {
            ObjectNode objectNode = result.getObjectNode();
            CommonVO vo = CommonVO.create();
            vo.set("avatar_190", objectNode.path("avatar_190").asText());
            vo.set("avatar_70", objectNode.path("avatar_70").asText());
            vo.set("avatar_22", objectNode.path("avatar_22").asText());
            serviceVOResult.setVo(vo);
        }

        return serviceVOResult;
    }
}
