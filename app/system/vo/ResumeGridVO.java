package system.vo;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

import models.OverseasResume;

/**
 * @ClassName: ResumeGridVO
 * @Description: 简历列表VO
 */
public class ResumeGridVO {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Long id;
	
	private Long userId;

	private String email;

	private String avatarUrl;

	private String source;

	private String sourceResume;

	private String sourceResumeUrl;

	private String translationResume;

	private String status;

	private String createDate;

	private String translateDate;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSourceResume() {
		return sourceResume;
	}

	public void setSourceResume(String sourceResume) {
		this.sourceResume = sourceResume;
	}

	public String getSourceResumeUrl() {
		return sourceResumeUrl;
	}

	public void setSourceResumeUrl(String sourceResumeUrl) {
		this.sourceResumeUrl = sourceResumeUrl;
	}

	public String getTranslationResume() {
		return translationResume;
	}

	public void setTranslationResume(String translationResume) {
		this.translationResume = translationResume;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getTranslateDate() {
		return translateDate;
	}

	public void setTranslateDate(String translateDate) {
		this.translateDate = translateDate;
	}
	
	public void convert(OverseasResume or) {
		this.id = or.getId();
		this.userId = or.userId;
		this.email = or.email;
		this.avatarUrl = or.avatarUrl;
		this.source = StringUtils.isEmpty(or.source) ? "-" : or.source;
		this.sourceResume = StringUtils.isEmpty(or.sourceResume) ? "-" : or.sourceResume;
		this.sourceResumeUrl = or.sourceResumeUrl;
		this.translationResume = or.translationResume;
		this.status = or.status == null ? OverseasResume.Status.UNTRANSLATED.toString().toLowerCase() : or.status.toString().toLowerCase();
		this.createDate = or.createDate == null ? "" : dateFormat.format(or.createDate);
		this.translateDate = or.translateDate == null ? "" : dateFormat.format(or.translateDate);
	}

}
