/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-1
 */
package vo;

import java.io.Serializable;

/**
 *
 *
 * @ClassName: JobExp
 * @Description: 工作经历
 * @date 2013-11-1 上午11:32:53
 * @author YangXuelin
 * 
 */
public class JobExp implements Serializable {

	private static final long serialVersionUID = 9065480459319729037L;

	/**
	 * 开始年份
	 */
	private String beginYear;
	/**
	 * 开始月份
	 */
	private String beginMonth;
	/**
	 * 截止年份
	 */
	private String endYear;
	/**
	 * 截止月份
	 */
	private String endMonth;
	/**
	 * 单位
	 */
	private String company;
	/**
	 * 职责
	 */
	private String duty;
	
	
	private String workInfo;
	
	

	public String getBeginYear() {
		return beginYear;
	}

	public void setBeginYear(String beginYear) {
		this.beginYear = beginYear;
	}

	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getBeginMonth() {
		return beginMonth;
	}

	public void setBeginMonth(String beginMonth) {
		this.beginMonth = beginMonth;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	/**
	 * @return the workInfo
	 */
	public String getWorkInfo() {
		return workInfo;
	}

	/**
	 * @param workInfo the workInfo to set
	 */
	public void setWorkInfo(String workInfo) {
		this.workInfo = workInfo;
	}

}
