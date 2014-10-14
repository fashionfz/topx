package ext.msg.controllers;

import models.User;
import play.mvc.Controller;
import play.mvc.Result;

import common.Constants;

import ext.msg.remote.MessageDTO;
import ext.msg.vo.ConsultMsg;

/**
 * @ClassName: TellApp
 * @Description: 远程消息tell类
 * @date 2013年12月19日 上午11:11:10
 * @author RenYouchao
 * 
 */
public class TellApp extends Controller {

	/**
	 * @param messageType 	消息类型
	 * @param ids      		唯一标示
	 * @param json			数据json
	 * @return
	 */
	public static Result tellMessage(String messageType, String jsonObj, String ids) {
//		if (MessageApp.listener.get(ids) != null) {
//			MessageDTO messageDTO = new MessageDTO(messageType,jsonObj, ids);
//			MessageApp.myActor.tell(messageDTO, null);
//		} else {
//			return ok("{\"status\":\"200\",\"message\":\"success!not this server\"}");
//		}
		return ok("{\"status\":\"200\",\"message\":\"success!now this server\"}");
	}
	
	
	/**
	 * @param messageType 	消息类型
	 * @param ids      		唯一标示
	 * @param json			数据json
	 * @return
	 */
	public static Result tellClear() {
		String userId = User.getFromSession(session()).id.toString();
//		if (MessageApp.listener.get(userId) != null) {
//			ConsultMsg consultMsg = new ConsultMsg();
//			consultMsg.setClear(true);
//			MessageDTO messageDTO = new MessageDTO(Constants.MSG_CONSULT_CLEAR, play.libs.Json.toJson(consultMsg).toString(), userId);
//			MessageApp.myActor.tell(messageDTO, null);
//		} else {
//			return ok("{\"status\":\"200\",\"message\":\"success!not this server\"}");
//		}
		return ok("{\"status\":\"200\",\"message\":\"success!now this server\"}");
	}
	
	

}
