package ext.MessageCenter.Message.chatMessage.vo;

import models.Group;

/**
 * 知识库系统中查询出的群组信息对应的VO
 */
public class ChatGroupVO {

	/**
	 * 群组id
	 */
	private Long id;

	/**
	 * 群组名称
	 */
	private String groupName;

	/**
	 * 成员个数
	 */
	private Long countMem;

	/**
	 * 群主userId
	 */
	private Long ownerId;

	/**
	 * 群组类型
	 */
	private Integer type;

	/**
	 * 翻译者id
	 */
	private Long translatorId;
	
	public ChatGroupVO() {
	}
	

	public ChatGroupVO(Long id, String groupName, Long ownerId,
			Integer type, Long translatorId) {
		this.id = id;
		this.groupName = groupName;
		this.ownerId = ownerId;
		this.type = type;
		this.translatorId = translatorId;
	}
	
	public ChatGroupVO(Long id, String groupName, Long countMem, Long ownerId,
			Integer type, Long translatorId) {
		this.id = id;
		this.groupName = groupName;
		this.countMem = countMem;
		this.ownerId = ownerId;
		this.type = type;
		this.translatorId = translatorId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Long getCountMem() {
		return countMem;
	}

	public void setCountMem(Long countMem) {
		this.countMem = countMem;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getTranslatorId() {
		return translatorId;
	}

	public void setTranslatorId(Long translatorId) {
		this.translatorId = translatorId;
	}
	
	/**
	 * 将该对象装换成Group对象
	 */
	public Group convert() {
		Group group = new Group();
		group.setId(this.id);
		group.setGroupName(this.groupName);
		group.setType(Group.Type.getByOrdinal(this.type));
		if (Group.Type.getByOrdinal(this.type) == Group.Type.TRANSLATE) {
			group.setCountTranslate(1L);
		}
		return group;
	}

}
