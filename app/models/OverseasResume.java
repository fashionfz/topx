package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;

import play.db.jpa.JPA;

/**
 * @ClassName: OverseasResume
 * @Description: 海外简历
 * @date 2014年7月23日 下午1:41:26
 * 
 */
@Entity
@Table(name = "tb_overseas_resume")
public class OverseasResume {
	
	/** 主键自增 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	/** 简历人Id */
	public Long userId;
	
	/** 简历人Email */
	public String email;
	
	/** 简历人头像访问路径 */
	public String avatarUrl;
	
	/** 来源 */
	public String source;
	
	/** 源简历（英文简历）名称 */
	public String sourceResume;
	
	/** 源简历（英文简历）访问路径 */
	public String sourceResumeUrl;
	
	/** 翻译简历 */
	public String translationResume;
	
	/** 状态 */
	public Status status;
	
	/** 接收时间 */
	public Date createDate;
	
	/** 翻译时间  */
	public Date translateDate;
	
	/**
	 * @ClassName: Status
	 * @Description 状态： UNTRANSLATED：未翻译，INPROGRESS：处理中，TRANSLATED：已翻译，INVALIDATE：作废
	 */
	public enum Status {
		UNTRANSLATED, INPROGRESS, TRANSLATED, INVALIDATE;
		
		public static Status getByOrdinal(int ordinal) {
			Status[] values = Status.values();
	        if (ordinal < 0 || ordinal > (values.length - 1)) {
	            return null;
	        } else {
	            return values[ordinal];
	        }
	    }
	}

	public OverseasResume() {

	}
	
	public OverseasResume(Long userId, String email, String avatarUrl,
			String source, String sourceResume, String sourceResumeUrl) {
		this.userId = userId;
		this.email = email;
		this.avatarUrl = avatarUrl;
		this.source = source;
		this.sourceResume = sourceResume;
		this.sourceResumeUrl = sourceResumeUrl;
	}
	
	public OverseasResume(Long userId, String email, String avatarUrl,
			String source, String sourceResume, String sourceResumeUrl, Date createDate) {
		this.userId = userId;
		this.email = email;
		this.avatarUrl = avatarUrl;
		this.source = source;
		this.sourceResume = sourceResume;
		this.sourceResumeUrl = sourceResumeUrl;
		this.createDate = createDate;
	}
	
	public void saveOrUpdate() {
		if (id != null) {
			JPA.em().merge(this);
		} else {
			JPA.em().persist(this);
		}
	}
	
	/**
	 * 根据userId查询OverseasResume
	 */
	public static OverseasResume queryOverseasResumeByUserId(Long userId) {
		List<OverseasResume> list = JPA.em().createQuery("from OverseasResume where userId = :userId order by id desc", OverseasResume.class)
				.setParameter("userId", userId).getResultList();
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public static OverseasResume queryById(Long id){
		List<OverseasResume> list = JPA.em().createQuery("from OverseasResume where id = :id", OverseasResume.class)
				.setParameter("id", id).getResultList();
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}
	
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getTranslateDate() {
		return translateDate;
	}

	public void setTranslateDate(Date translateDate) {
		this.translateDate = translateDate;
	}

}
