package system.controllers;

import java.io.UnsupportedEncodingException;

import models.OverseasResume;
import models.User;
import models.service.OverseasResumeService;
import models.service.OverseasResumeService.ResumeGridSearchCondition;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import controllers.base.ObjectNodeResult;
import play.Logger;
import play.Logger.ALogger;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import system.vo.Page;
import system.vo.ResumeGridVO;

/**
 * @ClassName: ResumeApp
 * @Description: 海外简历管理App
 * @date 2014-07-25 上午11:40:23
 * 
 */
public class ResumeApp extends Controller {
	private static final ALogger LOGGER = Logger.of(ResumeApp.class);

	@Transactional(readOnly = true)
	public static Result list(){
		Integer start = NumberUtils.toInt(request().getQueryString("start"), 0);
		String searchText = StringUtils.defaultIfBlank(request().getQueryString("searchText"), null);
		String statusStr = StringUtils.defaultIfBlank(request().getQueryString("status"), "");
		
		ResumeGridSearchCondition c = new ResumeGridSearchCondition();
		c.setStart(start);
		c.setLimit(20);
		c.setSearchText(searchText);
		c.setIsDesc(true);
		if (StringUtils.isNotBlank(statusStr)) {
			c.setStatus(models.OverseasResume.Status.getByOrdinal(Integer.valueOf(statusStr)));
		}
		
		Page<ResumeGridVO> page = OverseasResumeService.queryResumeGridPage(c);
		
		return ok(page.toJson());
		
	}
	
	/**
	 * 处理完成
	 * @return
	 */
	@Transactional
	public static Result toCompleted(){
		String id = request().getQueryString("id");
		OverseasResume.Status status = OverseasResume.Status.TRANSLATED;
		ObjectNodeResult result = new ObjectNodeResult();
		try {
			result = OverseasResumeService.changResumeStatus(Long.parseLong(id), status);
		} catch (NumberFormatException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error(e.getMessage()+"简历id："+id);
			}
		} catch (Exception e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("海外简历修改状态为已翻译出错。", e);
			}
			result.error(e.toString(), "-100019");
		}
		return ok(result.getObjectNode());
	}
	
	/**
	 * 作废
	 * @return
	 */
	@Transactional
	public static Result toInvalid(){
		String id = request().getQueryString("id");
		OverseasResume.Status status = OverseasResume.Status.INVALIDATE;
		ObjectNodeResult result = new ObjectNodeResult();
		try {
			result = OverseasResumeService.changResumeStatus(Long.parseLong(id), status);
		} catch (NumberFormatException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error(e.getMessage()+"简历id："+id);
			}
		} catch (Exception e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("海外简历修改状态为已作废出错。", e);
			}
			result.error(e.toString(), "-100019");
		}
		return ok(result.getObjectNode());
	}
	
	/**
	 * 发布海外简历
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@Transactional
	public static Result publishTask() throws UnsupportedEncodingException {
		String userId = request().getQueryString("userId");
		User user = User.findById(Long.parseLong(userId));
		if (user == null) {
			return ok("{\"status\":\"0\",\"error\":\"该用户已不存在。\"}");
		}
		ObjectNodeResult result = OverseasResumeService.addTaskForChinese(user, "管理员通过管理平台执行简历发布任务。");
		return ok(result.getObjectNode());
	}
	
}
