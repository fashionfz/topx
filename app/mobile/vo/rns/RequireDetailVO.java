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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mobile.util.MobileVOUtil;
import mobile.vo.MobileVO;
import mobile.vo.user.User;
import models.AttachOfRequire;
import models.Expert;
import models.Require;
import models.SkillTag;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import play.libs.Json;
import utils.Assets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.jackjson.JackJsonUtil;

/**
 *
 *
 * @ClassName: RequireDetailVO
 * @Description: 需求详细VO
 * @date 2014年9月2日 下午11:00:16
 * @author ShenTeng
 * 
 */
public class RequireDetailVO implements MobileVO {

    /** id */
    private Long id;
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
    /** 附件 */
    private List<Map<String, Object>> attachs = new ArrayList<Map<String, Object>>();
    /** 需求所有者 */
    @JsonIgnore
    private User owner;

    public static RequireDetailVO create(Require po) {
        ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
        RequireDetailVO vo = new RequireDetailVO();

        vo.setId(po.getId());

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

        Iterator<AttachOfRequire> iterator = po.getCaseAttachs().iterator();
        while (iterator.hasNext()) {
            AttachOfRequire attachOfRequire = iterator.next();
            HashMap<String, Object> pair = new HashMap<String, Object>();
            pair.put("attachId", attachOfRequire.id);
            pair.put("filename", attachOfRequire.fileName);
            pair.put("url", Assets.at(attachOfRequire.path));
            vo.getAttachs().add(pair);
        }

        Expert ownerExpert = po.getOwner().getExperts().iterator().next();
        vo.setOwner(User.create(ownerExpert));

        return vo;
    }

    @Override
    public JsonNode toJson() {
        ObjectNode node = (ObjectNode) Json.toJson(this);
        node.set("owner", MobileVOUtil.toJson(owner));
        return node;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Map<String, Object>> getAttachs() {
        return attachs;
    }

    public void setAttachs(List<Map<String, Object>> attachs) {
        this.attachs = attachs;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

}
