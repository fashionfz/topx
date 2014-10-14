/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月22日
 */
package vo.expertpage;

import java.util.List;

/**
 * 
 * 
 * @ClassName: Page
 * @Description: 分页的vo对象
 * @date 2013年10月22日 上午10:53:45
 * @author RenYouchao
 * 
 */
public class Page<T> {

	private int pageSize;
	
	private long totalRowCount;
	/**
	 * 当前页
	 */
	private int pageIndex;
	
	private List<T> list;
	/**
	 * 页面查询参数
	 */
	private String filter;
	
	private String fulltext;
	
	private String country;
	
	private String countryTop;
	
	private String serveState;
	
	private String serveStateTop;
	
	private String expenses;
	
	private String expensesTop;
	
	private String gender;

	private String genderTop;
	
	private String noFoundStr;

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the list
	 */
	public List<T> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List<T> list) {
		this.list = list;
	}

	/**
	 * @param totalRowCount the totalRowCount to set
	 */
	public void setTotalRowCount(long totalRowCount) {
		this.totalRowCount = totalRowCount;
	}

	/**
	 * @param pageIndex the pageIndex to set
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Page(List<T> data, long total, int page, int pageSize) {
		this.list = data;
		this.totalRowCount = total;
		this.pageIndex = page;
		this.pageSize = pageSize;
	}

	public long getTotalRowCount() {
		return totalRowCount;
	}

	public int getPageIndex() {
		return pageIndex;
	}
	public boolean hasPrev() {
		return pageIndex > 1;
	}

	public boolean hasNext() {
		return (totalRowCount / pageSize) >= pageIndex;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getFulltext() {
		return fulltext;
	}

	public void setFulltext(String fulltext) {
		this.fulltext = fulltext;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getServeState() {
		return serveState;
	}

	public void setServeState(String serveState) {
		this.serveState = serveState;
	}

	public String getExpenses() {
		return expenses;
	}

	public void setExpenses(String expenses) {
		this.expenses = expenses;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCountryTop() {
		return countryTop;
	}

	public void setCountryTop(String countryTop) {
		this.countryTop = countryTop;
	}

	public String getServeStateTop() {
		return serveStateTop;
	}

	public void setServeStateTop(String serveStateTop) {
		this.serveStateTop = serveStateTop;
	}

	public String getExpensesTop() {
		return expensesTop;
	}

	public void setExpensesTop(String expensesTop) {
		this.expensesTop = expensesTop;
	}

	public String getGenderTop() {
		return genderTop;
	}

	public void setGenderTop(String genderTop) {
		this.genderTop = genderTop;
	}

	public String getNoFoundStr() {
		return noFoundStr;
	}

	public void setNoFoundStr(String noFoundStr) {
		this.noFoundStr = noFoundStr;
	}

}
