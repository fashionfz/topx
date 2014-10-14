package vo;

import java.util.List;

import models.Gender;
import models.Service;
import models.User;

import org.apache.commons.lang3.StringUtils;

import common.Constants;

import utils.Assets;


public class ServiceListVO {


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
	
	private String price;

	private List<STagVo> tags;

	private String country;
	
	private String countryUrl;
	
	private String coverUrl;
	
	private Long commentNum;
	
	private String score;
	
	private String createDate;
	
	private String averageScore;

	public String getCoverUrl() {
		if (StringUtils.isBlank(coverUrl)) {
			return Assets.getServiceDefaultAvatar();
		}
		return Assets.at(coverUrl);
	}



	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the tags
	 */
	public List<STagVo> getTags() {
		return tags;
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
	 * @param tags the tags to set
	 */
	public void setTags(List<STagVo> tags) {
		this.tags = tags;
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
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}


	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}


	/**
	 * @return the ownerUserId
	 */
	public Long getOwnerUserId() {
		return ownerUserId;
	}


	/**
	 * @return the ownerUserName
	 */
	public String getOwnerUserName() {
		return ownerUserName;
	}


	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
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
	 * @return the commentNum
	 */
	public Long getCommentNum() {
		return commentNum;
	}


	/**
	 * @return the score
	 */
	public String getScore() {
		return score;
	}


	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}


	/**
	 * @param ownerUserId the ownerUserId to set
	 */
	public void setOwnerUserId(Long ownerUserId) {
		this.ownerUserId = ownerUserId;
	}


	/**
	 * @param ownerUserName the ownerUserName to set
	 */
	public void setOwnerUserName(String ownerUserName) {
		this.ownerUserName = ownerUserName;
	}


	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
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
	 * @param coverUrl the coverUrl to set
	 */
	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}


	/**
	 * @param commentNum the commentNum to set
	 */
	public void setCommentNum(Long commentNum) {
		this.commentNum = commentNum;
	}


	/**
	 * @param score the score to set
	 */
	public void setScore(String score) {
		this.score = score;
	}


	/**
	 * @return the gender
	 */
	public Gender getGender() {
		return gender;
	}


	/**
	 * @param gender the gender to set
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
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
	 * @param headUrl the headUrl to set
	 */
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}


	/**
	 * @return the job
	 */
	public String getJob() {
		return job;
	}


	/**
	 * @param job the job to set
	 */
	public void setJob(String job) {
		this.job = job;
	}



	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}



	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * @return the averageScore
	 */
	public String getAverageScore() {
		return this.averageScore == null ? "0" : this.averageScore;
	}

	
	public String getAverageScoreCompute() {
		String returnValue = "0";
		if (this.commentNum != 0 && StringUtils.isNotBlank(this.score))
			returnValue = Constants.dformat.format(Float.valueOf(this.score) / this.commentNum);
		return returnValue;
	}


	/**
	 * @param averageScore the averageScore to set
	 */
	public void setAverageScore(String averageScore) {
		this.averageScore = averageScore;
	}

}
