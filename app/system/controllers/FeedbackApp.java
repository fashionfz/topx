/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-1-2
 */
package system.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import models.AttachOfFeedback;
import models.Feedback;
import models.Feedback.FeedbackStatus;
import models.Attach;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Assets;
import utils.DateUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 用户投诉
 *
 * @ClassName: FeedbackApp
 * @Description: 
 * @date 2014-1-2 上午11:03:06
 * @author YangXuelin
 * 
 */
public class FeedbackApp extends Controller {
	
	/**
	 * 投诉列表
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result list() {
		int start = NumberUtils.toInt(request().getQueryString("start"), 0);
		int pageSize = NumberUtils.toInt(request().getQueryString("limit"), 20);
		String searchText = StringUtils.defaultIfBlank(request().getQueryString("searchText"), "");
		String searchStatus = StringUtils.defaultIfBlank(request().getQueryString("searchStatus"), "-1");
		FeedbackStatus status = null;
		if(!"-1".equals(searchStatus)) {
			status = FeedbackStatus.getStatusByOrdinal(Integer.parseInt(searchStatus));
		}
		searchText = "%" + searchText + "%"; 
		String sort = request().getQueryString("sort");
		String sortProperty = " f.createTime desc";
		if (StringUtils.isNotBlank(sort)) {
            try {
                JsonNode sortJsonArray = Json.parse(sort);
                if (sortJsonArray.isArray() && null != sortJsonArray.get(0)) {
                    JsonNode sortJsonNode = sortJsonArray.get(0);
                    if (sortJsonNode.hasNonNull("property") && sortJsonNode.hasNonNull("direction")) {
                    	sortProperty = " f.createTime ";
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
		int total = Feedback.getFeedbacksCount(searchText, status);
		result.put("total", total);
		if(total == 0) {
			result.putPOJO("data", null);
			return ok(result);
		}
		List<Feedback> fbs = Feedback.getFeedbacks(start, pageSize, searchText, status, sortProperty);
		List<ObjectNode> data = null;
		if(fbs != null) {
			data = new ArrayList<ObjectNode>(fbs.size());
			for(Feedback fb : fbs) {
				ObjectNode node = Json.newObject();
				node.put("id", fb.id);
				node.put("username", fb.username);
				node.put("expertName", fb.cuserName);
				node.put("number", fb.number);
				node.put("status", fb.status.ordinal());
				Set<AttachOfFeedback> attachs = fb.attach;
				if(attachs != null && attachs.size() > 0) {
					List<ObjectNode> nodes = new ArrayList<ObjectNode>(attachs.size());
					Iterator<AttachOfFeedback> iter = attachs.iterator();
					while(iter.hasNext()) {
						Attach attach = (Attach)iter.next();
						ObjectNode n = Json.newObject();
						n.put("proofName", attach.fileName);
						n.put("proofPath", Assets.at(attach.path));
						nodes.add(n);
					}
					node.putPOJO("proofs", nodes);
				} else {
					node.putPOJO("proofs", null);
				}
				node.put("content", fb.content);
				node.put("createTime", DateUtils.format(fb.createTime));
				data.add(node);
			}
		}
		result.putPOJO("data", data);
		return ok(result);
	}
	
	/**
	 * 更新投诉处理状态
	 * @return
	 */
	@Transactional
	public static Result modifyStatus() {
		DynamicForm requestData = Form.form().bindFromRequest();
		Long id = NumberUtils.createLong(requestData.get("feedbackId"));
		Integer state = NumberUtils.createInteger(requestData.get("handleState"));
		ObjectNode result = Json.newObject();
		if(id == null || state == null) {
			result.put("success", false);
			return ok(result);
		}
		Feedback fb = Feedback.getFeedback(id);
		if(fb == null) {
			result.put("success", false);
			return ok(result);
		}
		FeedbackStatus fbs = FeedbackStatus.getStatusByOrdinal(state.intValue());
		fb.status = fbs;
		fb.merge();
		result.put("success", true);
		return ok(result);
	}

}
