/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-1-10
 */
package mobile.vo.comment;

import java.util.ArrayList;
import java.util.List;

import mobile.vo.MobileVO;
import models.Comment;
import models.Expert;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * 
 * @ClassName: CommentDetail
 * @Description: 评价详情
 * @date 2014-1-10 下午5:00:17
 * @author ShenTeng
 * 
 */
public class CommentDetail implements MobileVO {

    /** 被评论者Id */
    private Long toCommentUserId;
    /** 被评论者姓名 */
    private String toCommentName;
    /** 被评论者职业 */
    private String toCommentJob;
    /** 被评论者性别 */
    private Integer toCommentGender;
    /** 被评论者头像 */
    private String toCommentAvatar_70;
    /** 被评论者头像 */
    private String toCommentCountry;
    /** 被评论者头像 */
    private Integer toCommentPayType;

    private CommentItem comment;

    private List<CommentItem> reply = new ArrayList<CommentItem>();

    public static CommentDetail create(Expert toCommentExpert, Comment c, List<Comment> r) {
        CommentDetail detail = new CommentDetail();

        detail.setToCommentUserId(toCommentExpert.userId);
        detail.setToCommentName(toCommentExpert.userName);
        detail.setToCommentJob(toCommentExpert.job);
        detail.setToCommentAvatar_70(toCommentExpert.user.getAvatar(70));
        detail.setToCommentGender(toCommentExpert.getGenderWithDefault().ordinal());
        detail.setToCommentCountry(toCommentExpert.country);
        detail.setToCommentPayType(toCommentExpert.getPayTypeWithDefault().ordinal());
        detail.setComment(CommentItem.create(c));

        List<CommentItem> reply = new ArrayList<>();
        for (Comment item : r) {
            reply.add(CommentItem.create(item));
        }
        detail.setReply(reply);

        return detail;
    }

    @Override
    public JsonNode toJson() {
        return Json.toJson(this);
    }

    public Long getToCommentUserId() {
        return toCommentUserId;
    }

    public void setToCommentUserId(Long toCommentUserId) {
        this.toCommentUserId = toCommentUserId;
    }

    public String getToCommentName() {
        return toCommentName;
    }

    public void setToCommentName(String toCommentName) {
        this.toCommentName = toCommentName;
    }

    public String getToCommentJob() {
        return toCommentJob;
    }

    public void setToCommentJob(String toCommentJob) {
        this.toCommentJob = toCommentJob;
    }

    public Integer getToCommentGender() {
        return toCommentGender;
    }

    public void setToCommentGender(Integer toCommentGender) {
        this.toCommentGender = toCommentGender;
    }

    public String getToCommentAvatar_70() {
        return toCommentAvatar_70;
    }

    public void setToCommentAvatar_70(String toCommentAvatar_70) {
        this.toCommentAvatar_70 = toCommentAvatar_70;
    }

    public String getToCommentCountry() {
        return toCommentCountry;
    }

    public void setToCommentCountry(String toCommentCountry) {
        this.toCommentCountry = toCommentCountry;
    }

    public Integer getToCommentPayType() {
        return toCommentPayType;
    }

    public void setToCommentPayType(Integer toCommentPayType) {
        this.toCommentPayType = toCommentPayType;
    }

    public CommentItem getComment() {
        return comment;
    }

    public void setComment(CommentItem comment) {
        this.comment = comment;
    }

    public List<CommentItem> getReply() {
        return reply;
    }

    public void setReply(List<CommentItem> reply) {
        this.reply = reply;
    }

}
