/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-10-30
 */
package models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Table;

import play.db.jpa.JPA;

/**
 *
 *
 * @ClassName: RandomCode
 * @Description: 随机码
 * @date 2013-10-30 下午2:41:24
 * @author YangXuelin
 * 
 */
@javax.persistence.Entity
@Table(name = "tb_randomcode")
public class RandomCode implements Serializable {

	private static final long serialVersionUID = -1022213368096943322L;

	/**
	 * 主键自增
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	/**
	 * 邮箱唯一
	 */
	@Column(unique = true, nullable = false)
	public String email;

	/**
	 * 随机码(重置密码用)
	 */
	@Column
	public String code;
	
	/**
	 * 新增或者更新随机码
	 * @param email
	 * @param code
	 */
	public void saveOrUpdateRandomCode(String email, String code) {
		RandomCode randomCode = null;
		try {
			randomCode = (RandomCode)JPA.em().createQuery("from RandomCode rc WHERE rc.email = :email")
					         .setParameter("email", email)
					         .getSingleResult();
		} catch(NoResultException e) {
			randomCode = null;
		}
		if(null == randomCode) {
			this.code = code;
			this.email = email;
			JPA.em().persist(this);
		} else {
			JPA.em().createQuery("UPDATE RandomCode rc SET rc.code = :randomCode WHERE rc.email = :email")
	        .setParameter("randomCode", code)
	        .setParameter("email", email)
	        .executeUpdate();
		}
	}
	
	/**
	 * 获取随机码
	 * @param email
	 * @return
	 */
	public String getRandomCode(String email) {
		String result = null;
		try {
			result = (String)JPA.em().createQuery("Select code from RandomCode rc WHERE rc.email = :email")
					        .setParameter("email", email)
					        .getSingleResult();
		} catch(Exception e) {
			result = null;
		}
		return result;

	}
	
	public static void removeByEmail(String email) {
	    String sql = "delete from RandomCode where email = :email";
	    JPA.em().createQuery(sql).setParameter("email", email).executeUpdate();
	}

}
