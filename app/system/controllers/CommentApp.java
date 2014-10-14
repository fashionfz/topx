package system.controllers;


import models.Comment;
import models.Expert;
import models.SkillTag;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import system.vo.Page;
import system.vo.TagFormVo;
import system.vo.ext.ExtForm;

/**
 * 评论和回复管理
 */
public class CommentApp extends Controller {

	@Transactional(readOnly = true)
	public static Result queryComment() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String limit = StringUtils.isBlank(requestData.get("limit")) ? "10" : requestData.get("limit");
		String page = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String start = StringUtils.isBlank(requestData.get("start")) ? "0" : requestData.get("start");
		
		String searchText = requestData.get("searchText").trim();
		String searchStatus = StringUtils.isBlank(requestData.get("searchStatus")) ? "-1" : requestData.get("searchStatus");
		Integer commentLevel = Integer.parseInt(searchStatus);
		
		Page<system.vo.CommentVO> commentPage = Comment.getAllCommentPage(Integer.parseInt(page), Integer.parseInt(start), Integer.parseInt(limit), searchText, commentLevel);
		return ok(play.libs.Json.toJson(commentPage));
	}
	
	@Transactional(readOnly = true)
	public static Result queryReply() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String commentId = StringUtils.isBlank(requestData.get("commentId"))?"0":requestData.get("commentId");
		String limit = StringUtils.isBlank(requestData.get("limit")) ? "10" : requestData.get("limit");
		String page = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String start = StringUtils.isBlank(requestData.get("start")) ? "0" : requestData.get("start");
		
		Page<system.vo.CommentVO> commentPage = Comment.getAllReplyOfCurrentCommentPage(Long.parseLong(commentId),Integer.parseInt(page), Integer.parseInt(start), Integer.parseInt(limit));
		return ok(play.libs.Json.toJson(commentPage));
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
	public static Result delete(Long id) throws Exception {
		String parentStr = new String();
		try {
			Comment comment = Comment.getCommentByID(id);
			Expert expert = Expert.getExpertByUserId(comment.toCommentUser.getId());
			if(comment!=null){
				Comment.deleteById(id);
			}
			if (comment.level > 0) {
				expert.sumScore = expert.sumScore - comment.level;
				if(expert.commentNum>0){
					expert.commentNum = expert.commentNum-1;
				}
				expert.recountAverageScore(); // 重新计算平均分
				expert.saveOrUpate();
			}
			
		} catch (Exception e) {
			if(Logger.isErrorEnabled()){
				Logger.error("删除id为"+id+"的Comment出错。",e);
			}
			parentStr = "删除id为"+id+"的Comment出错。" + e.getMessage();
		}
		return ok(parentStr);
	}

}
