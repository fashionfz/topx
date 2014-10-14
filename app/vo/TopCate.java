/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年1月16日
 */
package vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: TopCate
 * @Description: 首页的行业专家类
 * @date 2014年1月16日 上午10:01:50
 * @author RenYouchao
 * 
 */
public class TopCate implements java.io.Serializable {
    
    public static final String version = "v1.1";

	/**主键**/
	private Long id;
	/**标签名称**/
	private String tagName;
	/**首页的专家**/
	private List<TopExpert> topExperts = new ArrayList<TopExpert>();

	public Long getId() {
		return id;
	}

	public List<TopExpert> getTopExperts() {
		return topExperts;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void setTopExperts(List<TopExpert> topExperts) {
		this.topExperts = topExperts;
	}

	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName the tagName to set
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

}
