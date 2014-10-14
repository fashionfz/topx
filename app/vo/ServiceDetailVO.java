package vo;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.AttachOfService;
import models.Service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import utils.Assets;
import utils.HelomeUtil;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.jackjson.JackJsonUtil;

public class ServiceDetailVO {

	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/** id */
	private Long id;
	/** 创建人id */
	private Long ownerId;
	/** 创建人的用户名 */
	private String username;
	/** 行业id */
	private Long industryId;
	/** 行业名称 */
	private String industryName;
	/** 服务标题 */
	private String title;
	/** 服务说明 */
	private String info;
	/** 价格 */
	private Double price;
	/** 发布时间 */
	private String createDate;
	/** 标签["ddd",""] */
	private List<STagVo> skillsTags = new ArrayList<STagVo>();
	/** 附件 */
	public List<AttachmentVO> attachList = new ArrayList<AttachmentVO>();
	/** 服务封面默认图片 **/
	private String coverUrl;
	/** 平均分 */
	private String averageScore = "0.0";
	/** 评论数 */
	private Long commentNum = 0L;
	
	public List<String> tags = new ArrayList<String>();

	public Long getId() {
		return id;
	}
	
	public List<STagVo> getSkillsTags() {
		return skillsTags;
	}

	public void setSkillsTags(List<STagVo> skillsTags) {
		this.skillsTags = skillsTags;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	public String getIndustryName() {
		return industryName;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public List<AttachmentVO> getAttachList() {
		return attachList;
	}

	public void setAttachList(List<AttachmentVO> attachList) {
		this.attachList = attachList;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getAverageScore() {
		return averageScore == null ? "0.0" : averageScore;
	}

	public void setAverageScore(String averageScore) {
		this.averageScore = averageScore;
	}

	public Long getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Long commentNum) {
		this.commentNum = commentNum;
	}

	public void convert(Service service) {
		ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
		this.id = service.getId();
		this.ownerId = service.getOwner().getId();
		this.username = service.getOwner().getName();
		this.industryId = service.getIndustry() == null ? null : service.getIndustry().getId();
		this.industryName = service.getIndustry() == null ? "" : service.getIndustry().getTagName();
		this.title = service.getTitle();
		this.info = service.getInfo();
		this.price = service.getPrice();
		if (service.getCreateDate() != null) {
			this.createDate = dateformat.format(service.getCreateDate());
		}
		List<String> tags = null;
		List<JobExp> jobExps = null;
		List<EducationExp> educationExps = null;
		try {
			tags = HelomeUtil.isEmpty(service.getTags()) ? new ArrayList<String>() : objectMapper.readValue(service.getTags(), List.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.tags = tags;
		for (String st : tags) {
			STagVo stv = new STagVo();
			stv.setTag(st);
			stv.setNoMarkedTag(st);
			this.skillsTags.add(stv);
		}
		if (CollectionUtils.isNotEmpty(service.getCaseAttachs())) {
			List<AttachmentVO> attachVOList = new ArrayList<AttachmentVO>();
			for (AttachOfService item : service.getCaseAttachs()) {
				AttachmentVO attachVO = new AttachmentVO();
				attachVO.convert(item);
				attachVOList.add(attachVO);
			}
			this.attachList = attachVOList;
		}
	}

	public void convertVOList(Service service) {
		this.id = service.getId();
		this.ownerId = service.getOwner().getId();
		this.username = service.getOwner().getName();
		this.industryId = service.getIndustry() == null ? null : service.getIndustry().getId();
		this.industryName = service.getIndustry() == null ? "" : service.getIndustry().getTagName();
		this.title = service.getTitle();
		this.info = service.getInfo();
		this.price = service.getPrice();
		if (service.getCreateDate() != null) {
			this.createDate = dateformat.format(service.getCreateDate());
		}
		if (StringUtils.isNotBlank(service.getCoverUrl())) {
			this.coverUrl = Assets.at(service.getCoverUrl());
		} else {
			this.coverUrl = Assets.getServiceDefaultAvatar();
		}
		DecimalFormat df = new DecimalFormat("###.0");
		if (service.getAverageScore() != null && service.getAverageScore() > 0) {
			this.averageScore = df.format(service.getAverageScore());
		}
		this.commentNum = service.getCommentNum();
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
