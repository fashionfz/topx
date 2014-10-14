/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-24
 */
package system.vo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import models.Expert;
import models.SkillTag;
import models.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import system.convert.BooleanConvert;
import system.convert.SexConvert;
import system.convert.StatusConvert;
import utils.ExcelField;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.jackjson.JackJsonUtil;
import ext.paycenter.PayService;
import ext.paycenter.PayService.GetBalanceResult;

/**
 * 
 * 
 * @ClassName: UserGridVO
 * @Description: 用户列表VO
 * @date 2013-12-24 下午1:56:14
 * @author ShenTeng
 * 
 */
public class UserGridVO {
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@ExcelField(lableName="用户Id")
    private Long userId;
	@ExcelField(lableName="邮箱")
    private String email;
	@ExcelField(lableName="姓名")
    private String userName;
	@ExcelField(lableName="性别",covertClass=SexConvert.class)
    private Integer gender;
	@ExcelField(lableName="国籍")
    private String country;
	@ExcelField(lableName="标签")
    private String skillsTags;
	@ExcelField(lableName="交易次数")
    private Long tradeNum;
	@ExcelField(lableName="评分")
    private String averageScore;
	@ExcelField(lableName="余额")
    private String balance;
	@ExcelField(lableName="帐户状态",covertClass=StatusConvert.class)
    private boolean enable;
	
    private boolean isOnline;
	
    private boolean isComplain;
	@ExcelField(lableName="置顶",covertClass=BooleanConvert.class)
    private boolean top;
	@ExcelField(lableName="手机号码")
    private String phoneNumber;
	@ExcelField(lableName="注册时间")
    private String registerDate;
    
    /**
     * 行业
     */
	@ExcelField(lableName="行业")
    private String inTags;
    
    /** 置顶所在的行业id，对应tb_skill_tag表的记录的id */
	private Long topIndustry;
	
	/**
	 * 置顶行业名称
	 */
	private String topIndustryName;
	
	/**
	 * 嗨啰在线客服
	 */
	@ExcelField(lableName="嗨啰在线客服",covertClass=BooleanConvert.class)
	private boolean onlineService = Boolean.FALSE;
	
	/**
	 * 嗨啰在线翻译
	 */
	@ExcelField(lableName="嗨啰在线翻译",covertClass=BooleanConvert.class)
	private boolean onlineTranslation = Boolean.FALSE;
	
	/**
	 * 是否已经发布海外简历
	 */
	@ExcelField(lableName="海外简历发布情况")
	private String resumeStatus;

