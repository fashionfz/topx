/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-1-2
 */
package system.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Attach;
import models.AttachOfSuggestion;
import models.Suggestion;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DateUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 用户建议
 *
 * @ClassName: SuggestionApp
 * @Description: 
 * @date 2014-1-2 上午10:56:53
 * @author RenYouchao
 * 
 */
public class SuggestionApp extends Controller {
	
	@Transactional
	public static Result add() {
		
		return null;
	}
	
	
	@Transactional(readOnly = true)
	public static Result list() {
		int start = NumberUtils.toInt(request().getQueryString("start"), 0);
		int pageSize = NumberUtils.toInt(request().getQueryString("limit"), 20);
		String searchText = StringUtils.defaultIfBlank(request().getQueryString("searchText"), "");
		searchText = "%" + searchText + "%"; 
		String sort = request().getQueryString("sort");
		String sortProperty = " s.createTime desc";
		if (StringUtils.isNotBlank(sort)) {
            try {
                JsonNode sortJsonArray = Json.parse(sort);
                if (sortJsonArray.isArray() && null != sortJsonArray.get(0)) {
                    JsonNode sortJsonNode = sortJsonArray.get(0);
                    if (sortJsonNode.hasNonNull("property") && sortJsonNode.hasNonNull("direction")) {
                    	sortProperty = " s.createTime ";
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
		ObjectNode result = Json.newObject();
		int total = Suggestion.getSuggestionsCount(searchText);
		result.put("total", total);
		if(total == 0) {
			result.putPOJO("data", null);
			return ok(result);
		}
		List<Suggestion> suggestions = Suggestion.getSuggestions(start, pageSize, sortProperty, searchText);
		List<ObjectNode> data = null;
		if (suggestions != null) {
			data = new ArrayList<ObjectNode>(suggestions.size());
			for (Suggestion suggestion : suggestions) {
				ObjectNode node = Json.newObject();
				node.put("id", suggestion.id);
				node.put("userName", suggestion.userName);
				node.put("phone", suggestion.phone);
				node.put("email", suggestion.email);
				node.put("qq", suggestion.qq);
				node.put("content", suggestion.content);
				node.put("createTime", DateUtils.format(suggestion.createTime));
				node.put("href", suggestion.href);

				//Set<AttachOfSuggestion> attachSet = suggestion.attach;
				List<AttachOfSuggestion> attachList = new AttachOfSuggestion().queryByAttachId(suggestion.id);
				Set<AttachOfSuggestion> attachSet = new HashSet<AttachOfSuggestion>(attachList);
				List<ObjectNode> nodes = new ArrayList<ObjectNode>(attachSet == null ? 0 : attachSet.size());
				if (CollectionUtils.isNotEmpty(attachSet)) {
					for (Attach item : attachSet) {
						ObjectNode attachON = Json.newObject();
						attachON.put("attachFileName", StringUtils.isEmpty(item.fileName) ? "-" : item.fileName);
						attachON.put("attachPath", item.path);
						nodes.add(attachON);
					}
				}
				node.putPOJO("attachInfos", nodes);
				data.add(node);
			}
		}
		result.putPOJO("data", data);
		return ok(result);
	}

}
