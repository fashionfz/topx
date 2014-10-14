package system.filter;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Http.Session;
import play.mvc.Results;
import play.mvc.SimpleResult;
import system.models.OperateLog;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * url请求 权限验证
 * @author bin.deng
 *
 */
public class SecurityFilter{
	
	
	public static ThreadLocal<OperateLog> optLog = new ThreadLocal<OperateLog>();
	
	/**
	 * 权限验证过滤器
	 */
	public static SimpleResult filter(Session session,Request request){
		//如果不是‘/system’开始的url不进行验证
		if(!request.path().startsWith("/system")){
			return null;
		}
		
		OperateLog log = new OperateLog();
		//判断url是否在不需要验证的集合中
		if(SecurityUtils.getNotAuth().containsKey(request.path())){
			return null;
		}
		Object[] obj =  getAttributes(request.path());
		Set<String> pathRoles = (Set<String>) obj[0];
		if(null == pathRoles || pathRoles.size()<1){
			return null;
		}
		log.setMenuName(obj[1].toString());
		log.setMenuId(0l);
		log.setOperateIp(request.remoteAddress());
		log.setOperateTime(new Date());
		log.setOperator(session.get("username"));
		String parameters = getParameters(request.body().asFormUrlEncoded());
		if(parameters==null){
			log.setParamters(getParameters(request.queryString()));
		}else{
			log.setParamters(parameters);
		}
		log.setResult(true);
		log.setDescrible("success!");
		
		String roles = session.get("roles");
		String[] userRoles = null;
		if(roles!=null && !"".equals(roles)){
			userRoles = roles.split(",");
			for(String pathRole : pathRoles){
				for(String userRole : userRoles){
					if(pathRole.equals(userRole)){
						optLog.set(log);
						return null;
					}
				}
			}
		}
		log.setResult(false);
		log.setDescrible(obj[1]+": 权限不足，请联系管理员！");
	    ObjectNode item = Json.newObject();
        item.put("success", false);
        item.put("code", 503);
        item.put("msg", obj[1]+": 权限不足，请联系管理员！");
        SimpleResult result = Results.ok(item);
        optLog.set(log);
		return result;
	}
	/**
	 * 查询url所属的角色
	 */
	public static Object[] getAttributes(String path)
			throws IllegalArgumentException {
		Set<String> set = null;
		Iterator<String> ite = SecurityUtils.getMap().keySet().iterator();
		Object[] result = null;
		while (ite.hasNext()) {
			String resURL = ite.next();
			if (pathMatchesUrl(resURL.split(",")[0], path)) {
				result = new Object[2];
				set = SecurityUtils.getMap().get(resURL);
				result[0]=set;
				result[1]=resURL.split(",")[1];
				break;
			}
		}
		//如果没有配置任何角色，默认是超级管理员角色
		if(null == result){
			set = new HashSet<String>();
			set.add("SUPER_ADMIN");
			result = new Object[2];
			result[0]=set;
			result[1]=path;
			
		}
		return result;
	}
	/**
	 * url匹配
	 */
	public static boolean pathMatchesUrl(String resUrl, String url) {
		//以'*'结尾的使用模糊匹配
		if(resUrl.endsWith("*")){
			if(url.startsWith(resUrl.substring(0, resUrl.length()-1))){
				return true;
			}else{
				return false;
			}
		}else{
			if (resUrl.equals(url)||url.equals("/"+resUrl))
				return true;
			else
				return false;
		}
	}
	
	public static String getParameters(Map<String, String[]> map){
		if(map==null){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		Iterator<Map.Entry<String, String[]>> it = map.entrySet().iterator();
		while (it.hasNext()) {
		   Map.Entry<String, String[]> entry = it.next();
		   sb.append(entry.getKey() + ":" + entry.getValue()[0]+",");
		  }
		String result = sb.toString();
		if(result.length()==1){
			result=result+"]";
		}else{
			result = result.substring(0, result.length()-1)+"]";
		}
		return result;
	}

}
