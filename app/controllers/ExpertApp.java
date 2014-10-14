/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月22日
 */
package controllers;

import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import models.Comment;
import models.Expert;
import models.ExpertComment;
import models.Friends;
import models.SkillTag;
import models.User;
import models.service.RequireService;
import models.service.ServicesService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import play.cache.Cache;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import system.models.Keyword;
import utils.Assets;
import utils.SemUtils;
import vo.ExpertDetail;
import vo.ExpertListVO;
import vo.RequireDetailVO;
import vo.ServiceDetailVO;
import vo.expertpage.EPage;
import vo.page.Page;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.Constants;

import ext.search.SearchHttpClient;
import ext.search.Transformer;

/**
 * 
 * 
 * @ClassName: ExpertApp
 * @Description: 专家控制器
 * @date 2013年10月22日 下午3:59:36
 * @author RenYouchao
 * 
 */
public class ExpertApp extends Controller {

	@Transactional(readOnly = true)
	public static Result search() {
		User user = User.getFromSession(session());
		DynamicForm requestData = Form.form().bindFromRequest();
		String p = requestData.get("p") == null ? "1|1" : requestData.get("p");
		String type = requestData.get("type") == null ? "html" : requestData.get("type");
		String ft = requestData.get("ft") == null ? "" : requestData.get("ft").trim();
		String cf = requestData.get("cf");
		String ssf = requestData.get("ssf");
		String ef = requestData.get("ef");
		String gf = requestData.get("gf");
		String o = requestData.get("o");
		String ot = requestData.get("ot");
		ScriptEngine engine = SemUtils.getEngine();
		try {
			if (StringUtils.isNotBlank(ft))
				ft = engine.eval("decodeURIComponent('" + ft + "')").toString();
			if (StringUtils.isNotBlank(cf))
				cf = engine.eval("decodeURIComponent('" + cf + "')").toString();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		Transformer trf = new Transformer(ft, p, cf, ssf, ef, gf, o, ot);
		String resultJson = SearchHttpClient.advancedQuery(trf.tranAdSearchNVP(Constants.HOME_EXPERT_PAGE_SIZE));
		EPage<ExpertListVO> pageObj = null;
		if(StringUtils.isNotBlank(resultJson))
			pageObj = trf.pageFromJson(resultJson, user, Constants.HOME_EXPERT_PAGE_SIZE);
		else
			pageObj = new EPage(null, 0L, 1, 1);

		pageObj.setCf(cf);
		pageObj.setEf(ef);
		pageObj.setFt(ft);
		pageObj.setGf(gf);
		pageObj.setO(o);
		pageObj.setOt(ot);
		pageObj.setSsf(ssf);

		List<String> countryList = SkillTag.getCountryNameWithCache();
		pageObj.setCountryList(countryList);
		
		if (!type.equals("json")) {
			return ok(views.html.expert.search.render(pageObj));
		} else {
			return ok(play.libs.Json.toJson(pageObj));
		}
	}

	public static Result searchTag() {
		String st = request().getQueryString("st");
		String p = StringUtils.defaultIfBlank(request().getQueryString("p"), "1|1");
		String type = StringUtils.defaultIfBlank(request().getQueryString("type"), "html");
		ScriptEngine engine = SemUtils.getEngine();
		try {
			if (StringUtils.isNotBlank(st))
				st = engine.eval("decodeURIComponent('" + st + "')").toString();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		Transformer trf = new Transformer(st, p, null, null, null, null, null, null);
		String resultJson = SearchHttpClient.advancedQuery(trf.tranMustTagNVP());
		EPage<ExpertListVO> pageObj = trf.pageFromJson(resultJson, null, Constants.HOME_EXPERT_PAGE_SIZE);
		pageObj.setFt(st);
		if (!type.equals("json")) {
			return ok(views.html.expert.customerservice.render(pageObj));
		} else {
			return ok(play.libs.Json.toJson(pageObj));
		}
	}

	@Transactional(readOnly = true)
	public static Result reserve(Long id) {
		Expert expert = Expert.getExpertById(id);
		ExpertDetail expertDetail = new ExpertDetail();
		expertDetail.convert(expert);	
		return ok(views.html.expert.reserve.render(expertDetail));
	}

	@Transactional(readOnly = true)
	public static Result detail(Long userId) {
		User user = User.getFromSession(session());
		ExpertDetail expertDetail = new ExpertDetail();
		Expert expert = Expert.getExpertByUserId(userId);
		if (null == expert) {
		    return ok(views.html.common.pagenotfound.render());
		}
		if (user != null && expert.userId - user.id == 0)
			expertDetail.setIsSelf(true);
		expertDetail.convert(expert);
		
		if (user != null && expert != null) {
			Boolean isFriend = Friends.isFriend(user.getId(), expert.userId);
			expertDetail.setIsFriend(isFriend);
		}
		ExpertComment comment = new ExpertComment();
		Map<Integer, Long> totals = comment.getTotalRecordByLevel(expert.userId);
		return ok(views.html.expert.detail.render(expertDetail, totals));
	}
	
	@Transactional(readOnly = true)
	public static Result all() {
		Expert.transInputAllexperts();
		return TODO;
	}

	@Transactional(readOnly = true)
	public static Result listkw() {
		String[] words = queryKeywords();
		return ok(play.libs.Json.toJson(words));
	}

	public static String[] queryKeywords() {
		String[] words = (String[]) Cache.get(Constants.CACHE_KEYWORDS);
		if (words == null) {
			List<Keyword> ks = Keyword.queryKeywordList();
			if (CollectionUtils.isNotEmpty(ks)) {
				words = ks.get(0).words.split("\\s+|,");
			}
			Cache.set(Constants.CACHE_KEYWORDS, words);
		}
		return words;
	}

	@SuppressWarnings("deprecation")
	@Transactional(readOnly = true)
	public static Result getExpertInfo(Long expertUserId) {
		Expert expert = Expert.getExpertByUserId(expertUserId);
		ObjectNode node = Json.newObject();
		String countSql = "select count(m) from Comment m where toCommentUser.id = :id and level >= :commentLevel ";
		Long total = (Long) JPA.em().createQuery(countSql).setParameter("id", expert.userId).setParameter("commentLevel", 3)
				.getSingleResult();
		String headUrl = "";
		if (StringUtils.isBlank(expert.headUrl)) {
			headUrl = Assets.getDefaultAvatar();
		} else {
			headUrl = Assets.at(expert.headUrl);
		}
		JsonNode skillNode = null;
		if (StringUtils.isNotBlank(expert.skillsTags))
			skillNode = Json.parse(expert.skillsTags);
		else
			skillNode = Json.parse("[]");
		node.put("skillTagsArray", skillNode);
		node.put("status", "200");
		node.put("sex", expert.user.getGenderWithDefault().toString());
		node.put("userName", expert.userName);
		node.put("skillTags", expert.skillsTags);
		node.put("country", expert.country);
		node.put("countryImgUrl", Constants.countryPicKV.get(expert.country));
		node.put("headImgUrl", headUrl);
		node.put("linkUrl",routes.ExpertApp.detail(expertUserId).url());
		node.put("job", expert.job);
		node.put("personalInfo", expert.personalInfo);
		node.put("goodCommentNum", total);
		return ok(node);
	}

	@Transactional(readOnly = true)
	public static Result service(Long userId) {
		User user = User.findById(userId);
		User currentUser = User.getFromSession(session());
		ExpertDetail expertDetail = new ExpertDetail();
		Expert expert = Expert.getExpertByUserId(userId);
		if (null == expert) {
		    return ok(views.html.common.pagenotfound.render());
		}
		if (currentUser != null && expert.userId - currentUser.id == 0)
			expertDetail.setIsSelf(true);
		expertDetail.convert(expert);
		
		if (currentUser != null && expert != null) {
			// 设置是否是圈中好友
			Boolean isFriend = Friends.isFriend(currentUser.getId(), expert.userId);
			expertDetail.setIsFriend(isFriend);
		}
		
		DynamicForm requestData = Form.form().bindFromRequest();
		String pageStr = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "9" : requestData.get("pageSize");
		Page<ServiceDetailVO> serviceDetailVOPage = ServicesService.getServicVOListPage(Integer.parseInt(pageStr), Integer.parseInt(pageSize), user);
		return ok(views.html.expert.serviceDetail.render(expertDetail,serviceDetailVOPage.getList()));
	}

	@Transactional(readOnly = true)
	public static Result require(Long userId) {
		User user = User.findById(userId);
		User currentUser = User.getFromSession(session());
		ExpertDetail expertDetail = new ExpertDetail();
		Expert expert = Expert.getExpertByUserId(userId);
		if (null == expert) {
		    return ok(views.html.common.pagenotfound.render());
		}
		if (currentUser != null && expert.userId - currentUser.id == 0)
			expertDetail.setIsSelf(true);
		expertDetail.convert(expert);
		
		if (currentUser != null && expert != null) {
			// 设置是否是圈中好友
			Boolean isFriend = Friends.isFriend(currentUser.getId(), expert.userId);
			expertDetail.setIsFriend(isFriend);
		}
		
		DynamicForm requestData = Form.form().bindFromRequest();
		String pageStr = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "10" : requestData.get("pageSize");
		Page<RequireDetailVO> requireDetailVOPage = RequireService.getRequirePage(Integer.parseInt(pageStr), Integer.parseInt(pageSize), user);
		return ok(views.html.expert.requireDetail.render(expertDetail,requireDetailVOPage.getList()));
	}
}
