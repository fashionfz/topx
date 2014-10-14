/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-31
 */
package models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;

import play.db.jpa.JPA;

/**
 * @ClassName: Attach
 * @Description: 附件类
 * <br/> Hibernate继承映射 方式
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="attachType")
@Table(name = "tb_attachs")
public class Attach {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String fileName;

	public String path;
	
	public String suffix;
	
	public BigDecimal size;
	
	public Date createDate;
	
	public Long createUserId;
	
	public String createUserName;
	
	public Boolean isDelete = false;
	/** 排序编号 */
	public Long seq;
	
	public Attach() {
	}
	
	public Attach(String fileName, String path, String suffix,
			BigDecimal size, Date createDate, Long createUserId,
			String createUserName, Boolean isDelete) {
		this.fileName = fileName;
		this.path = path;
		this.suffix = suffix;
		this.size = size;
		this.createDate = createDate;
		this.createUserId = createUserId;
		this.createUserName = createUserName;
		this.isDelete = isDelete;
	}
	
	public void saveOrUpdate() {
		if (id != null) {
			JPA.em().merge(this);
		} else {
			JPA.em().persist(this);
		}
	}
	
	
	/**
	 * 根据id获取对应的附件
	 * @param <T>
	 * @return
	 */
	public static <T> T queryById(Long id,Class<T> entityClass) {
		List<T> attachList = JPA.em().createQuery("from " + entityClass.getName() + " where id = :id",entityClass).setParameter("id", id).getResultList();
		if(CollectionUtils.isNotEmpty(attachList)){
			return attachList.get(0);
		}
		return null;
	}
	
	/**
	 * 根据id获取对应的附件
	 * @param <T>
	 * @return
	 */
	public <T extends Attach> List<T> queryByAttachId(Long attachId) {
		List<T> attachList = JPA.em().createQuery("from " + this.getClass().getName() + " where attach_id = :attachId").setParameter("attachId", attachId).getResultList();
		if (CollectionUtils.isNotEmpty(attachList)) {
			return attachList;
		}
		return new ArrayList<T>();
	}
	
	/**
	 * 根据Attach的id获取对应的Attach，返回结果的顺序和传入List的顺序一致，数据库中不存在的数据将不会返回
	 */
	public static <T extends Attach> List<T> queryAttachListByIds(List<Long> s, Class<T> entityClass) {
	    List<T> result = new ArrayList<T>();
	    
		if (CollectionUtils.isNotEmpty(s)) {
			List<T> attachList = JPA.em().createQuery("from " + entityClass.getName() + " where id in (:ids)", entityClass)
					.setParameter("ids", s).getResultList();
			
			for (Long id : s) {
                for (T t : attachList) {
                    if (t.id.equals(id)) {
                        result.add(t);
                    }
                }
            }
		}
		return result;
	}
	
	/**
	 * 根据Attach的id和 createUserId获取对应的Attach，返回结果的顺序和传入List的顺序一致，数据库中不存在的数据将不会返回
	 */
	public <T extends Attach> List<T> queryAttachListByIdsAndCreateUserId(List<Long> s, Long createUserId) {
	    List<T> result = new ArrayList<T>();
	    
		if (CollectionUtils.isNotEmpty(s)) {
			List<T> attachList = JPA.em().createQuery("from " + this.getClass().getName() + " where createUserId = :createUserId and id in (:ids)")
					.setParameter("createUserId", createUserId).setParameter("ids", s).getResultList();
			if (CollectionUtils.isNotEmpty(attachList)) {
				for (Long id : s) {
					for (T t : attachList) {
						if (t.id.equals(id)) {
							result.add(t);
						}
					}
				}
			}
		}
		return result;
	}
	
	/*
	 * 逻辑删除
	 */
	public static <T> void deleteByIds(List<Long> attachIdList,Class<T> entityClass) {
		if (CollectionUtils.isNotEmpty(attachIdList)) {
			JPA.em().createQuery("update " + entityClass.getName() + " set isDelete=true where id in (:ids)").setParameter("ids", attachIdList).executeUpdate();
		}
	}
	
	
}
