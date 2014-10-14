package utils;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import system.models.EmailTemplate;
import vo.EmailInfo;

public class EmailUtil {

    private static Queue<EmailInfo> emailQueue = new ConcurrentLinkedQueue<EmailInfo>();  
    
    private static VelocityEngine engine = new VelocityEngine();
    
    public static final int TEXT_TYPE=1;
    public static final int HTML_TYPE=2;
    
    public static final String USER_REGISTER_SUCCESS_NOTIFY = "register_success_notify";
    
//    public static final String USER_SETTING_PHONE_CHANGE = "user_setting_phone_change";
//    
//    public static final String FORGET_PASSWORD_NOTIFY = "forget_password_nofigy";
    
    
    /**
     * velocity模板邮件
      * pushEmail
     */
    public static String pushEmail(String templateName,EmailInfo email,VelocityContext context){
    	EmailTemplate template = EmailTemplate.getTemplate(templateName);
    	StringWriter writer = new StringWriter();
    	engine.evaluate(context, writer, "eamilTemplate", template.getContext());
		if(template.getType() == EmailUtil.TEXT_TYPE){
			email.setBodyAsText(template.getContext());
		}else if(template.getType() == EmailUtil.HTML_TYPE){
			email.setBodyAsHTML(template.getContext());
		}
		emailQueue.add(email);
		return "email 已经发送";
    }
    
    /**
     * velocity模板邮件
      * pushEmail
     */
    public static String pushEmail(int type,String content,EmailInfo email,VelocityContext context){
    	StringWriter writer = new StringWriter();
    	engine.evaluate(context, writer, "eamilTemplate", content);
		if(type == EmailUtil.TEXT_TYPE){
			email.setBodyAsText(content);
		}else if(type == EmailUtil.HTML_TYPE){
			email.setBodyAsHTML(content);
		}
		emailQueue.add(email);
		return "email 已经发送";
    }
    
	/**
	 * 对象参数自定义解析
	  * pushEmail
	 */
	public static String pushEmail(String templateName,EmailInfo email,Object param){
		EmailTemplate template = EmailTemplate.getTemplate(templateName);
		String templateContent = template.getContext();
		Field[] fields = ProxyUtil.getFieldtoObject(param);
		if(null != fields && null != templateContent){
			for(Field field : fields){
				try {
					templateContent = templateContent.replaceAll("@"+field.getName(), String.valueOf(ProxyUtil.getter(param, field.getName())));
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
		if(template.getType() == EmailUtil.TEXT_TYPE){
			email.setBodyAsText(templateContent);
		}else if(template.getType() == EmailUtil.HTML_TYPE){
			email.setBodyAsHTML(templateContent);
		}
		emailQueue.add(email);
		return "email 已经发送";
	}
	/**
	 * 对象参数自定义解析
	  * pushEmail
	 */
	public static String pushEmail(int type,String content,EmailInfo email,Object param){
		Field[] fields = ProxyUtil.getFieldtoObject(param);
		if(null != fields && null != content){
			for(Field field : fields){
				try {
					content = content.replaceAll("@"+field.getName(), String.valueOf(ProxyUtil.getter(param, field.getName())));
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
		if(type == EmailUtil.TEXT_TYPE){
			email.setBodyAsText(content);
		}else if(type == EmailUtil.HTML_TYPE){
			email.setBodyAsHTML(content);
		}
		emailQueue.add(email);
		return "email 已经发送";
	}
	/**
	 * 直接发送方式
	  * pushEmail
	 */
	public static String pushEmail(EmailInfo email) {
		emailQueue.add(email);
		return "email 已经发送";
	}
	
	public static Queue<EmailInfo> getEmailQueue(){
		return emailQueue;
	}

}
