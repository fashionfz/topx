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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.db.jpa.JPA;

/**
 *
 *
 * @ClassName: Suggestion
 * @Description: 用户建议
 * @date 2013-12-31 上午10:39:52
 * @author YangXuelin
 * 
 */
@Entity
@Table(name = "tb_suggestion")
public class Suggestion {
	
	/**主键自增**/
	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	/**联系方式姓名**/
	public String userName;
	/**电话**/
	public String phone;
	/**邮箱**/
	public String email;
	/**qq**/
	public String qq;
	/**建议具体描述**/
	@Column(length = 4000)
	public String content;
	/**建议时间**/
	public Date createTime;
	/**超链地址**/
	public String href;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name="attach_id")
	public Set<AttachOfSuggestion> attach = new HashSet<AttachOfSuggestion>();
	/**
	 * 保存建议
	 */
	public void persist() {
		this.createTime = new Date();
		JPA.em().persist(this);
	}
	
	/**
	 * 获得所有建议记录，分页
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Suggestion> getSuggestions(int startRow, int pageSize, String sortStr, String searchText) {
        String sql = "from Suggestion s where s.userName like :searchText order by " + sortStr;
        List<Suggestion> data = JPA.em().createQuery(sql)
        								.setParameter("searchText", searchText)
					                    .setFirstResult(startRow)
					                    .setMaxResults(pageSize)
					                    .getResultList();
        return data;
	}
	
	/**
	 * 获得建议记录数
	 * 
	 * @param searchText
	 * @return
	 */
	public static int getSuggestionsCount(String searchText) {
		String countSql = "select count(s) from Suggestion s where s.userName like :searchText";
        Long total = (Long) JPA.em().createQuery(countSql).setParameter("searchText", searchText).getSingleResult();
        return total == null ? 0 : total.intValue();
	}

}
