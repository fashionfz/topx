/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月22日
 */
package controllers.group;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import models.Group;
import models.User;
import models.Group.GroupPriv;
import models.SkillTag;
import models.service.ChatService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang3.StringUtils;

import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import utils.HelomeUtil;
import utils.SemUtils;
import vo.GroupDetail;
import vo.GroupListVO;
import vo.GroupMemberVO;
import vo.GroupVO;
import vo.expertpage.GPage;
import vo.page.Page;
import common.Constants;
import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;
import ext.search.GTransformer;
import ext.search.SearchHttpClient;

/**
 * 
 * 
 * @ClassName: GroupApp
 * @Description: 群组控制器
 * @date 2013年10月22日 下午3:59:36
 * @author RenYouchao
 * 
 */
public class GroupApp extends BaseApp {

    /**
     * 首页页面
     */
    @Transactional
    public static Result group(String industryIdStr, String privacyStr) {
        GroupPriv groupPriv = GroupPriv.getByName(privacyStr);
        Long industryId = HelomeUtil.toLong(industryIdStr, null);
        List<SkillTag> tags = SkillTag.listCategories(100);

        if (null == industryId && CollectionUtils.isNotEmpty(tags)) {
            industryId = tags.get(0).id;
        }

        Page<GroupVO> groupPage = ChatService.queryGroupByPage(true, 1, 10, industryId, groupPriv, null, null, null);

        return ok(views.html.group.grouphome.render(tags, industryId, groupPriv, groupPage));
    }

    /**
     * 首页列表
     */
    @Transactional
    public static Result groupList(Integer pageIndex, String industryIdStr, String privacyStr) {
        GroupPriv groupPriv = GroupPriv.getByName(privacyStr);
        Long industryId = HelomeUtil.toLong(industryIdStr, null);
        List<SkillTag> tags = SkillTag.listCategories(100);

        if (pageIndex < 1) {
            pageIndex = 1;
        }
        if (null == industryId && CollectionUtils.isNotEmpty(tags)) {
            industryId = tags.get(0).id;
        }

        Page<GroupVO> groupPage = ChatService.queryGroupByPage(true, pageIndex, 10, industryId, groupPriv, null, null, null);

        ObjectNodeResult result = new ObjectNodeResult();
        result.put("list", Json.toJson(groupPage.getList()));

        return ok(result.getObjectNode());
    }

    /**
     * 获取群组成员列表
     */
    @Transactional
    public static Result groupMemberList(Integer pageIndex, Long groupId) {
        if (pageIndex < 1) {
            pageIndex = 1;
        }

        List<GroupMemberVO> list = new ArrayList<GroupMemberVO>();

        Group group = Group.queryGroupById(groupId);
        if (null != group && null != group.getOwner()) {
            list = ChatService.queryGroupMemberByPage(pageIndex, 10, groupId, group.getOwner().getId()).getList();
        }

        ObjectNodeResult result = new ObjectNodeResult();
        result.put("list", Json.toJson(list));

        return ok(result.getObjectNode());
    }

    /**
     * 群组详情页面
     */
    @Transactional
    public static Result groupDetail(Long groupId) {
        GroupDetail groupDetail = ChatService.queryGroupDetailById(groupId, 10, true);
        if (null == groupDetail) {
            return ok(views.html.common.error.render("该群不存在或已解散！","","群组提醒"));
        } else {
            return ok(views.html.group.groupdetail.render(groupDetail));
        }
    }

    /**
     * 群组搜索结果
     * @throws ScriptException 
     */
    @Transactional(readOnly = true)
    public static Result searchResult() throws ScriptException {
		DynamicForm requestData = Form.form().bindFromRequest();
    	User me = User.getFromSession(session());
		String p = requestData.get("p") == null ? "1|1" : requestData.get("p");
		String type = requestData.get("type") == null ? "html" : requestData.get("type");
		String ft = requestData.get("ft") == null ? "" : requestData.get("ft").trim();
		String inf = requestData.get("inf");
		String gpf = requestData.get("gpf");
		String o = requestData.get("o");
		String ot = requestData.get("ot");
		ScriptEngine engine = SemUtils.getEngine();
		if (StringUtils.isNotBlank(ft))
			ft = engine.eval("decodeURIComponent('" + ft + "')").toString();
		GTransformer gtrf = new GTransformer(ft, p, inf, gpf, o, ot);
		String resultJson = SearchHttpClient.advancedQuery(gtrf.tranAdSearchNVP(Constants.HOME_EXPERT_PAGE_SIZE));
		GPage<GroupListVO> gPage = null;
		if(StringUtils.isNotBlank(resultJson))
			gPage = gtrf.pageFromJson(resultJson,Constants.HOME_EXPERT_PAGE_SIZE,me);
		else
			gPage = new GPage(null, 0L, 1, 1);

		gPage.setGpf(gpf);
		gPage.setInf(inf);
		gPage.setFt(ft);
		gPage.setO(o);
		gPage.setOt(ot);

		ListOrderedMap cts = SkillTag.getCacheCategory();
		if(StringUtils.isNotBlank(inf))
			gPage.setInfStr((String)cts.get(new Long(inf)));
		else
			gPage.setInfStr("分类");
		
		if (!type.equals("json")) {
			return ok(views.html.group.searchresult.render(gPage,cts));
		} else {
			return ok(play.libs.Json.toJson(gPage));
		}
    }
}
