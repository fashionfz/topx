/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年12月18日
 */
package vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import models.Comment;
import models.Expert;
import models.Expert.PayType;
import models.SkillTag;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import utils.Assets;
import utils.HelomeUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import common.Constants;
import common.jackjson.JackJsonUtil;
import controllers.routes;

/**
 * @ClassName: TopExpert
 * @Description: 置顶专家的VO对象
 * @date 2013年12月18日 上午10:07:13
 * @author RenYouchao
 * 
 */
public class TopExpert implements java.io.Serializable {

	private static final long serialVersionUID = -8594349763483140910L;

	/** 专家Id **/
	private Long expertId;
	/** 账户id **/
	private Long userId;
	/** 专家姓名 **/
	private String userName;
	/** 专家职业 **/
	private String job;
	/** 公司 **/
	private String company;
	/** 国家图片 **/
	private String countryUrl;
	/** 国家 **/
	private String country;
	/** 头像地址 **/
	private String headUrl;
	/** 性别 **/
	private Integer gender;
	/**被收藏数**/
	private Long collectNum =0L;
	/** 技能标签模式 ["ddd",""] */
	private String skillsTags;
	/** 成交次数 **/
	private Long dealNum = 0L;
	/**
	 * 个人说明
	 */
	public String personalInfo;
	/**收费标准**/
	private Long expenses = 0L;
	/**支付类型**/
	private PayType payType;
	/**评论平均分**/
	private Float averageScore;
	/**评论次数**/
	private Long commentNum;
	
	/**
	 * 技能标签模式 ，list集合形式
	 */
	private List<String> skillsTagList;
	
