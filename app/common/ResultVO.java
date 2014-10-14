/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年3月24日
 */
package common;

import com.fasterxml.jackson.databind.JsonNode;

import play.i18n.Messages;

/**
 * @ClassName: ResultVO
 * @Description: 返回描述接口
 * @date 2014年3月24日 下午5:12:51
 * @author RenYouchao
 * 
 */
public class ResultVO {

	/** 状态200为正常 **/
	private String status;
	/** 提示消息 **/
	private String msg;
	/** 需要返回的数据 **/
	private Object data;


	private ResultVO(String status, String msg, Object data) {
		super();
		this.status = status;
		this.msg = msg;
		this.data = data;
	}

	public ResultVO(String status, String msg) {
		super();
		this.status = status;
		this.msg = msg;
	}

	public ResultVO(String status) {
		super();
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

}
