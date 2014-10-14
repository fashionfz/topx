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

import org.springframework.util.CollectionUtils;

import play.db.jpa.JPA;
import ext.msg.model.service.MessageService;

/**
 * @ClassName: ExpertComment
 * @Description: 服务者评论
 * @date 2014年8月26日 上午10:30:41
 * @author RenYouchao
 * 
 */
@Entity
@DiscriminatorValue("service")
public class ServiceComment extends Comment {

    public ServiceComment() {

    }

    public ServiceComment(Long id, User commentUser, User toCommentUser, Service toCommentService, String content,
            Date commentTime, Comment parent, int level) {
        super(id, commentUser, toCommentUser, toCommentService, content, commentTime, parent, level);
    }

    public void addServiceComment() {
    	JPA.em().persist(this);
        this.toCommentService.computeAverageScore(this.toCommentUser.id, this.toCommentService.id);
        this.toCommentService.saveOrUpdate();
        MessageService.pushMsgComment(this);
    }

    public static ServiceComment getLatestCommentByServiceId(Long serviceId) {
        String jpql = "from ServiceComment sc where sc.toCommentService.id = :serviceId and sc.parent is null " +
                "order by sc.id desc";
        List<ServiceComment> resultList = JPA.em().createQuery(jpql, ServiceComment.class)
                .setParameter("serviceId", serviceId).setMaxResults(1).getResultList();

        return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
    }
    
	public  void addServiceReplay() {
		JPA.em().persist(this);
		MessageService.pushMsgReply(this);
	}

	
}