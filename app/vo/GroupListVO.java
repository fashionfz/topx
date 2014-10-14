package vo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import utils.Assets;


public class GroupListVO {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private Long id;

	private String groupName;

	private Long ownerId;
	
	private String ownerName;

	private List<STagVo> tags;

	private String groupInfo;

	private Long groupPrivId;
	
	private String groupPrivName;
	
	private Long industryId;
	
	private String industryName;
	
	private String headUrl;
	
	private Long countMem;
	
	private String createDate;
	
	private Boolean isJoin = false;


	public String getHeadUrl() {
		if (StringUtils.isBlank(headUrl)) {
			return Assets.getDefaultGroupHeadUrl(false);
		}
		return Assets.at(headUrl);
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	/**
	 * @return the dateFormat
	 */
	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @return the ownerId
	 */
	public Long getOwnerId() {
		return ownerId;
	}

	/**
	 * @return the ownerName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @return the tags
	 */
	public List<STagVo> getTags() {
		return tags;
	}

	/**
	 * @return the groupInfo
	 */
	public String getGroupInfo() {
		return groupInfo;
	}

	/**
	 * @return the groupPrivId
	 */
	public Long getGroupPrivId() {
		return groupPrivId;
	}

	/**
	 * @return the groupPrivName
	 */
	public String getGroupPrivName() {
		return groupPrivName;
	}

	/**
	 * @return the industryId
	 */
	public Long getIndustryId() {
		return industryId;
	}

	/**
	 * @return the industryName
	 */
	public String getIndustryName() {
		return industryName;
	}

	/**
	 * @return the countMem
	 */
	public Long getCountMem() {
		return countMem;
	}

	/**
	 * @return the createDate
	 */
	public String getCreateDate() {
		return createDate;
	}

	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * @param ownerName the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<STagVo> tags) {
		this.tags = tags;
	}

	/**
	 * @param groupInfo the groupInfo to set
	 */
	public void setGroupInfo(String groupInfo) {
		this.groupInfo = groupInfo;
	}

	/**
	 * @param groupPrivId the groupPrivId to set
	 */
	public void setGroupPrivId(Long groupPrivId) {
		this.groupPrivId = groupPrivId;
	}

	/**
	 * @param groupPrivName the groupPrivName to set
	 */
	public void setGroupPrivName(String groupPrivName) {
		this.groupPrivName = groupPrivName;
	}

	/**
	 * @param industryId the industryId to set
	 */
	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	/**
	 * @param industryName the industryName to set
	 */
	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

	/**
	 * @param countMem the countMem to set
	 */
	public void setCountMem(Long countMem) {
		this.countMem = countMem;
	}

	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the isJoin
	 */
	public Boolean getIsJoin() {
		return isJoin;
	}

	/**
	 * @param isJoin the isJoin to set
	 */
	public void setIsJoin(Boolean isJoin) {
		this.isJoin = isJoin;
	}



}
