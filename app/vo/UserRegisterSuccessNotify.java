package vo;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Set;

import models.Expert;
import models.Gender;
import models.User;
import models.service.reminder.RemindService;
import system.vo.EmailVariableField;
import utils.ProxyUtil;

/**
 * 用户注册有邮件发送--参数
 * @author bin.deng
 *
 */
public class UserRegisterSuccessNotify implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EmailVariableField(remark="路径")
	private String path;
	@EmailVariableField(remark="系统日期")
	private String date;
	@EmailVariableField(remark="用户ID")
    public Long id;
	@EmailVariableField(remark="用户唯一ID")
    public String uid;
    /** 请描述 **/
    public Set<Expert> experts;
    /** 邮箱唯一 **/
    @EmailVariableField(remark="用户邮箱")
    public String email;
    /** 登录用户名 **/
    @EmailVariableField(remark="用户登录用户名")
    public String loginUsername;
    /** 姓名 **/
    @EmailVariableField(remark="用户姓名")
    public String userName;
    /** 手机号 **/
    @EmailVariableField(remark="用户手机号")
    public String phoneNumber;
    /** 加密后的用户密码 **/
    @EmailVariableField(remark="用户加密后的用户密码")
    public String encryptedPassword;
    /** 头像地址 **/
    @EmailVariableField(remark="用户头像地址")
    public String avatar;
    /** 性别有默认值 **/
    @EmailVariableField(remark="用户性别")
    public Gender gender = Gender.MAN;
    /** 注册日期 **/
    public Date registerDate = new java.util.Date();
    /** 是否禁用 **/
    public Boolean isEnable = true;
    /** 是否投诉 **/
    public Boolean isComplain = false;
    /** 是否是专家 **/
    public Boolean isEx = false;
    /** 提醒设置,不要直接使用该字段，请使用{@link RemindService}里的相关方法 **/
    public String safetyReminderConfig;
    /** 用户登录地址，记录最近一次 */
    public String loginAddress;
    // /** 负载均衡当前的服务器的ip跟端口 **/
    // public String ipLocal;
    /**
     * 最近一次记住我登录的时间
     */
    public Date lastRMLoginTime;
    
    /**
     * 是否禁止发布海外简历 <br/>
     * true:是，false:否
     */
    public Boolean isForbidAddResumeTask = false;
    
    /**
     * 是否已发布海外简历 <br/>
     * true：是，false：否
     */
    public Boolean isResumePublished = false;
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	
	
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}
	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	/**
	 * @return the experts
	 */
	public Set<Expert> getExperts() {
		return experts;
	}
	/**
	 * @param experts the experts to set
	 */
	public void setExperts(Set<Expert> experts) {
		this.experts = experts;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the loginUsername
	 */
	public String getLoginUsername() {
		return loginUsername;
	}
	/**
	 * @param loginUsername the loginUsername to set
	 */
	public void setLoginUsername(String loginUsername) {
		this.loginUsername = loginUsername;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	/**
	 * @return the encryptedPassword
	 */
	public String getEncryptedPassword() {
		return encryptedPassword;
	}
	/**
	 * @param encryptedPassword the encryptedPassword to set
	 */
	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}
	/**
	 * @return the avatar
	 */
	public String getAvatar() {
		return avatar;
	}
	/**
	 * @param avatar the avatar to set
	 */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
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
	 * @return the registerDate
	 */
	public Date getRegisterDate() {
		return registerDate;
	}
	/**
	 * @param registerDate the registerDate to set
	 */
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	/**
	 * @return the isEnable
	 */
	public Boolean getIsEnable() {
		return isEnable;
	}
	/**
	 * @param isEnable the isEnable to set
	 */
	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}
	/**
	 * @return the isComplain
	 */
	public Boolean getIsComplain() {
		return isComplain;
	}
	/**
	 * @param isComplain the isComplain to set
	 */
	public void setIsComplain(Boolean isComplain) {
		this.isComplain = isComplain;
	}
	/**
	 * @return the isEx
	 */
	public Boolean getIsEx() {
		return isEx;
	}
	/**
	 * @param isEx the isEx to set
	 */
	public void setIsEx(Boolean isEx) {
		this.isEx = isEx;
	}
	/**
	 * @return the safetyReminderConfig
	 */
	public String getSafetyReminderConfig() {
		return safetyReminderConfig;
	}
	/**
	 * @param safetyReminderConfig the safetyReminderConfig to set
	 */
	public void setSafetyReminderConfig(String safetyReminderConfig) {
		this.safetyReminderConfig = safetyReminderConfig;
	}
	/**
	 * @return the loginAddress
	 */
	public String getLoginAddress() {
		return loginAddress;
	}
	/**
	 * @param loginAddress the loginAddress to set
	 */
	public void setLoginAddress(String loginAddress) {
		this.loginAddress = loginAddress;
	}
	/**
	 * @return the lastRMLoginTime
	 */
	public Date getLastRMLoginTime() {
		return lastRMLoginTime;
	}
	/**
	 * @param lastRMLoginTime the lastRMLoginTime to set
	 */
	public void setLastRMLoginTime(Date lastRMLoginTime) {
		this.lastRMLoginTime = lastRMLoginTime;
	}
	/**
	 * @return the isForbidAddResumeTask
	 */
	public Boolean getIsForbidAddResumeTask() {
		return isForbidAddResumeTask;
	}
	/**
	 * @param isForbidAddResumeTask the isForbidAddResumeTask to set
	 */
	public void setIsForbidAddResumeTask(Boolean isForbidAddResumeTask) {
		this.isForbidAddResumeTask = isForbidAddResumeTask;
	}
	/**
	 * @return the isResumePublished
	 */
	public Boolean getIsResumePublished() {
		return isResumePublished;
	}
	/**
	 * @param isResumePublished the isResumePublished to set
	 */
	public void setIsResumePublished(Boolean isResumePublished) {
		this.isResumePublished = isResumePublished;
	}
	/**
	 * 构成参数
	 * @param user
	 */
	public UserRegisterSuccessNotify(User user){
		Field[] fields = ProxyUtil.getFieldtoObject(user);
		for(Field field : fields){
			try {
				ProxyUtil.setter(this, field.getName(), ProxyUtil.getter(user, field.getName()), field.getType());
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
}
