/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-1-6
 */
package controllers.feedback;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import models.Attach;
import models.AttachOfSuggestion;
import models.Suggestion;


import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;

/**
 *
 *
 * @ClassName: FeedbackApp
 * @Description: 用户反馈
 * @date 2014-1-6 下午4:45:39
 * @author YangXuelin
 * 
 */
public class FeedbackApp extends BaseApp {
	
	/**
	 * 用户建议
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	@Transactional
	public static Result addSuggestion() {
		ObjectNodeResult result = new ObjectNodeResult();
		Suggestion suggestion = new Suggestion();
		JsonNode json = getJson();
		String content = json.findPath("content").asText();
		String userName = json.findPath("userName").asText();
		String email = json.findPath("email").asText();
		String href = json.findPath("href").asText();
		String qq = json.findPath("qq").asText();
		String phone = json.findPath("phone").asText();
		Iterator<JsonNode> iter = json.findPath("attachs").elements();
		while(iter.hasNext()){
			JsonNode jn = iter.next();
			Long attachId = jn.findValue("attachId").asLong();
			AttachOfSuggestion attach = (AttachOfSuggestion)Attach.queryById(attachId, AttachOfSuggestion.class);
			if (attach != null) {
				suggestion.attach.add(attach);
			}
		}
		if (StringUtils.isNotBlank(content)) {
			content = common.SensitiveWordsFilter.doFilter(content);
			content = common.ReplaceWordsFilter.doFilter(content);
		}
		if (StringUtils.isNotBlank(userName)) {
			userName = common.SensitiveWordsFilter.doFilter(userName);
			userName = common.ReplaceWordsFilter.doFilter(userName);
		}
		if (StringUtils.isNotBlank(href)) {
			href = common.SensitiveWordsFilter.doFilter(href);
			href = common.ReplaceWordsFilter.doFilter(href);
		}
		if (StringUtils.isNotBlank(email)) {
			email = common.SensitiveWordsFilter.doFilter(email);
			email = common.ReplaceWordsFilter.doFilter(email);
		}
		if (StringUtils.isNotBlank(qq)) {
			qq = common.SensitiveWordsFilter.doFilter(qq);
			qq = common.ReplaceWordsFilter.doFilter(qq);
		}
		if (StringUtils.isNotBlank(phone)) {
			phone = common.SensitiveWordsFilter.doFilter(phone);
			phone = common.ReplaceWordsFilter.doFilter(phone);
		}

		suggestion.content = content;
		suggestion.userName = userName;
		suggestion.href = href;
		suggestion.email = email;
		suggestion.qq = qq;
		suggestion.phone = phone;
		suggestion.persist();
		result.successkey("suggestion.success");
		return ok(result.getObjectNode());
	}
	
	

}
