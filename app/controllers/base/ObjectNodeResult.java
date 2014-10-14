/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package controllers.base;

import models.User;

import org.apache.commons.lang3.StringUtils;

import play.i18n.Messages;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * @author ZhouChun
 * @ClassName: ObjectNodeResult
 * @Description: ObjectNode 封装
 * @date 13-10-30 上午11:19
 */
public class ObjectNodeResult {

	private static final String REGX = "\\:";

	private static final String STATUS = "status";

	private static final String SUCCESS = "success";

	private static final String SUCCESS_CODE = "successCode";

	private static final String ERROR = "error";

	private static final String ERROR_CODE = "errorCode";

	public static final int STATUS_SUCCESS = 1;

	public static final int STATUS_FAILED = 0;

	private ObjectNode node = null;

	private User user;

	public ObjectNodeResult() {
		this(STATUS_SUCCESS);
	}

	public ObjectNodeResult(int status) {
		node = Json.newObject();
		node.put(STATUS, status);
	}

	/**
	 * @deprecated replaced by <code>errorkey(String key)</code>.
	 */
	public ObjectNodeResult error(String error) {
		node.put(STATUS, STATUS_FAILED);
		node.put(ERROR, error);
		return this;
	}
	
	/**
	 * @deprecated replaced by <code>errorkey(String key)</code>.
	 */
	public ObjectNodeResult error(String error, String errorCode) {
		node.put(STATUS, STATUS_FAILED);
		node.put(ERROR, error);
		node.put(ERROR_CODE, errorCode);
		return this;
	}

	public ObjectNodeResult successkey(String key) {
		node.put(STATUS, STATUS_SUCCESS);
		String message = Messages.get(key);
		if (StringUtils.isNotBlank(message)) {
			String[] messArr = message.split(REGX);
			if (messArr.length == 2) {
				node.put(SUCCESS_CODE, messArr[0]);
				node.put(SUCCESS, messArr[1]);
				return this;
			}
		}		
		node.put(SUCCESS_CODE, "message消息格式不正确");	
		node.put(SUCCESS, "555");
		return this;
	}

	public ObjectNodeResult successkey(String key, Object... params) {
		node.put(STATUS, STATUS_SUCCESS);
		String message = Messages.get(key, params);
		if (StringUtils.isNotBlank(message)) {
			String[] messArr = message.split(REGX);
			if (messArr.length == 2) {
				node.put(SUCCESS_CODE, messArr[0]);
				node.put(SUCCESS, messArr[1]);
				return this;
			}
		}		
		node.put(SUCCESS_CODE, "message消息格式不正确");	
		node.put(SUCCESS, "555");
		return this;
	}

	/**
	 * 根据国际化配置key和message
	 * 
	 * @param key
	 *            国际化文件的KEY
	 * @return Json使用对象
	 */
	public ObjectNodeResult errorkey(String key) {
		node.put(STATUS, STATUS_FAILED);
		String message = Messages.get(key);
		if (StringUtils.isNotBlank(message)) {
			String[] messArr = message.split(REGX);
			if (messArr.length == 2) {
				node.put(ERROR_CODE, messArr[0]);
				node.put(ERROR, messArr[1]);
				return this;
			}
		}
		node.put(ERROR, "message消息格式不正确");	
		node.put(ERROR_CODE, "555");
		return this;
	}

	public ObjectNodeResult errorkey(String key, Object... params) {
		node.put(STATUS, STATUS_FAILED);
		String message = Messages.get(key, params);
		if (StringUtils.isNotBlank(message)) {
			String[] messArr = message.split(REGX);
			if (messArr.length == 2) {
				node.put(ERROR_CODE, messArr[0]);
				node.put(ERROR, messArr[1]);
				return this;
			}
		}
		node.put(ERROR, "message消息格式不正确");	
		node.put(ERROR_CODE, "555");
		return this;
	}

	public String getError() {
		return node.get(ERROR).asText();
	}
	
	public String getErrorCode() {
	    return node.get(ERROR_CODE).asText();
	}

	public ObjectNode getObjectNode() {
		return node;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		ObjectNode userNode = Json.newObject();
		userNode.put("id", user.getId());
		userNode.put("name", user.getName());
		userNode.put("avatar_190", user.getAvatar(190));
		userNode.put("avatar_70", user.getAvatar(70));
		userNode.put("avatar_22", user.getAvatar(22));
		node.put("user", userNode);
	}

	public boolean isSuccess() {
		return STATUS_SUCCESS == node.get(STATUS).asInt();
	}

	/**
	 * 设置json字段
	 * 
	 * @param fieldName
	 *            字段名
	 * @param v
	 *            值
	 */
	public ObjectNodeResult put(String fieldName, String v) {
		this.node.put(fieldName, v);
		return this;
	}

	public ObjectNodeResult put(String fieldName, JsonNode value) {
		this.node.put(fieldName, value);
		return this;
	}

	public ObjectNodeResult put(String fieldName, Object o) {
		this.node.putPOJO(fieldName, o);
		return this;
	}

	/**
	 * 设置json字段
	 * 
	 * @param fieldName
	 *            字段名
	 * @param v
	 *            值
	 */
	public ObjectNodeResult put(String fieldName, boolean v) {
		this.node.put(fieldName, v);
		return this;
	}

	/**
	 * Method for adding all properties of the given Object, overriding any
	 * existing values for those properties.
	 * 
	 * @param other
	 *            Object of which properties to add to this object
	 */
	public ObjectNodeResult setAll(ObjectNode other) {
		this.node.setAll(other);
		return this;
	}

	@Override
	public String toString() {
		return "ObjectNodeResult [node=" + node + ", user=" + user + "]";
	}

}
