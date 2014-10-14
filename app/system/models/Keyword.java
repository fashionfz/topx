/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年1月6日
 */
package system.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;

import play.db.jpa.JPA;

/**
 * @ClassName: Keyword
 * @Description: 关键字实体类
 * @date 2014年1月6日 下午1:35:38
 * @author RenYouchao
 * 
 */
@Entity
@Table(name = "tb_keyword")
public class Keyword {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String words;
 
	
	public Keyword() {
		super();
	}


	public Keyword(Long id, String words) {
		super();
		this.id = id;
		this.words = words;
	}
	
	/**
	 * 获取热门关键词
	 */
	public static List<Keyword> queryKeywordList() {
		List<Keyword> keywordList = JPA.em().createQuery("from Keyword k").getResultList();
		if (CollectionUtils.isEmpty(keywordList)) {
			return new ArrayList<Keyword>();
		}
		return keywordList;
	}
	

}
