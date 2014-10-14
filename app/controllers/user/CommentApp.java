/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-11
 */
package controllers.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import models.Comment;
import models.Expert;
import models.ExpertComment;
import models.Service;
import models.ServiceComment;
import models.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import utils.DateUtils;
import vo.CommentVO;
import vo.ExpertCommentVO;
import vo.ServiceListVO;
import vo.page.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.Constants;
import common.ResultVO;
import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;

/**
 * 
 * @ClassName: CommentApp
 * @Description: 用户评论
 * @date 2013-11-11 下午5:22:26
 * @author YangXuelin
 * 
 */
public class CommentApp extends BaseApp {

	@Transactional
	public static Result saveComment() throws Exception {
		JsonNode json = getJson();
		User user = User.getFromSession(session());
		Long toCommentId = json.findPath("toCommentId").asLong();
		int commentLevel = json.findPath("level").asInt(5);
		String content = json.findPath("content").asText();
		String type = json.findPath("type").asText();
		if (StringUtils.isNotBlank(content))
			content = common.SensitiveWordsFilter.doFilter(content);
		if (type.equals("expert")) {
			User toCommentUser = User.findById(toCommentId);
			ExpertComment comment = new ExpertComment(null, user, toCommentUser, content, new java.util.Date(), null, commentLevel);
			comment.addExpertComment();
		} else if (type.equals("service")) {
			Service service = Service.queryServiceById(toCommentId);
			ServiceComment comment = new ServiceComment(null, user, service.owner, service, content, new java.util.Date(), null,
					commentLevel);
			comment.addServiceComment();
		}
		return ok(play.libs.Json.toJson(new ResultVO("200", "保存评论成功")));
	}

	/**
	 * 回复(专家或者用户)
	 * 
	 * @return
	 */
	@Transactional
	public static Result addReply() {
		JsonNode json = getJson();
		User loginUser = User.getFromSession(session());
		ObjectNodeResult result = new ObjectNodeResult();
		Long parentId = json.findPath("parentId").asLong();
		String content = json.findPath("content").asText();
		if (StringUtils.isNotBlank(content))
			content = common.SensitiveWordsFilter.doFilter(content);
		Comment p = Comment.getParentCommenById(parentId);
		if (null == p)
			return ok(result.error("评论不存在！", "222001").getObjectNode());
		if ("".equals(content.trim()))
			return ok(result.error("请输入评论内容！", "222002").getObjectNode());
		if (content.trim().length() > 255)
			return ok(result.error("评论内容过长！", "222003").getObjectNode());
		User otherUser = null;
		if (!loginUser.id.equals(p.toCommentUser.id)) {
			otherUser = p.toCommentUser;
		} else {
			otherUser = p.commentUser;
		}
		if (p.toCommentService == null) {
			ExpertComment comment = new ExpertComment(null, loginUser, otherUser, content, new java.util.Date(), p, 0);
			comment.addExpertReplay();
			ObjectNode on = Comment.getObjectNode(comment.commentTime, loginUser);
			result.put("comment", on);
		} else if (p.toCommentService != null) {
			ServiceComment comment = new ServiceComment(null, loginUser, otherUser, p.toCommentService, content, new java.util.Date(), p, 0);
			comment.addServiceReplay();
			ObjectNode on = Comment.getObjectNode(comment.commentTime, loginUser);
			result.put("comment", on);
		}
		return ok(result.getObjectNode());
	}

