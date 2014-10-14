/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-16
 */
package mobile.vo.user;

import mobile.service.UserService;
import mobile.vo.MobileVO;
import models.Expert.PayType;

import org.apache.commons.lang3.StringUtils;

import play.libs.Json;
import vo.TopExpert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * @ClassName: TopUser
 * @Description: 置顶用户信息
 * @date 2014-6-16 下午2:42:04
 * @author ShenTeng
 * 
 */
public class TopUser implements MobileVO {

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

    public static TopUser create(TopExpert expert) {
        TopUser topUser = new TopUser();
        topUser.setAvatar_190(expert.getHeadUrl());

        topUser.setAverageScore(expert.getAverageScore());
        topUser.setCommentNum(expert.getCommentNum());
        topUser.setCountry(expert.getCountry());
        topUser.setGender(expert.getGender());
        topUser.setJob(expert.getJob());
        topUser.setOnlineState(UserService.getOnlineState(expert.getUserId()));
        topUser.setPayType(expert.getPayType() == null ? PayType.NEGOTIABLE.ordinal() : expert.getPayType().ordinal());
        topUser.setPersonalInfo(expert.getPersonalInfo());
        if (StringUtils.isNotBlank(expert.getSkillsTags())) {
            topUser.setSkillsTags(expert.getSkillsTags());
        }
        topUser.setUserId(expert.getUserId());
        topUser.setUserName(expert.getUserName());
        topUser.setExpenses(expert.getExpenses());

        return topUser;
    }

    @Override
    public JsonNode toJson() {
        ObjectNode objectNode = (ObjectNode) Json.toJson(this);
        objectNode.set("skillsTags", this.skillsTags == null ? null : Json.parse(this.skillsTags));
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
