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
 * @ClassName: EducationExp
 * @Description: 教育经历
 * @date 2013-11-1 上午11:35:23
 * @author YangXuelin
 * 
 */
public class EducationExp implements Serializable {
	
	private static final long serialVersionUID = 3057940030536779688L;
	/**年份**/
	private String year;
	/**请描述**/
	private String yearEnd;
	/**月份**/
	private String month;
	/**学校**/
	private String school;
	/**专业**/
	private String major;
	/**学历**/
	private String academicDegree;
	/**请描述**/
	private String eduInfo;
	

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getAcademicDegree() {
		return academicDegree;
	}

	public void setAcademicDegree(String academicDegree) {
		this.academicDegree = academicDegree;
	}
	
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}


	/**
	 * @return the yearEnd
	 */
	public String getYearEnd() {
		return yearEnd;
	}

	/**
	 * @return the eduInfo
	 */
	public String getEduInfo() {
		return eduInfo;
	}


	/**
	 * @param yearEnd the yearEnd to set
	 */
	public void setYearEnd(String yearEnd) {
		this.yearEnd = yearEnd;
	}

	/**
	 * @param eduInfo the eduInfo to set
	 */
	public void setEduInfo(String eduInfo) {
		this.eduInfo = eduInfo;
	}

	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

}
