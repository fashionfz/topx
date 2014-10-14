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
public class EPage<T> {

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

	private String encf;

	private String ssf;

	private String ef;

	private String gf;

	private String o;

	private String ot;
	
	/**
	 * 国家的集合
	 */
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

	public EPage(List<T> data, long total, int page, int pageSize) {
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

	public void setFt(String ft) {
		if (StringUtils.isNotBlank(ft)) {
			try {
				this.enft = engine.eval("encodeURIComponent('" + ft + "')").toString();
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		this.ft = ft;
	}

	public String getCf() {
		if (StringUtils.isBlank(cf))
			return "国家地区";
		return cf;
	}
	
	
	public String getCf(String cf) {
		StringBuffer urlbb = new StringBuffer(routes.ExpertApp.search().url());
		urlbb.append("?p=1|1&type=html");
		if (StringUtils.isNotBlank(this.enft))
			urlbb.append("&ft=").append(this.enft);
//		if (StringUtils.isNotBlank(this.encf))
//			urlbb.append("&cf=").append(this.encf);
		if (StringUtils.isNotBlank(this.ef))
			urlbb.append("&ef=").append(this.ef);
		if (StringUtils.isNotBlank(ssf))
			urlbb.append("&ssf=").append(ssf);
		if (StringUtils.isNotBlank(gf))
			urlbb.append("&gf=").append(gf);
		if (StringUtils.isNotBlank(this.o))
			urlbb.append("&o=").append(this.o);
		if (StringUtils.isNotBlank(this.ot))
			urlbb.append("&ot=").append(this.ot);
		if (StringUtils.isNotBlank(cf))
			urlbb.append("&cf=").append("");
		return urlbb.toString();
	}

	public void setCf(String cf) {
		if (StringUtils.isNotBlank(cf)) {
			try {
				this.encf = engine.eval("encodeURIComponent('" + cf + "')").toString();
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		this.cf = cf;
	}

	public String getSsf() {
		if (StringUtils.isBlank(ssf))
			return "在线状态";
		else if (ssf.equals("0"))
			return "离线";
		else if (ssf.equals("1"))
			return "在线";
//		else if (ssf.equals("2"))
//			return "面议";
		

		return ssf;
	}

	public void setSsf(String ssf) {
		this.ssf = ssf;
	}

	public String getEf() {
		if (StringUtils.isBlank(ef))
			return "咨询费用";
		else if (ef.equals("0"))
			return "免费";
		else if (ef.equals("1"))
			return "面议";
		return ef;
	}

	public void setEf(String ef) {
		this.ef = ef;
	}

	public String getGf() {
		if (StringUtils.isBlank(gf))
			return "性别";
		else if (gf.equals("0"))
			return "男性";
		else if (gf.equals("1"))
			return "女性";
		return gf;
	}

	public void setGf(String gf) {
		this.gf = gf;
	}

	public String getO() {
		return o;
	}

	public void setO(String o) {
		this.o = o;
	}

	public String getOt() {
		return ot;
	}

	public void setOt(String ot) {
		this.ot = ot;
	}

	public String getCfHref(String cf) {
		String uricf = null;
		try {
			uricf = engine.eval("encodeURIComponent('" + cf + "')").toString();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		StringBuffer urlbb = new StringBuffer(routes.ExpertApp.search().url());
		urlbb.append("?p=1|1&type=html");
		if (StringUtils.isNotBlank(this.enft))
			urlbb.append("&ft=").append(this.enft);
		if (StringUtils.isNotBlank(uricf))
			urlbb.append("&cf=").append(uricf);
		if (StringUtils.isNotBlank(this.ef))
			urlbb.append("&ef=").append(this.ef);
		if (StringUtils.isNotBlank(this.ssf))
			urlbb.append("&ssf=").append(this.ssf);
		if (StringUtils.isNotBlank(this.gf))
			urlbb.append("&gf=").append(this.gf);
		if (StringUtils.isNotBlank(this.o))
			urlbb.append("&o=").append(this.o);
		if (StringUtils.isNotBlank(this.ot))
			urlbb.append("&ot=").append(this.ot);
		return urlbb.toString();
	}

	public String getEfHref(String ef) {
		StringBuffer urlbb = new StringBuffer(routes.ExpertApp.search().url());
		urlbb.append("?p=1|1&type=html");
		if (StringUtils.isNotBlank(this.enft))
			urlbb.append("&ft=").append(this.enft);
		if (StringUtils.isNotBlank(this.encf))
			urlbb.append("&cf=").append(this.encf);
		if (StringUtils.isNotBlank(ef))
			urlbb.append("&ef=").append(ef);
		if (StringUtils.isNotBlank(this.ssf))
			urlbb.append("&ssf=").append(this.ssf);
		if (StringUtils.isNotBlank(this.gf))
			urlbb.append("&gf=").append(this.gf);
		if (StringUtils.isNotBlank(this.o))
			urlbb.append("&o=").append(this.o);
		if (StringUtils.isNotBlank(this.ot))
			urlbb.append("&ot=").append(this.ot);
		return urlbb.toString();
	}

	public String getSsfHref(String ssf) {
		StringBuffer urlbb = new StringBuffer(routes.ExpertApp.search().url());
		urlbb.append("?p=1|1&type=html");
		if (StringUtils.isNotBlank(this.enft))
			urlbb.append("&ft=").append(this.enft);
		if (StringUtils.isNotBlank(this.encf))
			urlbb.append("&cf=").append(this.encf);
		if (StringUtils.isNotBlank(this.ef))
			urlbb.append("&ef=").append(this.ef);
		if (StringUtils.isNotBlank(ssf))
			urlbb.append("&ssf=").append(ssf);
		if (StringUtils.isNotBlank(this.gf))
			urlbb.append("&gf=").append(this.gf);
		if (StringUtils.isNotBlank(this.o))
			urlbb.append("&o=").append(this.o);
		if (StringUtils.isNotBlank(this.ot))
			urlbb.append("&ot=").append(this.ot);
		return urlbb.toString();
	}

	public String getGfHref(String gf) {
		StringBuffer urlbb = new StringBuffer(routes.ExpertApp.search().url());
		urlbb.append("?p=1|1&type=html");
		if (StringUtils.isNotBlank(this.enft))
			urlbb.append("&ft=").append(this.enft);
		if (StringUtils.isNotBlank(this.encf))
			urlbb.append("&cf=").append(this.encf);
		if (StringUtils.isNotBlank(this.ef))
			urlbb.append("&ef=").append(this.ef);
		if (StringUtils.isNotBlank(ssf))
			urlbb.append("&ssf=").append(ssf);
		if (StringUtils.isNotBlank(gf))
			urlbb.append("&gf=").append(gf);
		if (StringUtils.isNotBlank(this.o))
			urlbb.append("&o=").append(this.o);
		if (StringUtils.isNotBlank(this.ot))
			urlbb.append("&ot=").append(this.ot);
		return urlbb.toString();
	}

	public String getOHref(String o) {
		StringBuffer urlbb = new StringBuffer(routes.ExpertApp.search().url());
		urlbb.append("?p=1|1&type=html");
		if (StringUtils.isNotBlank(this.enft))
			urlbb.append("&ft=").append(this.enft);
		if (StringUtils.isNotBlank(this.encf))
			urlbb.append("&cf=").append(this.encf);
		if (StringUtils.isNotBlank(this.ef))
			urlbb.append("&ef=").append(this.ef);
		if (StringUtils.isNotBlank(this.ssf))
			urlbb.append("&ssf=").append(this.ssf);
		if (StringUtils.isNotBlank(this.gf))
			urlbb.append("&gf=").append(this.gf);
		if (StringUtils.isNotBlank(o)) {
			urlbb.append("&o=").append(o);
			urlbb.append("&ot=DESC");
		}
		// if(StringUtils.isNotBlank(this.ot))
		return urlbb.toString();
	}

	public String getOClass(String oc) {
		if ((StringUtils.isBlank(oc) && StringUtils.isBlank(this.o)) || oc.equals(this.o))
			return "active";
		else
			return "";
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getNoResultStr() {
		StringBuffer strBuf = new StringBuffer(this.ft).append(" ");

		if (StringUtils.isNotBlank(this.cf))
			strBuf.append("<font color=red>").append(this.cf).append("</font>").append(" ");

		if (StringUtils.isNotBlank(this.ssf))
			if (this.ssf.equals("1")) {
				strBuf.append("<font color=red>在线</font>").append(" ");
			} else if (this.ssf.equals("0")) {
				strBuf.append("<font color=red>离线</font>").append(" ");
			}
		if (StringUtils.isNotBlank(this.ef))
			if (this.ef.equals("0")) {
				strBuf.append("<font color=red>免费</font>").append(" ");
			} else if (this.ef.equals(">0")) {
				strBuf.append("<font color=red>收费</font>").append(" ");
			} else if (this.ef.equals("2")) {
				strBuf.append("<font color=red>面议</font>").append(" ");
			}

		if (StringUtils.isNotBlank(this.gf))
			if (this.gf.equals("0")) {
				strBuf.append("<font color=red>男性</font>").append(" ");
			} else if (this.gf.equals("1")) {
				strBuf.append("<font color=red>女性</font>").append(" ");
			}

		return strBuf.toString();

	}

	/**
	 * @return the enft
	 */
	public String getEnft() {
		return enft;
	}

	/**
	 * @param enft
	 *            the enft to set
	 */
	public void setEnft(String enft) {
		this.enft = enft;
	}
	
	public List<String> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<String> countryList) {
		this.countryList = countryList;
	}

}
