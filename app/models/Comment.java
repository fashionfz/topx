/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-10-23
 */
package models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.h2.util.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import play.Logger;
import play.db.jpa.JPA;
import play.libs.Json;
import utils.DateUtils;
import utils.HelomeUtil;
import vo.page.Page;

import com.fasterxml.jackson.databind.node.ObjectNode;

import common.Constants;
import controllers.base.ObjectNodeResult;
import ext.msg.model.service.MessageService;

/**
 * 
 * 
 * @ClassName: Comment
 * @Description: 消息
 * @date 2013-11-11 下午5:00:00
 * @author RenYouchao
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Table(name = "tb_comment")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	/** 评论人 **/
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	public User commentUser;
	/** 被评论人 **/
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	public User toCommentUser;
	/** 被评论人 **/
	@ManyToOne(cascade = CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	public Service toCommentService;
	/** 评价内容 */
	public String content;
	/** 评价时间 **/
	public Date commentTime;
	/** 顶级评论 **/
	@ManyToOne(cascade = CascadeType.ALL)
	public Comment parent;
	/** 评分,默认5分 **/
	@Column(name = "type", insertable = false, updatable = false)
	public String type;

	public int level = 5;

	/**
	 * 评论
	 * 
	 * @param id
	 * @param merchandise
	 * @param commentUser
	 * @param toCommentUser
	 * @param content
	 * @param commentTime
	 * @param parent
	 * @param level
	 */
	public Comment(Long id, User commentUser, User toCommentUser, Service toCommentService, String content, Date commentTime,
			Comment parent, int level) {
		super();
		this.id = id;
		this.commentUser = commentUser;
		this.toCommentUser = toCommentUser;
		this.content = content;
		this.commentTime = commentTime;
		this.toCommentService = toCommentService;
		this.parent = parent;
		this.level = level;
	}

	/**
	 * 评论
	 * 
	 * @param id
	 * @param merchandise
	 * @param commentUser
	 * @param toCommentUser
	 * @param content
	 * @param commentTime
	 * @param parent
	 * @param level
	 */
	public Comment(Long id, User commentUser, User toCommentUser, String content, Date commentTime, Comment parent, int level) {
		super();
		this.id = id;
		this.commentUser = commentUser;
		this.toCommentUser = toCommentUser;
		this.content = content;
		this.commentTime = commentTime;
		this.parent = parent;
		this.level = level;
	}

	public Comment() {

	}

	// /**
	// * 用户评论
	// *
	// * @param session
	// * @param node
	// * @return
	// */
	// @Deprecated
	// public static ObjectNodeResult addComment(User loginUser, Long
	// toCommentUserId, String content, int commentLevel,
	// List<String> snsList) throws Exception {
	// ObjectNodeResult result = new ObjectNodeResult();
	// User toCommentUser = User.findById(toCommentUserId);
	// Comment comment = new Comment(null, loginUser, toCommentUser, content,
	// new java.util.Date(), null, commentLevel);
	// comment.save();
	// Expert expert = Expert.getExpertByUserId(toCommentUserId);
	// expert.computeAverageScore(toCommentUserId);
	// expert.saveOrUpate();
	// MessageService.pushMsgComment(comment);
	// if (CollectionUtils.isNotEmpty(snsList))
	// for (String sns : snsList) {
	// UserOAuth userOAuth =
	// UserOAuthService.getValidByUserId(loginUser.id).get(sns);
	// if (null != userOAuth) {
	// SNSService.postMsg(userOAuth, content);
	// }
	// }
	// return result;
	// }

	/**
	 * 回复(用户或者专家)
	 * 
	 * @param session
	 * @param node
	 * @return
	 */
	public static ObjectNodeResult addReply(User loginUser, Long parentId, String content) {
		ObjectNodeResult result = new ObjectNodeResult();
		Comment comment = new Comment();
		Comment p = Comment.getParentCommenById(parentId);
		if (null == p) {
			return result.error("评论不存在！", "222001");
		}
		comment.parent = p;
		comment.level = p.level;
		if ("".equals(content.trim())) {
			return result.error("请输入评论内容！", "222002");
		}
		if (content.trim().length() > 255) {
			return result.error("评论内容过长！", "222003");
		}
		comment.commentUser = loginUser;
		if (p.commentUser.id.equals(loginUser.id)) {
			comment.toCommentUser = p.toCommentUser;
		} else if (p.toCommentUser.id.equals(loginUser.id)) {
			comment.toCommentUser = p.commentUser;
		} else {
			return result.error("评论不存在！", "222001");
		}
		comment.content = content;
		comment.commentTime = new Date();
		JPA.em().persist(comment);
		MessageService.pushMsgReply(comment);
		ObjectNode on = getObjectNode(comment.commentTime, loginUser);
		result.put("comment", on);
		return result;
	}

	public static ObjectNode getObjectNode(Date commentTime, User user) {
		ObjectNode node = Json.newObject();
		node.put("commentTime", DateUtils.format(commentTime, DateUtils.FORAMT_DATE_TIME));
		node.put("commentTimeDetail", DateUtils.format(commentTime, DateUtils.FORMAT_DATETIME));
		node.put("headUrl", user.getAvatar(70));
		if (StringUtils.isNullOrEmpty(user.userName)) {
			node.put("username", HelomeUtil.getMaskEmail(user.email));
		} else {
			node.put("username", user.userName);
		}
		node.put("userId", user.id);
		return node;
	}

	/**
	 * 获得用户对专家全部顶级评论
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page<Comment> getExpertParentComments(int pageIndex, int pageSize, Long userId, Long serviceId, List<Integer> levelList) {
		List<Comment> comments = new ArrayList<>();
		Long total = 0l;
		if (CollectionUtils.isEmpty(levelList)) {
			if (userId != null) {
				total = (Long) JPA
						.em()
						.createQuery(
								"select count(id) from " + this.getClass().getName()
										+ " where toCommentUser.id = :userId  and parent is null").setParameter("userId", userId)
						.getSingleResult();
				comments = JPA
						.em()
						.createQuery(
								"from " + this.getClass().getName()
										+ " c where c.toCommentUser.id = :userId and c.parent is null order by c.commentTime desc")
						.setParameter("userId", userId).setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).getResultList();
			} else if (serviceId != null) {
				total = (Long) JPA
						.em()
						.createQuery(
								"select count(id) from " + this.getClass().getName()
										+ " where  toCommentService.id = :serviceId  and parent is null")
						.setParameter("serviceId", serviceId).getSingleResult();
				comments = JPA
						.em()
						.createQuery(
								"from " + this.getClass().getName()
										+ " c where toCommentService.id = :serviceId and c.parent is null order by c.commentTime desc")
						.setParameter("serviceId", serviceId).setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize)
						.getResultList();
			}
		} else {
			if (userId != null) {
				total = (Long) JPA
						.em()
						.createQuery(
								"select count(id) from " + this.getClass().getName()
										+ " where toCommentUser.id = :userId and parent is null and level in (:levels)")
						.setParameter("userId", userId).setParameter("levels", levelList).getSingleResult();
				comments = JPA
						.em()
						.createQuery(
								"from "
										+ this.getClass().getName()
										+ " c  where c.toCommentUser.id = :userId and c.parent is null and c.level in (:levels) order by c.commentTime desc")
						.setParameter("userId", userId).setParameter("levels", levelList).setFirstResult((pageIndex - 1) * pageSize)
						.setMaxResults(pageSize).getResultList();
			} else if (serviceId != null) {
				total = (Long) JPA
						.em()
						.createQuery(
								"select count(id) from " + this.getClass().getName()
										+ " where  toCommentService.id = :serviceId and parent is null and level in (:levels)")
						.setParameter("serviceId", serviceId).setParameter("levels", levelList).getSingleResult();
				comments = JPA
						.em()
						.createQuery(
								"from "
										+ this.getClass().getName()
										+ " c  where  toCommentService.id = :serviceId and c.parent is null and c.level in (:levels) order by c.commentTime desc")
						.setParameter("serviceId", serviceId).setParameter("levels", levelList).setFirstResult((pageIndex - 1) * pageSize)
						.setMaxResults(pageSize).getResultList();

			}
		}
		Page<Comment> page = new Page<>(Constants.SUCESS, total, comments);
		return page;
	}

	/**
	 * 获得某条交易的顶级评论
	 * 
	 * @param id
	 * @return
	 */
	public static Comment getParentCommenById(Long id) {
		List<Comment> resultList = JPA
				.em()
				.createQuery(
						"from Comment c left join fetch c.commentUser cu left join fetch c.toCommentUser tcu "
								+ "where c.id = :id and c.parent is null", Comment.class).setParameter("id", id).getResultList();

		return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
	}

	/**
	 * 获得所有回复
	 * 
	 * @param parentId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Comment> getCommentsByParent(Long parentId) {
		List<Comment> replays = (List<Comment>) JPA.em().createQuery("from Comment where parent.id = :parentId order by commentTime asc")
				.setParameter("parentId", parentId).getResultList();
		if(CollectionUtils.isNotEmpty(replays))
			return replays;
		else
			return null;
	}

	/**
	 * 获得交易的所有回复
	 * 
	 * @param parentId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Comment> getCommentsByMerchandise(Long merchandiseId) {
		try {
			return (List<Comment>) JPA.em().createQuery("from Comment where merchandise.id = :merchandiseId order by commentTime asc")
					.setParameter("merchandiseId", merchandiseId).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Long> getTotalRecordByLevel(Long userid, Long... serviceId) {
		Map<Integer, Long> levelsTotal = new HashMap<Integer, Long>();
		List<Object[]> totals = new ArrayList<Object[]>();
		if (serviceId.length > 0) {
			totals = JPA
					.em()
					.createQuery(
							"select level, count(id) from "
									+ this.getClass().getName()
									+ " where toCommentUser.id = :userid and toCommentService.id=:serviceId and parent is null group by level")
					.setParameter("userid", userid).setParameter("serviceId", serviceId[0]).getResultList();
		} else {
			totals = JPA
					.em()
					.createQuery(
							"select level, count(id) from " + this.getClass().getName()
									+ " where toCommentUser.id = :userid and parent is null group by level").setParameter("userid", userid)
					.getResultList();
		}
		for (int i = 5; i > 0; i--) {
			levelsTotal.put(i, 0l);
		}
		if (totals != null && totals.size() > 0) {
			for (int i = 0; i < totals.size(); i++) {
				Object[] obj = (Object[]) totals.get(i);
				levelsTotal.put((Integer) obj[0], (Long) obj[1]);
			}
		}
		return levelsTotal;
	}

	/**
	 * 获取用户对应的评价数
	 * 
	 * @param <T>
	 * @param userId
	 * @return
	 */
	public static <T> Long getTotalCountBytoCommentUserId(Long userId, Class<T> entityClass) {
		Long total = (Long) JPA.em()
				.createQuery("select count(id) from " + entityClass.getName() + " where toCommentUser.id = :userId and parent is null")
				.setParameter("userId", userId).getSingleResult();
		if (total == null) {
			return 0L;
		}
		return total;
	}

	/**
	 * 获取用户所有的评价的总分
	 * 
	 * @param <T>
	 * @param userId
	 * @return
	 */
	public static <T> Long getTotalLevelBytoCommentUserId(Long userId, Class<T> entityClass) {
		Long total = (Long) JPA.em()
				.createQuery("select sum(level) from " + entityClass.getName() + " where toCommentUser.id = :userId and parent is null")
				.setParameter("userId", userId).getSingleResult();
		if (total == null) {
			return 0L;
		}
		return total;
	}

	/**
	 * 获取服务对应的评价数
	 * 
	 * @param <T>
	 * @param userId
	 * @return
	 */
	public static <T> Long getTotalCountBytoCommentServiceId(Long userId, Long serviceId, Class<T> entityClass) {
		Long total = (Long) JPA
				.em()
				.createQuery(
						"select count(id) from " + entityClass.getName()
								+ " where toCommentUser.id = :userId and toCommentService.id = :serviceId and parent is null")
				.setParameter("userId", userId).setParameter("serviceId", serviceId).getSingleResult();
		if (total == null) {
			return 0L;
		}
		return total;
	}

	/**
	 * 获取服务所有的评价的总分
	 * 
	 * @param <T>
	 * @param userId
	 * @return
	 */
	public static <T> Long getTotalLevelBytoCommentServiceId(Long userId, Long serviceId, Class<T> entityClass) {
		Long total = (Long) JPA
				.em()
				.createQuery(
						"select sum(level) from " + entityClass.getName()
								+ " where toCommentUser.id = :userId and toCommentService.id = :serviceId and parent is null")
				.setParameter("userId", userId).setParameter("serviceId", serviceId).getSingleResult();
		if (total == null) {
			return 0L;
		}
		return total;
	}

	/**
	 * 后台管理中 - 获取当前页的Comment
	 * 
	 * @param page
	 *            当前页
	 * @param start
	 *            起始记录号
	 * @param limit
	 *            每页显示的行数
	 * @return
	 */
	public static system.vo.Page<system.vo.CommentVO> getAllCommentPage(Integer page, Integer start, Integer limit, String searchText,
			Integer commentLevel) {
		Long total = null;
		List<Comment> comments = null;
		if (commentLevel == -1) {
			if (org.apache.commons.lang.StringUtils.isBlank(searchText)) {
				total = (Long) JPA.em().createQuery("select count(c) from Comment c where c.parent.id is null").getSingleResult();
				comments = JPA.em().createQuery("from Comment c where c.parent.id is null order by c.commentTime desc,c.level desc")
						.setFirstResult(start).setMaxResults(limit).getResultList();
			} else {
				total = (Long) JPA
						.em()
						.createQuery(
								"select count(c) from Comment c where c.parent.id is null and (c.commentUser.userName like :searchText or c.toCommentUser.userName like :searchText or c.content like :searchText)")
						.setParameter("searchText", "%" + searchText + "%").getSingleResult();
				comments = JPA
						.em()
						.createQuery(
								"from Comment c where c.parent.id is null and (c.commentUser.userName like :searchText or c.toCommentUser.userName like :searchText or c.content like :searchText) order by c.commentTime desc,c.level desc")
						.setParameter("searchText", "%" + searchText + "%").setFirstResult(start).setMaxResults(limit).getResultList();
			}
		} else {
			total = (Long) JPA
					.em()
					.createQuery(
							"select count(c) from Comment c where c.parent.id is null and (c.commentUser.userName like :searchText or c.toCommentUser.userName like :searchText or c.content like :searchText) and c.level = :commentLevel")
					.setParameter("searchText", "%" + searchText + "%").setParameter("commentLevel", commentLevel).getSingleResult();
			comments = JPA
					.em()
					.createQuery(
							"from Comment c where c.parent.id is null and (c.commentUser.userName like :searchText or c.toCommentUser.userName like :searchText or c.content like :searchText) and c.level = :commentLevel order by c.commentTime desc,c.level desc")
					.setParameter("searchText", "%" + searchText + "%").setParameter("commentLevel", commentLevel).setFirstResult(start)
					.setMaxResults(limit).getResultList();
		}

		system.vo.Page<system.vo.CommentVO> p = new system.vo.Page<system.vo.CommentVO>();
		p.setTotal(total);

		List<system.vo.CommentVO> commnetVOList = new ArrayList<system.vo.CommentVO>();
		comment2commentVO(comments, commnetVOList);

		p.setData(commnetVOList);
		return p;
	}

	/**
	 * 后台管理中 - 获取当前页的Comment
	 * 
	 * @param commentId
	 *            评论的id
	 * @param page
	 *            当前页
	 * @param start
	 *            起始记录号
	 * @param limit
	 *            每页显示的行数
	 * @return
	 */
	public static system.vo.Page<system.vo.CommentVO> getAllReplyOfCurrentCommentPage(Long commentId, Integer page, Integer start,
			Integer limit) {
		Long total = (Long) JPA.em().createQuery("select count(c) from Comment c where c.parent.id = :parentId")
				.setParameter("parentId", commentId).getSingleResult();
		List<Comment> comments = JPA.em().createQuery("from Comment c where c.parent.id = :parentId order by c.commentTime")
				.setParameter("parentId", commentId).setFirstResult(start).setMaxResults(limit).getResultList();
		system.vo.Page<system.vo.CommentVO> p = new system.vo.Page<system.vo.CommentVO>();
		p.setTotal(total);

		List<system.vo.CommentVO> commnetVOList = new ArrayList<system.vo.CommentVO>();
		comment2commentVO(comments, commnetVOList);

		p.setData(commnetVOList);
		return p;
	}

	/**
	 * Comment PO 转 Comment VO
	 * 
	 * @param comments
	 * @param commnetVOList
	 * @return
	 */
	private static List<system.vo.CommentVO> comment2commentVO(List<Comment> comments, List<system.vo.CommentVO> commnetVOList) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (CollectionUtils.isNotEmpty(comments)) {
			for (Comment comment : comments) {
				Long id = comment.id;
				Integer level = comment.level;
				Long commentUserId = comment.commentUser == null ? 0L : comment.commentUser.getId();
				String commentUserName = comment.commentUser == null ? "" : comment.commentUser.getName();
				String commentTime = "";
				if (comment.commentTime != null) {
					commentTime = dateFormat.format(comment.commentTime);
				}
				Long toCommentUserId = comment.toCommentUser == null ? 0L : comment.toCommentUser.getId();
				String toCommentUserName = comment.toCommentUser == null ? "" : comment.toCommentUser.getName();
				String content = comment.content;

				system.vo.CommentVO vo = new system.vo.CommentVO(id, level, commentTime, commentUserId, commentUserName, toCommentUserId,
						toCommentUserName, content);

				commnetVOList.add(vo);
			}
		}
		return commnetVOList;
	}

	/**
	 * 根据id获取评论或回复
	 * 
	 * @param id
	 * @return
	 */
	public static Comment getCommentByID(Long id) {
		try {
			return (Comment) JPA.em().createQuery("from Comment where id = :ID order by commentTime asc").setParameter("ID", id)
					.getSingleResult();
		} catch (Exception e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("根据id[" + id + "]获取Comment失败", e);
			}
			return null;
		}
	}

	/**
	 * 根据id删除
	 * 
	 * @param id
	 */
	public static void deleteById(Long id) {
		// 删除子Comment
		JPA.em().createQuery("delete from Comment c where c.parent.id = :parentId").setParameter("parentId", id).executeUpdate();
		// 删除该Comment
		JPA.em().createQuery("delete from Comment where id=" + id).executeUpdate();
	}

	/**
	 * 根据id的list删除
	 * 
	 * @param id
	 */
	public static void deleteByIds(List<Long> ids) {
		if (CollectionUtils.isNotEmpty(ids)) {
			// 删除子Comment
			JPA.em().createQuery("delete from Comment c where c.parent.id in (:parentIds)").setParameter("parentIds", ids).executeUpdate();
			// 删除该Comment
			JPA.em().createQuery("delete from Comment where id in (:ids)").setParameter("ids", ids).executeUpdate();
		}
	}

	public static <T> List<Long> queryCommnetByServiceId(Long serviceId, Class<T> entityClass) {
		List<Long> commentIdList = JPA.em()
				.createQuery("select id from " + entityClass.getName() + " where toCommentService.id = :serviceId")
				.setParameter("serviceId", serviceId).getResultList();
		if (CollectionUtils.isNotEmpty(commentIdList)) {
			return commentIdList;
		}
		return new ArrayList<Long>();
	}

}
