package ext.msg.vo;

import java.util.List;

/**
 * GroupInfoVO类，群信息
 */
public class GroupInfoVO {

	/**
	 * 群组ID
	 */
	private Long groupId;

	/**
	 * 群组名称
	 */
	private String groupName;

	/**
	 * 群主(管理员)ID
	 */
	private Long groupAdminId;

	/**
	 * 所有成员的ID集合
	 */
	private List<Long> groupmembers;
	
	public GroupInfoVO() {
	}

	public GroupInfoVO(Long groupId, String groupName, Long groupAdminId,
			List<Long> groupmembers) {
		this.groupId = groupId;
		this.groupName = groupName;
		this.groupAdminId = groupAdminId;
		this.groupmembers = groupmembers;
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

	public Long getGroupAdminId() {
		return groupAdminId;
	}

	public void setGroupAdminId(Long groupAdminId) {
		this.groupAdminId = groupAdminId;
	}

	public List<Long> getGroupmembers() {
		return groupmembers;
	}

	public void setGroupmembers(List<Long> groupmembers) {
		this.groupmembers = groupmembers;
	}

}
