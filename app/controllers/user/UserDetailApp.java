/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package controllers.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import models.Expert;
import models.SkillTag;
import models.User;
import models.User.ResumeStatus;
import play.db.jpa.Transactional;
import play.mvc.Result;
import vo.EducationExp;
import vo.ExpertDetailInfo;
import vo.JobExp;
import vo.STagVo;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;

/**
 * @author ZhouChun
 * @ClassName: UserDetailController
 * @Description: 用户个人信息
 * @date 13-10-30 上午9:50
 */
public class UserDetailApp extends BaseApp {


	/**
	 * 个人中心Tab切换之基本信息
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result baseview() {
		ExpertDetailInfo expert = Expert.viewBase(session());
		List<SkillTag> sts = SkillTag.getCategoryTag(false);
		List<STagVo> ss = new ArrayList<STagVo>();
		for (SkillTag st:sts){
			STagVo stv = new STagVo();
			stv.setSid(st.id);
			stv.setTag(st.tagName);
			if (expert.getInId().contains(stv.getSid()))
				stv.setIsMarked(true);
			ss.add(stv);
		}
		User me = User.getFromSession(session());
		String isForbidAddResumeTask = "false"; // isForbidAddResumeTask：true：禁止发布，false：允许发布
		if (me.getResumeStatus() != null && me.getResumeStatus() == ResumeStatus.PUBLISHED) {
			isForbidAddResumeTask = "true";
		}
		
		return ok(views.html.usercenter.detail.base.render(expert,ss,isForbidAddResumeTask));
	}

	/**
	 * 个人中心Tab切换之上传头像
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result userhead() {
		User me = User.getFromSession(session());
		String isForbidAddResumeTask = "false"; // isForbidAddResumeTask：true：禁止发布，false：允许发布
		if (me.getResumeStatus() != null && me.getResumeStatus() == ResumeStatus.PUBLISHED) {
			isForbidAddResumeTask = "true";
		}
		return ok(views.html.usercenter.detail.userhead.render(isForbidAddResumeTask));
	}

	/**
	 * 个人中心Tab切换之职业生涯
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result workinfo() {
		ExpertDetailInfo expert = Expert.view(session());
		
		//职业经历根据截至时间降序、开始时间降序 排序
		List<JobExp> jobExpList = expert.getJobExp();
		if (jobExpList != null && jobExpList.size() > 0) {
			sortJobExpList(jobExpList);
		}
		
		User me = User.getFromSession(session());
		String isForbidAddResumeTask = "false"; // isForbidAddResumeTask：true：禁止发布，false：允许发布
		if (me.getResumeStatus() != null && me.getResumeStatus() == ResumeStatus.PUBLISHED) {
			isForbidAddResumeTask = "true";
		}
		return ok(views.html.usercenter.detail.workinfo.render(expert, isForbidAddResumeTask));
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

	/**
	 * 个人中心Tab切换之教育经历
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result eduinfo() {
		ExpertDetailInfo expert = Expert.view(session());
		
		// 教育经历根据毕业日期降序排序
		List<EducationExp> educationExpList = expert.getEducationExp();
		if (educationExpList != null && educationExpList.size() > 0) {
			sortEducationExpList(educationExpList);
		}
		User me = User.getFromSession(session());
		String isForbidAddResumeTask = "false"; // isForbidAddResumeTask：true：禁止发布，false：允许发布
		if (me.getResumeStatus() != null && me.getResumeStatus() == ResumeStatus.PUBLISHED) {
			isForbidAddResumeTask = "true";
		}
		return ok(views.html.usercenter.detail.eduinfo.render(expert, isForbidAddResumeTask));
	}
	
	//SEAGULL-865 [教育经历]在教育经历排序中没有对月份进行排序处理
	/**
	 * 教育经历根据毕业日期降序排序
	 */
	private static void sortEducationExpList(List<EducationExp> educationExps){
		Collections.sort(educationExps,new Comparator<EducationExp>() {
			public int compare(EducationExp o1, EducationExp o2) {
				if (o1 != null && o2 != null && o1.getYearEnd() != null && o2.getYearEnd() != null && o1.getYear() != null && o2.getYear() != null) {
					if (StringUtils.isBlank(o2.getYearEnd())) {
						return 1;
					} else if (StringUtils.isBlank(o1.getYearEnd())) {
						return -1;
					} else {
						int result = Integer.valueOf(o2.getYearEnd()) - Integer.valueOf(o1.getYearEnd());
						if (result == 0) {
							int result2 = Integer.valueOf(o2.getYear()) - Integer.valueOf(o1.getYear());
							return result2;
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
	 * 保存个人信息
	 * 
	 * @return
	 */
	@Transactional
	public static Result savePersonalInfo() {
		JsonNode json = getJson();
		ObjectNodeResult result = Expert.saveExpertByJson(session(), json);
		return ok(result.getObjectNode());
	}

	


}
