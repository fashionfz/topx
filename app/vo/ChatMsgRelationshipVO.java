package vo;

/**
 * 聊天最近联系人VO
 */
public class ChatMsgRelationshipVO {

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 群组id
	 */
	private Long groupId;

	/**
	 * 群组名称
	 */
	private String groupName;

	/**
	 * 联系人的类型：person (人) ， group (群组)
	 */
	private String type;

	/**
	 * 联系人的类型 person (人) ， group (群组)
	 */
	public enum Type {
		PERSON, GROUP
	}

	public ChatMsgRelationshipVO() {

	}
	
	/**
	 * @param userId
	 * @param userName
	 * @param groupId
	 * @param groupName
	 * @param type
	 */
	public ChatMsgRelationshipVO(Long userId, Long groupId,String type) {
		this.userId = userId;
		this.groupId = groupId;
		this.type = type;
	}

	/**
	 * @param userId
	 * @param userName
	 * @param groupId
	 * @param groupName
	 * @param type
	 * @param msgNum
	 */
	public ChatMsgRelationshipVO(Long userId, String userName, Long groupId,
			String groupName, String type) {
		this.userId = userId;
		this.userName = userName;
		this.groupId = groupId;
		this.groupName = groupName;
		this.type = type;
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

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
