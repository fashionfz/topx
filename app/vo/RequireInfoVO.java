package vo;

import models.Require;

import org.apache.commons.lang.StringUtils;

/**
 * require展示页面VO
 */
public class RequireInfoVO {
	
	/** require明细 */
	private RequireDetailVO requireDetailVO = new RequireDetailVO();
	
	/** 最多28个字符 */
	private String shortTitle;
	
	/** 最多1000个字符 */
	private String shortInfo;
	
	/** 拥有者个人说明 */
	private String ownerPersonalInfo;
	
	/** require创建者 */
	private ExpertDetail requireOwnerVO = new ExpertDetail();

	public RequireDetailVO getRequireDetailVO() {
		return requireDetailVO;
	}

	public void setRequireDetailVO(RequireDetailVO requireDetailVO) {
		this.requireDetailVO = requireDetailVO;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getShortInfo() {
		return shortInfo;
	}

	public void setShortInfo(String shortInfo) {
		this.shortInfo = shortInfo;
	}
	
	public String getOwnerPersonalInfo() {
		return ownerPersonalInfo;
	}

	public void setOwnerPersonalInfo(String ownerPersonalInfo) {
		this.ownerPersonalInfo = ownerPersonalInfo;
	}

	public ExpertDetail getRequireOwnerVO() {
		return requireOwnerVO;
	}

	public void setRequireOwnerVO(ExpertDetail requireOwnerVO) {
		this.requireOwnerVO = requireOwnerVO;
	}

	public void convert(Require require) {
		requireDetailVO.convert(require);
		if (require.getOwner() != null && require.getOwner().getExperts()!=null) {
			requireOwnerVO.convert(require.getOwner().getExperts().iterator().next());
		}

		// 最多28个字符的简单标题
		String title = require.getTitle();
		if (title != null && title.length() > 28) {
			this.shortTitle = StringUtils.substring(title, 0, 28) + " ......";
		} else {
			this.shortTitle = title;
		}
		// 最多1000个字符的说明
		String info = require.getInfo();
		if (info != null && title.length() > 1000) {
			this.shortInfo = StringUtils.substring(info, 0, 1000) + " ......";
		} else {
			this.shortInfo = info;
		}
		
		// 拥有者个人说明最多200个字符
		String personalInfo = requireOwnerVO.getPersonalInfo();
		if (personalInfo != null && personalInfo.length() > 200) {
			this.ownerPersonalInfo = StringUtils.substring(personalInfo, 0, 200) + " ......";
		} else {
			this.ownerPersonalInfo = personalInfo;
		}
		
	}
	

}
