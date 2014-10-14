/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-1-13
 */
package system.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Query;
import javax.persistence.Table;

import play.db.jpa.JPA;

/**
 * 操作日志
 *
 * @ClassName: OperateLog
 * @Description: 操作日志
 * @date 2014-1-13 上午11:16:34
 * @author YangXuelin
 * 
 */
@Entity
@Table(name = "tb_log")
public class OperateLog {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	/** 操作人 */
	@Column(nullable = false)
	public String operator;
	
	/** 操作时间 */
	@Column(nullable = false)
	public Date operateTime;
	
	@Column(nullable = false)
	public String operateIp;
	
	/** 操作 */
	@Column(nullable = true)
	public Long menuId;
	
	@Column(nullable = false)
	public String menuName;
	
	@Column
	public String paramters;
	
	@Column
	public boolean result;
	
	@Lob()
	@Column(name="describle",columnDefinition = "TEXT")
	public String describle;
	
	
	
	
	
//	/**
//	 * 操作所属模块
//	 */
//	@Enumerated(EnumType.ORDINAL)
//	@Column(length = 1)
//	public ModuleEnum module = ModuleEnum.OTHER;
	
//	/**
//	 * 操作所属模块
//	 *
//	 * @ClassName: Module
//	 * @Description: 
//	 * @date 2014-1-13 上午11:44:21
//	 * @author YangXuelin
//	 *
//	 */
//	public enum ModuleEnum {
//		TAG("标签"), 
//		USER("用户账户"),
//		FEEDBACK("用户反馈"),
//		OTHER("其他");
//		
//		private String name;
//		
//		private ModuleEnum(String name) {
//			this.name = name;
//		}
//		
//		public String getName() {
//			return this.name;
//		}
//		
//		public static ModuleEnum getModuleEnum(int ordinal) {
//			if(TAG.ordinal() == ordinal) {
//				return TAG;
//			} else if(USER.ordinal() == ordinal) {
//				return USER;
//			} else if(FEEDBACK.ordinal() == ordinal) {
//				return FEEDBACK;
//			} else {
//				return OTHER;
//			}
//		}
//	}
	
	/** 
	 * @return id 
	 */
	public Long getId() {
		return id;
	}

	/** 
	 * @param id 要设置的 id 
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/** 
	 * @return operator 
	 */
	public String getOperator() {
		return operator;
	}

	/** 
	 * @param operator 要设置的 operator 
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/** 
	 * @return operateTime 
	 */
	public Date getOperateTime() {
		return operateTime;
	}

	/** 
	 * @param operateTime 要设置的 operateTime 
	 */
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	/** 
	 * @return operateIp 
	 */
	public String getOperateIp() {
		return operateIp;
	}

	/** 
	 * @param operateIp 要设置的 operateIp 
	 */
	public void setOperateIp(String operateIp) {
		this.operateIp = operateIp;
	}

	/** 
	 * @return menuId 
	 */
	public Long getMenuId() {
		return menuId;
	}

	/** 
	 * @param menuId 要设置的 menuId 
	 */
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	/** 
	 * @return menuName 
	 */
	public String getMenuName() {
		return menuName;
	}

	/** 
	 * @param menuName 要设置的 menuName 
	 */
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	/** 
	 * @return paramters 
	 */
	public String getParamters() {
		return paramters;
	}

	/** 
	 * @param paramters 要设置的 paramters 
	 */
	public void setParamters(String paramters) {
		this.paramters = paramters;
	}

	/**
	 * 持久化操作记录
	 */
	public void persist() {
		this.operateTime = new Date();
		JPA.em().persist(this);
	}
	
	
	/** 
	 * @return result 
	 */
	public boolean isResult() {
		return result;
	}

	/** 
	 * @param result 要设置的 result 
	 */
	public void setResult(boolean result) {
		this.result = result;
	}

	
	/** 
	 * @return describle 
	 */
	public String getDescrible() {
		return describle;
	}

	/** 
	 * @param describle 要设置的 describle 
	 */
	public void setDescrible(String describle) {
		this.describle = describle;
	}
	
	public static void save(OperateLog log){
		try{
			EntityTransaction tr = JPA.em().getTransaction();
			if(tr.isActive()){
				JPA.em().merge(log);
			}else{
				tr.begin();
				JPA.em().merge(log);
				tr.commit();
			}
		}catch(Exception e){
			System.out.println(log.getMenuName()+"日志保存失败");
		}
	}

	/**
	 * 获取操作日志记录数
	 * 
	 * @param searchText
	 * @param module
	 * @return
	 */
	public static int getOperateLogCount(String searchText,String result) {
		String countHql = "select count(ol) from OperateLog ol where 1=1";
		if(searchText != null && !"".equals(searchText)) {
			countHql += " and ol.menuName like :searchText";
		}
		if(result!=null&&!"".equals(result)){
			countHql +=" and ol.result = :result";
		}
		Query query = JPA.em().createQuery(countHql);
		if(searchText != null && !"".equals(searchText)) {
			query.setParameter("searchText", searchText);
		}
		if(result!=null&&!"".equals(result)){
			query.setParameter("result", Boolean.parseBoolean(result));
		}
		Long total = (Long) query.getSingleResult();
        return total == null ? 0 : total.intValue();
	}
	
	/**
	 * 获取操作日志记录
	 * 
	 * @param searchText
	 * @param module
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<OperateLog> getOperateLogs(int startRow, int pageSize, String searchText,String result, String sortStr) {
		String hql = "from OperateLog ol where 1=1";
		if(searchText != null && !"".equals(searchText)) {
			hql += " and ol.menuName like :searchText";
		}
		if(null!=result && !"".equals(result)){
			hql +=" and ol.result = :result";
		}
		hql += " order by " + sortStr;
		Query query = JPA.em().createQuery(hql);
		if(searchText != null && !"".equals(searchText)) {
			query.setParameter("searchText", searchText);
		}
		if(null!=result && !"".equals(result)){
			query.setParameter("result", Boolean.parseBoolean(result));
		}
		query.setFirstResult(startRow);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}

}
