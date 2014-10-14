/*
\ * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-1
 */
package vo;

import java.util.ArrayList;
import java.util.List;
import models.Expert;
import play.libs.Json;
import utils.Assets;
import utils.HelomeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.Constants;
import common.jackjson.JackJsonUtil;

/**
 * @ClassName: ExpertComment
 * @Description: 专家信息（评价显示）
 * @date 2013-11-19 下午18:09:59
 * @author YangXuelin
 */
public class ExpertCommentVO {

	private Long id;
	
	private Long userId;
	/** 头像URL地址**/
	private String headUrl;
	/** 专家姓名**/
	private String userName;
    /** 职业 **/
	private String job;
	/** 国旗地址*/
	private String countryUrl;
	
	private List<STagVo> skillsTags = new ArrayList<STagVo>();
	/** 技能标签Json格式	 **/
	private String skillsTagsJson;
	

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

	public String getHeadUrl() {
        if(HelomeUtil.isEmpty(headUrl)) {
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCountryUrl() {
		return countryUrl;
	}

	public String getSkillsTagsJson() {
		return skillsTagsJson;
	}

	/**
	 * 将数据库中查询出来的Expert对象转换为web页面需要的对象
	 * @param expert
	 */
    public void convert(Expert expert){
		this.id = expert.id;
		this.userId = expert.userId;
		this.headUrl = expert.headUrl; 
		this.userName = expert.userName;
		this.job = expert.job;
		this.countryUrl = Constants.countryPicKV.get(expert.country);
		this.skillsTagsJson = expert.skillsTags;
		ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
		List<String> tags = null;
		try {
			tags = HelomeUtil.isEmpty(expert.skillsTags) ? new ArrayList<String>() : objectMapper.readValue(expert.skillsTags, List.class);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		for (String st : tags) {
			STagVo stv = new STagVo();
			stv.setTag(st);
			stv.setNoMarkedTag(st);
			this.skillsTags.add(stv);
		}
	}
	
	/**
	 * 转换为Json格式数据
	 * @return
	 */
	public ObjectNode convertToJson() {
		ObjectNode node = Json.newObject();
		node.put("id", this.id);
		node.put("expertUserId", this.userId);
		node.put("headUrl", this.headUrl);
		node.put("username", this.userName);
		node.put("job", this.job);
		node.put("countryUrl", this.countryUrl);
		node.putPOJO("skillsTags", this.skillsTagsJson);
		return node;
	}

	/**
	 * @return the skillsTags
	 */
	public List<STagVo> getSkillsTags() {
		return skillsTags;
	}

	/**
	 * @param skillsTags the skillsTags to set
	 */
	public void setSkillsTags(List<STagVo> skillsTags) {
		this.skillsTags = skillsTags;
	}

}
