package vo;

import java.lang.reflect.Field;
import models.User;
import utils.ProxyUtil;

/**
 * 用户设置修改手机号码后邮件发送--参数
 * @author bin.deng
 *
 */
public class UserSettingPhoneChange extends User implements java.io.Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String locatin;
    private String path;
    private String date;
	/**
	 * @return the locatin
	 */
	public String getLocatin() {
		return locatin;
	}
	/**
	 * @param locatin the locatin to set
	 */
	public void setLocatin(String locatin) {
		this.locatin = locatin;
	}
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
	 * 构造参数
	 * @param user
	 */
	public UserSettingPhoneChange(User user){
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
