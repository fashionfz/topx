/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月22日
 */
package vo.expertpage;

import common.Constants;

/**
 * 
 * 
 * @ClassName: PageParam
 * @Description: 分页查询参数装载类
 * @date 2013年10月22日 上午11:26:14
 * @author RenYouchao
 * 
 */
public class EPageParam {
	
	/**
	 * 分页，分屏的刷新模式3|1,3|2
	 */
	private String perPageParam;

	/**
	 * 从0开始的页码
	 */
	private int pageIndex;
	/**
	 * 每页个数
	 */
	private int pageSize = Constants.HOME_EXPERT_PAGE_SIZE;
	/**
	 * 排序属性
	 */
	private String sortBy;
	/**
	 * 全文
	 */
	private String fulltext;
	/**
	 * 正序 or 反序 asc desc
	 */
	private String order;
	/**
	 * 过滤
	 */
	private String filter;
	
	

	public EPageParam(String perPageParam, String fulltext,
			String filter) {
		super();
		this.perPageParam = perPageParam;
		this.fulltext = fulltext;
		this.filter = filter;
	}
	

	public int getPageIndex() {
		String[] perPageArr = this.perPageParam.split("\\|");
		int bigPageIndex =  (Integer.parseInt(perPageArr[0]) - 1) * Constants.HOME_EXPERT_PER_NUM;
		int allPageIndex =  bigPageIndex + (Integer.parseInt(perPageArr[1]) - 1);
		return allPageIndex;
	}

	public String getPerPageParam() {
		return perPageParam;
	}

	public void setPerPageParam(String perPageParam) {
		this.perPageParam = perPageParam;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public String getFulltext() {
		return fulltext;
	}

	public void setFulltext(String fulltext) {
		this.fulltext = fulltext;
	}

}
