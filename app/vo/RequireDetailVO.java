package vo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import common.jackjson.JackJsonUtil;
import utils.HelomeUtil;
import models.AttachOfRequire;
import models.Require;

public class RequireDetailVO {
	
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private SimpleDateFormat dateSimpleformat = new SimpleDateFormat("yyyy-MM-dd");

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
	/** 需求标题 */
	private String title;
	/** 需求说明 */
	private String info;
	/** 预算 */
	private Double budget;
	/** 发布时间 */
	private String createDate;
	
	/** 标签["ddd",""] */
	private List<String> tags = new ArrayList<String>();
	/** 附件 */
	private List<AttachmentVO> attachList = new ArrayList<AttachmentVO>();
	
	/** 序号 */
	private long index;
	
	public Long getId() {
		return id;
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

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<AttachmentVO> getAttachList() {
		return attachList;
	}

	public void setAttachList(List<AttachmentVO> attachList) {
		this.attachList = attachList;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public void convert(Require requirement) {
		ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
		this.id = requirement.getId();
		this.ownerId = requirement.getOwner().getId();
		this.username = requirement.getOwner().getName();
		this.industryId = requirement.getIndustry() == null ? null
				: requirement.getIndustry().getId();
		this.industryName = requirement.getIndustry() == null ? ""
				: requirement.getIndustry().getTagName();
		this.title = requirement.getTitle();
		this.info = requirement.getInfo();
		this.budget = requirement.budget;
		if (requirement.getCreateDate() != null) {
			this.createDate = dateformat.format(requirement.getCreateDate());
		}
		try {
			this.tags = HelomeUtil.isEmpty(requirement.getTags()) ? new ArrayList<String>() : objectMapper.readValue(requirement.getTags(), List.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (CollectionUtils.isNotEmpty(requirement.getCaseAttachs())) {
			List<AttachmentVO> attachVOList = new ArrayList<AttachmentVO>();
			for (AttachOfRequire item : requirement.getCaseAttachs()) {
				AttachmentVO attachVO = new AttachmentVO();
				attachVO.convert(item);
				attachVOList.add(attachVO);
			}
			this.attachList = attachVOList;
		}
	}
	
	public void convertVOList(Require requirement){
		this.id = requirement.getId();
		this.ownerId = requirement.getOwner().getId();
		this.username = requirement.getOwner().getName();
		this.industryId = requirement.getIndustry() == null ? null
				: requirement.getIndustry().getId();
		this.industryName = requirement.getIndustry() == null ? ""
				: requirement.getIndustry().getTagName();
		this.title = requirement.getTitle();
		this.info = requirement.getInfo();
		this.budget = requirement.budget;
		if (requirement.getCreateDate() != null) {
			this.createDate = dateSimpleformat.format(requirement.getCreateDate());
		}
	}

}
