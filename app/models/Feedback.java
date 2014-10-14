/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-31
 */
package models;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.JPA;

/**
 * 用户投诉
 *
 * @ClassName: Feedback
 * @Description: 用户投诉
 * @date 2013-12-31 上午11:21:04
 * @author YangXuelin
 * 
 */
@Entity
@Table(name = "tb_feedback")
public class Feedback {

	/**
	 * 主键自增
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	/**
	 * 投诉者
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")  
	public User user;
	
	/**
	 * 投诉者姓名（冗余）
	 */
	@Column(nullable = false, updatable = false)
	public String username;
	
	/**
	 * 被投诉用户
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="cuser_id")  
	public User cuser;
	
	/**
	 * 被投诉专家姓名（冗余）
	 */
	@Column(nullable = false)
	public String cuserName;
	
	/**
	 * 投诉凭证
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name="attach_id")
	public Set<AttachOfFeedback> attach = new HashSet<AttachOfFeedback>(0);

	
	/**
	 * 交易号(冗余)
	 */
	@Column(nullable = false, updatable = false)
	public String number;
	
	/**
	 * 内容
	 */
	@Column(updatable = false)
	public String content;
	
	/**
	 * 投诉时间
	 */
	@Column(nullable = false, updatable = false)
	public Date createTime;
	
	/**
	 * 投诉处理时间
	 */
	@Column(nullable = true, insertable = false)
	public Date handleTime;
	
	/**
	 * 投诉状态,默认未处理
	 */
	@Column(nullable = false)
	public FeedbackStatus status = FeedbackStatus.UNHANDLE;
	
	/**
	 * 投诉状态：未处理，已处理
	 * @ClassName: FeedbackStatus
	 * @Description: 投诉状态
	 * @date 2013-12-31 上午11:46:16
	 * @author YangXuelin
	 *
	 */
	public enum FeedbackStatus {
		UNHANDLE, PROCESSED;
		
		public static FeedbackStatus getStatusByOrdinal(int ordinal) {
			if(PROCESSED.ordinal() == ordinal) {
				return PROCESSED;
			} else {
				return UNHANDLE;
			} 
		}
	}
	
	/**
	 * 保存投诉
	 */
	public void persist() {
		this.createTime = new Date();
		JPA.em().persist(this);
	}
	
	/**
	 * 更新投诉信息
	 */
	public void merge() {
		this.handleTime = new Date();
		JPA.em().merge(this);
	}
	
	/**
	 * 根据id获取投诉记录
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Feedback getFeedback(Long id) {
		List<Feedback> lists = JPA.em().createQuery("from Feedback f where f.id = :id")
									   .setParameter("id", id)
									   .getResultList();
	    if(lists != null && lists.size() > 0) {
	    	return lists.get(0);
	    } else {
	    	return null;
	    }
	}
	
	/**
	 * 投诉记录数
	 * 
	 * @param searchText
	 * @param status
	 * @return
	 */
	public static int getFeedbacksCount(String searchText, FeedbackStatus status) {
		if(status != null) {
			String countSql = "select count(f) from Feedback f where f.status = :status and (f.username like :searchText or f.cuserName like :searchText or f.number like :searchText)";
			Long total = (Long) JPA.em().createQuery(countSql).setParameter("status", status).setParameter("searchText", searchText).getSingleResult();
	        return total == null ? 0 : total.intValue();
		}
		String countSql = "select count(f) from Feedback f where f.username like :searchText or f.cuserName like :searchText or f.number like :searchText";
        Long total = (Long) JPA.em().createQuery(countSql).setParameter("searchText", searchText).getSingleResult();
        return total == null ? 0 : total.intValue();
	}
	
	/**
	 * 投诉记录，分页
	 * 
	 * @param startRow
	 * @param pageSize
	 * @param searchText
	 * @param status
	 * @param sortStr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Feedback> getFeedbacks(int startRow, int pageSize, String searchText, FeedbackStatus status, String sortStr) {
		if(status != null) {
			String sql = "from Feedback f left join fetch f.proof fp where f.status = :status and (f.username like :searchText or f.cuserName like :searchText or f.number like :searchText) order by " + sortStr;
			return JPA.em().createQuery(sql)
										  .setParameter("status", status)
										  .setParameter("searchText", searchText)
										  .setFirstResult(startRow)
										  .setMaxResults(pageSize)
										  .getResultList();
		}
		String sql = "from Feedback f left join fetch f.proof fp where f.username like :searchText or f.cuserName like :searchText or f.number like :searchText order by " + sortStr;
		return JPA.em().createQuery(sql)
									  .setParameter("searchText", searchText)
									  .setFirstResult(startRow)
									  .setMaxResults(pageSize)
									  .getResultList();
        
	}
	
	/**
	 * 根据交易id和用户id查询投诉记录数
	 * 
	 * @param merchandiseId
	 * @param userId
	 * @return
	 */
	public static int getFeedbackCountByUserAndMer(Long merchandiseId, Long userId) {
		String countSql = "select count(f) from Feedback f where f.user.id = :userId and f.merchandise.id = :merchandiseId";
		Long total = (Long) JPA.em().createQuery(countSql)
									.setParameter("userId", userId)
									.setParameter("merchandiseId", merchandiseId)
									.getSingleResult();
        return total == null ? 0 : total.intValue();
	}
}
