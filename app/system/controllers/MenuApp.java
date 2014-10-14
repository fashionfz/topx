package system.controllers;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import system.filter.SecurityUtils;
import system.models.Admin;
import system.models.RRoleUrl;
import system.models.RUserRole;
import system.models.SystemRole;
import system.models.SystemUrl;
import system.vo.UserRoleVO;
import utils.MD5Util;

public class MenuApp extends Controller{

	/**
	 * 获取所有的后台功能菜单
	  * getAll
	 */
	@Transactional
	public static Result getAll(){
		List<SystemUrl> list = SystemUrl.getAll();
		if(null != list && list.size()>0) {
			return ok(play.libs.Json.toJson(list));
		}else{
			return ok();
		}
	}
	/**
	 * 获取所有的系统后台用户
	  * getAllAdmin
	 */
	@Transactional
	public static Result getAllAdmin(){
		List<Admin> list = Admin.getAll();
		if(null != list && list.size()>0) {
			return ok(play.libs.Json.toJson(list));
		}else{
			return ok();
		}
	}
	/**
	 * 获取所有的角色
	  * getAllRole
	 */
	@Transactional
	public static Result getAllRole(){
		List<SystemRole> list = SystemRole.getAll();
		if(null != list && list.size()>0) {
			return ok(play.libs.Json.toJson(list));
		}else{
			return ok();
		}
	}
	/**
	 * 根据用户获取授权和未授权的角色（所有系统角色）
	  * getAllRoleByUser
	 */
	@Transactional
	public static Result getAllRoleByUser(){
		String username = request().getQueryString("username");
		Set<UserRoleVO> list = SystemRole.getAllByUser(username);
		if(null != list && list.size()>0) {
			return ok(play.libs.Json.toJson(list));
		}else{
			return ok();
		}
	}
	/**
	 * 保存角色
	  * addRole
	 */
	@Transactional
	public static Result addRole(){
	    DynamicForm requestData = Form.form().bindFromRequest();
	    String name = requestData.get("name").trim();
	    String remark = requestData.get("remark").trim();
	    String id = requestData.get("id").trim();
	    if(id!=null&&!"".equals(id)){
	    	SystemRole role = SystemRole.findById(Long.parseLong(id));
	    	if("SUPER_ADMIN".equals(role.getName())){
	    	    ObjectNode item = Json.newObject();
	            item.put("success", false);
	            item.put("msg", "不能修改超级管理员");
	    		return ok(play.libs.Json.toJson(item));
	    	}
		    role.setName(name);
		    role.setRemark(remark);
		    SystemRole.save(role);
	    }else{
		    SystemRole role = new SystemRole();
		    role.setName(name);
		    role.setRemark(remark);
		    SystemRole.save(role);
	    }
	    ObjectNode item = Json.newObject();
        item.put("success", true);
        item.put("msg", "保存成功");
		return ok(play.libs.Json.toJson(item));
	}
	/**
	 * 删除角色
	  * delRole
	 */
	@Transactional
	public static Result delRole(Long id){
		SystemRole role = SystemRole.findById(id);
	    ObjectNode item = Json.newObject();
		if("SUPER_ADMIN".equals(role.getName())){
	        item.put("success", false);
	        item.put("msg", "不能删除超级管理员");
			return ok(play.libs.Json.toJson(item));
		}
		//清除角色和功能的关系
		RRoleUrl.clearAuth(id);
		//清除角色和用户的关系
		RUserRole.clearByRoleId(id);
		//删除角色
		SystemRole.delete(id);
        item.put("success", true);
        item.put("msg", "删除成功");
		return ok(play.libs.Json.toJson(item));
	}
	/**
	 * 用户授权角色
	  * authUser
	 */
	@Transactional
	public static Result authUser(String userName,String roleIds){
		Admin admin = Admin.queryUserByUsername(userName);
		//清除之前用户和角色的关系
		RUserRole.clearByUserId(admin.getId());
		if(roleIds!=null&&!"".equals(roleIds)){
			String[] array = roleIds.split(",");
			for(String roleId : array){
				if(!"".equals(roleId)){
					RUserRole r = new RUserRole();
					r.setRoleId(Long.parseLong(roleId));
					r.setUserId(admin.getId());
					RUserRole.save(r);
				}
			}
		}
	    ObjectNode item = Json.newObject();
        item.put("success", true);
        item.put("msg", "保存成功");
		return ok(play.libs.Json.toJson(item));
	}
	/**
	 * 保存后台用户
	  * addAdmin
	 */
	@Transactional
	public static Result addAdmin(){
		DynamicForm requestData = Form.form().bindFromRequest();
		String id = requestData.get("id");
		String userName = requestData.get("userName");
		String password = requestData.get("password");
		String email = requestData.get("email");
		String phoneNumber = requestData.get("phoneNumber");
		Admin admin = null;
		if(id == null ||"".equals(id)){
			admin = new Admin();
			admin.setPassword(MD5Util.MD5(password));
		}else{
			admin = Admin.findAdminById(Long.parseLong(id));
			if(password.length()!=32){
				admin.setPassword(MD5Util.MD5(password));
			}

		}
		admin.setUserName(userName);
		admin.setEmail(email);
		admin.setPhoneNumber(phoneNumber);
		Admin.saveAdmin(admin);
	    ObjectNode item = Json.newObject();
        item.put("success", true);
        item.put("msg", "保存成功");
		return ok(play.libs.Json.toJson(item));
	}
	/**
	 * 删除后台用户
	  * delAdmin
	 */
	@Transactional
	public static Result delAdmin(Long id){
		//清除用户的角色和关系
		RUserRole.clearByUserId(id);
		Admin admin = Admin.findAdminById(id);
		String username = session("username");
	    ObjectNode item = Json.newObject();
		if(username.equals(admin.getUserName())){
	        item.put("success", false);
	        item.put("msg", "不能删除自己");
			return ok(play.libs.Json.toJson(item));
		}else{
			Admin.delete(admin);
	        item.put("success", true);
	        item.put("msg", "删除成功");
			return ok(play.libs.Json.toJson(item));
		}
	}
	@Transactional
	public static Result getChild(){
		String id = request().getQueryString("id");
		if(id==null||"".equals(id)||"-1".equals(id)){
			id="0";
		}
		List<SystemUrl> list = SystemUrl.getChild(Long.parseLong(id));
		if(null != list && list.size()>0) {
			return ok(play.libs.Json.toJson(list));
		}else{
			return ok();
		}
	}
	@Transactional
	public static Result getMenuById(Long id){
		SystemUrl url = SystemUrl.findById(id);
		if(null != url) {
			return ok(play.libs.Json.toJson(url));
		}else{
			return ok();
		}
	}
	@Transactional
	public static Result updateMenu(){
		DynamicForm requestData = Form.form().bindFromRequest();
		String id = requestData.get("id");
		String url = requestData.get("url");
		String remark = requestData.get("remark");
		String name = requestData.get("name");
		String extId = requestData.get("extId");
		String tabxtype = requestData.get("tabxtype");
		if(null != id && !"".equals(id)){
			SystemUrl menu = SystemUrl.findById(Long.parseLong(id));
			menu.setName(name);
			menu.setRemark(remark);
			menu.setUrl(url);
			menu.setExtId(extId);
			menu.setTabxtype(tabxtype);
			SystemUrl.save(menu);
		}
		//初始化系统菜单
		SecurityUtils.refrushAuth();
	    ObjectNode item = Json.newObject();
        item.put("success", true);
        item.put("msg", "保存成功");
		return ok(play.libs.Json.toJson(item));
	}
	@Transactional
	public static Result delMenu(Long id){
	    ObjectNode item = Json.newObject();
		List<SystemUrl> list = SystemUrl.getChild(id);
		if(null == list || list.size()==0) {
			RRoleUrl.clearAuthByUrl(id);
			SystemUrl url = SystemUrl.findById(id);
			//更新父功能菜单
			List<SystemUrl> list2 = SystemUrl.getChild(url.getParentId());
			if(list2.size() == 1){
				SystemUrl parent = SystemUrl.findById(url.getParentId());
				parent.setLeaf(true);
				SystemUrl.save(parent);
			}
			//初始化超级管理员
			SecurityUtils.initSuperAdmin(url.getId(),SecurityUtils.DEL_MENU);
			SystemUrl.delete(url);
			//初始化系统菜单
			SecurityUtils.refrushAuth();
	        item.put("success", true);
	        item.put("msg", "删除成功");
			return ok(play.libs.Json.toJson(item));
		}else
		{
	        item.put("success", false);
	        item.put("msg", "该菜单有子项，请先删除子项！");
			return ok(play.libs.Json.toJson(item));
		}
	}
	@Transactional
	public static Result addMenu(){
		DynamicForm requestData = Form.form().bindFromRequest();
		String parentId = requestData.get("parentId");
		String urlstr = requestData.get("url");
		String name = requestData.get("name");
		String remark = requestData.get("remark");
		String sort = requestData.get("sort");
		String extId = requestData.get("extId");
		String tabxtype = requestData.get("tabxtype");
		if("-1".equals(parentId)){
			parentId = "0";
		}
		if(parentId != null && !"".equals(parentId)){
			SystemUrl parent = SystemUrl.findById(Long.parseLong(parentId));
			if(parent.isLeaf()){
				parent.setLeaf(false);
				SystemUrl.save(parent);
			}
			
			SystemUrl url = new SystemUrl();
			url.setUrl(urlstr);
			url.setLeaf(true);
			url.setName(name);
			url.setParentId(Long.parseLong(parentId));
			url.setRemark(remark);
			url.setSort(Integer.parseInt(sort));
			url.setTabxtype(tabxtype);
			url.setExtId(extId);
			SystemUrl.save(url);
			//初始化超级管理员
			SecurityUtils.initSuperAdmin(url.getId(),SecurityUtils.ADD_MENU);
			//初始化系统菜单
			SecurityUtils.refrushAuth();
		}
	    ObjectNode item = Json.newObject();
        item.put("success", true);
        item.put("msg", "保存成功");
		return ok(play.libs.Json.toJson(item));
	}
}
