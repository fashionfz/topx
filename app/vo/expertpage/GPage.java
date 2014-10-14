/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月22日
 */
package vo.expertpage;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;

import utils.SemUtils;
import controllers.routes;

/**
 * 
 * 
 * @ClassName: Page
 * @Description: 分页的vo对象
 * @date 2013年10月22日 上午10:53:45
 * @author RenYouchao
 * 
 */
public class GPage<T> {

	ScriptEngine engine = SemUtils.getEngine();

	private int pageSize;

	private long totalRowCount;
	/**
	 * 当前页
	 */
	private int pageIndex;

	private List<T> list;

	private String href;
	/**
	 * 页面查询参数
	 */
	private String ft;

	private String enft;

	private String gpf;

	private String inf;
	
	private String infStr;

	private String o;

	private String ot;

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize
	 *            the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the list
	 */
	public List<T> getList() {
		return list;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<T> list) {
		this.list = list;
	}

	/**
	 * @param totalRowCount
	 *            the totalRowCount to set
	 */
	public void setTotalRowCount(long totalRowCount) {
		this.totalRowCount = totalRowCount;
	}

	/**
	 * @param pageIndex
	 *            the pageIndex to set
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public GPage(List<T> data, long total, int page, int pageSize) {
		this.list = data;
		this.totalRowCount = total;
		this.pageIndex = page;
		this.pageSize = pageSize;
	}

	public long getTotalRowCount() {
		return totalRowCount;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public boolean hasPrev() {
		return pageIndex > 1;
	}

	public boolean hasNext() {
		return (totalRowCount / pageSize) >= pageIndex;
	}

	public String getFt() {
		return ft;
	}

	public void setFt(String ft) throws ScriptException {
		if (StringUtils.isNotBlank(ft)) {
			this.enft = engine.eval("encodeURIComponent('" + ft + "')").toString();
		}
		this.ft = ft;
	}

	public String getInfHref(String inf) {
		StringBuffer urlbb = new StringBuffer(controllers.group.routes.GroupApp.searchResult().url());
		urlbb.append("?p=1|1&type=html");
		if (StringUtils.isNotBlank(this.enft))
			urlbb.append("&ft=").append(this.enft);
		if (StringUtils.isNotBlank(this.gpf))
			urlbb.append("&gpf=").append(this.gpf);
		if (StringUtils.isNotBlank(inf))
			urlbb.append("&inf=").append(inf);
		if (StringUtils.isNotBlank(this.o))
			urlbb.append("&o=").append(this.o);
		if (StringUtils.isNotBlank(this.ot))
			urlbb.append("&ot=").append(this.ot);
		return urlbb.toString();
	}
	

	public String getGpfHref(String gpf) {
		StringBuffer urlbb = new StringBuffer(controllers.group.routes.GroupApp.searchResult().url());
		urlbb.append("?p=1|1&type=html");
		if (StringUtils.isNotBlank(this.enft))
			urlbb.append("&ft=").append(this.enft);
		if (StringUtils.isNotBlank(gpf))
			urlbb.append("&gpf=").append(gpf);
		if (StringUtils.isNotBlank(this.inf))
			urlbb.append("&inf=").append(this.inf);
		if (StringUtils.isNotBlank(this.o))
			urlbb.append("&o=").append(this.o);
		if (StringUtils.isNotBlank(this.ot))
			urlbb.append("&ot=").append(this.ot);
		return urlbb.toString();
	}

	public String getGpf() {
		if (StringUtils.isBlank(this.gpf))
			return "权限";
		else if (this.gpf.equals("0"))
			return "公开自由加入";
		else if (this.gpf.equals("1"))
			return "需要申请加入";
		return "权限";
	}

	public String getOHref(String o) {
		StringBuffer urlbb = new StringBuffer(controllers.group.routes.GroupApp.searchResult().url());
		urlbb.append("?p=1|1&type=html");
		if (StringUtils.isNotBlank(this.enft))
			urlbb.append("&ft=").append(this.enft);
		if (StringUtils.isNotBlank(gpf))
			urlbb.append("&gpf=").append(gpf);
		if (StringUtils.isNotBlank(this.inf))
			urlbb.append("&inf=").append(this.inf);
		if (StringUtils.isNotBlank(o)){
			urlbb.append("&o=").append(o);
			urlbb.append("&ot=DESC");
		}
		return urlbb.toString();
	}

	public String getOClass(String oc) {
		if ((StringUtils.isBlank(oc) && StringUtils.isBlank(this.o)) ||
				oc.equals(this.o))
			return "active";
		else
			return "";
	}
	//
	// public void setHref(String href) {
	// this.href = href;
	// }
	//
	// public String getNoResultStr() {
	// StringBuffer strBuf = new StringBuffer(this.ft).append(" ");
	//
	// if (StringUtils.isNotBlank(this.cf))
	// strBuf.append("<font color=red>").append(this.cf).append("</font>").append(" ");
	//
	// if (StringUtils.isNotBlank(this.ssf))
	// if (this.ssf.equals("1")) {
	// strBuf.append("<font color=red>在线</font>").append(" ");
	// } else if (this.ssf.equals("0")) {
	// strBuf.append("<font color=red>离线</font>").append(" ");
	// }
	// if (StringUtils.isNotBlank(this.ef))
	// if (this.ef.equals("0")) {
	// strBuf.append("<font color=red>免费</font>").append(" ");
	// } else if (this.ef.equals(">0")) {
	// strBuf.append("<font color=red>收费</font>").append(" ");
	// } else if (this.ef.equals("2")) {
	// strBuf.append("<font color=red>面议</font>").append(" ");
	// }
	//
	// if (StringUtils.isNotBlank(this.gf))
	// if (this.gf.equals("0")) {
	// strBuf.append("<font color=red>男性</font>").append(" ");
	// } else if (this.gf.equals("1")) {
	// strBuf.append("<font color=red>女性</font>").append(" ");
	// }
	//
	// return strBuf.toString();
	//
	// }
	//
	// /**
	// * @return the enft
	// */
	// public String getEnft() {
	// return enft;
	// }
	//
	// /**
	// * @param enft
	// * the enft to set
	// */
	// public void setEnft(String enft) {
	// this.enft = enft;
	// }
	//
	// public List<String> getCountryList() {
	// return countryList;
	// }
	//
	// public void setCountryList(List<String> countryList) {
	// this.countryList = countryList;
	// }

	/**
	 * @param enft
	 *            the enft to set
	 */
	public void setEnft(String enft) {
		this.enft = enft;
	}

	/**
	 * @param gpf
	 *            the gpf to set
	 */
	public void setGpf(String gpf) {
		this.gpf = gpf;
	}

	/**
	 * @param inf
	 *            the inf to set
	 */
	public void setInf(String inf) {
		this.inf = inf;
	}

	/**
	 * @param o
	 *            the o to set
	 */
	public void setO(String o) {
		this.o = o;
	}

	/**
	 * @param ot
	 *            the ot to set
	 */
	public void setOt(String ot) {
		this.ot = ot;
	}
	
	/**
	 * @param infStr the infStr to set
	 */
	public void setInfStr(String infStr) {
		this.infStr = infStr;
	}

	/**
	 * @return the infStr
	 */
	public String getInfStr() {
		return infStr;
	}

}