	/**
	 * @deprecated 不使用，改用queryTopExpertList()方法
	 * @param cates
	 * @param map
	 */
	public static void setTopExpertList(List<TopCate> cates,HashMap map) {
		ArrayList<TopExpert> tops = new ArrayList<TopExpert>();
		List<Expert> experts = Expert.listExpertTops();
		for (Expert expert : experts) {
			TopExpert topExpert = new TopExpert();
			topExpert.setExpertId(expert.id);
			topExpert.setUserName(expert.userName);
			topExpert.setCountryUrl(Constants.countryPicKV.get(expert.country));
			topExpert.setCountry(expert.country);
			topExpert.setUserId(expert.userId);
			topExpert.setJob(expert.job);
			topExpert.setDealNum(expert.dealNum);
			topExpert.setCollectNum(expert.collectNum);
			topExpert.setExpenses(expert.expenses);
			topExpert.setSkillsTags(expert.skillsTags);
			topExpert.setGender(expert.getGenderWithDefault().ordinal());
			topExpert.setPayType(expert.payType);
			topExpert.setAverageScore(expert.averageScore);
			topExpert.setCommentNum(expert.collectNum);
			ObjectMapper objectMapper = JackJsonUtil.getMapperInstance();
			List<JobExp> jobExps = null;
			try {
				jobExps = StringUtils.isEmpty(expert.jobExp) ? null : Arrays.asList(objectMapper.readValue(
						expert.jobExp, JobExp[].class));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(CollectionUtils.isNotEmpty(jobExps))
				topExpert.setCompany(jobExps.get(0).getCompany());
			topExpert.setHeadUrl(expert.headUrl);
			topExpert.setGender(expert.gender.ordinal());
			Set<SkillTag> ins = expert.inTags;
			for (TopCate tc : cates) 
				for (SkillTag st : ins) 
					if (st.id.equals(tc.getId()) && !map.containsKey(topExpert.getUserId())){
						tc.getTopExperts().add(topExpert);
						map.put(topExpert.getUserId(), null);
					}
			tops.add(topExpert);
		}
	}
	
	/**
	 * 查询置顶专家
	 * @param cates
	 * @param map
	 */
	public static void queryTopExpertList(List<TopCate> cates) {
		// 1、查询出所有的置顶专家，不是分行业查询，减少数据库的读取次数
		List<Expert> expertList = Expert.queryExpertTops();
		// 1、遍历List<TopCate>，依次设置对应行业的置顶专家
		ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
		for (TopCate tc : cates) {
			ArrayList<TopExpert> tops = new ArrayList<TopExpert>();
			Long industryId = tc.getId();
			for (Expert expert : expertList) {
					if (expert.topIndustry - industryId==0) {
						TopExpert topExpert = new TopExpert();
						topExpert.setExpertId(expert.id);
						topExpert.setUserName(expert.userName);
						topExpert.setCountryUrl(Constants.countryPicKV.get(expert.country));
						topExpert.setCountry(expert.country);
						topExpert.setUserId(expert.userId);
						topExpert.setJob(expert.job);
						topExpert.setDealNum(expert.dealNum);
						topExpert.setCollectNum(expert.collectNum);
						topExpert.setExpenses(expert.expenses);
						topExpert.setSkillsTags(expert.skillsTags);
						try {
							topExpert.setSkillsTagList(HelomeUtil.isEmpty(expert.skillsTags) ? new ArrayList<String>() : objectMapper.readValue(expert.skillsTags, List.class));
						} catch (Exception e) {
							e.printStackTrace();
						} 
						topExpert.setGender(expert.getGenderWithDefault().ordinal());
						topExpert.setPersonalInfo(expert.personalInfo);
						topExpert.setPayType(expert.payType);
						topExpert.setAverageScore(expert.averageScore);
						topExpert.setCommentNum(expert.commentNum);
						List<JobExp> jobExps = null;
						try {
							jobExps = StringUtils.isEmpty(expert.jobExp) ? null : Arrays.asList(objectMapper.readValue(
									expert.jobExp, JobExp[].class));
						} catch (Exception e) {
							e.printStackTrace();
						}
						if(CollectionUtils.isNotEmpty(jobExps))
							topExpert.setCompany(jobExps.get(0).getCompany());
						topExpert.setHeadUrl(expert.headUrl);
						
						tops.add(topExpert);
					}
			}
			tc.setTopExperts(tops);
		}
	}

	public Long getExpertId() {
		return expertId;
	}

	public void setExpertId(Long expertId) {
		this.expertId = expertId;
	}

	public String getUserName() {
		if (userName.length() > 15)
			userName = userName.substring(0, 14);
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getCountryUrl() {
		return this.countryUrl;
	}

	public void setCountryUrl(String countryUrl) {
		this.countryUrl = countryUrl;
	}

	public String getHeadUrl() {
		if (StringUtils.isBlank(headUrl)) {
			return Assets.getDefaultAvatar();
		}
		return Assets.at(headUrl);
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getLinkUrl() {
		return routes.ExpertApp.detail(this.userId).url();
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @param company
	 *            the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	public Long getCollectNum() {
		if (collectNum == null) {
			return 0L;
		}
		return collectNum;
	}

	public void setCollectNum(Long collectNum) {
		this.collectNum = collectNum;
	}

	public String getSkillsTags() {
		return skillsTags;
	}

	public void setSkillsTags(String skillsTags) {
		this.skillsTags = skillsTags;
	}

	public Long getDealNum() {
		if (dealNum == null) {
			return 0L;
		}
		return dealNum;
	}

	public void setDealNum(Long dealNum) {
		this.dealNum = dealNum;
	}

	public String getPersonalInfo() {
		return personalInfo;
	}

	public void setPersonalInfo(String personalInfo) {
		this.personalInfo = personalInfo;
	}

	public Long getExpenses() {
		if (expenses == null) {
			return 0L;
		}
		return expenses;
	}

	public void setExpenses(Long expenses) {
		this.expenses = expenses;
	}

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public Float getAverageScore() {
		if (averageScore == null) {
			return 0f;
		}
        return averageScore;
    }

    public void setAverageScore(Float averageScore) {
        this.averageScore = averageScore;
    }

    public Long getCommentNum() {
		if (commentNum == null) {
			return 0L;
		}
        return commentNum;
    }

    public void setCommentNum(Long commentNum) {
        this.commentNum = commentNum;
    }

	public List<String> getSkillsTagList() {
		return skillsTagList;
	}

	public void setSkillsTagList(List<String> skillsTagList) {
		this.skillsTagList = skillsTagList;
	}

}
