package system.controllers;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import play.Logger;
import play.cache.Cache;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import system.models.SensitiveCharacter;
import system.vo.ext.ExtForm;
import common.Constants;

/**
 * @ClassName: SensitiveCharacterApp
 * @date 2014-04-14
 */
public class SensitiveCharacterApp extends Controller{
	
	@Transactional
	public static Result update() throws Exception {
		Cache.remove(Constants.CACHE_SENSITIVE_WORDS);
		DynamicForm requestData = Form.form().bindFromRequest();
		String id = requestData.get("id");
		String words = requestData.get("words");

		List<SensitiveCharacter> scList = JPA.em().createQuery("from SensitiveCharacter s").getResultList();
		SensitiveCharacter sc = null;
		ExtForm extForm = new ExtForm();
		try {
			if (CollectionUtils.isNotEmpty(scList)) {
				sc = scList.get(0);
				sc.setWords(words);
				JPA.em().merge(sc);
			} else {
				sc = new SensitiveCharacter(null, words);
				JPA.em().persist(sc);
			}
		} catch (Exception e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("保存或修改敏感字符出错。", e);
			}
			throw e;
//			extForm.setSuccess(false);
//			extForm.setMsg(e.getMessage());
//			return ok(play.libs.Json.toJson(extForm));
		}
		
		extForm.setSuccess(true);
		extForm.setMsg("updated");
		extForm.setData(sc);
		return ok(play.libs.Json.toJson(extForm));
	}
	
	@Transactional(readOnly = true)
	public static Result get(){
		ExtForm extForm = new ExtForm();
		try {
			List<SensitiveCharacter> scList = JPA.em().createQuery("from SensitiveCharacter s").getResultList();
			if (CollectionUtils.isNotEmpty(scList)) {
				SensitiveCharacter sc = scList.get(0);
				extForm.setSuccess(true);
				extForm.setMsg("get");
				extForm.setData(sc);
				return ok(play.libs.Json.toJson(extForm));
			} else {
				extForm.setSuccess(true);
				extForm.setMsg("get");
				extForm.setData(null);
				return ok(play.libs.Json.toJson(extForm));
			}
		} catch (Exception e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("查询敏感字符出错啦。", e);
			}
			extForm.setSuccess(false);
			extForm.setMsg(e.getMessage());
			return ok(play.libs.Json.toJson(extForm));
		}
	}
}
