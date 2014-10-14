/*
\ * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-1
 */
package vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import models.Expert;
import models.Gender;
import models.SkillTag;
import models.User;

import org.apache.commons.lang3.StringUtils;

import utils.Assets;
import utils.HelomeUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.jackjson.JackJsonUtil;

/**
 * 
 * 
 * @ClassName: ExpertDetailInfo
 * @Description: 专家详细信息
 * @date 2013-11-1 上午11:09:59
 * @author YangXuelin
 * 
 */
public class ExpertDetailInfo {

	private Long id;

	private Long userId;
	
	private String headUrl;

	private String userName;

	private Gender gender = Gender.MAN;

	private String country;

	private List<Long> inId = new ArrayList<Long>();

	private String timeZone;

	private String job;

	private String personalInfo;

	private List<String> skillsTags = new ArrayList<String>();

	private List<JobExp> jobExp = new ArrayList<JobExp>();

	private List<EducationExp> educationExp = new ArrayList<EducationExp>();

	private String expenses;

	private String payType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getHeadUrl() {
		if (HelomeUtil.isEmpty(headUrl)) {
			return Assets.getDefaultAvatar();
		}
		return Assets.at(headUrl);
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getPersonalInfo() {
		return personalInfo;
	}

	public void setPersonalInfo(String personalInfo) {
		this.personalInfo = personalInfo;
	}

	public String getExpenses() {
		return expenses;
	}

	public void setExpenses(String expenses) {
		this.expenses = expenses;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<String> getSkillsTags() {
		return skillsTags;
	}

	public void setSkillsTags(List<String> skillsTags) {
		this.skillsTags = skillsTags;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public List<JobExp> getJobExp() {
		return jobExp;
	}

	public void setJobExp(List<JobExp> jobExp) {
		this.jobExp = jobExp;
	}

	public List<EducationExp> getEducationExp() {
		return educationExp;
	}

	public void setEducationExp(List<EducationExp> educationExp) {
		this.educationExp = educationExp;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void convertIn(Expert expert,models.User user){
		Set<SkillTag> insets = expert.inTags;
		for (SkillTag st :insets){
			this.inId.add(st.id);
		}
		this.convert(expert,user);
	}
	
	public void convert(Expert expert,User user) {
		this.id = expert.id;
		this.userId = expert.user.getId();
		this.headUrl = expert.headUrl;
		this.userName = expert.userName;
		if (StringUtils.isEmpty(expert.userName))
			this.userName= user.getEmail();
		if (expert.payType != null){
			this.payType = expert.payType.name();
		}
		this.gender = expert.gender;
		this.country = expert.country;
		this.timeZone = expert.timeZone;
		this.job = expert.job;
		this.personalInfo = expert.personalInfo;
		this.expenses = String.valueOf(expert.expenses);
		ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
		try {
			if (StringUtils.isNotBlank(expert.skillsTags)) {
				this.skillsTags = objectMapper.readValue(expert.skillsTags, List.class);
			}
			if (StringUtils.isNotBlank(expert.jobExp)) {
				this.jobExp = Arrays.asList(objectMapper.readValue(expert.jobExp, JobExp[].class));
			}
			if (StringUtils.isNotBlank(expert.educationExp)) {
				this.educationExp = Arrays.asList(objectMapper.readValue(expert.educationExp, EducationExp[].class));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	/**
	 * @return the inId
	 */
	public List<Long> getInId() {
		return inId;
	}

	/**
	 * @param inId the inId to set
	 */
	public void setInId(List<Long> inId) {
		this.inId = inId;
	}
}
