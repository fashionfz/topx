/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-16
 */
package mobile.vo.user;

import mobile.service.UserService;
import mobile.vo.MobileVO;
import models.Expert;

import org.apache.commons.lang3.StringUtils;

import play.libs.Json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 *
 *
 * @ClassName: User
 * @Description: 用户列表元素
 * @date 2014年7月10日 下午6:29:55
 * @author ShenTeng
 *
 */
public class User implements MobileVO {

    /** 用户id **/
    private Long userId;
    /** 姓名 **/
    private String userName;
    /** 职业 **/
    private String job;
    /** 190×190头像地址 **/
    private String avatar_190;
    /** 国家 **/
    private String country;
    /** 性别 **/
    private Integer gender;
    /** 技能标签模式 ["ddd",""] */
    @JsonIgnore
    private String skillsTags = "[]";
    /** 在线状态 */
    private Integer onlineState;
    /** 收费标准0为免费，单位为元/分钟 */
    private Long expenses;
    /** 支付类型。0 - 计费（包括免费）、1 - 面议 **/
    private Integer payType;
    /** 评论平均分 **/
    private Float averageScore;
    /** 评论次数 **/
    private Long commentNum;
    /**
     * 个人说明
     */
    public String personalInfo;

    public static User create(Expert e) {
        User topUser = new User();
        topUser.setAvatar_190(e.getFullHeadUrl());

        topUser.setAverageScore(e.getAverageScoreWithDefault());
        topUser.setCommentNum(e.getCommentNumWithDefault());
        topUser.setCountry(e.country);
        topUser.setGender(e.getGenderWithDefault().ordinal());
        topUser.setJob(e.job);
        topUser.setOnlineState(UserService.getOnlineState(e.userId));
        topUser.setPayType(e.getPayTypeWithDefault().ordinal());
        topUser.setPersonalInfo(e.personalInfo);
        if (StringUtils.isNotBlank(e.skillsTags)) {
            topUser.setSkillsTags(e.skillsTags);
        }
        topUser.setUserId(e.userId);
        topUser.setUserName(e.userName);
        topUser.setExpenses(e.getExpensesWithDefault());

        return topUser;
    }

    @Override
    public JsonNode toJson() {
        ObjectNode objectNode = (ObjectNode) Json.toJson(this);
        objectNode.set("skillsTags",
                this.skillsTags == null ? Json.newObject().arrayNode() : Json.parse(this.skillsTags));
        return objectNode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getAvatar_190() {
        return avatar_190;
    }

    public void setAvatar_190(String avatar_190) {
        this.avatar_190 = avatar_190;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getSkillsTags() {
        return skillsTags;
    }

    public void setSkillsTags(String skillsTags) {
        this.skillsTags = skillsTags;
    }

    public Integer getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(Integer onlineState) {
        this.onlineState = onlineState;
    }

    public Long getExpenses() {
        return expenses;
    }

    public void setExpenses(Long expenses) {
        this.expenses = expenses;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Float getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Float averageScore) {
        this.averageScore = averageScore;
    }

    public Long getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Long commentNum) {
        this.commentNum = commentNum;
    }

    public String getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(String personalInfo) {
        this.personalInfo = personalInfo;
    }

}
