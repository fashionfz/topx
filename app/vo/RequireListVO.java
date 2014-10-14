package vo;

import java.util.List;

import models.Gender;
import models.Service;
import models.User;

import org.apache.commons.lang3.StringUtils;

import utils.Assets;


public class RequireListVO {


	private Long id;

	private String title;
	
	private Long industryId;
	
	private String industryName;

	private Long ownerUserId;
	
	private Gender gender;
	
	private String headUrl;
	
	private String ownerUserName;
	
	private String job;
	
	private String info;
	
	private String budget;

	private List<STagVo> tags;

	private String country;
	
	private String countryUrl;
		
	private String createDate;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
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
	 * @return the ownerUserId
	 */
	public Long getOwnerUserId() {
		return ownerUserId;
	}

	/**
	 * @return the gender
	 */
	public Gender getGender() {
		return gender;
	}

	/**
	 * @return the headUrl
	 */
	public String getHeadUrl() {
		if (StringUtils.isBlank(headUrl)) {
			return Assets.getDefaultAvatar();
		}
		return Assets.at(headUrl);
	}
	
	/**
	 * @return the ownerUserName
	 */
	public String getOwnerUserName() {
		return ownerUserName;
	}

	/**
	 * @return the job
	 */
	public String getJob() {
		return job;
	}

	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * @return the budget
	 */
	public String getBudget() {
		return budget;
	}

	/**
	 * @return the tags
	 */
	public List<STagVo> getTags() {
		return tags;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @return the countryUrl
	 */
	public String getCountryUrl() {
		return countryUrl;
	}

	/**
	 * @return the createDate
	 */
	public String getCreateDate() {
		return createDate;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
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
	 * @param ownerUserId the ownerUserId to set
	 */
	public void setOwnerUserId(Long ownerUserId) {
		this.ownerUserId = ownerUserId;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	/**
	 * @param headUrl the headUrl to set
	 */
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	/**
	 * @param ownerUserName the ownerUserName to set
	 */
	public void setOwnerUserName(String ownerUserName) {
		this.ownerUserName = ownerUserName;
	}

	/**
	 * @param job the job to set
	 */
	public void setJob(String job) {
		this.job = job;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * @param budget the budget to set
	 */
	public void setBudget(String budget) {
		this.budget = budget;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<STagVo> tags) {
		this.tags = tags;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @param countryUrl the countryUrl to set
	 */
	public void setCountryUrl(String countryUrl) {
		this.countryUrl = countryUrl;
	}

	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	

}
