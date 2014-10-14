package vo;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * 聊天记录VO
 */
public class ChatMessageRecordVO {
//	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 用户邮箱
	 */
	private String email;

	/**
	 * 消息内容
	 */
	private String content;

	/**
	 * 消息发送时间
	 */
	private Date msgTime;
	
	/**
	 * 用户头像 静态资源访问地址
	 */
	private String avatar;
	
	/**
	 * 消息总数
	 */
	private Long msgNum;
	
	/**
	 * 聊天消息的类型 
	 * me opposite
	 */
	private String type;
	
	/**
	 * 群组id
	 */
	private Long groupId;
	
	/**
	 * 群组名称
	 */
	private String groupName;
	
	/**
	 * 群组头像 静态资源访问地址
	 */
	private String groupAvatar;
	
	/**
	 * 群组类型 
	 * normal (普通) translate (翻译)
	 */
	private String groupType;
	
	/**
	 * content的类型
	 */
	private Long contentType;
	
	/**
	 * messageId
	 */
	private String messageId;
	

	public ChatMessageRecordVO() {
	}
	
	public ChatMessageRecordVO(Long userId,String userName, String email, String content,
			Date msgTime,String type) {
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		this.content = content;
		this.msgTime = msgTime;
		this.type = type;
	}

	public ChatMessageRecordVO(String userName, String email, String content,
			Date msgTime) {
		this.userName = userName;
		this.email = email;
		this.content = content;
		this.msgTime = msgTime;
	}

	public ChatMessageRecordVO(Long userId, String content, Date msgTime,String type) {
		this.userId = userId;
		this.content = content;
		this.msgTime = msgTime;
		this.type = type;
	}

	/**
	 * @param userId
	 * @param content
	 * @param msgTime
	 * @param type
	 * @param groupId 群组的id
	 */
	public ChatMessageRecordVO(Long userId, String content, Date msgTime, String type, Long groupId) {
		this.userId = userId;
		this.content = content;
		this.msgTime = msgTime;
		this.type = type;
		this.groupId = groupId;
	}
	
	/**
	 * @param userId
	 * @param content
	 * @param msgTime
	 * @param type
	 * @param groupId 群组的id
	 * @param messageId
	 */
	public ChatMessageRecordVO(Long userId, String content, Date msgTime, String type, Long groupId, String messageId) {
		this.userId = userId;
		this.content = content;
		this.msgTime = msgTime;
		this.type = type;
		this.groupId = groupId;
		this.messageId = messageId;
	}
	
	/**
	 * @param userId
	 * @param content
	 * @param msgTime
	 * @param type
	 * @param groupId 群组的id
	 * @param contentType content的类型
	 * @param messageId
	 */
	public ChatMessageRecordVO(Long userId, String content, Date msgTime, String type, Long groupId, Long contentType, String messageId) {
		this.userId = userId;
		this.content = content;
		this.msgTime = msgTime;
		this.type = type;
		this.groupId = groupId;
		this.contentType = contentType;
		this.messageId = messageId;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getMsgTime() {
		return msgTime;
	}

//	public String  getMsgformatTime() {
//		if (msgTime == null) { // 避免空指针异常
//			return null;
//		}
//		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msgTime);
//	}
	public void setMsgTime(Date msgTime) {
		this.msgTime = msgTime;
	}
	
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Long getMsgNum() {
		return msgNum;
	}

	public void setMsgNum(Long msgNum) {
		this.msgNum = msgNum;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	
	public String getGroupAvatar() {
		return groupAvatar;
	}

	public void setGroupAvatar(String groupAvatar) {
		this.groupAvatar = groupAvatar;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}
	
	public Long getContentType() {
		return contentType;
	}

	public void setContentType(Long contentType) {
		this.contentType = contentType;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	///////////////////////////////////////////////////////////////
	// 重写equals方法和HashCode方法，便于set集合去重
	///////////////////////////////////////////////////////////////
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return this == null;
		if (obj instanceof ChatMessageRecordVO) {
			ChatMessageRecordVO other = (ChatMessageRecordVO) obj;
			return (this.userId != null) && (other.userId != null)
					&& (this.userId - other.userId == 0)
					&& StringUtils.equals(this.content, other.content)
					&& this.msgTime.equals(other.msgTime);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if (this.userId != null && this.content != null) {
			return this.userId.hashCode() << 5 + this.content.hashCode();
		}
		return "".hashCode();
	}
	
}
