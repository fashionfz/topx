/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年9月2日
 */
package mobile.vo.rns;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import mobile.vo.MobileVO;
import models.Expert;
import models.Require;
import models.SkillTag;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.jackjson.JackJsonUtil;

/**
 *
 *
 * @ClassName: RequireVO
 * @Description: 需求VO
 * @date 2014年9月2日 下午7:00:26
 * @author ShenTeng
 * 
 */
public class RequireVO implements MobileVO {

    /** id */
    private Long id;
    /** 创建人id */
    private Long ownerUserId;
    /** 创建人的用户名 */
    private String ownerUsername;
    /** 创建人的70×70头像 */
    private String ownerAvatar_70;
    /** 创建人的工作 */
    private String ownerJob;
    /** 创建人的国际 */
    private String ownerCountry;
    /** 行业id */
    private Long industryId;
    /** 行业名称 */
    private String industryName;
    /** 需求标题 */
    private String title;
    /** 需求说明 */
    private String info;
    /** 预算 */
    private String budget;
    /** 发布时间 */
    private String createDate;
    /** 标签["ddd",""] */
    private List<String> tags = new ArrayList<String>();

    public static RequireVO create(Require po) {
        ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
        RequireVO vo = new RequireVO();

        vo.setId(po.getId());
        vo.setOwnerUserId(po.getOwner().getId());
        vo.setOwnerUsername(po.getOwner().getUserNameOrEmail());
        vo.setOwnerAvatar_70(po.getOwner().getAvatar(70));

        Expert ownerExpert = po.getOwner().getExperts().iterator().next();
        vo.setOwnerJob(ownerExpert.job);
        vo.setOwnerCountry(ownerExpert.country);

        SkillTag industry = po.getIndustry();
        if (null != industry) {
            vo.setIndustryId(industry.getId());
            vo.setIndustryName(industry.getTagName());
        }

        vo.setTitle(po.getTitle());
        vo.setInfo(po.getInfo());

        if (null == po.getBudget()) {
            vo.setBudget("-1"); // -1 - 面议
        } else {
            vo.setBudget(new BigDecimal(po.getBudget()).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
        }

        vo.setCreateDate(new DateTime(po.getCreateDate()).toString("yyyy-MM-dd HH:mm:ss"));

        if (StringUtils.isNotBlank(po.getTags())) {
            try {
                @SuppressWarnings("unchecked")
                List<String> readValue = objectMapper.readValue(po.getTags(), List.class);
                vo.setTags(readValue);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getOwnerAvatar_70() {
        return ownerAvatar_70;
    }

    public void setOwnerAvatar_70(String ownerAvatar_70) {
        this.ownerAvatar_70 = ownerAvatar_70;
    }

    public String getOwnerJob() {
        return ownerJob;
    }

    public void setOwnerJob(String ownerJob) {
        this.ownerJob = ownerJob;
    }

    public String getOwnerCountry() {
        return ownerCountry;
    }

    public void setOwnerCountry(String ownerCountry) {
        this.ownerCountry = ownerCountry;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}
