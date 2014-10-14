/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年12月26日
 */
package vo;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;

import utils.SemUtils;
import controllers.routes;
import models.Expert;

/**
 * @ClassName: SPage
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2013年12月26日 下午12:59:07
 * @author RenYouchao
 * 
 */
public class SPage {

	private int p;

	private Long i;

	private String s;

	private List<TagListVo> is;

	private List<TagListVo> ss;

	private List<ExpertListVO> experts;

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

	public SPage(int p, Long i, String s, String cf, String ssf, String ef, String gf, String o, String ot, List<TagListVo> is,
			List<TagListVo> ss, List<ExpertListVO> experts) {
		super();
		this.p = p;
		this.cf = cf;
		if (StringUtils.isNotBlank(this.cf)) {
			try {
				this.encf = SemUtils.getEngine().eval("encodeURIComponent('" + cf + "')").toString();
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		this.ssf = ssf;
		this.ef = ef;
		this.gf = gf;
		this.o = o;
		this.ot = ot;
		this.i = i;
		this.s = s;
		this.is = is;
		this.ss = ss;
		this.experts = experts;
	}

	/**
	 * @return the p
	 */
	public int getP() {
		return p;
	}

	/**
	 * @return the is
	 */
	public List<TagListVo> getIs() {
		return is;
	}

	/**
	 * @return the ss
	 */
	public List<TagListVo> getSs() {
		return ss;
	}

	/**
	 * @return the experts
	 */
	public List<ExpertListVO> getExperts() {
		return experts;
	}

	/**
	 * @param p
	 *            the p to set
	 */
	public void setP(int p) {
		this.p = p;
	}

	/**
	 * @param is
	 *            the is to set
	 */
	public void setIs(List<TagListVo> is) {
		this.is = is;
	}

	/**
	 * @param ss
	 *            the ss to set
	 */
	public void setSs(List<TagListVo> ss) {
		this.ss = ss;
	}

	/**
	 * @param experts
	 *            the experts to set
	 */
	public void setExperts(List<ExpertListVO> experts) {
		this.experts = experts;
	}

	/**
	 * @return the i
	 */
	public Long getI() {
		return i;
	}

	/**
	 * @param i
	 *            the i to set
	 */
	public void setI(Long i) {
		this.i = i;
	}

	public String getCf() {
		if (StringUtils.isBlank(cf))
			return "国家地区";
		return cf;
	}
	
	public String getCf(String countryName) {
		StringBuffer urlbb = new StringBuffer(controllers.skilltag.routes.SkillTagApp.list().url());
		urlbb.append("?i=").append(this.i);
		if (this.s != null)
			urlbb.append("&s=").append(this.s);
//		if (StringUtils.isNotBlank(this.encf))
//			urlbb.append("&cf=").append(countryName);
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
		if (StringUtils.isNotBlank(countryName))
			urlbb.append("&cf=").append("");
		return urlbb.toString();
	}

	public String getSsf() {
		if (StringUtils.isBlank(ssf))
			return "在线状态";
		else if (ssf.equals("1"))
			return "在线";
		else if (ssf.equals("0"))
			return "离线";

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
		StringBuffer urlbb = new StringBuffer(controllers.skilltag.routes.SkillTagApp.list().url());
		urlbb.append("?i=").append(this.i);
		if (this.s != null)
			urlbb.append("&s=").append(this.s);
		if (StringUtils.isNotBlank(this.encf))
			urlbb.append("&cf=").append(this.encf);
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
		StringBuffer urlbb = new StringBuffer(controllers.skilltag.routes.SkillTagApp.list().url());
		urlbb.append("?i=").append(this.i);
		if (this.s != null)
			urlbb.append("&s=").append(this.s);
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
		StringBuffer urlbb = new StringBuffer(controllers.skilltag.routes.SkillTagApp.list().url());
		urlbb.append("?i=").append(this.i);
		if (this.s != null)
			urlbb.append("&s=").append(this.s);
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
		StringBuffer urlbb = new StringBuffer(controllers.skilltag.routes.SkillTagApp.list().url());
		urlbb.append("?i=").append(this.i);
		if (this.s != null)
			urlbb.append("&s=").append(this.s);
		if (StringUtils.isNotBlank(this.encf))
			urlbb.append("&cf=").append(this.encf);
		if (StringUtils.isNotBlank(this.ef))
			urlbb.append("&ef=").append(this.ef);
		if (StringUtils.isNotBlank(this.ssf))
			urlbb.append("&ssf=").append(this.ssf);
		if (StringUtils.isNotBlank(gf))
			urlbb.append("&gf=").append(gf);
		if (StringUtils.isNotBlank(this.o))
			urlbb.append("&o=").append(this.o);
		if (StringUtils.isNotBlank(this.ot))
			urlbb.append("&ot=").append(this.ot);
		return urlbb.toString();
	}

	public String getOHref(String o) {
		StringBuffer urlbb = new StringBuffer(controllers.skilltag.routes.SkillTagApp.list().url());
		urlbb.append("?i=").append(this.i);
		if (this.s != null)
			urlbb.append("&s=").append(this.s);
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
		if (StringUtils.isBlank(oc) && StringUtils.isBlank(this.o))
			return " active";
		else if (oc.equals(this.o))
			return " active";
		else
			return "";
	}

	/**
	 * @return the s
	 */
	public String getS() {
		return s;
	}

	/**
	 * @param s the s to set
	 */
	public void setS(String s) {
		this.s = s;
	}

	public List<String> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<String> countryList) {
		this.countryList = countryList;
	}
	
}
