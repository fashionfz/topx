/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月22日
 */
package controllers.skilltag;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import models.Expert;
import models.SkillTag;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import utils.CoinUtil;
import utils.HelomeUtil;
import utils.SemUtils;
import vo.ESPage;
import vo.ExpertListVO;
import vo.SPage;
import vo.STagVo;
import vo.TagListVo;
import common.Constants;
import common.jackjson.JackJsonUtil;

/**
 * 
 * 
 * @ClassName: SkillTagApp
 * @Description: 专家技能标签控制器
 * @date 2013年10月22日 下午3:59:36
 * @author RenYouchao
 * 
 */
public class SkillTagApp extends Controller {

	static DecimalFormat fnum = new DecimalFormat("##0.0");

	@Transactional(readOnly = true)
	public static Result list() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String p = requestData.get("p") == null ? "1" : requestData.get("p");
		Long i = StringUtils.isBlank(requestData.get("i")) ? 2L : Long.parseLong(requestData.get("i"));
		String s = StringUtils.isBlank(requestData.get("s")) ? null : requestData.get("s");

		String cf = requestData.get("cf");
		String ssf = requestData.get("ssf");
		String ef = requestData.get("ef");
		String gf = requestData.get("gf");
		String o = requestData.get("o");
		String ot = requestData.get("ot");

		List<SkillTag> skillTags = SkillTag.getAll(i, 1, "all");

