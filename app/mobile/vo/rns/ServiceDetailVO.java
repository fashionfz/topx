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
import mobile.vo.comment.CommentInfo;
import mobile.vo.comment.CommentItem;
import mobile.vo.user.User;
import models.AttachOfService;
import models.Expert;
import models.Service;
import models.ServiceComment;
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
 * @ClassName: ServiceDetailVO
 * @Description: 服务详细VO
 * @date 2014年9月2日 下午11:01:01
 * @author ShenTeng
 * 
 */
public class ServiceDetailVO implements MobileVO {

    /** id */
    private Long id;
    /** 行业id */
    private Long industryId;
    /** 行业名称 */
    private String industryName;
    /** 服务标题 */
    private String title;
    /** 服务说明 */
    private String info;
    /** 价格 */
    private String price;
    /** 发布时间 */
    private String createDate;
    /** 服务封面默认图片 **/
    private String coverUrl;
    /** 标签["ddd",""] */
    private List<String> tags = new ArrayList<String>();
    /** 附件 */
    private List<Map<String, Object>> attachs = new ArrayList<Map<String, Object>>();
    /** 需求所有者 */
    @JsonIgnore
    private User owner;
    /** 评论信息 */
    @JsonIgnore
    private CommentInfo commentInfo;
    /** 最近一条评论 */
    @JsonIgnore
    private CommentItem latestComment;

    public static ServiceDetailVO create(Service po) {
        ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
        ServiceDetailVO vo = new ServiceDetailVO();

        vo.setId(po.getId());

        SkillTag industry = po.getIndustry();
        if (null != industry) {
            vo.setIndustryId(industry.getId());
            vo.setIndustryName(industry.getTagName());
        }

        vo.setTitle(po.getTitle());
        vo.setInfo(po.getInfo());

        if (null == po.getPrice()) {
            vo.setPrice("-1");// -1 - 面议
        } else {
            vo.setPrice(new BigDecimal(po.getPrice()).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
        }

        vo.setCreateDate(new DateTime(po.getCreateDate()).toString("yyyy-MM-dd HH:mm:ss"));

        vo.setCoverUrl(Assets.at(po.getCoverUrl()));

        if (StringUtils.isNotBlank(po.getTags())) {
            try {
                @SuppressWarnings("unchecked")
                List<String> readValue = objectMapper.readValue(po.getTags(), List.class);
                vo.setTags(readValue);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Iterator<AttachOfService> iterator = po.getCaseAttachs().iterator();
        while (iterator.hasNext()) {
            AttachOfService attachOfService = iterator.next();
            HashMap<String, Object> pair = new HashMap<String, Object>();
            pair.put("attachId", attachOfService.id);
            pair.put("url", Assets.at(attachOfService.path));
            vo.getAttachs().add(pair);
        }

        Expert ownerExpert = po.getOwner().getExperts().iterator().next();
        vo.setOwner(User.create(ownerExpert));

        vo.setCommentInfo(CommentInfo.create(po));

        ServiceComment latestComment = ServiceComment.getLatestCommentByServiceId(po.getId());
        vo.setLatestComment(CommentItem.create(latestComment));

        return vo;
    }

    @Override
    public JsonNode toJson() {
        ObjectNode node = (ObjectNode) Json.toJson(this);
        node.set("owner", MobileVOUtil.toJson(owner));
        node.set("commentInfo", MobileVOUtil.toJson(commentInfo));
        node.set("latestComment", MobileVOUtil.toJson(latestComment));
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
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

    public CommentInfo getCommentInfo() {
        return commentInfo;
    }

    public void setCommentInfo(CommentInfo commentInfo) {
        this.commentInfo = commentInfo;
    }

    public CommentItem getLatestComment() {
        return latestComment;
    }

    public void setLatestComment(CommentItem latestComment) {
        this.latestComment = latestComment;
    }
}
