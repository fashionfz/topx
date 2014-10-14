/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-7-4
 */
package mobile.vo.group;

import java.util.ArrayList;
import java.util.List;

import mobile.vo.MobileVO;
import play.libs.Json;
import vo.GroupMemberVO;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * 
 * @ClassName: GroupMember
 * @Description: 群组成员VO
 * @date 2014-7-4 下午3:44:42
 * @author ShenTeng
 * 
 */
public class GroupMember implements MobileVO {

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 角色 MEMBER:成员 ， TRANSLATE：翻译者
     */
    private String role;
    /**
     * 个人说明
     */
    private String personalInfo;
    /**
     * 头像
     */
    private String mediumHeadUrl;
    /**
     * 职业
     */
    private String job;

    public static GroupMember create(GroupMemberVO src) {
        GroupMember vo = new GroupMember();

        vo.setRole(src.getRole());
        vo.setUserId(src.getUserId());
        vo.setUserName(src.getUserName());
        vo.setPersonalInfo(src.getPersonalInfo());
        vo.setMediumHeadUrl(src.getMediumHeadUrl());
        vo.setJob(src.getJob());

        return vo;
    }

    public static List<GroupMember> createList(List<GroupMemberVO> srcList) {
        List<GroupMember> voList = new ArrayList<GroupMember>();

        for (GroupMemberVO src : srcList) {
            voList.add(create(src));
        }

        return voList;
    }

    @Override
    public JsonNode toJson() {
        return Json.toJson(this);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(String personalInfo) {
        this.personalInfo = personalInfo;
    }

    public String getMediumHeadUrl() {
        return mediumHeadUrl;
    }

    public void setMediumHeadUrl(String mediumHeadUrl) {
        this.mediumHeadUrl = mediumHeadUrl;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
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

}
