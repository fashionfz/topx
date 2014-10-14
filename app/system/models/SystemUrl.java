package system.models;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;

import play.db.jpa.JPA;
import system.vo.ExtjsBeanVO;
import system.vo.UrlBeanVO;

@Entity
@Table(name = "tb_url")
public class SystemUrl implements java.io.Serializable{

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
	@Column(name="url")
	private String url;
	@Column(name="name")
	private String name;
	@Column(name="parent_id")
	private Long parentId;
	@Column(name="sort")
	private int sort;
	@Column(name="path")
	private String path;
	@Column(name="remark")
	private String remark;
	//页面需要
	@Column(name="ext_id")
	private String extId;
	@Column(name="tabxtype")
	private String tabxtype;
	@Column(name="leaf")
	private boolean leaf;

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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
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
	 * @return the parentId
	 */
	public Long getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the sort
	 */
	public int getSort() {
		return sort;
	}

	/**
	 * @param sort the sort to set
	 */
	public void setSort(int sort) {
		this.sort = sort;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
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
	 * @return the extId
	 */
	public String getExtId() {
		return extId;
	}

	/**
	 * @param extId the extId to set
	 */
	public void setExtId(String extId) {
		this.extId = extId;
	}

	/**
	 * @return the tabxtype
	 */
	public String getTabxtype() {
		return tabxtype;
	}

	/**
	 * @param tabxtype the tabxtype to set
	 */
	public void setTabxtype(String tabxtype) {
		this.tabxtype = tabxtype;
	}

	/**
	 * @return the leaf
	 */
	public boolean isLeaf() {
		return leaf;
	}

	/**
	 * @param leaf the leaf to set
	 */
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	
	
	public static Set<SystemUrl> getMainTree(String username){
		
		SystemUrl url = SystemUrl.getRootNode();
		if(null == username || "".equals(username)){
			return null;
		}
		String hql="select u from SystemUrl u,Admin a,SystemRole l,RUserRole r,RRoleUrl rr "
				+ "where u.id=rr.urlId and l.id=rr.roleId and l.id=r.roleId and a.id=r.userId and u.parentId = ? and a.userName = ?";
		Query query = JPA.em().createQuery(hql);
		query.setParameter(1, url.getId());
		query.setParameter(2, username);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Set<SystemUrl> result = new TreeSet<SystemUrl>(
				new Comparator(){
					@Override
					public int compare(Object arg0, Object arg1) {
						SystemUrl url0 = (SystemUrl) arg0;
						SystemUrl url1 = (SystemUrl) arg1;
						return url0.getSort()-url1.getSort();
					}
				}
		);
		result.addAll(query.getResultList());
		return result;
	}
	
	public static List<SystemUrl> getAll(){
		String hql="from SystemUrl u";
		Query query = JPA.em().createQuery(hql);
		return query.getResultList();
	}
	
	public static Set<ExtjsBeanVO> getMainTreeByRole(Long roleId){
		if(null == roleId || "".equals(roleId)){
			return null;
		}
		String hql="select u.id,u.name,u.leaf,r.id as rid,u.sort from tb_url u left join r_role_url r on u.id=r.url_id and r.role_id=? where u.parent_id=0";
		Query query = JPA.em().createNativeQuery(hql);
		query.setParameter(1, roleId);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Set<ExtjsBeanVO> result = new TreeSet<ExtjsBeanVO>(
				new Comparator(){
					@Override
					public int compare(Object arg0, Object arg1) {
						ExtjsBeanVO url0 = (ExtjsBeanVO) arg0;
						ExtjsBeanVO url1 = (ExtjsBeanVO) arg1;
						return url0.getSort()-url1.getSort();
					}
				}
		);
		@SuppressWarnings("unchecked")
		List<Object[][]> list = query.getResultList();
		for(Object[] objs : list){
			ExtjsBeanVO vo = new ExtjsBeanVO();
			vo.setId(Long.parseLong(objs[0].toString()));
			vo.setText(objs[1].toString());
			vo.setLeaf((Boolean)objs[2]);
			if(objs[3]==null){
				vo.setChecked(false);
			}else{
				vo.setChecked(true);
			}
			vo.setSort((Integer)objs[4]);
			result.add(vo);
		}
		return result;
	}
	
	
	
	public static Set<ExtjsBeanVO> getMainTree(){
		String hql="from SystemUrl u where u.parentId=0";
		Query query = JPA.em().createQuery(hql);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Set<ExtjsBeanVO> result = new TreeSet<ExtjsBeanVO>(
				new Comparator(){
					@Override
					public int compare(Object arg0, Object arg1) {
						ExtjsBeanVO url0 = (ExtjsBeanVO) arg0;
						ExtjsBeanVO url1 = (ExtjsBeanVO) arg1;
						return url0.getSort()-url1.getSort();
					}
				}
		);
		@SuppressWarnings("unchecked")
		List<SystemUrl> list = query.getResultList();
		for(SystemUrl obj : list){
			ExtjsBeanVO vo = new ExtjsBeanVO();
			vo.setId(obj.getId());
			vo.setText(obj.getName());
			vo.setLeaf(obj.isLeaf());
			vo.setSort(obj.getSort());
			result.add(vo);
		}
		return result;
	}
	
	public static Set<ExtjsBeanVO> getMainTreeByRole(Long roleId,Long parentId){
		if(null == roleId || "".equals(roleId)){
			return null;
		}
		String hql="select u.id,u.name,u.leaf,r.id as rid,u.sort from tb_url u left join r_role_url r on u.id=r.url_id and r.role_id=? where u.parent_id=?";
		Query query = JPA.em().createNativeQuery(hql);
		query.setParameter(1, roleId);
		query.setParameter(2, parentId);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Set<ExtjsBeanVO> result = new TreeSet<ExtjsBeanVO>(
				new Comparator(){
					@Override
					public int compare(Object arg0, Object arg1) {
						ExtjsBeanVO url0 = (ExtjsBeanVO) arg0;
						ExtjsBeanVO url1 = (ExtjsBeanVO) arg1;
						return url0.getSort()-url1.getSort();
					}
				}
		);
		@SuppressWarnings("unchecked")
		List<Object[][]> list = query.getResultList();
		for(Object[] objs : list){
			ExtjsBeanVO vo = new ExtjsBeanVO();
			vo.setId(Long.parseLong(objs[0].toString()));
			vo.setText(objs[1].toString());
			vo.setLeaf((Boolean)objs[2]);
			if(objs[3]==null){
				vo.setChecked(false);
			}else{
				vo.setChecked(true);
			}
			vo.setSort((Integer)objs[4]);
			result.add(vo);
		}
		return result;
	}
	
	
	public static Set<UrlBeanVO> getAllTreeByRole(Long roleId){
		if(null == roleId || "".equals(roleId)){
			return null;
		}
		String hql="select u.id,u.name,u.leaf,r.id as rid,u.sort,u.parent_id from tb_url u left join r_role_url r on u.id=r.url_id and r.role_id=?";
		Query query = JPA.em().createNativeQuery(hql);
		query.setParameter(1, roleId);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Set<UrlBeanVO> result = new TreeSet<UrlBeanVO>(
				new Comparator(){
					@Override
					public int compare(Object arg0, Object arg1) {
						UrlBeanVO url0 = (UrlBeanVO) arg0;
						UrlBeanVO url1 = (UrlBeanVO) arg1;
						return (int) (url0.getId()-url1.getId());
					}
				}
		);
		@SuppressWarnings("unchecked")
		List<Object[][]> list = query.getResultList();
		for(Object[] objs : list){
			UrlBeanVO vo = new UrlBeanVO();
			vo.setId(Long.parseLong(objs[0].toString()));
			vo.setText(objs[1].toString());
			vo.setLeaf((Boolean)objs[2]);
			if(objs[3]==null){
				vo.setChecked(false);
			}else{
				vo.setChecked(true);
			}
			vo.setSort((Integer)objs[4]);
			vo.setParentId(Long.parseLong(objs[5].toString()));
			result.add(vo);
		}
		return result;
	}
	
	public static Set<ExtjsBeanVO> getMainTree(Long parentId){
		String hql="from SystemUrl u where u.parentId=?";
		Query query = JPA.em().createQuery(hql);
		query.setParameter(1, parentId);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Set<ExtjsBeanVO> result = new TreeSet<ExtjsBeanVO>(
				new Comparator(){
					@Override
					public int compare(Object arg0, Object arg1) {
						ExtjsBeanVO url0 = (ExtjsBeanVO) arg0;
						ExtjsBeanVO url1 = (ExtjsBeanVO) arg1;
						return url0.getSort()-url1.getSort();
					}
				}
		);
		@SuppressWarnings("unchecked")
		List<SystemUrl> list = query.getResultList();
		for(SystemUrl obj : list){
			ExtjsBeanVO vo = new ExtjsBeanVO();
			vo.setId(obj.getId());
			vo.setText(obj.getName());
			vo.setLeaf(obj.isLeaf());
			vo.setSort(obj.getSort());
			result.add(vo);
		}
		return result;
	}
	
	public static List<SystemUrl> getChild(Long id){
		String hql="from SystemUrl u where u.parentId = ?";
		Query query = JPA.em().createQuery(hql);
		query.setParameter(1, id);
		return query.getResultList();
	}
	
	public static SystemUrl findById(Long id){
		return JPA.em().find(SystemUrl.class, id);
	}
	
	public static void save(SystemUrl url){
		JPA.em().persist(url);
	}
	
	public static void delete(SystemUrl url){
		JPA.em().remove(url);
	}
	
	public static SystemUrl getRootNode(){
		String hql="from SystemUrl u where u.parentId =0";
		Query query = JPA.em().createQuery(hql);
		List<SystemUrl> list = query.getResultList();
		if(null != list && list.size()>0){
			return list.get(0);
		}
		else{
			return null;
		}
	}
}
