package system.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Query;
import javax.persistence.Table;

import play.db.jpa.JPA;

@Entity
@Table(name = "tb_template")
public class EmailTemplate implements java.io.Serializable{

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
	@Column(name="name")
	private String name;
	@Column(name="type")
	private int type;
	@Lob()
	@Column(name="context",columnDefinition = "TEXT")
	private String context;
	@Column(name="remark")
	private String remark;
	@Column(name="para_clazz")
	private String paraClazz;
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
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * @return the context
	 */
	public String getContext() {
		return context;
	}
	/**
	 * @param context the context to set
	 */
	public void setContext(String context) {
		this.context = context;
	}
	
	
	
	/**
	 * @return the paraClazz
	 */
	public String getParaClazz() {
		return paraClazz;
	}
	/**
	 * @param paraClazz the paraClazz to set
	 */
	public void setParaClazz(String paraClazz) {
		this.paraClazz = paraClazz;
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
	public static List<EmailTemplate> getAll(){
		String jpql="from EmailTemplate t";
		Query query = JPA.em().createQuery(jpql);
		List<EmailTemplate> list = query.getResultList();
		return list;
	}
	
	public static EmailTemplate findById(Long id){
		return JPA.em().find(EmailTemplate.class, id);
	}
	
	public static void update(EmailTemplate template){
		JPA.em().persist(template);
	}
	
	public static String getTemplateContent(String name){
		String jpql = "select t.context from EmailTemplate t where t.name = ?";
		Query query = JPA.em().createQuery(jpql);
		query.setParameter(1, name);
		List<String> list = query.getResultList();
		if(null != list && list.size()>0){
			return list.get(0);
		}else{
			return "";
		}
	}
	
	public static EmailTemplate getTemplate(String name){
		String jpql = "from EmailTemplate t where t.name = ?";
		Query query = JPA.em().createQuery(jpql);
		query.setParameter(1, name);
		List<EmailTemplate> list = query.getResultList();
		if(null != list && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
}
