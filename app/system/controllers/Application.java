/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月21日
 */
package system.controllers;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import common.Constants;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.Context;
import system.models.Admin;
import system.models.SystemRole;
import utils.MD5Util;

public class Application extends Controller {
	
	static HashMap<String,String> accounts;
	

	
    public static Result index() {
        return ok(system.views.html.login.render());
    }
    
    @Transactional
    public static Result login() {
	    DynamicForm requestData = Form.form().bindFromRequest();
	    String userAccount = requestData.get("userAccount").trim();
	    String password = requestData.get("password").trim();
//	    if(accounts.get(userAccount) != null && accounts.get(userAccount).equals(password)){
//	    	String encrypt = encryptAdmin(userAccount, password);
//	    	return ok(play.libs.Json.parse("{\"status\":\"200\",\"message\":\"success\",\"uuid\":\"" + encrypt + "\"}"));
//	    }
	    Admin admin = Admin.queryUserByUsername(userAccount);
		if (admin != null) {
			if (StringUtils.isNotBlank(admin.getPassword())) {
				if (StringUtils.equalsIgnoreCase(MD5Util.MD5(password), admin.getPassword())) {
					String encrypt = encryptAdmin(userAccount, password);
					String clientIP = request().remoteAddress();
					Admin.updateEncryptUUID(admin.getId(), encrypt,clientIP); // 更新encryptUUID
					//获取权限
					String roles = SystemRole.getRoleByUser(admin.getUserName());
					session("roles",roles);
					session("username",admin.getUserName());
					return ok(play.libs.Json.parse("{\"status\":\"200\",\"message\":\"success\",\"uuid\":\""+ encrypt + "\"}"));
				}
	    	}
	    }
	    return ok(play.libs.Json.parse("{\"status\":\"0\",\"message\":\"用户名或密码错误\"}"));
    }

    @Transactional
    public static Result main(String uuid) {
    	Admin result = isValidEncrypt(uuid);
    	if(result==null) {
    		return redirect(system.controllers.routes.Application.index());
    	}
    	
        return ok(system.views.html.main.render(result.userName,session().get("roles")));
    }
    
    /**
     * 根据用户名和密码进行MD5加密，返回加密后的串
     * @param username
     * @param password
     * @return
     */
    private static String encryptAdmin(String username, String password) {
    	return MD5Util.MD5(username + "_" + password);
    }
    
    /**
     * 判断一个加密串是否是合法的加密串
     * 
     * @param encrypt 加密串
     * @param roleType 角色类型
     * @return
     */
    private static boolean isValidEncrypt(String encrypt,StringBuffer roleType) {
    	if(StringUtils.isBlank(encrypt)) {
    		return false;
    	}
    	Admin admin = Admin.queryUserByEncryptUUID(encrypt);
		if (admin != null) {
			if (admin.getRoleType() != null && (admin.getRoleType() - 1 == 0)) {
				roleType.append("1");
			} else {
				roleType.append("0");
			}
			return true;
		}
    	
//    	for(Map.Entry<String, String> entry : accounts.entrySet()) {
//    		String validEncrypt = encryptAdmin(entry.getKey(), entry.getValue());
//    		if(validEncrypt.equals(encrypt)) {
//    			return true;
//    		}
//    	}
    	return false;
    }
    
    
    /**
     * 判断一个加密串是否是合法的加密串
     * 
     * @param encrypt 加密串
     * @param roleType 角色类型
     * @return
     */
    private static Admin isValidEncrypt(String encrypt) {
    	if(StringUtils.isBlank(encrypt)) {
    		return null;
    	}
    	Admin admin = Admin.queryUserByEncryptUUID(encrypt);
		return admin;
    }

    @Transactional
    public static Result logout(){
    	String userName = session().get("username");
    	session().remove("roles");
    	session().remove("username");
		Admin admin = Admin.findByUserName(userName);
		if(admin!=null)
			Admin.updateEncryptUUID(admin.getId(), "",admin.getLoginIp()); // 更新encryptUUID
		return redirect(system.controllers.routes.Application.index());
    }
}
