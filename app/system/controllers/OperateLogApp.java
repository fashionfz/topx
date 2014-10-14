/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-1-13
 */
package system.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import system.models.OperateLog;
import utils.DateUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 操作日志APP
 *
 * @ClassName: OperateLogApp
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-1-13 下午2:33:47
 * @author YangXuelin
 * 
 */
public class OperateLogApp extends Controller {
	
	@Transactional(readOnly = true)
	public static Result list() {
		ObjectNode result = Json.newObject();
		int start = NumberUtils.toInt(request().getQueryString("start"), 0);
		int pageSize = NumberUtils.toInt(request().getQueryString("limit"), 20);
		String searchText = StringUtils.defaultIfBlank(request().getQueryString("searchText"), "");
		String searchModule = StringUtils.defaultIfBlank(request().getQueryString("searchModule"), "-1");
		String optResult = StringUtils.defaultIfBlank(request().getQueryString("result"), "");
		searchText = "%" + searchText + "%"; 
		String sort = request().getQueryString("sort");
		String sortProperty = " ol.operateTime desc";
		if (StringUtils.isNotBlank(sort)) {
            try {
                JsonNode sortJsonArray = Json.parse(sort);
                if (sortJsonArray.isArray() && null != sortJsonArray.get(0)) {
                    JsonNode sortJsonNode = sortJsonArray.get(0);
                    if (sortJsonNode.hasNonNull("property") && sortJsonNode.hasNonNull("direction")) {
                    	sortProperty = " ol.operateTime ";
                        boolean isDesc = "DESC".equals(sortJsonNode.get("direction").asText());
                        if(isDesc) {
                        	sortProperty += " desc";
                        }
                    }
                }
            } catch (RuntimeException e) {
                play.Logger.error("failed to parse JSON. JSON: " + sort);
            }
        }
		int total = OperateLog.getOperateLogCount(searchText,optResult);
		result.put("total", total);
		if(total == 0) {
			result.putPOJO("data", null);
			return ok(result);
		}
		List<OperateLog> ols = OperateLog.getOperateLogs(start, pageSize, searchText,optResult, sortProperty);
		List<ObjectNode> data = null;
		if(ols != null) {
			data = new ArrayList<ObjectNode>(ols.size());
			for(OperateLog ol : ols) {
				ObjectNode node = Json.newObject();
				node.put("id", ol.id);
				node.put("operateTime", DateUtils.format(ol.operateTime));
				node.put("operator", ol.operator);
				node.put("operateIp", ol.operateIp);
				node.put("menuName", ol.menuName);
				node.put("paramters", ol.paramters);
				node.put("result", ol.result);
				node.put("describle", ol.describle);
				data.add(node);
			}
		}
		result.putPOJO("data", data);
		return ok(result);
	}
	
	/**
	 * 获得操作模块
	 * @return
	 */
	public static Result operateModules() {
//		ObjectNode result = Json.newObject();
//		List<ObjectNode> modules = new ArrayList<ObjectNode>();
//		ObjectNode head = Json.newObject();
//		head.put("name", "全部模块");
//		head.put("value", -1);
//		modules.add(head);
//		for(ModuleEnum module : ModuleEnum.values()) {
//			ObjectNode node = Json.newObject();
//			node.put("name", module.getName());
//			node.put("value", module.ordinal());
//			modules.add(node);
//		}
//		result.putPOJO("data", modules);
//		return ok(result);
		return ok();
	}

}
