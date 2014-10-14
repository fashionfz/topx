package controllers.user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

import models.User;
import models.service.OverseasResumeService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.jackjson.JackJsonUtil;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;
import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;


/**
 * @ClassName: OverseasResumeApp
 * @Description: 海外简历
 *
 */
public class OverseasResumeApp extends BaseApp {

	/**
	 * 创建海外简历
	 * @return
	 */
	@play.db.jpa.Transactional
	public static Result create() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String jsonStr = requestData.get("conditions");
		JsonNode json = null;
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		try {
			json = mapper.readTree(jsonStr);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ObjectNodeResult result = new ObjectNodeResult();
		if (Logger.isInfoEnabled()) {
			Logger.info("OverseasResumeApp - create() ->传入的json：" + json);
		}
		if (json == null) {
			result = result.error("Json can't be empty.", "501"); // Json can't be empty.  ---  传入的json不能为空。
			return ok(result.getObjectNode());
		}
		result = OverseasResumeService.createOrUpdateOverseasResume(json);
		return ok(result.getObjectNode());
	}
	
	/**
	 * 发布任务
	 */
	@play.db.jpa.Transactional
	public static Result addTaskForChinese(){
		DynamicForm requestData = Form.form().bindFromRequest();
		String remarkStr = requestData.get("remark");
		User currentUser = User.getFromSession(session());
		String remark = ""; // 备注
		if(StringUtils.isNotBlank(remarkStr)){
			remark = remarkStr;
		}
		ObjectNodeResult result = null;
		try {
			result = OverseasResumeService.addTaskForChinese(currentUser, remark);
		} catch (UnsupportedEncodingException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("发布海外简历任务出错。", e);
			}
		}
		return ok(result.getObjectNode());
	}
	
}
