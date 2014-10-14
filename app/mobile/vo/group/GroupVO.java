/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年8月8日
 */
package mobile.vo.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mobile.vo.MobileVO;
import models.Group.GroupPriv;
import models.Group.Type;
import models.User;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import play.libs.Json;
import play.mvc.Http.Context;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 *
 * @ClassName: Group
 * @Description: 群组基本信息VO
 * @date 2014年8月8日 下午2:45:14
 * @author ShenTeng
 * 
 */
public class GroupVO implements MobileVO {

    /**
     * 群组id
     */
    private Long id;

    /**
     * 群组名称
     */
    private String groupName;

    /**
     * 成员个数
     */
    private Long countMem;

    /**
     * 创建日期
     */
    private String createDate;

    /** 头像 **/
    private String headUrl;

    /**
     * 群组行业id
     */
    private Long industryId;

    /**
     * 群组行业名称
     */
    private String industryName;

    /** 群组说明 **/
    private String groupInfo;

    /** 群标签模式 ["ddd",""] */
    private List<String> skillsTags = new ArrayList<String>();

    /**
     * 群组权限
     */
    private String groupPriv;

    /** 群组成员最大数量限制 （默认1万人，包括群主） */
    private Long maxMemberNum;

    /**
     * 当前用户是否加入
     */
    private Boolean isJoin;

    /**
     * 是否是群主
     */
    private Boolean isOwner;
    
    /**
     * 群组类型
     */
    private String type;

    public static GroupVO create(vo.GroupVO source) {
        GroupVO vo = new GroupVO();

        vo.id = source.getId();
        vo.groupName = source.getGroupName();
        vo.countMem = source.getCountMem();
        vo.createDate = new DateTime(source.getCreateDate()).toString("yyyy-MM-dd HH:mm:ss");
        vo.headUrl = source.getHeadUrl();
        vo.industryId = source.getIndustryId();
        vo.industryName = source.getIndustryName();
        vo.groupInfo = StringUtils.defaultIfEmpty(source.getGroupInfo(), "");
        vo.skillsTags = source.getTags();

        GroupPriv priv = GroupPriv.getByName(source.getGroupPriv());
        if (null != priv) {
            vo.groupPriv = priv.toString();
        }

        vo.maxMemberNum = source.getMaxMemberNum();
        vo.isJoin = source.getIsJoin();

        Type tp = Type.getByName(source.getType());
        if (null != tp) {
            vo.type = tp.toString();
        }
        
        User user = User.getFromSession(Context.current().session());
        if (null != user && tp == Type.NORMAL) {
            vo.isOwner = Objects.equals(user.getId(), source.getOwnerId());
        }

        return vo;
    }

    @Override
    public JsonNode toJson() {
        return Json.toJson(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getCountMem() {
        return countMem;
    }

    public void setCountMem(Long countMem) {
        this.countMem = countMem;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public Long getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Long industryId) {
        this.industryId = industryId;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public String getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(String groupInfo) {
        this.groupInfo = groupInfo;
    }

    public List<String> getSkillsTags() {
        return skillsTags;
    }

    public void setSkillsTags(List<String> skillsTags) {
        this.skillsTags = skillsTags;
    }

    public String getGroupPriv() {
        return groupPriv;
    }

    public void setGroupPriv(String groupPriv) {
        this.groupPriv = groupPriv;
    }

    public Long getMaxMemberNum() {
        return maxMemberNum;
    }

    public void setMaxMemberNum(Long maxMemberNum) {
        this.maxMemberNum = maxMemberNum;
    }

    public Boolean getIsJoin() {
        return isJoin;
    }

    public void setIsJoin(Boolean isJoin) {
        this.isJoin = isJoin;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