		List<TagListVo> ivs = new ArrayList<TagListVo>();
		List<TagListVo> svs = new ArrayList<TagListVo>();
		ScriptEngine engine = SemUtils.getEngine();
		try {
			for (SkillTag st : skillTags) {
				TagListVo tv = new TagListVo();
				tv.setTagName(st.tagName);
				tv.setTagId(st.id);
				if (st.tagType != null && st.tagType.ordinal() == SkillTag.TagType.CATEGORY.ordinal()) {
					if (st.id.equals(i))
						tv.setIsCurr(true);
					tv.setHref(controllers.skilltag.routes.SkillTagApp.list().url() + "?p=1&i=" + tv.getTagId());
					ivs.add(tv);
				} else {
					tv.setHref(controllers.skilltag.routes.SkillTagApp.list().url() + "?p=1&i=" + i + "&s="
							+ engine.eval("encodeURIComponent('" + tv.getTagName() + "')").toString());
					svs.add(tv);
				}
			}
			if (StringUtils.isNotBlank(s)) {
				TagListVo tv = new TagListVo();
				tv.setTagName(s);
				tv.setIsCurr(true);
				tv.setHref(controllers.skilltag.routes.SkillTagApp.list().url() + "?p=1&i=" + i + "&s="
						+ engine.eval("encodeURIComponent('" + tv.getTagName() + "')").toString());
				svs.add(0, tv);
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		List<ExpertListVO> elv = getTagExperts(p, i, s, cf, ssf, ef, gf, o, ot);
		SPage spage = new SPage(Integer.parseInt(p), i, s, cf, ssf, ef, gf, o, ot, ivs, svs, elv);
		List<String> countryList = SkillTag.getCountryNameWithCache();
		spage.setCountryList(countryList);

		return ok(views.html.skilltag.skilltagall.render(spage));
	}

	/**
	 * 敏感字符过滤
	 * 
	 * @param elv
	 *            ExpertListVO的list集合
	 * @deprecated 不使用
	 */
	private static void doSensitiveWordFilter(List<ExpertListVO> elv) {
		if (CollectionUtils.isNotEmpty(elv)) {
			for (ExpertListVO vo : elv) {
				String userName = common.SensitiveWordsFilter.doFilter(vo.getUserName()); // 用户名
				vo.setUserName(userName);
			}
		}
	}

	private static List<ExpertListVO> getTagExperts(String p, Long i, String s, String cf, String ssf, String ef, String gf, String o,
			String ot) {
		List<ExpertListVO> elv = new ArrayList<ExpertListVO>();
		List<Expert> experts = Expert.getPartExpert(p, 18, i, s, cf, ssf, ef, gf, o, ot);
		for (Expert e : experts) {
			ExpertListVO ev = new ExpertListVO();
			ev.setId(e.id);
			ev.setCountry(e.country);
			ev.setHeadUrl(e.headUrl);
			ev.setUserId(e.userId);
			ev.setUserName(e.userName);
			List<STagVo> stvs = new ArrayList<STagVo>();
			ObjectMapper objectMapper = JackJsonUtil.getMapperInstance();
			List<String> list = null;
			try {
				if (StringUtils.isNotBlank(e.skillsTags)) {
					list = objectMapper.readValue(e.skillsTags, List.class);
				}
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
			if (list != null)
				for (String tagName : list) {
					STagVo sTagVo = new STagVo();
					sTagVo.setTag(tagName);
					stvs.add(sTagVo);
				}
			ev.setJob(e.job);
			ev.setSkillsTags(stvs);
			ev.setCommentNum(e.commentNum);
			ev.setPayType(e.payType);
			ev.setAverageScore(e.averageScore);
			ev.setCountryUrl(Constants.countryPicKV.get(e.country));
			elv.add(ev);
		}
//		if(Logger.isDebugEnabled()){
//			System.out.println("query data  --->>>>>");
//			
//			for(ExpertListVO vo:elv){
//				System.out.print(vo.getUserName());
//				System.out.print(",");
//			}
//			System.out.println();
//		}
		
		return elv;
	}

	@Transactional(readOnly = true)
	public static Result listMore() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String p = requestData.get("p") == null ? "1" : requestData.get("p");
		Long i = StringUtils.isBlank(requestData.get("i")) ? 2L : Long.parseLong(requestData.get("i"));
		String s = StringUtils.isBlank(requestData.get("s")) ? null : requestData.get("s");
		String cf = requestData.get("cf");
		String ssf = requestData.get("ssf");
		String ef = requestData.get("ef");
		String gf = requestData.get("gf");
		String o = requestData.get("o");
		String ot = requestData.get("ot");
		List<ExpertListVO> elv = getTagExperts(p, i, s, cf, ssf, ef, gf, o, ot);
		ESPage espage = new ESPage(18L, Long.parseLong(p), elv);
		return ok(play.libs.Json.toJson(espage));
	}

	@Transactional(readOnly = true)
	public static Result AssociativeQuery() {
		String keyword = request().getQueryString("keyword");
		List<SkillTag> skillTags = null;
		if (!HelomeUtil.isEmail(keyword)) {
			skillTags = SkillTag.query(keyword, 10);
		} else {
			skillTags = new ArrayList<SkillTag>(0);
		}
		return ok(JackJsonUtil.writeValueAsString(convert(skillTags)));
	}

	@Transactional(readOnly = true)
	public static Result change(Long i, int seq) {
		List<SkillTag> skillTags = SkillTag.getAll(i, seq, "tag");
		List<TagListVo> svs = new ArrayList<TagListVo>();
		try {
			ScriptEngine engine = SemUtils.getEngine();
			for (SkillTag st : skillTags) {
				TagListVo tv = new TagListVo();
				tv.setTagName(st.tagName);
				// tv.setIsCurr(true);
				tv.setHref(controllers.skilltag.routes.SkillTagApp.list().url() + "?p=1&i=" + i + "&s="
						+ engine.eval("encodeURIComponent('" + tv.getTagName() + "')").toString());
				svs.add(tv);
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return ok(play.libs.Json.toJson(svs));
	}

	public static List<String> convert(List<SkillTag> skillTags) {
		List<String> tagNameList = new ArrayList<String>(0);
		if (!HelomeUtil.isEmpty(skillTags)) {
			for (SkillTag tag : skillTags) {
				tagNameList.add(tag.tagName);
			}
		}
		return tagNameList;
	}
}
