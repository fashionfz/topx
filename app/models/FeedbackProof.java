/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-31
 */
package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.jpa.JPA;

/**
 * 用户投诉凭证（截图）
 *
 * @ClassName: FeedbackProof
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2013-12-31 下午3:17:39
 * @author YangXuelin
 * 
 */
@Entity
@Table(name = "tb_fb_proof")
public class FeedbackProof {
	
	/**
	 * 主键自增
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	/**
	 * 截图名
	 */
	@Column(nullable = false, updatable = false)
	public String fileName;
	
	/**
	 * 凭证路径
	 */
	@Column(nullable = false, updatable = false)
	public String path;
	
	/**
	 * 保存用户凭证
	 */
	public void persist() {
		JPA.em().persist(this);
	}
	
}