	public static UserGridVO initByUser(User user) {
        if (null == user) {
            return null;
        }

        Expert expert = CollectionUtils.isEmpty(user.experts) ? null : user.getExperts().iterator().next();

        UserGridVO vo = new UserGridVO();
        vo.setAverageScore(null == expert ? "" : Float.toString(expert.getAverageScoreWithDefault()));
        vo.setComplain(user.isComplain == null ? false : user.isComplain);
        vo.setCountry(null == expert ? "" : expert.country);
        vo.setEmail(user.email);
        vo.setEnable(user.isEnable);
		vo.setGender(user.getGender() == null ? 0 : user.getGender().ordinal());
        ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
        List<String> skillsTagsList = new ArrayList<String>();
        try {
        	if (expert !=null && StringUtils.isNotEmpty(expert.skillsTags)) {
        		skillsTagsList = objectMapper.readValue(expert.skillsTags, List.class);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
        StringBuffer skillsTags = new StringBuffer("");
		if (CollectionUtils.isNotEmpty(skillsTagsList)) {
			for(String item:skillsTagsList){
				skillsTags.append(item).append("@");
			}
			skillsTags.deleteCharAt(skillsTags.length()-1);
			
			// 嗨啰在线客服
			if (skillsTagsList.contains(new String("嗨啰在线客服"))) {
				vo.setOnlineService(true);
			}
			
			// 嗨啰在线翻译
			if (skillsTagsList.contains(new String("嗨啰在线翻译"))) {
				vo.setOnlineTranslation(true);
			}
		}
        vo.setSkillsTags(null == expert ? "" : StringUtils.defaultIfBlank(skillsTags.toString(), ""));
        vo.setTop(null == expert ? false : BooleanUtils.toBooleanDefaultIfNull(expert.isTop, false));
        vo.setTradeNum(null == expert ? 0L : expert.dealNum);
        vo.setUserId(user.id);
        vo.setUserName(StringUtils.isEmpty(user.userName)?"-":user.userName);
        vo.setPhoneNumber(user.getPhoneNumber());
		if (user.registerDate != null) {
        	vo.setRegisterDate(dateFormat.format(user.registerDate));
        }
		if (Logger.isDebugEnabled()) {
			if (expert != null) {
        		Logger.debug("专家[id:"+expert.getId()+",用户名:"+expert.userName+"]所在的行业："+expert.inTags);
        	}
        }
		// 加入对expert.topIndustry是否为空的判断
		if (vo.isTop()) {
			if (expert != null && (expert.topIndustry==null||expert.topIndustry == 0)) {
				vo.setTop(Boolean.FALSE);
			}
		}
		StringBuffer inTags = new StringBuffer("");
		if (expert != null && CollectionUtils.isNotEmpty(expert.inTags)) {
			for (SkillTag item : expert.inTags) {
				if (item != null) {
					inTags.append(item.tagName).append("@");
					if (vo.isTop() && expert.topIndustry != null && StringUtils.isEmpty(vo.getTopIndustryName())) {
						if (item.id - expert.topIndustry == 0) { // 给topIndustryName设置值
							vo.setTopIndustryName(item.tagName);
						}
					}
				}
			}
			inTags.deleteCharAt(inTags.length() - 1);
		}
		vo.setInTags(null == expert?"":StringUtils.defaultIfBlank(inTags.toString(), ""));

        if (StringUtils.isNotBlank(user.email)) {
            GetBalanceResult balance = PayService.getBalanceByEmail(user.email);
            if (GetBalanceResult.STATE.SUCCESS == balance.state) {
                vo.setBalance(balance.balance.toString());
            }
        }
        vo.setResumeStatus(user.getResumeStatus().toString().toLowerCase()); // 海外简历发布情况

        return vo;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSkillsTags() {
        return skillsTags;
    }

    public void setSkillsTags(String skillsTags) {
        this.skillsTags = skillsTags;
    }

    public Long getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(Long tradeNum) {
        this.tradeNum = tradeNum;
    }

    public String getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(String averageScore) {
        this.averageScore = averageScore;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean isEnable) {
        this.enable = isEnable;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public boolean isComplain() {
        return isComplain;
    }

    public void setComplain(boolean isComplain) {
        this.isComplain = isComplain;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean isTop) {
        this.top = isTop;
    }

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}

	public String getInTags() {
		return inTags;
	}

	public void setInTags(String inTags) {
		this.inTags = inTags;
	}
	
	public Long getTopIndustry() {
		return topIndustry;
	}

	public void setTopIndustry(Long topIndustry) {
		this.topIndustry = topIndustry;
	}

	public String getTopIndustryName() {
		return topIndustryName;
	}

	public void setTopIndustryName(String topIndustryName) {
		this.topIndustryName = topIndustryName;
	}

	public boolean isOnlineService() {
		return onlineService;
	}

	public void setOnlineService(boolean isOnlineService) {
		this.onlineService = isOnlineService;
	}

	public boolean isOnlineTranslation() {
		return onlineTranslation;
	}

	public void setOnlineTranslation(boolean isOnlineTranslation) {
		this.onlineTranslation = isOnlineTranslation;
	}

	public String getResumeStatus() {
		return resumeStatus;
	}

	public void setResumeStatus(String resumeStatus) {
		this.resumeStatus = resumeStatus;
	}
    
}
