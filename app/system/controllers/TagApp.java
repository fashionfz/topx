package system.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Attach;
import models.AttachOfIndustry;
import models.SkillTag;
import models.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.cache.Cache;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import system.vo.Page;
import system.vo.SkillTagListVo;
import system.vo.TagFormVo;
import system.vo.ext.ExtForm;
import utils.Assets;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.Constants;
import controllers.attachment.AttachUploadApp;
import controllers.base.ObjectNodeResult;

public class TagApp extends Controller {

	@Transactional(readOnly = true)
	public static Result queryCate() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String cateId = requestData.get("cateId");
		String limit = requestData.get("limit");
		String page = requestData.get("page");
		String start = requestData.get("start");
		String searchText = request().getQueryString("searchText");
		Page<SkillTagListVo> tags = SkillTag.listTagByCate(Long.parseLong(cateId), Integer.parseInt(page), Integer.parseInt(start),
				Integer.parseInt(limit),searchText);
		return ok(play.libs.Json.toJson(tags));
	}

	@Transactional(readOnly = true)
	public static Result queryInd() {
		List<SkillTagListVo> tagList = new ArrayList<SkillTagListVo>();
		String type = request().getQueryString("type");
		if (StringUtils.equals(type, "all")) {
			SkillTagListVo defaultTag = new SkillTagListVo();
			defaultTag.setTagName("全部行业");
			defaultTag.setId(-1L);
			tagList.add(defaultTag);
			List<SkillTag> tags = SkillTag.listCategories(100);
			List<SkillTagListVo> sklvs = new ArrayList<SkillTagListVo>();
			for (SkillTag skillTag : tags) {
				SkillTagListVo sklv = new SkillTagListVo();
				sklv.setId(skillTag.id);
				if (skillTag.industry != null) {
					sklv.setIndustryId(skillTag.industry.id);
					sklv.setIndustryName(skillTag.industry.tagName);
				}
				sklv.setHits(skillTag.hits);
				sklv.setTagNameEn(skillTag.tagNameEn);
				sklv.setTagName(skillTag.tagName);
				sklv.setSeq(skillTag.seq);
				sklv.setTagType(skillTag.tagType);
				sklvs.add(sklv);
			}
			tagList.addAll(1, sklvs);
			return ok(play.libs.Json.toJson(tagList));
		} else if(StringUtils.equals(type, "industry")) {
			List<SkillTag> tags = SkillTag.listCategories(100);
			List<SkillTagListVo> sklvs = new ArrayList<SkillTagListVo>();
			for (SkillTag skillTag : tags) {
				SkillTagListVo sklv = new SkillTagListVo();
				sklv.setId(skillTag.id);
				if (skillTag.industry != null) {
					sklv.setIndustryId(skillTag.industry.id);
					sklv.setIndustryName(skillTag.industry.tagName);
				}
				sklv.setHits(skillTag.hits);
				sklv.setTagNameEn(skillTag.tagNameEn);
				sklv.setTagName(skillTag.tagName);
				sklv.setSeq(skillTag.seq);
				sklv.setTagType(skillTag.tagType);
				sklvs.add(sklv);
			}
			tagList.addAll(0, sklvs);
			return ok(play.libs.Json.toJson(tagList));
		} else {
			ObjectNode result = Json.newObject();
			List<SkillTag> tags = SkillTag.listCategories(100);
			result.put("total", CollectionUtils.isEmpty(tags) ? 0 : tags.size());
			List<ObjectNode> data = new ArrayList<ObjectNode>(tags.size());
			for (SkillTag skillTag : tags) {
				ObjectNode node = Json.newObject();
				node.put("id", skillTag.getId());
				node.put("tagName",skillTag.getTagName());
				node.put("tagNameEn", skillTag.getTagNameEn());
				skillTag.industry = null;
				List<AttachOfIndustry> attachList = new AttachOfIndustry().queryByAttachId(skillTag.getId());
				Set<AttachOfIndustry> attachSet = new HashSet<AttachOfIndustry>(attachList);
				List<ObjectNode> nodes = new ArrayList<ObjectNode>(attachSet == null ? 0 : attachSet.size());
				if (CollectionUtils.isNotEmpty(attachSet)) {
					for (Attach item : attachSet) {
						ObjectNode attachON = Json.newObject();
						attachON.put("attachId", item.id);
						attachON.put("attachFileName", StringUtils.isEmpty(item.fileName) ? "-" : item.fileName);
						attachON.put("attachPath", Assets.at(item.path));
						attachON.put("industryId", skillTag.getId());
						nodes.add(attachON);
					}
				}
				node.putPOJO("attachInfos", nodes);
				data.add(node);
			}
			result.putPOJO("data", data);
			return ok(result);
//			tagList.addAll(0, tags);
		}
	}

	@Transactional
	public static Result updateCate() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String cateId = requestData.get("cateId");
		String oldId = requestData.get("oldId");
		String id = requestData.get("id");
		SkillTag st = SkillTag.getTagById(Long.parseLong(id));
		st.industryId = Long.parseLong(cateId);
		st.seq = SkillTag.getMaxSeq(cateId);
		st.seq++;
		SkillTag.saveOrUpdate(st);
		return ok("success!");

	}

	@Transactional(readOnly = true)
	public static Result get(Long id) {
		SkillTag skillTag = SkillTag.getTagById(id);
		ExtForm extForm = new ExtForm();
		TagFormVo tagFormVo = new TagFormVo();
		tagFormVo.setId(skillTag.id);
		tagFormVo.setTagName(skillTag.tagName);
		tagFormVo.setTagNameEn(skillTag.tagNameEn);
		tagFormVo.setIndustryId(skillTag.industryId);
		tagFormVo.setHits(skillTag.hits);
		tagFormVo.setSeq(skillTag.seq);
		tagFormVo.setTagType(skillTag.tagType.ordinal());
		extForm.setSuccess(true);
		extForm.setMsg("updated");
		extForm.setData(tagFormVo);
		return ok(play.libs.Json.toJson(extForm));
	}

	@Transactional
	public static Result delete(Long id) {
		String parentStr = new String();
		SkillTag skillTag = SkillTag.getTagById(id);
		if (skillTag.tagType.equals(SkillTag.TagType.CATEGORY)){
			SkillTag.deleteExpertIn(skillTag);
			//SkillTag.deleteTagAndInByIn(skillTag);
			SkillTag.delete(skillTag);
			Cache.remove(Constants.CACHE_EXPERT_TOPS);
			List<Long> attachIdList = new ArrayList<Long>();
			Set<AttachOfIndustry> attachSet = skillTag.getAttachs();
			if (CollectionUtils.isNotEmpty(attachSet)) {
				for (AttachOfIndustry item : attachSet) {
					attachIdList.add(item.id);
				}
				Attach.deleteByIds(attachIdList, AttachOfIndustry.class);
			}
		} else {
			SkillTag.deleteById(id);
		}
		return ok(parentStr);
	}

	@Transactional
	public static Result saveOrUpdate() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String id = requestData.get("id");
		ExtForm extForm = new ExtForm();
		String tagName = requestData.get("tagName");
		if (SkillTag.isExist(tagName,StringUtils.isBlank(id)? null : Long.parseLong(id))) {
			extForm.setSuccess(false);
			extForm.setMsg("标签数据重复,请检查.");
			return ok(play.libs.Json.toJson(extForm));
		}
		String tagNameEn = requestData.get("tagNameEn");
		String hits = requestData.get("hits");
		String seq = requestData.get("seq");
		String tagType = requestData.get("tagType");
		String industryId = requestData.get("industryId");
        ObjectNodeResult result = new ObjectNodeResult();
        User currentUser = null;
        
		int size = 0;
		SkillTag skillTag = null;
		if (StringUtils.isNotBlank(id)) {
			skillTag = SkillTag.getTagById(Long.parseLong(id));
		} else {
			skillTag = new SkillTag();
		}
		if (StringUtils.isBlank(seq)) {
			size = SkillTag.getMaxSeq(industryId);
			size++;
		} else {
			try {
				size = Integer.parseInt(seq);
				if(size<0){
					extForm.setSuccess(false);
					extForm.setMsg("填写的顺序必须是大于或等于0的整数，请检查！");
					return ok(play.libs.Json.toJson(extForm));
				}
			} catch (NumberFormatException e) {
				if(Logger.isErrorEnabled()){
					Logger.error("新增或修改行业标签，填写的顺序转换为数字出错。",e);
				}
				extForm.setMsg("填写的顺序不是数字，请检查！");
				return ok(play.libs.Json.toJson(extForm));
			}
		}
		skillTag.tagNameEn = tagNameEn;
		skillTag.tagName = tagName;
		skillTag.seq = size;
		skillTag.industryId = StringUtils.isBlank(industryId) ? null : Long.parseLong(industryId);
		
		if (tagType.equals("0")){
			MultipartFormData body = request().body().asMultipartFormData();
	        FilePart uploadFile = body.getFile("certificate");
	        Long attachId = null;
	        if (null != uploadFile) {
//	            extForm.setSuccess(false);
//	            extForm.setMsg("请选择上传图片");
//	            return ok(Json.toJson(extForm));
		        AttachUploadApp.save(uploadFile.getFile(), uploadFile.getFilename(), result, currentUser, "industry");
		        attachId = result.getObjectNode().get("attachId").asLong();
	        }
			skillTag.tagType = SkillTag.TagType.CATEGORY;
			if (attachId != null) {
				Set<AttachOfIndustry> attachOfIndustrySet = new HashSet<AttachOfIndustry>();
				if (skillTag != null && skillTag.getId() != null) {
					attachOfIndustrySet = skillTag.getAttachs();
				}
				AttachOfIndustry attachOfIndustry = (AttachOfIndustry)Attach.queryById(attachId, AttachOfIndustry.class);
				attachOfIndustrySet.add(attachOfIndustry);
				skillTag.setAttachs(attachOfIndustrySet);
	        }
			SkillTag.saveOrUpdate(skillTag);
			play.cache.Cache.remove(Constants.CACHE_EXPERT_TOPS);
	        play.cache.Cache.remove(Constants.CACHE_INDUSTRY);
		}else{
			skillTag.tagType = SkillTag.TagType.TAG;
			SkillTag.saveOrUpdate(skillTag);
		}
		TagFormVo tagFormVo = new TagFormVo();
		tagFormVo.setId(skillTag.id);
		tagFormVo.setTagName(skillTag.tagName);
		tagFormVo.setHits(skillTag.hits);
		tagFormVo.setParentId(skillTag.industryId);
		tagFormVo.setIndustryId(skillTag.industryId);
		tagFormVo.setSeq(skillTag.seq);
		tagFormVo.setTagType(skillTag.tagType.ordinal());
		extForm.setSuccess(true);
		extForm.setMsg("updated");
		extForm.setData(tagFormVo);
		return ok(play.libs.Json.toJson(extForm));
	}
	
	/**
	 * 删除行业对应的图片附件
	 * @return
	 */
	@Transactional
	public static Result deleteAttach() {
		ObjectNodeResult result = new ObjectNodeResult();
		DynamicForm requestData = Form.form().bindFromRequest();
		String attachId = requestData.get("attachId");
		String industryId = requestData.get("industryId");
		AttachOfIndustry aoi = Attach.queryById(Long.parseLong(attachId), AttachOfIndustry.class);
		SkillTag st = SkillTag.getTagById(Long.parseLong(industryId));
		if (aoi != null && st != null) {
			Set<AttachOfIndustry> aiSet = st.getAttachs();
			if (aiSet.contains(aoi)) {
				aiSet.remove(aoi);
			}
			st.setAttachs(aiSet);
			SkillTag.saveOrUpdate(st);
		}
		return ok(result.getObjectNode());
	}
}
