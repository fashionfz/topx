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
@Table(name="r_role_url")
public class RRoleUrl implements java.io.Serializable{

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
	@Column(name="role_id")
	private Long roleId;
	@Column(name="url_id")
	private Long urlId;
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
	/**
	 * @return the urlId
	 */
	public Long getUrlId() {
		return urlId;
	}
	/**
	 * @param urlId the urlId to set
	 */
	public void setUrlId(Long urlId) {
		this.urlId = urlId;
	}
	
	public static void clearAuth(Long id){
		String hql="delete from RRoleUrl r where r.roleId=?";
		Query query = JPA.em().createQuery(hql);
		query.setParameter(1, id);
		query.executeUpdate();
	}
	
	public static void clearAuthByUrl(Long urlId){
		String hql="delete from RRoleUrl r where r.urlId=?";
		Query query = JPA.em().createQuery(hql);
		query.setParameter(1, urlId);
		query.executeUpdate();
	}
	
	
	public static void save(RRoleUrl r){
		JPA.em().persist(r);
	}
	
	public static void initSuperAdmin(Long roleId){
		String sql="insert into r_role_url(url_id,role_id)(select id,"+roleId+" from tb_url)";
		Query query = JPA.em().createNativeQuery(sql);
		query.executeUpdate();
	}
	
	public static void updateSuperAdmin(Long menuId,Long roleId){
		String sql="insert into r_role_url(url_id,role_id) values(?,?)";
		Query query = JPA.em().createNativeQuery(sql);
		query.setParameter(1, menuId);
		query.setParameter(2, roleId);
		query.executeUpdate();
	}
	
	public static void delSuperAdmin(Long menuId,Long roleId){
		String sql="delete from RRoleUrl r where r.urlId=? and r.roleId=?";
		Query query = JPA.em().createQuery(sql);
		query.setParameter(1, menuId);
		query.setParameter(2, roleId);
		query.executeUpdate();
	}
}
