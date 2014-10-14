package vo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import models.Group;
import utils.Assets;
import utils.HelomeUtil;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.jackjson.JackJsonUtil;

@JsonInclude(Include.NON_NULL)
public class GroupVO {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 群组id
	 */
	public Long id;
	
	/**
	 * 群组名称
	 */
	public String groupName;
	
	/**
	 * 成员个数
	 */
	public Long countMem;
	
	/**
	 * 创建日期
	 */
	public Date createDate;
	
	/**
	 * 创建日期，字符串形式
	 */
	public String createDateFormat;
	
	/** 头像 **/
	public String headUrl;
	
	/** 群组背景图 **/
	public String backgroudUrl;
	/** 头像的关系地址*/
	public String relationHeadUrl;
	
	/** 群组背景图的关系地址 */
	public String relationBackgroudUrl;
	
	/**
	 * 群组行业id
	 */
	public Long industryId;
	
	/**
	 * 群组行业名称
	 */
	public String industryName;
	
	/** 群组说明 **/
	public String groupInfo;
	
	/**
	 * 群主userId
	 */
	public Long ownerId;
	
	/** 群标签模式 ["ddd",""] */
	public List<String> tags = new ArrayList<String>();
	
	/**
	 * 群组类型
	 */
	public String type;
	
	/**
	 * 群组权限
	 */
	public String groupPriv;
	
	/** 群组成员最大数量限制 （默认100人，包括群主） */
	public Long maxMemberNum;
	
	/**
	 * 当前用户是否加入
	 */
	public Boolean isJoin;
	
	public GroupVO() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Long getCountMem() {
		return countMem;
	}

	public void setCountMem(Long countMem) {
		this.countMem = countMem;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String getCreateDateFormat() {
		return createDateFormat;
	}

	public void setCreateDateFormat(String createDateFormat) {
		this.createDateFormat = createDateFormat;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getBackgroudUrl() {
		return backgroudUrl;
	}

	public void setBackgroudUrl(String backgroudUrl) {
		this.backgroudUrl = backgroudUrl;
	}

	public String getRelationHeadUrl() {
		return relationHeadUrl;
	}

	public void setRelationHeadUrl(String relationHeadUrl) {
		this.relationHeadUrl = relationHeadUrl;
	}

	public String getRelationBackgroudUrl() {
		return relationBackgroudUrl;
	}

	public void setRelationBackgroudUrl(String relationBackgroudUrl) {
		this.relationBackgroudUrl = relationBackgroudUrl;
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

	public String getGroupInfo() {
		return groupInfo;
	}

	public void setGroupInfo(String groupInfo) {
		this.groupInfo = groupInfo;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public List<String> getTags() {
		return CollectionUtils.isEmpty(tags) ? new ArrayList<String>() : tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGroupPriv() {
		return groupPriv;
	}

	public void setGroupPriv(String groupPriv) {
		this.groupPriv = groupPriv;
	}

	public Long getMaxMemberNum() {
		return maxMemberNum;
	}

	public void setMaxMemberNum(Long maxMemberNum) {
		this.maxMemberNum = maxMemberNum;
	}
	
	public Boolean getIsJoin() {
        return isJoin;
    }

    public void setIsJoin(Boolean isJoin) {
        this.isJoin = isJoin;
    }

    public void convert(Group group) {
		ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
		this.id = group.getId();
		this.groupName = group.getGroupName();
		this.countMem = group.getCountMem();
		this.createDate = group.getCreateDate();
		if (group.getCreateDate() != null) {
			this.createDateFormat = dateFormat.format(group.getCreateDate());
		}
		if (StringUtils.isNotBlank(group.getHeadUrl())) {
            this.headUrl = Assets.at(group.getHeadUrl());
            this.relationHeadUrl = group.getHeadUrl();
        } else {
            this.headUrl = Assets.getDefaultGroupHeadUrl(false);
            this.relationHeadUrl = "";
        }
		if (StringUtils.isNotBlank(group.getBackgroudUrl())) {
            this.backgroudUrl = Assets.at(group.getBackgroudUrl());
            this.relationBackgroudUrl = group.getBackgroudUrl();
        } else {
            this.backgroudUrl = Assets.getDefaultGroupBackgroundUrl(group.getIndustry() == null ? 0L: group.getIndustry().getId(), false);
            this.relationBackgroudUrl = "";
        }
		this.industryId = group.getIndustry() == null ? null : group.getIndustry().getId();
		this.industryName = group.getIndustry() == null ? null : group.getIndustry().getTagName();
		this.groupInfo = group.getGroupInfo();
		this.ownerId = group.getOwner() == null ? null : group.getOwner().getUserId();
		try {
			this.tags = HelomeUtil.isEmpty(group.getTags()) ? new ArrayList<String>() : objectMapper.readValue(group.getTags(), List.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.type = group.getType() == null ? Group.Type.NORMAL.toString()
				.toLowerCase() : group.getType().toString().toLowerCase();
		this.groupPriv = group.groupPriv == null ? Group.GroupPriv.PUBLIC
				.toString().toLowerCase() : group.groupPriv.toString().toLowerCase();
		this.maxMemberNum = group.maxMemberNum;
	}
	
	
}
