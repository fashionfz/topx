package ext.usercenter.vo;

/**
 * 用户中心返回的结果
 */
public class UCUserVO {

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 用户英文名称
	 */
	private String englishName;

	public UCUserVO() {
	}

	public UCUserVO(Long userId, String userName, String englishName) {
		this.userId = userId;
		this.userName = userName;
		this.englishName = englishName;
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

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

}
