/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年8月26日
 */
package models;

import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.collections.CollectionUtils;

import play.db.jpa.JPA;
import controllers.base.ObjectNodeResult;
import ext.msg.model.service.MessageService;
import ext.sns.model.UserOAuth;
import ext.sns.service.SNSService;
import ext.sns.service.UserOAuthService;

/**
 * @ClassName: ExpertComment
 * @Description: 服务者评论
 * @date 2014年8月26日 上午10:30:41
 * @author RenYouchao
 * 
 */
@Entity
@DiscriminatorValue("expert")
public class ExpertComment extends Comment {
	
	
	public ExpertComment(){
		
	}
	
	
    public ExpertComment(Long id, User commentUser, User toCommentUser, String content, Date commentTime, Comment parent,
            int level) {
        super(id,commentUser,toCommentUser,content,commentTime,parent,level);
    }
	
	public  void addExpertComment() {
		JPA.em().persist(this);
		Expert expert = Expert.getExpertByUserId(toCommentUser.id);
		expert.computeAverageScore(toCommentUser.id);
		expert.saveOrUpate();
		MessageService.pushMsgComment(this);
	}
	
	public  void addExpertReplay() {
		JPA.em().persist(this);
		MessageService.pushMsgReply(this);
	}

}