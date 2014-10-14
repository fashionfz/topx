package controllers.user;

import java.util.List;

import models.Require;
import models.SkillTag;
import models.User;
import models.service.RequireService;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Result;
import vo.RequireDetailVO;
import vo.page.Page;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;

public class UserRequireApp extends BaseApp {

	/**
	 * 需求 list
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result list() {
		
		return ok(views.html.usercenter.require.list.render());
	}

	/**
	 * 添加修改 需求
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result write() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String requireId = requestData.get("rid");
		
		List<SkillTag> tags = SkillTag.listCategories(100);
    	for (SkillTag skillTag : tags) {
			skillTag.industry = null;
		}
    	
    	RequireDetailVO requireVO = new RequireDetailVO();
		if (requireId != null) {
			Require require = Require.queryRequireById(Long.parseLong(requireId));
			if (require != null) {
				requireVO.convert(require);
			}
		}
    	
		return ok(views.html.usercenter.require.writeInfo.render(tags,requireVO));
	}
	
	/**
	 * 创建或修改需求
	 * @return
	 */
	@Transactional
	public static Result createOrUpdateRequire(){
		JsonNode json = getJson();
		User currentUser = User.getFromSession(session());
		ObjectNodeResult result = RequireService.createOrUpdateService(currentUser, json);
		return ok(result.getObjectNode());
	}
	
	@Transactional(readOnly = true)
	public static Result queryRequires() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String pageStr = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "10" : requestData.get("pageSize");
		User currentUser = User.getFromSession(session());
		
		Page<RequireDetailVO> pageRequires = null;
		String searchText = requestData.get("searchText");
		Integer page = Integer.parseInt(pageStr);
		if (page != null && page < 0) {
			page = 0;
		}
		if (StringUtils.isNotBlank(searchText)) {
			pageRequires = RequireService.getRequirePage(page, Integer.parseInt(pageSize), currentUser, searchText);
		} else {
			pageRequires = RequireService.getRequirePage(page, Integer.parseInt(pageSize), currentUser, "");
		}
		
		if (Logger.isDebugEnabled()) {
			Logger.debug("调用queryRequires()方法返回给个人中心-需求中的json		----> " + play.libs.Json.toJson(pageRequires));
		}
		
		return ok(play.libs.Json.toJson(pageRequires));
	}
	
	@Transactional
	public static Result deleteRequire(Long requireId) {
		if (requireId == null) {
			return ok("{\"status\":\"0\",\"error\":\"参数requireId不能为空。\"}");
		}
		User currentUser = User.getFromSession(session());
		ObjectNodeResult result = RequireService.deleteById(requireId, currentUser);
		return ok(result.getObjectNode());
	}

}
