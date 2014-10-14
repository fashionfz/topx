package system.vo;

import system.models.SystemRole;

public class UserRoleVO {
	private Long id;
	
	private String name;
	
	private String remark;
	
	private int auth;

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the auth
	 */
	public int getAuth() {
		return auth;
	}

	/**
	 * @param auth the auth to set
	 */
	public void setAuth(int auth) {
		this.auth = auth;
	}
	
	
	public void copy(SystemRole role){
		this.id = role.getId();
		this.name = role.getName();
		this.remark = role.getRemark();
	}
}
