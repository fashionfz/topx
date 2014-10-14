package system.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;

import play.db.jpa.JPA;
import system.vo.UserRoleVO;

@Entity
@Table(name="tb_role")
public class SystemRole implements java.io.Serializable{

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
	
	private String name;
	
	private String remark;

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
	
	
	public static String getRoleByUser(String userName){
		String hql="select l.name from SystemRole l,Admin a,RUserRole r where l.id=r.roleId and a.id = r.userId and a.userName=?";
		Query query = JPA.em().createQuery(hql);
		query.setParameter(1, userName);
		StringBuilder sb = new StringBuilder();
		List<String> list = query.getResultList();
		for(String name : list){
			sb.append(name);
			sb.append(",");
		}
		String result = sb.toString();
		if(null == result || "".equals(result)){
			return "";
		}else{
			return result.substring(0, result.length()-1);
		}
	}
	
	public static List<SystemRole> getAll(){
		String jpql = "from SystemRole r";
		Query query = JPA.em().createQuery(jpql);
		return query.getResultList();
	}
	
	public static Set<UserRoleVO> getAllByUser(String username){
		Set<UserRoleVO> set = new HashSet<UserRoleVO>();
		String sql="select l.id,l.name,l.remark,t.id as rid from tb_role l left join "
				+ "(select r.* from r_admin_role r,tb_admin a where r.admin_id=a.id and a.userName=?) t "
				+ "on t.role_id=l.id;";
		Query query = JPA.em().createNativeQuery(sql);
		query.setParameter(1, username);
		List<Object[][]> list = query.getResultList();
		for(Object[] objs : list){
			UserRoleVO vo = new UserRoleVO();
			vo.setId(Long.parseLong(objs[0].toString()));
			vo.setName(objs[1].toString());
			vo.setRemark(objs[2].toString());
			if(objs[3]==null){
				vo.setAuth(0);
			}else{
				vo.setAuth(1);
			}
			set.add(vo);
		}
		return set;
		
	}
	
	public static void save(SystemRole role){
		JPA.em().persist(role);
	}
	
	public static void delete(Long id){
		SystemRole role = JPA.em().find(SystemRole.class, id);
		if(role != null) {
			JPA.em().remove(role);
		}
	}
	
	public static SystemRole findById(Long id){
		return JPA.em().find(SystemRole.class, id);
	}
	
	public static SystemRole findByName(String name){
		String hql="from SystemRole r where r.name = ?";
		Query query = JPA.em().createQuery(hql);
		query.setParameter(1, name);
		List<SystemRole> list = query.getResultList();
		if(list != null && list.size() >0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
}
