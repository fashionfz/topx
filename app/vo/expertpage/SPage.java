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
public class SPage<T> {

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

	private String cf;
	
	private String cfStr;
	
	private String gf;

	private String inf;
	
	private String infStr;

	private String o;

	private String ot;
	
	private List<String> countryList;

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

	public SPage(List<T> data, long total, int page, int pageSize) {
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
		StringBuffer urlbb = new StringBuffer(controllers.services.routes.ServicesApp.search().url());
		urlbb.append("?p=1|1&type=html");
		if (StringUtils.isNotBlank(this.enft))
			urlbb.append("&ft=").append(this.enft);
		if (StringUtils.isNotBlank(this.cf))
			urlbb.append("&cf=").append(this.cf);
		if (StringUtils.isNotBlank(this.gf))
			urlbb.append("&gf=").append(this.gf);
		if (StringUtils.isNotBlank(inf))
			urlbb.append("&inf=").append(inf);
		if (StringUtils.isNotBlank(this.o))
			urlbb.append("&o=").append(this.o);
		if (StringUtils.isNotBlank(this.ot))
			urlbb.append("&ot=").append(this.ot);
		return urlbb.toString();
	}
	
	
	public String getGfHref(String gf) {
		StringBuffer urlbb = new StringBuffer(controllers.services.routes.ServicesApp.search().url());
		urlbb.append("?p=1|1&type=html");
		if (StringUtils.isNotBlank(this.enft))
			urlbb.append("&ft=").append(this.enft);
		if (StringUtils.isNotBlank(this.cf))
			urlbb.append("&cf=").append(this.cf);
		if (StringUtils.isNotBlank(gf))
			urlbb.append("&gf=").append(gf);
		if (StringUtils.isNotBlank(this.inf))
			urlbb.append("&inf=").append(this.inf);
		if (StringUtils.isNotBlank(this.o))
			urlbb.append("&o=").append(this.o);
		if (StringUtils.isNotBlank(this.ot))
			urlbb.append("&ot=").append(this.ot);
		return urlbb.toString();
	}
	
	public String getCfHref(String cf) throws ScriptException {
		StringBuffer urlbb = new StringBuffer(controllers.services.routes.ServicesApp.search().url());
		urlbb.append("?p=1|1&type=html");
		if (StringUtils.isNotBlank(this.enft))
			urlbb.append("&ft=").append(this.enft);
		if (StringUtils.isNotBlank(cf))
			urlbb.append("&cf=").append(engine.eval("decodeURIComponent('" + cf + "')").toString());
		if (StringUtils.isNotBlank(gf))
			urlbb.append("&gf=").append(gf);
		if (StringUtils.isNotBlank(this.inf))
			urlbb.append("&inf=").append(this.inf);
		if (StringUtils.isNotBlank(this.o))
			urlbb.append("&o=").append(this.o);
		if (StringUtils.isNotBlank(this.ot))
			urlbb.append("&ot=").append(this.ot);
		return urlbb.toString();
	}
	

	public String getGfStr() {
		if (StringUtils.isBlank(gf))
			return "性别";
		else if (gf.equals("0"))
			return "男性";
		else if (gf.equals("1"))
			return "女性";
		return gf;
	}

	public String getOHref(String o) {
		StringBuffer urlbb = new StringBuffer(controllers.services.routes.ServicesApp.search().url());
		urlbb.append("?p=1|1&type=html");
		if (StringUtils.isNotBlank(this.enft))
			urlbb.append("&ft=").append(this.enft);
		if (StringUtils.isNotBlank(this.cf))
			urlbb.append("&cf=").append(this.cf);
		if (StringUtils.isNotBlank(this.gf))
			urlbb.append("&gf=").append(this.gf);
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

	/**
	 * @param enft
	 *            the enft to set
	 */
	public void setEnft(String enft) {
		this.enft = enft;
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

	/**
	 * @return the cf
	 */
	public String getCf() {
		return cf;
	}

	/**
	 * @param cf the cf to set
	 */
	public void setCf(String cf) {
		this.cf = cf;
	}

	/**
	 * @param gf the gf to set
	 */
	public void setGf(String gf) {
		this.gf = gf;
	}

	/**
	 * @return the countryList
	 */
	public List<String> getCountryList() {
		return countryList;
	}

	/**
	 * @param countryList the countryList to set
	 */
	public void setCountryList(List<String> countryList) {
		this.countryList = countryList;
	}

	
	/**
	 * @return the cfStr
	 */
	public String getCfStr() {
		return cfStr;
	}

	/**
	 * @param cfStr the cfStr to set
	 */
	public void setCfStr(String cfStr) {
		this.cfStr = cfStr;
	}
}
