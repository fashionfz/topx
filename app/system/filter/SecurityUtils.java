package system.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import play.Logger;
import play.cache.Cache;
import system.controllers.UserView;
import system.models.Admin;
import system.models.RRoleUrl;
import system.models.RUserRole;
import system.models.SystemRole;
import system.models.SystemUrl;


/**
 * 预加载所有url所属权限关系
 * @author Administrator
 *
 */
public class SecurityUtils {
	
	public static final int  ADD_MENU =1;
	public static final int  DEL_MENU =2;

	/**url->roles关系， 集群环境下需要放到memcache中*/
	public static Map<String,Set<String>> map = new HashMap<String,Set<String>>();
	/**不需要验证的url，没有配置功能，不需要同步更新，可以存在本地*/
	private static Map<String,Integer> notAuth = new HashMap<String,Integer>();
	
	/**
	 * 初始化角色和url关系
	 */
	public static void initAuth(){
		map.clear();
		EntityManagerFactory emf = null;
		EntityManager em = null;
		try{
			emf = javax.persistence.Persistence.createEntityManagerFactory("defaultPersistenceUnit");
			em = emf.createEntityManager();
			String hql="select u,l from SystemUrl u,SystemRole l,RRoleUrl r where u.id=r.urlId and l.id=r.roleId";
			Query query = em.createQuery(hql);
			@SuppressWarnings("unchecked")
			List<Object[]> list = query.getResultList();
			String key="";
			for(Object[] objs : list) {
				
				SystemUrl url = (SystemUrl) objs[0];
				SystemRole role = (SystemRole) objs[1];
				key = url.getUrl()+","+url.getName();
				if(map.containsKey(key)){
					map.get(key).add(role.getName());
				}else{
					Set<String> roles = new HashSet<String>();
					roles.add(role.getName());
					map.put(key, roles);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(null!=em){
				em.close();
			}
			if(emf!=null){
				emf.close();
			}
		}
		Cache.set("security", map);	
		Logger.info("更新权限memcache缓存完毕！"+map);
	}
	/**
	 * 初始化不需要验证的url
	 */
	public static void initNotAuth(){
		notAuth.put("/system", null);
		notAuth.put("/system/login", null);
		notAuth.put("/system/user_url", null);
		notAuth.put("/system/logout", null);
	}
	/**
	 * 对外部调用的接口
	 */
	public static Map<String,Integer> getNotAuth(){
		return notAuth;
	}
	
	public static void refrushAuth(){
		map.clear();
		UserView.refrushAuth();
	}
	
	/**
	 * 系统第一次运行初始化数据，也可以手动导入数据而不需要运行这个初始化方法
	 */
	public static void initAuthData(){
		EntityManagerFactory emf = null;
		EntityManager em = null;
		EntityTransaction tr = null;
		try{
			emf = javax.persistence.Persistence.createEntityManagerFactory("defaultPersistenceUnit");
			em = emf.createEntityManager();
			tr = em.getTransaction();
			String sql2 = "from SystemUrl u where u.name='菜单功能管理'";
			Query query2 = em.createQuery(sql2);
			@SuppressWarnings("unchecked")
			List<SystemUrl> urls = query2.getResultList();
			if(null == urls || urls.size() ==0){
				tr.begin();
				SystemUrl main = new SystemUrl();
				main.setExtId("main-treenode");
				main.setTabxtype("maingrid");
				main.setName("嗨啰后台中心");
				main.setSort(1);
				main.setParentId(0l);
				main.setLeaf(false);
				main.setPath("/1");
				main.setRemark("嗨啰后台中心");
				main.setUrl("/system/main/*");
				em.persist(main);
				tr.commit();
				
				
				tr.begin();
				SystemUrl url = new SystemUrl();
				url.setExtId("menu-treenode");
				url.setTabxtype("menugrid");
				url.setName("菜单功能管理");
				url.setSort(1);
				url.setParentId(main.getId());
				url.setLeaf(true);
				url.setPath("/1");
				url.setRemark("菜单功能管理");
				url.setUrl("/system/url_tree");
				em.persist(url);
				tr.commit();
			}
			String sql="from SystemRole r where r.name='SUPER_ADMIN'";
			Query query = em.createQuery(sql);
			@SuppressWarnings("unchecked")
			List<SystemRole> roles = query.getResultList();
			if(null == roles || roles.size() ==0){
				tr.begin();
				SystemRole role = new SystemRole();
				role.setName("SUPER_ADMIN");
				role.setRemark("超级管理员");
				em.persist(role);
				tr.commit();
				
				tr.begin();
				String sql3 = "insert into r_role_url(url_id,role_id)(select id,"+role.getId()+" from tb_url)";
				Query query3 = em.createNativeQuery(sql3);
				query3.executeUpdate();
				
				String sql4 ="from Admin a where a.userName='admin'";
				Query query4 = em.createQuery(sql4);
				@SuppressWarnings("unchecked")
				List<Admin> admins = query4.getResultList();
				for(Admin admin : admins){
					RUserRole r = new RUserRole();
					r.setRoleId(role.getId());
					r.setUserId(admin.getId());
					em.persist(r);
				}
				tr.commit();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(em!=null){
				em.close();
			}
			if(emf!=null){
				emf.close();
			}
		}
		
	}
	/**
	 * 对外调用的接口
	 */
	public static Map<String,Set<String>> getMap(){
		@SuppressWarnings("unchecked")
		Map<String,Set<String>> cacahe = (Map<String, Set<String>>) Cache.get("security");
		if(cacahe == null){
			return map;
		}
		return cacahe;
	}
	
	/**
	 * 但用户修改了url后（新增，删除）初始化超级管理员的角色
	 */
	public static void initSuperAdmin(Long menuId,int type){
		SystemRole role = SystemRole.findByName("SUPER_ADMIN");
		if(type == ADD_MENU) {
			RRoleUrl.updateSuperAdmin(menuId, role.getId());
		}else if(type == DEL_MENU) {
			RRoleUrl.delSuperAdmin(menuId, role.getId());
		}
	}
	
}
