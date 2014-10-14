package system.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;

import play.db.jpa.JPA;

/**
 * @ClassName: Admin
 * @Description: 后台管理员Admin
 * @date 2014年04月30日
 */
@javax.persistence.Entity
@Table(name = "tb_admin")
public class Admin implements java.io.Serializable {
	private static final long serialVersionUID = -101010101548098360L;

	/**
	 * 主键自增
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	/**
	 * 用户名
	 */
	@Column(unique = true)
	public String userName;
	/**
	 * 密码
	 */
	public String password;
	/**
	 * 邮箱唯一
	 */
	public String email;
	/**
	 * 手机号
	 */
	public String phoneNumber;
	/**
	 * 最后登录日期
	 */
	public Date loginDate;
	/**
	 * 最后登录IP
	 */
	public String loginIp;
	/**
	 * 备注
	 */
	public String remark;

	/**
	 * url加密字符串
	 */
	public String encryptUUID;
	
	/**
	 * 角色类型：0-运维，1-开发
	 */
	public Long roleType;
	
	public Admin() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getEncryptUUID() {
		return encryptUUID;
	}

	public void setEncryptUUID(String encryptUUID) {
		this.encryptUUID = encryptUUID;
	}

	public Long getRoleType() {
		return roleType;
	}

	public void setRoleType(Long roleType) {
		this.roleType = roleType;
	}

	/**
	 * 根据用户名获取Admin
	 * @param userName
	 * @return
	 */
	public static Admin queryUserByUsername(String userName) {
		List<Admin> adminList = JPA.em().createQuery("from Admin t where trim(t.userName) = :userName").setParameter("userName", userName).getResultList();
		if(CollectionUtils.isNotEmpty(adminList)){
			return adminList.get(0);
		}
		return null;
	}
	
	/**
	 * 更新encryptUUID
	 * @param adminId id值
	 * @param encryptUUID encryptUUID值
	 */
	public static void updateEncryptUUID(Long adminId, String encryptUUID,String clientIP) {
		JPA.em().createQuery("update Admin set encryptUUID=:encryptUUID,loginDate=:loginDate,loginIp=:loginIp where id=:id")
				.setParameter("encryptUUID", encryptUUID).setParameter("loginDate", new Date()).setParameter("loginIp", clientIP).setParameter("id", adminId).executeUpdate();
	}
	
	/**
	 * 根据encryptUUID查询Admin
	 * @param encryptUUID
	 * @return
	 */
	public static Admin queryUserByEncryptUUID(String encryptUUID) {
		List<Admin> adminList = JPA.em().createQuery("from Admin t where t.encryptUUID = :encryptUUID").setParameter("encryptUUID", encryptUUID).getResultList();
		if(CollectionUtils.isNotEmpty(adminList)){
			return adminList.get(0);
		}
		return null;
	}
	
	public static List<Admin> getAll(){
		String jpql="from Admin a";
		Query query = JPA.em().createQuery(jpql);
		return query.getResultList();	
	}
	
	public static Admin findByUserName(String userName){
		String hql="from Admin a where a.userName = ?";
		Query query = JPA.em().createQuery(hql);
		query.setParameter(1, userName);
		List<Admin> list = query.getResultList();
		if(null!=list&&list.size()>0){
			return list.get(0);
		}else
			return null;
	}
	
	public static void saveAdmin(Admin admin){
		JPA.em().persist(admin);
	}
	
	public static Admin findAdminById(Long id){
		return JPA.em().find(Admin.class, id);
	}
	
	public static void delete(Admin admin){
		JPA.em().remove(admin);
	}

}