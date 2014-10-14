/*
\ * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-1
 */
package vo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import models.Expert;
import models.Gender;
import models.User;
import utils.Assets;
import utils.HelomeUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import common.Constants;
import common.jackjson.JackJsonUtil;

/**
 * @ClassName: ExpertDetailInfo
 * @Description: 专家详细信息(搜索出来点击显示)
 * @date 2013-11-1 上午11:09:59
 * @author RenYouchao
 */
public class ExpertDetail {
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**id唯一标示**/
	private Long id;
	/**头像URL地址**/
	private String headUrl;
	/**专家姓名**/
	private String userName;
	/**专家账号id**/
	private Long userId;
	/**专家性别**/
	private Gender gender = Gender.MAN;
	/**专家加入时间**/
	public String joinDate;
	/**国家**/
	private String country;
	/**国旗地址**/
	private String countryUrl;
	/**时区**/
	private String timeZone;
	/**职业**/
	private String job;
	/**个人说明**/
	private String personalInfo;
	/**技能标签**/
	private List<STagVo> skillsTags = new ArrayList<STagVo>();
	/**工作经历**/
	private List<JobExp> jobExp;
	/**教育经历**/
	private List<EducationExp> educationExp;
	/**收费标准0.0为免费，单位为每分钟**/
	private String expenses;
	/**平均分数**/
	public Float averageScore = 0.0f;
	/**总评价数**/
	public int commentCount = 0;
	/**是否是自己**/
	private Boolean isSelf = false;
	/**专家是否在线**/
	private Boolean isOnline = false;
	/**下一次预约时间**/
	private String firstCanBook;
    /**服务器时间**/
    private String serverDate;
//    /**
//	 * 是否已经收藏，true：已收藏，false：未收藏 （注：默认未收藏）
//	 */
//	private Boolean isFavorite = Boolean.FALSE;
	
	/**
	 * 是否是圈中好友，true：是，false：否
	 */
	private Boolean isFriend = Boolean.FALSE;
    
