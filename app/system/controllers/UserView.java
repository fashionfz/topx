package system.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Query;

import play.Logger;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import system.filter.SecurityUtils;
import system.models.RRoleUrl;
import system.models.SystemRole;
import system.models.SystemUrl;
import system.vo.ExtjsBeanVO;
import system.vo.MainMenuBeanVO;
import system.vo.UrlBeanVO;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserView extends Controller{

	/**
	 * 根据用户获取可视菜单
	  * getView
	 */
	@Transactional
	public static Result getView(){
		String name = session("username");
		Set<SystemUrl> list = SystemUrl.getMainTree(name);
		List<MainMenuBeanVO> result = new ArrayList<MainMenuBeanVO>();
		if(null != list && list.size()>0) {
			for(SystemUrl url : list){
				MainMenuBeanVO vo = new MainMenuBeanVO();
				vo.setId(url.getExtId());
				vo.setText(url.getName());
				vo.setLeaf(true);
				vo.setTabxtype(url.getTabxtype());
				result.add(vo);
			}
		}
		return ok(play.libs.Json.toJson(result));
	}
	/**
	 * 根据角色获取授权和未授权的功能（所有功能菜单）
	  * urlTreeByRole
	 */
	@Transactional
	public static Result urlTreeByRole(Long roleId,String parentId){
		Set<ExtjsBeanVO> list = null;
		if("-1".equals(parentId)){
			list = SystemUrl.getMainTreeByRole(roleId);
		}else{
			list = SystemUrl.getMainTreeByRole(roleId,Long.parseLong(parentId));
		}
		if(null != list && list.size()>0) {
			return ok(play.libs.Json.toJson(list));
		}else{
			return ok();
		}
	}
	/**
	 * 根据角色获取授权和未授权的功能（所有功能菜单）
	  * urlTreeByRole
	 */
	@Transactional
	public static Result urlTreeRole(Long roleId){
		Set<UrlBeanVO> list = SystemUrl.getAllTreeByRole(roleId);
		Map<Long,Set<UrlBeanVO>> map = new HashMap<Long,Set<UrlBeanVO>>();
		for(UrlBeanVO url : list){
			if(map.containsKey(url.getParentId())){
				map.get(url.getParentId()).add(url);
			}else{
				@SuppressWarnings("unchecked")
				Set<UrlBeanVO> child = new TreeSet<UrlBeanVO>(
						new Comparator(){
							@Override
							public int compare(Object arg0, Object arg1) {
								UrlBeanVO url0 = (UrlBeanVO) arg0;
								UrlBeanVO url1 = (UrlBeanVO) arg1;
								return url0.getSort()-url1.getSort();
							}
						}
				);
				child.add(url);
				map.put(url.getParentId(), child);
			}
		}
        ObjectNode json = Json.newObject();
        ArrayNode data = json.arrayNode();
        createTree(0,map,data);
        return ok(data);
	}
	
	public static void createTree(long parentId,Map<Long,Set<UrlBeanVO>> map,ArrayNode node){
		if(map.containsKey(parentId)){
			Set<UrlBeanVO> list = map.get(parentId);
			for(UrlBeanVO url : list){
	            ObjectNode item = Json.newObject();
	            ArrayNode data = item.arrayNode();
	            item.put("id", url.getId());
	            item.put("text", url.getText());
	            item.put("leaf", url.getLeaf());
	            item.put("checked", url.getChecked());
	            item.put("expanded", false);
	            createTree(url.getId(),map,data);
	            item.put("children", data);
	            node.add(item);
			}
		}
	}
	
	
	/**
	 * 根据角色获取授权和未授权的功能（所有功能菜单）
	  * urlTreeByUser
	 */
	@Transactional
	public static Result urlTree(String parentId){
		Set<ExtjsBeanVO> list = null;
		if("-1".equals(parentId)){
			list = SystemUrl.getMainTree();
		}else{
			list = SystemUrl.getMainTree(Long.parseLong(parentId));
		}
		if(null != list && list.size()>0) {
			return ok(play.libs.Json.toJson(list));
		}else{
			return ok();
		}
	}
	/**
	 * 角色授权功能菜单保存
	  * authRole
	 */
	@Transactional
	public static Result authRole(Long id,String menuIds){
		SystemRole role = SystemRole.findById(id);
		if("SUPER_ADMIN".equals(role.getName())){
			return ok(play.libs.Json.toJson("不允许配置超级管理权限!"));
		}
		//清除之前角色和菜单的关系
		RRoleUrl.clearAuth(id);
		if(menuIds!=null&&!"".equals(menuIds)){
			String[] array = menuIds.split(",");
			for(String menuId : array){
				if(!"".equals(menuId) && !"-1".equals(menuId) && !"root".equals(menuId)){
					RRoleUrl r = new RRoleUrl();
					r.setRoleId(id);
					r.setUrlId(Long.parseLong(menuId));
					RRoleUrl.save(r);
				}
			}
		}
		//更新角色功能菜单关系缓存
		SecurityUtils.refrushAuth();
	    ObjectNode item = Json.newObject();
        item.put("success", true);
        item.put("msg", "保存成功");
		return ok(play.libs.Json.toJson(item));
	}
	
	
	public static void refrushAuth(){
		String hql="select u,l from SystemUrl u,SystemRole l,RRoleUrl r where u.id=r.urlId and l.id=r.roleId";
		Query query = JPA.em().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Object[]> list = query.getResultList();
		String key="";
		for(Object[] objs : list) {
			SystemUrl url = (SystemUrl) objs[0];
			SystemRole role = (SystemRole) objs[1];
			key = url.getUrl()+","+url.getName();
			if(SecurityUtils.map.containsKey(key)){
				SecurityUtils.map.get(key).add(role.getName());
			}else{
				Set<String> roles = new HashSet<String>();
				roles.add(role.getName());
				SecurityUtils.map.put(key, roles);
			}
		}
		Cache.set("security", SecurityUtils.map);	
		Logger.info("更新权限memcache缓存完毕！"+SecurityUtils.map);
	}
}
