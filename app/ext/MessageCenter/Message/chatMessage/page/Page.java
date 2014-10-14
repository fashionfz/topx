/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月22日
 */
package ext.MessageCenter.Message.chatMessage.page;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: Page
 * @Description: 聊天记录分页的vo对象
 */
public class Page<T> {

	private int status;
	
	private long totalRowCount;
	
	private List<T> list;
	
	private Date lastQueryDate;
	
	private Boolean isEmpty;

	public Page(int status, long totalRowCount, List<T> list) {
		super();
		this.status = status;
		this.totalRowCount = totalRowCount;
		this.list = list;
	}
	
	public Page(int status, long totalRowCount, List<T> list,Date lastQueryDate,Boolean isEmpty) {
		super();
		this.status = status;
		this.totalRowCount = totalRowCount;
		this.list = list;
		this.lastQueryDate = lastQueryDate;
		this.isEmpty = isEmpty;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTotalRowCount() {
		return totalRowCount;
	}

	public void setTotalRowCount(long totalRowCount) {
		this.totalRowCount = totalRowCount;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
	
	public Date getLastQueryDate() {
		return lastQueryDate;
	}

	public void setLastQueryDate(Date lastQueryDate) {
		this.lastQueryDate = lastQueryDate;
	}
	
	public Boolean getIsEmpty() {
		return isEmpty;
	}

	public void setIsEmpty(Boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

}
