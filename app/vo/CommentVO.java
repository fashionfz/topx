/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-20
 */
package vo;

import models.Comment;
import utils.Assets;
import utils.DateUtils;
import utils.HelomeUtil;
/**
 *
 *
 * @ClassName: CommentVO
 * @Description: 评价VO
 * @date 2013-11-20 下午2:54:25
 * @author YangXuelin
 * 
 */
public class CommentVO {
	
	private Long id;
	
	private int level;
	
	private String commentTime;
	
	private String commentUserName;
	
	private String toCommentUserName;
	
	private String content;
	
	private String headUrl;
	
	private Long userId;
	
	
	public int getLevel() {
		return level;
	}

	public String getCommentTime() {
		return commentTime;
	}

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getHeadUrl() {
		return this.headUrl;
	}

    public String getHeadUrl(int size) {
        if(HelomeUtil.isEmpty(headUrl)) {
            return Assets.getDefaultAvatar();
        }
        return headUrl;

    }

	/**
	 * 
	 * @param comment
	 */
	public void convert(Comment comment, boolean isParent) {
		this.id = comment.id;
		this.commentTime = DateUtils.format(comment.commentTime, DateUtils.FORAMT_DATE_TIME);
		this.content = comment.content;
		if(isParent) {
			this.level = 20 * comment.level;
			this.commentUserName = comment.commentUser.getName();
			this.toCommentUserName = comment.toCommentUser.getName();
		}
		this.userId = comment.commentUser.id;
		this.headUrl = comment.commentUser.getAvatar(70);
	}

	/**
	 * @return the commentUserName
	 */
	public String getCommentUserName() {
		return commentUserName;
	}

	public void setCommentUserName(String commentUserName) {
		this.commentUserName = commentUserName;
	}

	/**
	 * @return the toCommentUserName
	 */
	public String getToCommentUserName() {
		return toCommentUserName;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

}
