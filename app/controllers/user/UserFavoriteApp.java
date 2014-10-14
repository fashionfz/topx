package controllers.user;

import org.apache.commons.lang3.StringUtils;

import models.Expert;
import models.User;
import models.service.FavoriteService;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Result;
import vo.ExpertDetail;
import vo.page.Page;
import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;

public class UserFavoriteApp extends BaseApp {
	
	/**
	 * 个人中心 - 收藏夹
	 */
	@Transactional(readOnly = true)
	public static Result view() {
		return ok(views.html.usercenter.userfavorite.render());
	}
	
	/**
	 * 添加收藏某个用户
	 * @param userId 要收藏的用户的id
	 * @return
	 */
	@play.db.jpa.Transactional
	public static Result addFavorite(Long userId) {
		if (userId == null) {
			throw new NullPointerException("参数不能为空。");
		}
		ObjectNodeResult result = new ObjectNodeResult();
		User currentUser = User.getFromSession(session());
		Expert expert = Expert.findByUserId(userId);
		if (expert == null) {
			return ok("{\"status\":\"0\",\"error\":\"收藏失败，该专家信息没有找到。\"}");
		}
		models.service.FavoriteService.addFavorite(currentUser, expert);
		return ok(result.getObjectNode());
	}
	
	/**
	 * 删除收藏某个用户
	 * @param userId 要取消收藏的用户的id
	 * @return
	 */
	@play.db.jpa.Transactional
	public static Result deleteFavorite(Long userId) {
		if (userId == null) {
			throw new NullPointerException("参数不能为空。");
		}
		ObjectNodeResult result = new ObjectNodeResult();
		User currentUser = User.getFromSession(session());
		Expert expert = Expert.findByUserId(userId);
		if (expert == null) {
			return ok("{\"status\":\"0\",\"error\":\"取消收藏失败，该专家信息没有找到。\"}");
		}
		models.service.FavoriteService.deleteFavorite(currentUser, expert);
		return ok(result.getObjectNode());
	}
	
	/**
	 * 获取个人中心 - 收藏夹的Page数据
	 */
	@play.db.jpa.Transactional
	public static Result queryFavorite(){
		DynamicForm requestData = Form.form().bindFromRequest();
		String page = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "10" : requestData.get("pageSize");
		User currentUser = User.getFromSession(session());
		Page<ExpertDetail> pageFavorite = FavoriteService.getFavoritePage(Integer.parseInt(page), Integer.parseInt(pageSize), currentUser);
		if (Logger.isDebugEnabled()) {
			Logger.debug("返回给个人中心-收藏夹界面的json 	  ----> "+play.libs.Json.toJson(pageFavorite));
		}
		return ok(play.libs.Json.toJson(pageFavorite));
	}
	
	
}
