/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-27
 */
package mobile.vo.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mobile.service.UserService;
import mobile.util.MobileVOUtil;
import mobile.vo.MobileVO;
import models.Expert;
import models.Friends;
import models.SkillTag;
import models.User;

import org.apache.commons.lang3.StringUtils;

import play.libs.Json;
import play.mvc.Http.Context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * @ClassName: UserDetail
 * @Description: 用户详情信息
 * @date 2014-4-8 上午11:35:16
 * @author ShenTeng
 * 
 */
public class UserDetailInfo implements MobileVO {

    /** 用户Id */
    private Long userId;
    /** 190x190头像URL地址 */
    private String avatar_190;
    /** 70x70头像URL地址 */
    private String avatar_70;
    /** 22x22头像URL地址 */
    private String avatar_22;
    /** 姓名 */
    private String userName;
    /** 性别 */
    private Integer gender;
    /** 国家 */
    private String country;
    /** 职业 */
    private String job;
    /** 时区uid */
    private String timezoneUid;
    /** 语言 */
    private String language;
    /** 收费标准0为免费，单位为元/分钟 */
    private Long expenses;
    /** 支付类型 */
    private Integer payType;
    /** 个人说明 */
    private String personalInfo;
    /** 在线状态 */
    private Integer onlineState;
    /** 评价次数 */
    private Long commentNum = 0L;
    /** 评论平均分 */
    private Float averageScore;
    /** 是否是圈中好友，true：是，false：否 */
    private Boolean isFriend;
    /** 所属行业Id */
    private List<Long> industryIds = new ArrayList<>();
    /** 技能标签模式 ["ddd",""] **/
    @JsonIgnore
    private String skillsTags = "[]";
    /** 工作经历 **/
    @JsonIgnore
    private List<JobExp> jobExpList = new ArrayList<>();
    /** 教育经历 **/
    @JsonIgnore
    private List<EducationExp> educationExpList = new ArrayList<>();

    private UserDetailInfo() {
    };

    public static UserDetailInfo create(Expert expert, User user) {
        UserDetailInfo detail = new UserDetailInfo();
        if (null == user) {
            return null;
        }

        detail.setUserId(user.id);
        detail.setAvatar_190(user.getAvatar(190));
        detail.setAvatar_70(user.getAvatar(70));
        detail.setAvatar_22(user.getAvatar(22));
        detail.setUserName(user.getName());
        detail.setGender(user.getGenderWithDefault().ordinal());
        detail.setOnlineState(UserService.getOnlineState(user.id));

        User currentUser = User.getFromSession(Context.current().session());
        if (null != currentUser && !currentUser.id.equals(user.id)) {
            detail.setIsFriend(Friends.isFriend(currentUser.id, user.id));
        }

        if (null != expert) {
            detail.setCountry(expert.country);
            detail.setExpenses(expert.getExpensesWithDefault());
            detail.setJob(expert.job);
            detail.setPayType(expert.getPayTypeWithDefault().ordinal());
            detail.setPersonalInfo(expert.personalInfo);
            detail.setAverageScore(expert.getAverageScoreWithDefault());
            detail.setTimezoneUid(expert.timeZone);
            detail.setLanguage(expert.getLanguageWithDefault());
            detail.setCommentNum(expert.getCommentNumWithDefault());

            Set<SkillTag> inTags = expert.inTags;
            for (SkillTag skillTag : inTags) {
                detail.getIndustryIds().add(skillTag.id);
            }
            if (StringUtils.isNotBlank(expert.skillsTags) && Json.parse(expert.skillsTags).isArray()) {
                detail.setSkillsTags(expert.skillsTags);
            }
            detail.setJobExpList(JobExp.createList(expert));
            detail.setEducationExpList(EducationExp.createList(expert));
        }

        return detail;
    }

    @Override
    public JsonNode toJson() {
        ObjectNode objectNode = (ObjectNode) Json.toJson(this);
        objectNode.set("skillsTags", this.skillsTags == null ? null : Json.parse(this.skillsTags));
        MobileVOUtil.setToField(objectNode, "jobExpList", this.jobExpList);
        MobileVOUtil.setToField(objectNode, "educationExpList", this.educationExpList);

        return objectNode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAvatar_190() {
        return avatar_190;
    }

    public void setAvatar_190(String avatar_190) {
        this.avatar_190 = avatar_190;
    }

    public String getAvatar_70() {
        return avatar_70;
    }

    public void setAvatar_70(String avatar_70) {
        this.avatar_70 = avatar_70;
    }

    public String getAvatar_22() {
        return avatar_22;
    }

    public void setAvatar_22(String avatar_22) {
        this.avatar_22 = avatar_22;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getTimezoneUid() {
        return timezoneUid;
    }

    public void setTimezoneUid(String timezoneUid) {
        this.timezoneUid = timezoneUid;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public String getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(String personalInfo) {
        this.personalInfo = personalInfo;
    }

    public Integer getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(Integer onlineState) {
        this.onlineState = onlineState;
    }

    public Long getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Long commentNum) {
        this.commentNum = commentNum;
    }

    public Float getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Float averageScore) {
        this.averageScore = averageScore;
    }

    public Boolean getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(Boolean isFriend) {
        this.isFriend = isFriend;
    }

    public List<Long> getIndustryIds() {
        return industryIds;
    }

    public void setIndustryIds(List<Long> industryIds) {
        this.industryIds = industryIds;
    }

    public String getSkillsTags() {
        return skillsTags;
    }

    public void setSkillsTags(String skillsTags) {
        this.skillsTags = skillsTags;
    }

    @JsonIgnore
    public List<JobExp> getJobExpList() {
        return jobExpList;
    }

    public void setJobExpList(List<JobExp> jobExpList) {
        this.jobExpList = jobExpList;
    }

    @JsonIgnore
    public List<EducationExp> getEducationExpList() {
        return educationExpList;
    }

    public void setEducationExpList(List<EducationExp> educationExpList) {
        this.educationExpList = educationExpList;
    }

}
