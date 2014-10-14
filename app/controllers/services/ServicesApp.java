/*
 * Copyright (c) 2014, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年08月13日
 */
package controllers.services;

import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import models.Service;
import models.ServiceComment;
import models.SkillTag;
import models.User;
import models.service.ServicesService;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Result;
import utils.SemUtils;
import vo.ServiceDetailVO;
import vo.ServiceInfoVO;
import vo.ServiceListVO;
import vo.expertpage.SPage;
import vo.page.Page;

import common.Constants;

import controllers.base.BaseApp;
import ext.search.STransformer;
import ext.search.SearchHttpClient;

/**
 *
 *
 * @ClassName: ServicesApp
 * @Description: 服务控制器
 * @date 2014年08月13日 下午5:42:31
 * @author
 *
 */
public class ServicesApp extends BaseApp {
    /**
     * 服务首页
     */
	@Transactional(readOnly = true)
    public static Result index() {
    	DynamicForm requestData = Form.form().bindFromRequest();
		ListOrderedMap cts = SkillTag.getCacheCategory();
		Integer page = StringUtils.isBlank(requestData.get("page")) ? 
				1 : new Integer(requestData.get("page"));
		Integer pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? 
				10 : new Integer(requestData.get("pageSize"));
		Long categoryId = StringUtils.isBlank(requestData.get("categoryId")) ? 
				(Long)cts.asList().get(0) : new Long(requestData.get("categoryId"));
		String type = requestData.get("type") == null ? "html" : requestData.get("type");
		Page<ServiceListVO> returnPage = Service.queryServiceByPage(page, pageSize, categoryId);
		if (!type.equals("json")) {
			return ok(views.html.services.index.render(returnPage,cts,categoryId));
		}
		return ok(play.libs.Json.toJson(returnPage));
    }
    /**
     * 详情页面
     */
    @Transactional(readOnly = true)
    public static Result detail(Long id) {
    	ServiceInfoVO serviceInfoVO = ServicesService.queryServiceById(id);
    	User user = User.getFromSession(session());
    	if (user != null && serviceInfoVO.getServiceOwner() != null && serviceInfoVO.getServiceOwner().getUserId() - user.id == 0){
    		serviceInfoVO.getServiceOwner().setIsSelf(true);
    	}
		ServiceComment comment = new ServiceComment();
		Map<Integer, Long> totals = comment.getTotalRecordByLevel(serviceInfoVO.getServiceOwner().getUserId(), serviceInfoVO.getServiceDetailVO().getId());
        return ok(views.html.services.detail.render(serviceInfoVO,totals));
    }
    
    /**
     * Ta的服务
     */
    @Transactional(readOnly = true)
	public static Result servicesOfTa() {
    	DynamicForm requestData = Form.form().bindFromRequest();
		String pageStr = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "9" : requestData.get("pageSize");
		String userIdStr = requestData.get("userId");
		User user = User.findById(Long.parseLong(userIdStr));
		Page<ServiceDetailVO> serviceDetailVOPage = ServicesService.getServicVOListPage(Integer.parseInt(pageStr), Integer.parseInt(pageSize), user);
		if (Logger.isDebugEnabled()) {
			Logger.debug("返回给Ta的服务的json		----> " + play.libs.Json.toJson(serviceDetailVOPage));
		}
		return ok(play.libs.Json.toJson(serviceDetailVOPage));
    }
    
    
    
    /**
     * 详情页面
     * @throws ScriptException 
     */
    @Transactional(readOnly = true)
    public static Result search() throws ScriptException {
		DynamicForm requestData = Form.form().bindFromRequest();
    	User me = User.getFromSession(session());
		String p = requestData.get("p") == null ? "1|1" : requestData.get("p");
		String type = requestData.get("type") == null ? "html" : requestData.get("type");
		String ft = requestData.get("ft") == null ? "" : requestData.get("ft").trim();
		String inf = requestData.get("inf");
		String cf = requestData.get("cf");
		String gf = requestData.get("gf");
		String o = requestData.get("o");
		String ot = requestData.get("ot");
		ScriptEngine engine = SemUtils.getEngine();
		if (StringUtils.isNotBlank(ft))
			ft = engine.eval("decodeURIComponent('" + ft + "')").toString();
		STransformer gtrf = new STransformer(ft, p, inf, cf, gf, o, ot);
		String resultJson = SearchHttpClient.advancedQuery(gtrf.tranAdSearchNVP(Constants.HOME_EXPERT_PAGE_SIZE));
		SPage<ServiceListVO> sPage = null;
		if(StringUtils.isNotBlank(resultJson))
			sPage = gtrf.pageFromJson(resultJson,Constants.HOME_EXPERT_PAGE_SIZE,me);
		else
			sPage = new SPage(null, 0L, 1, 1);

		sPage.setGf(gf);
		sPage.setCf(cf);
		sPage.setInf(inf);
		sPage.setFt(ft);
		sPage.setO(o);
		sPage.setOt(ot);
		
		List<String> countryList = SkillTag.getCountryNameWithCache();
		sPage.setCountryList(countryList);

		ListOrderedMap cts = SkillTag.getCacheCategory();
		if(StringUtils.isNotBlank(inf))
			sPage.setInfStr((String)cts.get(new Long(inf)));
		else
			sPage.setInfStr("行业分类");
		
		if(StringUtils.isNotBlank(cf))
			sPage.setCfStr(cf);
		else
			sPage.setCfStr("国家地区");
		
		if (!type.equals("json")) {
			return ok(views.html.services.search.render(sPage,cts));
		}
		sPage.setCountryList(null);
		return ok(play.libs.Json.toJson(sPage));
    }
}
