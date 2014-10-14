/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-1-14
 */
package models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.jpa.JPA;

/**
 * 咨询实体类，保存咨询相关信息
 * 
 * @ClassName: Consult
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-1-14 下午2:34:22
 * @author YangXuelin
 * 
 */
@Entity
@Table(name = "tb_consult")
public class Consult {

	/** 主键自增 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	/** 用户咨询产生的key， uuid */
	@Column(nullable = false, updatable = false)
	public String consultKey;

	/** 预约Id */
	@Column(nullable = true, updatable = false)
	public Long reservationId;

	/** 交易Id */
	@Column(nullable = true)
	public Long merchandiseId;

	/** 用户的id */
	@Column(nullable = false, updatable = false)
	public Long userId;

	/** 专家的用户id */
	@Column(nullable = false, updatable = false)
	public Long expertUserId;

	/** 专家id */
	@Column(nullable = false, updatable = false)
	public Long expertId;

	/** 用户名字（冗余） */
	@Column(nullable = false)
	public String userName;
	
	/** 用户邮箱（冗余） */
	@Column(nullable = false)
	public String email;

	/** 专家名字（冗余） */
	@Column(nullable = false)
	public String expertName;

	/** 用户发起咨询的时间 */
	@Column(nullable = false, updatable = false)
	public Date initiateTime;

	/** 咨询结束时间 */
	@Column
	public Date finishTime;

	/** 专家是否拒绝，默认拒绝 */
	@Column(nullable = false)
	public boolean isReject = true;
	
	/**
	 * 用户咨询前余额
	 */
	@Column
	public BigDecimal balance;
	
	/** 根据用户的余额以及专家的收费标准计算用户可以咨询的时长（秒）**/
	@Column
	public int timeLimit = 0;

	/** 实际通话时长（秒） */
	@Column
	public int talkTime = 0;

	/** 专家收费单价 */
	@Column
	public float expenses;
	
	/** 备注 */
	@Column(nullable = true)
	public String remark;

	public Consult() {

	}

	public Consult(String consultKey, Long reservationId, Long merchandiseId,
			Long userId, Long expertUserId, Long expertId, String userName, String email,
			String expertName, float expenses) {
		this.consultKey = consultKey;
		this.reservationId = reservationId;
		this.merchandiseId = merchandiseId;
		this.userId = userId;
		this.expertUserId = expertUserId;
		this.expertId = expertId;
		this.userName = userName;
		this.email = email;
		this.expertName = expertName;
		this.expenses = expenses;
	}

	/**
	 * 持久化咨询对象
	 */
	public void persist() {
		JPA.em().persist(this);
	}

	/**
	 * 更新当前对象
	 */
	public void merge() {
		JPA.em().merge(this);
	}
	
	/**
	 * 根据key取得咨询记录
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Consult getConsultByKey(String key) {
		List<Consult> consults = JPA.em().createQuery("from Consult c where c.consultKey = :key")
										 .setParameter("key", key)
										 .getResultList();
		if(consults != null && consults.size() > 0) {
			return consults.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * 根据咨询id取得咨询记录
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Consult getConsultById(Long id) {
		List<Consult> consults = JPA.em().createQuery("from Consult c where c.id = :id")
										 .setParameter("id", id)
										 .getResultList();
		if(consults != null && consults.size() > 0) {
			return consults.get(0);
		} else {
			return null;
		}
	}
}