	/**
	 * 将数据库中查询出来的Expert对象转换为web页面需要的对象
	 * @param expert
	 */
	public void convert(Expert expert) {
		this.id = expert.id;
		this.headUrl = expert.headUrl;
		this.userName = expert.userName;
		this.gender = expert.gender == null ? Gender.MAN : expert.gender;
		this.userId = expert.userId;
		this.country = expert.country;
		this.timeZone = expert.timeZone;
		this.job = expert.job;		
		this.personalInfo = expert.personalInfo;
		this.countryUrl = Constants.countryPicKV.get(expert.country);
		this.serverDate = dateformat.format(new java.util.Date());
		if (expert.payType == Expert.PayType.TIMEBILL){
			this.expenses = "免费";
		} else {
			this.expenses = "面议";
		}
		ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
		List<String> tags = null;
		List<JobExp> jobExps = null;
		List<EducationExp> educationExps = null;
		try {
			tags = HelomeUtil.isEmpty(expert.skillsTags) ? new ArrayList<String>() : objectMapper.readValue(expert.skillsTags, List.class);
			jobExps = HelomeUtil.isEmpty(expert.jobExp) ? null : Arrays.asList(objectMapper.readValue(expert.jobExp, JobExp[].class));
			educationExps = HelomeUtil.isEmpty(expert.educationExp) ? null : Arrays.asList(objectMapper.readValue(expert.educationExp,
					EducationExp[].class));
			for (String st : tags) {
				STagVo stv = new STagVo();
				stv.setTag(st);
				stv.setNoMarkedTag(st);
				this.skillsTags.add(stv);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 教育经历根据毕业日期降序排序
		if (CollectionUtils.isNotEmpty(educationExps)) {
			sortEducationExpList(educationExps);
		}
		// 职业经历排序
		if (CollectionUtils.isNotEmpty(jobExps)) {
			sortJobExpList(jobExps);
		}
		
		this.jobExp = jobExps;
		this.educationExp = educationExps;
		this.averageScore = expert.averageScore == null ? 0f : expert.averageScore;
		this.commentCount = expert.commentNum == null ? 0 : expert.commentNum.intValue();
	}
	
	/**
	 * 职业经历根据截至时间降序、开始时间降序 排序
	 */
	private static void sortJobExpList(List<JobExp> jobExps){
		Collections.sort(jobExps,new Comparator<JobExp>() {
			public int compare(JobExp o1, JobExp o2) {
				if (o1 != null && o2 != null && o1.getEndYear() != null && o2.getEndYear() != null && o1.getEndMonth() != null && o2.getEndMonth() != null) {
					if (StringUtils.isBlank(o2.getEndYear()) || StringUtils.equals(o2.getEndYear(), "至今")) {
						return 1;
					}
					if (StringUtils.isBlank(o1.getEndYear()) || StringUtils.equals(o1.getEndYear(), "至今")) {
						return -1;
					}
					
					int result = Integer.valueOf(o2.getEndYear()) - Integer.valueOf(o1.getEndYear());
					if (result == 0) {
						if (StringUtils.isBlank(o2.getEndMonth())) {
							return 1;
						}
						if (StringUtils.isBlank(o1.getEndMonth())) {
							return -1;
						}
						int result2 = Integer.valueOf(o2.getEndMonth()) - Integer.valueOf(o1.getEndMonth());
						if (result2 == 0) {
							int result3 = Integer.valueOf(o2.getBeginYear()) - Integer.valueOf(o1.getBeginYear());
							if (result3 == 0) {
								return Integer.valueOf(o2.getBeginMonth()) - Integer.valueOf(o1.getBeginMonth());
							} else {
								return result3;
							}
						} else {
							return result2;
						}
					} else {
						return result;
					}
				}
				return 0;
			}
			
		});
	}
	
	//SEAGULL-865 [教育经历]在教育经历排序中没有对月份进行排序处理
	/**
	 * 教育经历根据毕业日期降序排序
	 */
	private void sortEducationExpList(List<EducationExp> educationExps){
		Collections.sort(educationExps,new Comparator<EducationExp>() {
			public int compare(EducationExp o1, EducationExp o2) {
				if (o1 != null && o2 != null && o1.getYearEnd() != null && o2.getYearEnd() != null && o1.getYear() != null && o1.getYear() != null) {
					if (StringUtils.isBlank(o2.getYearEnd())) {
						return 1;
					} else if (StringUtils.isBlank(o1.getYearEnd())) {
						return -1;
					} else {
						int result = Integer.valueOf(o2.getYearEnd()) - Integer.valueOf(o1.getYearEnd());
						if (result == 0) {
							return Integer.valueOf(o2.getYear()) - Integer.valueOf(o1.getYear());
						} else {
							return result;
						}
					}
				}
				return 0;
			}
		});
	}

	/**
	 * 将数据库中查询出来的User对象转换为web页面需要的对象
	 * 
	 * @param expert
	 */
	public void convertFromUser(User user) {
		this.id = -1l;
		if (user == null){
			System.out.println("为空");
		}
		this.userName = user.getName();
		this.userId = user.id;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (user.registerDate == null) {
			user.registerDate = new Date();
		}
		this.joinDate = sdf.format(user.registerDate);
		this.expenses = "0";
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<STagVo> getSkillsTags() {
		return skillsTags;
	}

	public void setSkillsTags(List<STagVo> skillsTags) {
		this.skillsTags = skillsTags;
	}

	public Float getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(Float averageScore) {
		this.averageScore = averageScore;
	}

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
		if (HelomeUtil.isEmpty(headUrl))
			return Assets.getDefaultAvatar();
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

	public String getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}

	public String getCountryUrl() {
		return countryUrl;
	}

	public Boolean getIsSelf() {
		return isSelf;
	}

	public void setIsSelf(Boolean isSelf) {
		this.isSelf = isSelf;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	
	public Boolean getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}


	/**
	 * @return the firstCanBook
	 */
	public String getFirstCanBook() {
		return firstCanBook;
	}

	/**
	 * @param firstCanBook the firstCanBook to set
	 */
	public void setFirstCanBook(String firstCanBook) {
		this.firstCanBook = firstCanBook;
	}
	
	public String getServerDate() {
		return this.serverDate;
	}

	public void setServerDate(String serverDate) {
		this.serverDate = serverDate;
	}

//	public Boolean getIsFavorite() {
//		return isFavorite;
//	}
//
//	public void setIsFavorite(Boolean isFavorite) {
//		this.isFavorite = isFavorite;
//	}
	
	public Boolean getIsFriend() {
		return isFriend;
	}

	public void setIsFriend(Boolean isFriend) {
		this.isFriend = isFriend;
	}

	public String getShortPersonalInfo(int length){
		
		if (StringUtils.isNotBlank(this.personalInfo) && this.personalInfo.length() > length ){
			return this.personalInfo.substring(0, length);
		} else {
			return this.personalInfo;
		}
	}

}
