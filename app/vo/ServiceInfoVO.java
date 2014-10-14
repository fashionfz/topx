package vo;

import org.apache.commons.lang.StringUtils;

import models.Service;


/**
 * service展示页面VO
 */
public class ServiceInfoVO {

	/** service明细 */
	private ServiceDetailVO serviceDetailVO = new ServiceDetailVO();
	
	/** 最多28个字符 */
	private String shortTitle;
	
	/** 最多1000个字符 */
	private String shortInfo;
	
	/** 拥有者个人说明 */
	private String ownerPersonalInfo;

	/** service创建者 */
	private ExpertDetail serviceOwner = new ExpertDetail();

	public ServiceDetailVO getServiceDetailVO() {
		return serviceDetailVO;
	}

	public void setServiceDetailVO(ServiceDetailVO serviceDetailVO) {
		this.serviceDetailVO = serviceDetailVO;
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

	public ExpertDetail getServiceOwner() {
		return serviceOwner;
	}

	public void setServiceOwner(ExpertDetail serviceOwner) {
		this.serviceOwner = serviceOwner;
	}

	public void convert(Service service) {
		serviceDetailVO.convert(service);
		if (service.getOwner() != null && service.getOwner().getExperts() != null) {
			serviceOwner.convert(service.getOwner().getExperts().iterator().next());
		}
		// 最多28个字符的简单标题
		String title = service.getTitle();
		if (title != null && title.length() > 28) {
			this.shortTitle = StringUtils.substring(title, 0, 28) + " ......";
		} else {
			this.shortTitle = title;
		}
		// 最多1000个字符的说明
		String info = service.getInfo();
		if(info!=null&& title.length()>1000){
			this.shortInfo = StringUtils.substring(info, 0,1000) + " ......";
		}else{
			this.shortInfo = info;
		}
		
		// 拥有者个人说明最多200个字符
		String personalInfo = serviceOwner.getPersonalInfo();
		if (personalInfo != null && personalInfo.length() > 200) {
			this.ownerPersonalInfo = StringUtils.substring(personalInfo, 0, 200) + " ......";
		} else {
			this.ownerPersonalInfo = personalInfo;
		}
	}

}
