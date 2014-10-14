package system.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;

import play.db.jpa.JPA;

@Entity
@Table(name="r_admin_role")
public class RUserRole implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键自增
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name="admin_id")
	private Long userId;
	@Column(name="role_id")
	private Long roleId;

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
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return the roleId
	 */
	public Long getRoleId() {
		return roleId;
	}

	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	
	public static void save(RUserRole r){
		JPA.em().persist(r);
	}
	
	public static void clearByUserId(Long userId){
		String hql="delete from RUserRole r where r.userId=?";
		Query query = JPA.em().createQuery(hql);
		query.setParameter(1, userId);
		query.executeUpdate();
	}
	
	public static void clearByRoleId(Long roleId){
		String hql="delete from RUserRole r where r.roleId=?";
		Query query = JPA.em().createQuery(hql);
		query.setParameter(1, roleId);
		query.executeUpdate();
	}
	
}
