/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月22日
 */
package models;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import models.OverseasResume.Status;
import models.service.UserInfoCookieService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import play.db.jpa.JPA;
import play.libs.Akka;
import play.mvc.Http;
import scala.concurrent.duration.Duration;
import utils.Assets;
import utils.HelomeUtil;
import utils.TimeZoneUtils;
import vo.ExpertDetailInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import controllers.base.ObjectNodeResult;
import ext.MessageCenter.utils.MCMessageUtil;
import ext.search.SearchHttpClient;
import ext.search.Transformer;
import ext.usercenter.UserCenterService;

/**
 * 
 * 
 * @ClassName: Expert
 * @Description: 专家实体类
 * @date 2013年10月22日 上午9:44:12
 * @author RenYouchao
 * 
 */
@javax.persistence.Entity
@Table(name = "tb_expert")
public class Expert implements java.io.Serializable {

	private static final long serialVersionUID = 705114432265079541L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	/** 专家账号关联 **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userid")
	public User user;
	@Column(name = "userid", insertable = false, updatable = false, unique = true)
	public Long userId;
	/** 专家姓名 **/
	public String userName;
	/** 专家行业关联 **/
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "tb_expert_in", joinColumns = { @JoinColumn(name = "expertId", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "inId", referencedColumnName = "id") })
	public Set<SkillTag> inTags = new HashSet<SkillTag>();
	/** 专家性别有默认值 **/
	@Enumerated(EnumType.ORDINAL)
	@Column(length = 1)
	public Gender gender = Gender.MAN;
	/** 专家加入时间 */
	public Date joinDate;
	/** 技能标签模式 ["ddd",""] */
	@Column(length = 4000)
	public String skillsTags;
	/** 国别 **/
	public String country;
	/** 时区 **/
	public String timeZone;
	/** 头像URL地址 **/
	public String headUrl;
	/** 职业 **/
	public String job;
	/** 个人说明 **/
	@Column(length = 4000)
	public String personalInfo;
	/** 个人短说明 **/
	/** 职业信息 **/
	@Column(length = 4000)
	public String jobExp;
	/** 教育经历  **/
	@Column(length = 4000)
	public String educationExp;
	/** 收费标准0为免费，单位为每分钟 **/
	public Long expenses = 0L;
	/** 评价次数 **/
	public Long commentNum = 0L;
    /** 平均分数 **/
	public Float averageScore = 0.0f;
	/** 成交次数 **/
	public Long dealNum = 0L;
	/** 评价总分 **/
	public Long sumScore = 0L;
	/** 是否置顶 **/
	public Boolean isTop = false;
	/** 置顶所在的行业，存储tb_skill_tag表的记录的id */
	public Long topIndustry;
	/** 付款成功 **/
	public PayType payType = PayType.NEGOTIABLE;
	@Column(columnDefinition = "bigint default 0")
	public Long collectNum = 0L;
    /**语言 **/
	@Column(columnDefinition = "varchar(255) default 'zh-cn'")
	public String language = "zh-cn";
	/** 按时计费、面议 **/
	public enum PayType {
		TIMEBILL, NEGOTIABLE
	}

	public Expert() {
		super();
	}

	public Expert(Long id, Long userId, String userName, String country, String headUrl, String job) {
		super();
		this.id = id;
		this.userName = userName;
		this.country = country;
		this.headUrl = headUrl;
		this.job = job;
		this.userId = userId;

	}

	public Expert(Long id, Long userId, String userName, String country, String headUrl, String job, Gender gender, Float averageScore) {
		super();
		this.id = id;
		this.userName = userName;
		this.country = country;
		this.headUrl = headUrl;
		this.job = job;
		this.userId = userId;
		this.gender = gender;
		this.averageScore = averageScore;
	}

	public String combineEducationExp(ObjectMapper objectMapper) {
		List<HashMap> list = null;
		StringBuffer str = new StringBuffer();
		try {
			list = objectMapper.readValue(this.educationExp, List.class);
			if (CollectionUtils.isNotEmpty(list))
				for (HashMap map : list) {
					Collection<String> dd = map.values();
					for (String strMap : dd) {
						str.append(strMap).append(" ");
					}
				}
		} catch (Exception e) {
			// e.printStackTrace();
			// TODO renyouchao
			return "";
		}
		return str.toString();
	}

	public String combinejobExp(ObjectMapper objectMapper) {
		List<HashMap> list = null;
		StringBuffer str = new StringBuffer();
		try {
			list = objectMapper.readValue(this.jobExp, List.class);
			if (CollectionUtils.isNotEmpty(list))
				for (HashMap map : list) {
					Collection<String> dd = map.values();
					for (String strMap : dd) {
						str.append(strMap).append(" ");
					}
				}
		} catch (Exception e) {
			// e.printStackTrace();
			// TODO renyouchao
			return "";
		}
		return str.toString();
	}

	/**
	 * 获取专家的timezone 如果没有，就获取从cookie获取到的 还是没有就使用服务器的
	 * 
	 * @param session
	 * @return
	 */
	public int getTimezoneOffset(Http.Session session) {
		vo.TimeZone timeZone = TimeZoneUtils.get(this.timeZone);
		if (timeZone != null) {
			return timeZone.getOffset();
		}
		timeZone = TimeZoneUtils.getFromSession(session);
		if (timeZone != null) {
			return timeZone.getOffset();
		}
		return Calendar.getInstance().getTimeZone().getRawOffset() / 1000 / 60;
	}

	public void saveOrUpate() {
		if (id != null)
			JPA.em().merge(this);
		else
			JPA.em().persist(this);
		final Expert thiz = this;
		Akka.system().scheduler().scheduleOnce(Duration.create(100, TimeUnit.MILLISECONDS), new Runnable() {
			public void run() {
				Transformer tf = new Transformer(thiz);
				SearchHttpClient.createOrUpdateDocument(tf.tranInputsNVP());
			}
		}, Akka.system().dispatcher());
		
	}

	public void saveOrUpateFromUser(User user) {
		if (null != user) {
			this.user = user;
			this.userId = user.id;
			this.joinDate = user.registerDate;
			this.userName = user.userName;
			this.gender = user.gender;
		}
		saveOrUpate();
	}

	/**
	 * 计算平均分<br/>
	 * 正确性不是很高
	 * @param score
	 */
	public void computeAverageScore(int score) {
		this.sumScore = this.sumScore + score;
		this.commentNum = this.commentNum + 1;
		DecimalFormat df = new DecimalFormat("###.0");
		if (commentNum != null && commentNum > 0) {
			this.averageScore = Float.parseFloat(df.format((float) sumScore / (float) commentNum));
		}
	}
	
	/**
	 * 计算平均分 <br/>
	 * 提高正确性
	 * @param score
	 */
	public void computeAverageScore(Long toCommentUserId) {
		this.sumScore = Comment.getTotalLevelBytoCommentUserId(toCommentUserId,ExpertComment.class);
		this.commentNum = Comment.getTotalCountBytoCommentUserId(toCommentUserId,ExpertComment.class);
		DecimalFormat df = new DecimalFormat("###.0");
		if (commentNum != null && commentNum > 0) {
			this.averageScore = Float.parseFloat(df.format((float) sumScore / (float) commentNum));
		}
	}
	
	/**
	 * 刷新专家的平均分
	 */
	public void recountAverageScore(){
		DecimalFormat df = new DecimalFormat("###.0");
		if(commentNum>0){
			this.averageScore = Float.parseFloat(df.format((float) sumScore / (float) commentNum));
		} else {
			this.averageScore = Float.parseFloat(df.format(0F));
		}
	}
	
	// @Override
	// public int compareTo(Object arg) {
	// int dN = ((Expert)arg).dealNum.intValue();
	// return (dealNum.intValue() - dN);
	// }

	public String getFullHeadUrl() {
		if (StringUtils.isBlank(headUrl)) {
			return Assets.getDefaultAvatar();
		}
		return Assets.at(headUrl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Expert other = (Expert) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * 用户ID查询 对应的专家数据
	 * 
	 * @param userid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Expert findByUserId(Long userid) {
		List<Expert> experts = JPA.em().createQuery("from Expert e where e.userId = :id").setParameter("id", userid).getResultList();
		if (CollectionUtils.isNotEmpty(experts)) {
			return experts.get(0);
		}
		return null;
	}
	
	public static ObjectNodeResult saveExpertByJson(JsonNode json){
		ObjectNodeResult result = new ObjectNodeResult();
		Long userId = json.findPath("userId").asLong();
		User user = User.findById(userId);
		Expert expert = Expert.getExpertByUserId(userId);
		expert.joinDate = expert.user.registerDate;
		if (json.has("gender")) {
			if (json.findPath("gender").asInt(0) == 1) {
				expert.gender = Gender.WOMAN;
			} else {
				expert.gender = Gender.MAN;
			}
			user.gender = expert.gender;
		}
		if (json.has("name")) {
			String userName = StringUtils.isBlank(json.findPath("name").asText()) ? "" : json.findPath("name").asText();
			if (StringUtils.isNotBlank(userName)) {
				userName = common.SensitiveWordsFilter.doFilter(userName);
				userName = common.ReplaceWordsFilter.doFilter(userName);
			}
			expert.userName = userName;
			user.userName = expert.userName;
			GroupMember.updateUserNameByUserId(expert.userName, user.getId());
		}
		if (json.has("country")) {
			expert.country = StringUtils.isBlank(json.findPath("country").asText()) ? null : json.findPath("country").asText();
		}
		if (json.has("timeZone")) {
			String timezoneUid = json.findPath("timeZone").asText();
			if (null == TimeZoneUtils.get(timezoneUid)) {
				return result.errorkey("userinfo.save.unknowntimezone");
			}
			expert.timeZone = timezoneUid;
		}
		if (json.has("job")) {
			String job = json.findPath("job").asText();
			if (StringUtils.isNotBlank(job)) {
				job = common.SensitiveWordsFilter.doFilter(job);
				job = common.ReplaceWordsFilter.doFilter(job);
			}
			expert.job = job;
		}
		if (json.has("language")) {
			expert.language = json.findPath("language").asText();
		}
		if (json.has("personalInfo")) {
			String personalInfo = json.findPath("personalInfo").asText();
			if (StringUtils.isNotBlank(personalInfo)) {
				personalInfo = common.SensitiveWordsFilter.doFilter(personalInfo);
				personalInfo = common.ReplaceWordsFilter.doFilter(personalInfo);
			}
			expert.personalInfo = personalInfo;
		}
		if (json.has("jobExp") && json.get("jobExp").isArray()) {
			String jobExp = HelomeUtil.sortJsonNode(json.findPath("jobExp"), "beginYear", true).toString();
			if (StringUtils.isNotBlank(jobExp)) {
				jobExp = common.SensitiveWordsFilter.doFilter(jobExp);
				jobExp = common.ReplaceWordsFilter.doFilter(jobExp);
			}
			expert.jobExp = jobExp;
		}
		if (json.has("educationExp") && json.get("educationExp").isArray()) {
			String educationExp = HelomeUtil.sortJsonNode(json.findPath("educationExp"), "year", true).toString();
			if (StringUtils.isNotBlank(educationExp)) {
				educationExp = common.SensitiveWordsFilter.doFilter(educationExp);
				educationExp = common.ReplaceWordsFilter.doFilter(educationExp);
			}
			expert.educationExp = educationExp;
		}
		if (json.has("expenses")) {
			expert.expenses = json.findPath("expenses").asLong(0);
		}
		if (json.has("payType")) {
			if (json.findPath("payType").asInt() == 1) {
				expert.payType = PayType.NEGOTIABLE;
			} else if (json.findPath("payType").asInt() == 0){
				expert.payType = PayType.TIMEBILL;
			}
		}
		if (json.has("skillsTags") && json.get("skillsTags").isArray()) {
			String skillsTags = json.findPath("skillsTags").toString();
			if (StringUtils.isNotBlank(skillsTags)) {
				if (skillsTags.indexOf("嗨啰") != -1)
					return result.errorkey("userinfo.save.legalcharacter");	
				else if (skillsTags.indexOf("嗨啰") != -1 && skillsTags.indexOf("客服") != -1)
					return result.errorkey("userinfo.save.legalcharacter");
				else if (skillsTags.indexOf("嗨啰") != -1 && skillsTags.indexOf("翻译") != -1)
					return result.errorkey("userinfo.save.legalcharacter");
				else
					skillsTags = common.SensitiveWordsFilter.doFilter(skillsTags);
					skillsTags = common.ReplaceWordsFilter.doFilter(skillsTags);
			}
			expert.skillsTags = skillsTags;
		}
		if (json.has("industryIds")) {
			Iterator<JsonNode> iter = json.get("industryIds").elements();
			List<Long> ss = new ArrayList<Long>();
			while (iter.hasNext()) {
				ss.add(iter.next().asLong());
			}
			List<SkillTag> sst = SkillTag.getCategoryTag(ss);
			expert.inTags = new HashSet(sst);
		}
		expert.saveOrUpate();
		User.merge(user);
		// 更新海外简历
		OverseasResume or = OverseasResume.queryOverseasResumeByUserId(userId);
		if (or != null) {
			or.setTranslationResume(user.userName);
			or.setStatus(Status.INPROGRESS); // 处理中
			or.saveOrUpdate();
		}
		
				
		return result;
	}

	/**
	 * @param session
	 * @param json
	 * @return
	 */
	public static ObjectNodeResult saveExpertByJson(Http.Session session, JsonNode json) {
		User user = User.getFromSession(session);
		ObjectNodeResult result = new ObjectNodeResult();
		Expert expert = Expert.getExpertByUserId(user.id);
		if (expert == null) {
			expert = new Expert();
			expert.saveOrUpateFromUser(user);
		}
		expert.joinDate = expert.user.registerDate;
		if (json.has("gender")) {
			if (json.findPath("gender").asInt(0) == 1) {
				expert.gender = Gender.WOMAN;
			} else {
				expert.gender = Gender.MAN;
			}
			user.gender = expert.gender;
		}
		if (json.has("name")) {
			String userName = StringUtils.isBlank(json.findPath("name").asText()) ? "" : json.findPath("name").asText();
			if (StringUtils.isNotBlank(userName)) {
				userName = common.SensitiveWordsFilter.doFilter(userName);
				userName = common.ReplaceWordsFilter.doFilter(userName);
			}
			expert.userName = userName;
			user.userName = expert.userName;
			GroupMember.updateUserNameByUserId(expert.userName, user.getId());
		}
		if (json.has("country")) {
			expert.country = StringUtils.isBlank(json.findPath("country").asText()) ? null : json.findPath("country").asText();
		}
		if (json.has("timeZone")) {
			String timezoneUid = json.findPath("timeZone").asText();
			if (null == TimeZoneUtils.get(timezoneUid)) {
				return result.errorkey("userinfo.save.unknowntimezone");
			}
			expert.timeZone = timezoneUid;
		} else if (null != expert.timeZone) {
			expert.timeZone = TimeZoneUtils.getUid(expert.getTimezoneOffset(session));
		}
		if (json.has("job")) {
			String job = json.findPath("job").asText();
			if (StringUtils.isNotBlank(job)) {
				job = common.SensitiveWordsFilter.doFilter(job);
				job = common.ReplaceWordsFilter.doFilter(job);
			}
			expert.job = job;
		}
		if (json.has("language")) {
			expert.language = json.findPath("language").asText();
		}
		if (json.has("personalInfo")) {
			String personalInfo = json.findPath("personalInfo").asText();
			if (StringUtils.isNotBlank(personalInfo)) {
				personalInfo = common.SensitiveWordsFilter.doFilter(personalInfo);
				personalInfo = common.ReplaceWordsFilter.doFilter(personalInfo);
			}
			expert.personalInfo = personalInfo;
		}
		if (json.has("jobExp") && json.get("jobExp").isArray()) {
			String jobExp = HelomeUtil.sortJsonNode(json.findPath("jobExp"), "beginYear", true).toString();
			if (StringUtils.isNotBlank(jobExp)) {
				jobExp = common.SensitiveWordsFilter.doFilter(jobExp);
				jobExp = common.ReplaceWordsFilter.doFilter(jobExp);
			}
			expert.jobExp = jobExp;
		}
		if (json.has("educationExp") && json.get("educationExp").isArray()) {
			String educationExp = HelomeUtil.sortJsonNode(json.findPath("educationExp"), "year", true).toString();
			if (StringUtils.isNotBlank(educationExp)) {
				educationExp = common.SensitiveWordsFilter.doFilter(educationExp);
				educationExp = common.ReplaceWordsFilter.doFilter(educationExp);
			}
			expert.educationExp = educationExp;
		}
		if (json.has("expenses")) {
			expert.expenses = json.findPath("expenses").asLong(0);
		}
		if (json.has("payType")) {
			if (json.findPath("payType").asInt() == 1) {
				expert.payType = PayType.NEGOTIABLE;
			} else if (json.findPath("payType").asInt() == 0){
				expert.payType = PayType.TIMEBILL;
			}
		}
		if (json.has("skillsTags") && json.get("skillsTags").isArray()) {
			String skillsTags = json.findPath("skillsTags").toString();
			if (StringUtils.isNotBlank(skillsTags)) {
				if (skillsTags.indexOf("嗨啰") != -1)
					return result.errorkey("userinfo.save.legalcharacter");	
				else if (skillsTags.indexOf("嗨啰") != -1 && skillsTags.indexOf("客服") != -1)
					return result.errorkey("userinfo.save.legalcharacter");
				else if (skillsTags.indexOf("嗨啰") != -1 && skillsTags.indexOf("翻译") != -1)
					return result.errorkey("userinfo.save.legalcharacter");
				else
					skillsTags = common.SensitiveWordsFilter.doFilter(skillsTags);
					skillsTags = common.ReplaceWordsFilter.doFilter(skillsTags);
			}
			expert.skillsTags = skillsTags;
		}
		if (json.has("industryIds")) {
			Iterator<JsonNode> iter = json.get("industryIds").elements();
			List<Long> ss = new ArrayList<Long>();
			while (iter.hasNext()) {
				ss.add(iter.next().asLong());
			}
			List<SkillTag> sst = SkillTag.getCategoryTag(ss);
			expert.inTags = new HashSet(sst);
		}
		expert.saveOrUpate();
		User.merge(user);
		UserInfoCookieService.createOrUpdateCookie(true);
		if (json.has("name")) {
			String userName = StringUtils.isBlank(json.findPath("name").asText()) ? "" : json.findPath("name").asText();
			if (StringUtils.isNotBlank(userName)) {
				userName = common.SensitiveWordsFilter.doFilter(userName);
				userName = common.ReplaceWordsFilter.doFilter(userName);
			}
			UserCenterService.modifyRealname(session, userName);
		}
		return result;
	}

	public static ObjectNodeResult saveServiceInfo(Http.Session session, JsonNode json) {
		ObjectNodeResult result = new ObjectNodeResult();
		User user = User.getFromSession(session);
		Expert expert = findByUserId(user.id);
		if (expert == null) {
			expert = new Expert();
			expert.saveOrUpateFromUser(user);
		}
		expert.expenses = json.findPath("expenses").asLong(0);
		if (json.findPath("payType").asInt() == 1) {
			expert.payType = PayType.NEGOTIABLE;
			expert.expenses = 0L;
		} else {
			expert.payType = PayType.TIMEBILL;
		}
		expert.saveOrUpate();
		return result;
	}

	public static ExpertDetailInfo view(Http.Session session) {
		User user = User.getFromSession(session);
		Expert expert = findByUserId(user.id);
		ExpertDetailInfo detail = new ExpertDetailInfo();
		detail.convert(expert,user);
		return detail;
	}
	
	public static ExpertDetailInfo viewByUserId(Long userId) {
		Expert expert = findByUserId(userId);
		ExpertDetailInfo detail = new ExpertDetailInfo();
		detail.convert(expert,expert.user);
		return detail;
	}

	public static ExpertDetailInfo viewBase(Http.Session session) {
		User user = User.getFromSession(session);
		Expert expert = findByUserId(user.id);
		ExpertDetailInfo detail = new ExpertDetailInfo();
		detail.convertIn(expert,user);
		return detail;
	}
	
	public static ExpertDetailInfo viewBaseByUserId(Long userId) {
		Expert expert = findByUserId(userId);
		ExpertDetailInfo detail = new ExpertDetailInfo();
		detail.convertIn(expert,expert.user);
		return detail;
	}

	/**
	 * 根据专家id获得专家详细
	 * 
	 * @param id
	 * @return
	 */
	public static Expert getExpertById(Long id) {
		List<Expert> experts = JPA.em().createQuery("from Expert e left join fetch e.user u where e.id = :id").setParameter("id", id)
				.getResultList();
		if (CollectionUtils.isNotEmpty(experts)) {
			return experts.get(0);
		} else {
			return null;
		}
	}



	public static List<Expert> listExpertAll() {
		List<Expert> experts = (List<Expert>) JPA.em().createQuery("from Expert e order by e.id asc").getResultList();
		if (CollectionUtils.isEmpty(experts))
			return new ArrayList<Expert>();
		return experts;
	}

	/**
	 * 更新用户头像地址
	 * 
	 * @param avatar
	 * @param userId
	 */
	public static void updateAvatarByUserId(String avatar, User user) {
		Expert expert = Expert.getExpertByUserId(user.id);
		if (expert != null) {
			expert.headUrl = avatar;
			expert.saveOrUpate();
		} else {
			expert = new Expert();
			expert.headUrl = avatar;
			expert.saveOrUpateFromUser(user);
		}
	}

	/**
	 * 根据用户Id获取专家信息
	 * 
	 * @param userId
	 * @return
	 */
	public static Expert getExpertByUserId(Long userId) {
		List<Expert> resultList = JPA.em().createQuery("from Expert e where e.userId = :id").setParameter("id", userId).getResultList();
		return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
	}

	public static void transInputAllexperts() {
		List<Expert> experts = (List<Expert>) JPA.em().createQuery("from Expert e left join fetch e.user u where u.isEnable = true").getResultList();
		for (final Expert expert : experts) {
			Akka.system().scheduler().scheduleOnce(Duration.create(100, TimeUnit.MILLISECONDS), new Runnable() {
				public void run() {
					Transformer tf = new Transformer(expert);
					SearchHttpClient.createOrUpdateDocument(tf.tranInputsNVP());
				}
			}, Akka.system().dispatcher());
		}
	}

	/**
	 * @deprecated 不使用，改用queryExpertTops()方法
	 * @return
	 */
	public static List<Expert> listExpertTops() {
		List<Expert> experts = (List<Expert>) JPA.em().createQuery("from Expert e where e.isTop=true order by e.id asc").getResultList();
		if (CollectionUtils.isEmpty(experts))
			return new ArrayList<Expert>();
		return experts;
	}
	
	/**
	 * 查询所有的置顶专家的list集合
	 * @param inId
	 * @param isTop
	 * @return
	 */
	public static List<Expert> queryExpertTops() {
		List<Expert> experts = (List<Expert>) JPA.em().createQuery("from Expert e where e.isTop=true and e.topIndustry is not null order by e.id asc").getResultList();
		if (CollectionUtils.isEmpty(experts)) {
			return new ArrayList<Expert>();
		}
		return experts;
	}

	@SuppressWarnings("unchecked")
	public static List<Expert> getPartExpert(String p, Integer pageSize, Long i, String s, String cf, String ssf, String ef, String gf,
			String o, String ot) {
		StringBuffer joinBuffer = new StringBuffer("from Expert e join fetch e.user u ");
		StringBuffer whereStr = new StringBuffer("where (e.userName is not null and u.isEnable = true) ");
		StringBuffer orderStr = new StringBuffer();
		if (null != i) {
			joinBuffer.append("left join fetch e.inTags i ");
			whereStr.append("and i.id = :industryId ");
		}
		if (StringUtils.isNotBlank(o)) {
			if (o.equals("averageScore"))
				orderStr.append(" order by e.averageScore desc,e.joinDate desc");
			if (o.equals("dealNum"))
				orderStr.append(" order by e.dealNum desc,e.joinDate desc");
			if (o.equals("commentNum")) {
				orderStr.append(" order by e.commentNum desc,e.joinDate desc");
			}
		} else {
			orderStr.append(" order by e.joinDate desc");
		}
		if (StringUtils.isNotBlank(cf))
			whereStr.append(" and e.country = '").append(cf).append("'");
		if (StringUtils.isNotBlank(ef)) {
			if (ef.equals("0")) {
				whereStr.append(" and e.payType = 0 ");
			} else if (ef.equals("1")) {
				whereStr.append(" and e.payType = 1 ");
			}
		}
		if (StringUtils.isNotBlank(gf))
			whereStr.append(" and e.gender = ").append(gf);
		if (s != null) {
			whereStr.append(" and e.skillsTags like :skilltags ");
		}

		joinBuffer.append(whereStr).append(orderStr);
		Query query = JPA.em().createQuery(joinBuffer.toString());
		if (null != i) {
			query.setParameter("industryId", i);
		}

		if (s != null) {
			return (List<Expert>) query.setParameter("skilltags", "%" + s + "%").setFirstResult((Integer.parseInt(p) - 1) * pageSize)
					.setMaxResults(pageSize).getResultList();
		} else {
			return (List<Expert>) query.setFirstResult((Integer.parseInt(p) - 1) * pageSize).setMaxResults(pageSize).getResultList();
		}

	}

	public static List<Expert> getPartExpert(String p, Integer pageSize, Long i, String s) {
		return getPartExpert(p, pageSize, i, s, null, null, null, null, null, null);
	}

	/**
	 * 增加专家收藏次数
	 * 
	 * @return
	 */
	public static void addCollNum(Long expertId) {
		JPA.em().createNativeQuery(" update tb_expert set collectNum=collectNum+1 where id=:idStr and collectNum is not null")
				.setParameter("idStr", expertId).executeUpdate();

	}
	
	/**
     * 查询客服
     */
	public static List<Expert> queryCustomerServices(String service, Boolean isOnline) {
		List<Expert> expertList = JPA.em().createQuery("from Expert e where e.user.id is not null and e.skillsTags like :skillsTags",Expert.class).setParameter("skillsTags", "%"+service+"%").getResultList();
		if (CollectionUtils.isNotEmpty(expertList)) {
		    List<Expert> resultList = null;
		    if (null != isOnline) {
		        resultList = new ArrayList<Expert>();
		        for (Expert e : expertList) {
		            if (MCMessageUtil.whetherOnline(e.userId) == isOnline) {
		                resultList.add(e); 
		            }
                }
		    } else {
		        resultList = expertList;
		    }
		    
			return resultList;
		}
		return new ArrayList<Expert>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTimeZone() {
		return timeZone;
	}
	
	public Gender getGenderWithDefault() {
	    return gender == null ? Gender.MAN : gender;
	}
	
	public PayType getPayTypeWithDefault() {
	    return payType == null ? PayType.NEGOTIABLE : payType;
	}
	
    public Long getExpensesWithDefault() {
        return expenses == null ? 0 : expenses;
    }

    public Long getCommentNumWithDefault() {
        return commentNum == null ? 0 : commentNum;
    }

    public Float getAverageScoreWithDefault() {
        return averageScore == null ? 0.0f : averageScore;
    }

    public String getLanguageWithDefault() {
        return language == null ? "zh-cn" : language;
    }


	// @Override
	// public int compareTo(Object arg) {
	// int dN = ((Expert)arg).dealNum.intValue();
	// return (dealNum.intValue() - dN);
	// }

}
