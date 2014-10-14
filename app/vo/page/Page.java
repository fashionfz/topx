/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月22日
 */
package vo.page;

import java.util.List;

/**
 * 
 * 
 * @ClassName: Page
 * @Description: 分页的vo对象
 * @date 2013年10月22日 上午10:53:45
 * @author RenYouchao
 * 
 */
public class Page<T> {

	private int status;
	
	private long totalRowCount;
	
	private List<T> list;

	public Page(int status, long totalRowCount, List<T> list) {
		super();
		this.status = status;
		this.totalRowCount = totalRowCount;
		this.list = list;
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

}
