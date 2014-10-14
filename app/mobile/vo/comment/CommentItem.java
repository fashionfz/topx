/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-1-10
 */
package mobile.vo.comment;

import mobile.vo.MobileVO;
import models.Comment;
import play.libs.Json;
import utils.DateUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * 
 * @ClassName: CommentItem
 * @Description: 一条评论或者回复
 * @date 2014-4-8 下午5:19:08
 * @author ShenTeng
 * 
 */
public class CommentItem implements MobileVO {

    private Long commentId;
    private String avatar_70;
    private Long fromUserId;
    private Long toUserId;
    private String fromUserName;
    private String toUserName;
    private int level;
    private String commentTime;
    private String content;

    private CommentItem() {
    };

    public static CommentItem create(Comment c) {
        CommentItem commentItem = new CommentItem();

        if (c == null) {
            return null;
        }

        commentItem.setCommentId(c.id);
        commentItem.setAvatar_70(c.commentUser.getAvatar(70));
        commentItem.setFromUserId(c.commentUser.getId());
        commentItem.setToUserId(c.toCommentUser.getId());
        commentItem.setFromUserName(c.commentUser.getName());
        commentItem.setToUserName(c.toCommentUser.getName());
        commentItem.setLevel(c.level);
        // 评论时间
        commentItem.setCommentTime(DateUtils.format(c.commentTime, DateUtils.FORMAT_DATETIME));
        // 评论内容
        commentItem.setContent(c.content);

        return commentItem;
    }

    @Override
    public JsonNode toJson() {
        return Json.toJson(this);
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getAvatar_70() {
        return avatar_70;
    }

    public void setAvatar_70(String avatar_70) {
        this.avatar_70 = avatar_70;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
