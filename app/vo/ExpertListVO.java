package vo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Expert;
import models.Expert.PayType;
import models.Gender;

import org.apache.commons.lang3.StringUtils;

import utils.Assets;
import utils.RegexUtils;
import controllers.routes;

public class ExpertListVO {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


	private Long id;
	/** 专家姓名 **/
	private String userName;
	/** 专家性别有默认值 **/
	private Gender gender = Gender.MAN;

	private Long userId;

	/** 专家加入时间 **/
	private Date joinDate;
	/** 技能标签模式 JAVA;UI设计; **/
	private  List<STagVo> skillsTags;
	/** 国别 **/
	private String country;

	private String countryUrl;
	/** 头像URL地址 **/
	private String headUrl;
	/** getURL地址 **/
	private String linkUrl;
	/** 预约url **/
	private String reserveUrl;
	/** 咨询url **/
	private String consultUrl;
	/** 职业 **/
	private String job;
	/** 个人说明 **/
	private String personalInfo;
	/** 收费标准0.0为免费，单位为每分钟 **/
	private String expenses;
	/** 是否在线 **/
	private Boolean isOnline = false;
	/** 评价次数 **/
	private Long commentNum = 0L;
	/** 平均分数最小单位0.5 **/
	private Float averageScore = 0.0f;
	/** 是否是自己**/
	private Boolean isSelf = false;
	
	private PayType payType;
	
	/**
	 * 是否已经收藏，true：已收藏，false：未收藏 （注：默认未收藏）
	 */
	public Boolean isFavorite = Boolean.FALSE;

	public enum ServeState {
		CONSULT, RESERVE
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		if (this.userName == null)
			return "";
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

	public String getJoinDate() {
		if (joinDate != null)
			return dateFormat.format(joinDate);
		else
			return "";
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
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
	
	public String getJob() {
		if (this.job == null)
			return "";
		return this.job;
	}

	public String getJobNoMark() {
		if (this.job == null)
			return "";
		return RegexUtils.replaceFont(this.job);
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getPersonalInfo() {
		if (this.personalInfo == null)
			return "";
		return personalInfo;
	}

	public void setPersonalInfo(String personalInfo) {
		this.personalInfo = personalInfo;
	}

	public String getExpenses() {
		if (this.payType == Expert.PayType.TIMEBILL) {
			return "免费";
		} else if (this.payType == Expert.PayType.NEGOTIABLE) {
			return "面议";
		}
		return expenses;
	}

	/**
	 * @return the isOnline
	 */
	public Boolean getIsOnline() {
		return isOnline;
	}

	/**
	 * @param isOnline the isOnline to set
	 */
	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}

	public void setExpenses(String expenses) {
		this.expenses = expenses;
	}

	public List<STagVo> getSkillsTags() {
		if (skillsTags == null)
			return new ArrayList<STagVo>();
		return skillsTags;
	}

	public void setSkillsTags(List<STagVo> skillsTags) {
		this.skillsTags = skillsTags;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryUrl() {
		return this.countryUrl;
	}

	public void setCountryUrl(String countryUrl) {
		this.countryUrl = countryUrl;
	}

	public Long getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Long commentNum) {
		this.commentNum = commentNum;
	}

	public Float getAverageScore() {
		return averageScore == null ? 0.0f : averageScore;
	}

	public void setAverageScore(Float averageScore) {
		this.averageScore = averageScore;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getConsultUrl() {
		return routes.ChatApp.chat(this.userId).url();
	}

	public String getReserveUrl() {
		return routes.ExpertApp.reserve(this.id).url();
	}

	public String getLinkUrl() {
		if(this.userId != null)
			return routes.ExpertApp.detail(this.userId).url();
		else
			return "www.helome.com";
	}

	public void setConsultUrl(String consultUrl) {
		this.consultUrl = consultUrl;
	}

	public void setReserveUrl(String reserveUrl) {
		this.reserveUrl = reserveUrl;
	}

	public Boolean getIsSelf() {
		return isSelf;
	}

	public void setIsSelf(Boolean isSelf) {
		this.isSelf = isSelf;
	}

	/**
	 * @return the payType
	 */
	public PayType getPayType() {
		return payType;
	}

	/**
	 * @param payType the payType to set
	 */
	public void setPayType(PayType payType) {
		this.payType = payType;
	}

	public Boolean getIsFavorite() {
		return isFavorite;
	}

	public void setIsFavorite(Boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

}
