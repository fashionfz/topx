/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年12月26日
 */
package vo;

/**
 * @ClassName: TagListVo
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2013年12月26日 上午11:19:43
 * @author RenYouchao
 * 
 */
public class TagListVo {
	
	/**标签标示**/
	private Long tagId;
	/**行业标示**/
	private Long iid;
	/**标签名称**/
	private String tagName;
	/**是否查询条件**/
	private Boolean isCurr = false;
	/**链接**/
	private String href;

	/**
	 * @return the tagId
	 */
	public Long getTagId() {
		return tagId;
	}

	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * @return the isCurr
	 */
	public Boolean getIsCurr() {
		return isCurr;
	}


	/**
	 * @param tagId the tagId to set
	 */
	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	/**
	 * @param tagName the tagName to set
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @param isCurr the isCurr to set
	 */
	public void setIsCurr(Boolean isCurr) {
		this.isCurr = isCurr;
	}

	/**
	 * @return the iid
	 */
	public Long getIid() {
		return iid;
	}

	/**
	 * @param iid the iid to set
	 */
	public void setIid(Long iid) {
		this.iid = iid;
	}

	/**
	 * @return the href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @param href the href to set
	 */
	public void setHref(String href) {
		this.href = href;
	}



	

}
