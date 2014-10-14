package system.controllers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import system.models.EmailTemplate;
import system.vo.EmailVariableField;
import system.vo.KeyValueVO;
import utils.EmailUtil;
import utils.ProxyUtil;
import vo.EmailInfo;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 邮件模板操作接口
 * @author bin.deng
 *
 */
public class TemplateApp extends Controller{

	/**
	 * 获取邮件模板
	  * getTemplate
	 */
	@Transactional
	public static Result getTemplate(){
		List<EmailTemplate> list = EmailTemplate.getAll();
		return ok(play.libs.Json.toJson(list));
	}
	/**
	 * 保存模板内容
	  * saveTemplate
	 */
	@Transactional
	public static Result saveTemplate(){
		DynamicForm requestData = Form.form().bindFromRequest();
		String id = requestData.get("templateId");
		String content = requestData.get("content");
		EmailTemplate template = EmailTemplate.findById(Long.parseLong(id));
		ObjectNode item = Json.newObject();
		if(template!=null){
			template.setContext(content);
			EmailTemplate.update(template);
	        item.put("success", true);
	        item.put("msg", "保存成功");
		}else{
	        item.put("success", false);
	        item.put("msg", "保存失败");
		}
		return ok(play.libs.Json.toJson(item));
	}
	/**
	 * 群发邮件测试
	  * test
	 */
	@Transactional
	public static Result test(){
		EmailInfo info = new EmailInfo("ibcm@qq.com,sbtvd@163.com,admin@dengbin.tk","test166");
		EmailUtil.pushEmail(EmailUtil.USER_REGISTER_SUCCESS_NOTIFY, info, null);
		
		EmailInfo info2 = new EmailInfo("ibcm@qq.com,sbtvd@163.com,admin@dengbin.tk","test2666");
		EmailUtil.pushEmail(EmailUtil.USER_REGISTER_SUCCESS_NOTIFY, info2, null);
		
		EmailInfo info3 = new EmailInfo("ibcm@qq.com,sbtvd@163.com,admin@dengbin.tk","test3666");
		EmailUtil.pushEmail(EmailUtil.USER_REGISTER_SUCCESS_NOTIFY, info3, null);
		return ok(play.libs.Json.toJson("ok"));
	}
	/**
	 * 邮件模板变量获取
	  * getVariable
	 */
	@Transactional
	public static Result getVariable(Long templateId){
		EmailTemplate template = EmailTemplate.findById(templateId);
		List<KeyValueVO> list = new ArrayList<KeyValueVO>();
		Field[] fields = null;
		if(EmailUtil.USER_REGISTER_SUCCESS_NOTIFY.equals(template.getName())){
			fields = ProxyUtil.getFields("vo.UserRegisterSuccessNotify");
		}
		if(fields == null){
			return ok();
		}
		for(int j=0;j<fields.length;j++){
			EmailVariableField annotation = fields[j].getAnnotation(EmailVariableField.class);
			if(annotation!=null){
				KeyValueVO vo = new KeyValueVO();
				vo.setKey(fields[j].getName());
				vo.setValue(annotation.remark());
				list.add(vo);
			}
		}
		return ok(play.libs.Json.toJson(list));
	}
}
