package vo;

import ext.MessageCenter.utils.MCMessageUtil;
import models.Expert;
import models.GroupMember;

public class GroupMemberVO {

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 角色 MEMBER:成员 ， TRANSLATE：翻译者，OWNER：群主
     */
    private String role;
    /**
     * 个人说明
     */
    private String personalInfo;
    /**
     * 小头像
     */
    private String mediumHeadUrl;
    /**
     * 职业
     */
    private String job;
    
    /**
     * 展示优先级:从0开始，值越大优先级越高（成员（不在线）：0，成员（在线）：1，当前登录用户并且不是群主本人：2，翻译者：3，群主：4 ...）
     */
    private int displayPriority;
    
    /**
     * 是否在线
     */
    private boolean isOnline;

    public void convert(GroupMember member) {
        this.userId = member.getUserId();
        this.userName = member.getUserName();
        this.role = member.getRole().name();
        if (MCMessageUtil.whetherOnline(userId)) {
        	this.isOnline = Boolean.TRUE;
        }else{
        	this.isOnline = Boolean.FALSE;
        }
		if (member.getRole() == GroupMember.Role.MEMBER) {
			this.displayPriority = 0;
			if (this.isOnline) { // 在线的普通成员
				this.displayPriority = 1;
	        }
		} else {
			this.displayPriority = 3;
		}
    }

    public void convertWithDetailInfo(GroupMember member) {
        convert(member);
        Expert expert = Expert.getExpertByUserId(this.userId);
        this.personalInfo = expert.personalInfo;
        this.mediumHeadUrl = expert.user.getAvatar(70);
        this.job = expert.job;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(String personalInfo) {
        this.personalInfo = personalInfo;
    }

    public String getMediumHeadUrl() {
        return mediumHeadUrl;
    }

    public void setMediumHeadUrl(String mediumHeadUrl) {
        this.mediumHeadUrl = mediumHeadUrl;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

	public int getDisplayPriority() {
		return displayPriority;
	}

	public void setDisplayPriority(int displayPriority) {
		this.displayPriority = displayPriority;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

}