	/**
	 * 专家详细页面评论列表
	 * 
	 * @return
	 */
	@Transactional
	public static Result list() {
		JsonNode json = getJson();
		int pageIndex = json.findPath("pageIndex").asInt(1);
		int pageSize = json.findPath("pageSize").asInt(10);
		Long userId = json.findPath("userId").asLong(0);
		Long serviceId = json.findPath("serviceId").asLong(0);
		int level = json.findPath("level").asInt(0);
		String type = json.findPath("type").asText();
		ObjectNode result = Json.newObject();
		List<ObjectNode> nodes = new ArrayList<ObjectNode>();
		List<Integer> levelList = new ArrayList<Integer>();
		if (level != 0)
			levelList.add(level);
		Page<Comment> page = null;
		if (type.equals("expert")) {
			ExpertComment comment = new ExpertComment();
			page = comment.getExpertParentComments(pageIndex, pageSize, userId, null, levelList);
		} else if (type.equals("service")) {
			ServiceComment comment = new ServiceComment();
			page = comment.getExpertParentComments(pageIndex, pageSize, null, serviceId, levelList);
		}
		result.put("totalRecords", page.getTotalRowCount());
		if (CollectionUtils.isNotEmpty(page.getList())) {
			for (Comment c : page.getList()) {
				ObjectNode node = Json.newObject();
				node.put("headUrl", c.commentUser.getAvatar(70));
				node.put("id", c.id);
				node.put("uid", c.commentUser.id);
				node.put("username", c.commentUser.getName());
				node.put("level", c.level);
				node.put("commentTime", DateUtils.format(c.commentTime, DateUtils.FORAMT_DATE_TIME));
				node.put("content", c.content);
				nodes.add(node);
			}
		}
		result.putPOJO("comments", nodes);
		return ok(result);
	}

	/**
	 * 专家详细页面评论部分点击更多跳转页面
	 * 
	 * @param id
	 *            交易id
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result detailComment(Long id) {
		User user = User.getFromSession(session());
		Comment parent = Comment.getParentCommenById(id);
		if (parent == null)
			return ok(views.html.notFoundResource.render());
		List<Comment> replys = Comment.getCommentsByParent(parent.id);
		Boolean isCommentAble = false;
		CommentVO p = new CommentVO();
		p.convert(parent, true);
		List<CommentVO> vos = new ArrayList<CommentVO>();
		if (CollectionUtils.isNotEmpty(replys)) {
			vos = new ArrayList<CommentVO>(replys.size());
			for (Comment c : replys) {
				CommentVO vo = new CommentVO();
				vo.convert(c, false);
				vos.add(vo);
			}
		}
		if (parent.type.equals("expert")) {
			Expert expert = Expert.getExpertByUserId(parent.toCommentUser.id);
			ExpertCommentVO ec = new ExpertCommentVO();
			ec.convert(expert);
			if (user.id.equals(parent.toCommentUser.id) || user.id.equals(parent.commentUser.id))
				isCommentAble = true;
			return ok(views.html.comment.expertreply.render(ec, p, vos, isCommentAble));
		} else if (parent.type.equals("service")) {
			if (user != null && (user.id.equals(parent.toCommentUser.id) || user.id.equals(parent.commentUser.id)))
				isCommentAble = true;
			Service service = Service.queryServiceById(parent.toCommentService.id);
			ServiceListVO slv = new ServiceListVO();
			slv.setId(service.id);
			if (service.getOwner() != null && service.getOwner().experts != null) {
				Expert expert = service.getOwner().getExperts().iterator().next();
				slv.setCountry(expert.country);
				slv.setCountryUrl(Constants.countryPicKV.get(expert.country));
				slv.setJob(expert.job);
				slv.setGender(expert.gender);
				slv.setOwnerUserName(service.owner.userName);
				if (StringUtils.isNotBlank(expert.headUrl))
					slv.setHeadUrl(User.getAvatarFileRelativePath(service.owner.id, 70));
			}
			slv.setCoverUrl(service.coverUrl);
			slv.setOwnerUserId(service.owner.id);
			slv.setTitle(service.title);
			slv.setInfo(service.info);
			slv.setScore(String.valueOf(service.score));
			slv.setCommentNum(service.commentNum);
			if (service.price == null)
				slv.setPrice("");
			else
				slv.setPrice(Constants.dformat.format(service.price));
			return ok(views.html.comment.servicereply.render(slv, p, vos, isCommentAble));
		}
		return ok(views.html.notFoundResource.render());
	}

}
