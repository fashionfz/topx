package controllers.user;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import models.Service;
import models.SkillTag;
import models.User;
import models.service.ServicesService;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Result;
import vo.ServiceDetailVO;
import vo.page.Page;
import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;

/**
 * @ClassName: UserServiceApp
 * @Description: 服务controller
 * @date 2014-08-15 下午17:46:56
 */
public class UserServiceApp extends BaseApp {

	/**
	 * 服务 list
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result list() {
		return ok(views.html.usercenter.service.list.render());
	}

	/**
	 * 添加修改 服务
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result write() {
		DynamicForm requestData = Form.form().bindFromRequest();
        String serviceId = requestData.get("sid");
        
		List<SkillTag> tags = SkillTag.listCategories(100);
    	for (SkillTag skillTag : tags) {
			skillTag.industry = null;
		}
    	
    	ServiceDetailVO serviceVO = new ServiceDetailVO();
		if (serviceId != null) {
			Service service = Service.queryServiceById(Long.parseLong(serviceId));
			if (service != null) {
				serviceVO.convert(service);
			}
		}
    	
		return ok(views.html.usercenter.service.writeInfo.render(tags,serviceVO));
	}

	/**
	 * 创建或修改服务
	 */
	@Transactional
	public static Result createOrUpdateService() {
		JsonNode json = getJson();
		User currentUser = User.getFromSession(session());
		ObjectNodeResult result = ServicesService.createOrUpdateService(currentUser, json);
		return ok(result.getObjectNode());
	}

	/**
	 * 查询服务
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result queryServices() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String pageStr = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "10" : requestData.get("pageSize");
		User currentUser = User.getFromSession(session());

		Page<ServiceDetailVO> pageServices = null;
		String searchText = requestData.get("searchText");
		Integer page = Integer.parseInt(pageStr);
		if (page != null && page < 0) {
			page = 0;
		}
		if (StringUtils.isNotBlank(searchText)) {
			pageServices = ServicesService.getServicePage(page, Integer.parseInt(pageSize), currentUser, searchText.trim());
		} else {
			pageServices = ServicesService.getServicePage(page,	Integer.parseInt(pageSize), currentUser, "");
		}

		if (Logger.isDebugEnabled()) {
			Logger.debug("调用queryService()方法返回给个人中心-服务中的json		----> " + play.libs.Json.toJson(pageServices));
		}

		return ok(play.libs.Json.toJson(pageServices));
	}

	/**
	 * 
	 * @param serviceId
	 * @return
	 */
	@Transactional
	public static Result deleteService(Long serviceId) {
		if (serviceId == null) {
			return ok("{\"status\":\"0\",\"error\":\"参数serviceId不能为空。\"}");
		}
		User currentUser = User.getFromSession(session());
		ObjectNodeResult result = ServicesService.deleteById(serviceId, currentUser);
		return ok(result.getObjectNode());
	}

}
