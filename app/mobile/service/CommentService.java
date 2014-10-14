/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-5
 */
package mobile.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.util.ServiceResults;
import mobile.vo.comment.CommentDetail;
import mobile.vo.comment.CommentInfo;
import mobile.vo.comment.CommentItem;
import mobile.vo.result.CommonVO;
import mobile.vo.result.MobilePage;
import models.Comment;
import models.Expert;
import models.ExpertComment;
import models.Service;
import models.ServiceComment;
import models.User;
import play.libs.Json;
import play.mvc.Http.Context;
import vo.page.Page;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * @ClassName: CommentService
 * @Description: 评论相关服务
 * @date 2014-6-5 下午4:40:36
 * @author ShenTeng
 * 
 */
public class CommentService {

    /**
     * 获取当前用户的评论信息
     * 
     * @return
     */
    public static CommentInfo getMyCommentInfo() {
        User user = User.getFromSession(Context.current().session());
        return getCommentInfoByUserId(user.id);
    }

    /**
     * 获取指定用户的评论信息
     * 
     * @param userId 用户Id
     * @return
     */
    public static CommentInfo getCommentInfoByUserId(Long userId) {
        Expert expert = Expert.getExpertByUserId(userId);
        return CommentInfo.create(expert);
    }

    /**
     * 获取指定服务的评论信息
     * 
     * @param serviceId 服务Id
     * @return
     */
    public static CommentInfo getCommentInfoByServiceId(Long serviceId) {
        Service service = Service.queryServiceById(serviceId);
        return CommentInfo.create(service);
    }

    /**
     * 获得指定评论的详细
     * 
     * @param commentId 评论Id
     * @return
     */
    public static ServiceVOResult<CommentDetail> getCommentDetail(Long commentId) {
        Comment parent = Comment.getParentCommenById(commentId);
        if (null == parent) {
            return ServiceVOResult.error("220001", "评论不存在");
        }
        // 全部回复
        List<Comment> replys = Comment.getCommentsByParent(parent.id);
        // 获取专家信息
        Expert toCommentExpert = Expert.getExpertByUserId(parent.toCommentUser.id);
        CommentDetail detail = CommentDetail.create(toCommentExpert, parent, replys);

        ServiceVOResult<CommentDetail> result = ServiceVOResult.success();
        result.setVo(detail);

        return result;
    }

    /**
     * 当前用户对指定用户进行评价
     * 
     * @param toCommentUserId 指定用户Id
     * @param content 评价内容
     * @param commentLevel 评价等级
     * @return
     */
    public static ServiceResult commentToUser(Long toCommentUserId, String content, int commentLevel) {
        User toCommentUser = User.findById(toCommentUserId);
        if (null == toCommentUser) {
            return ServiceResults.illegalParameters("用户不存在");
        }
        if (commentLevel <= 0 || commentLevel > 5) {
            return ServiceResults.illegalParameters("评价等级超出范围");
        }

        User user = User.getFromSession(Context.current().session());

        ExpertComment expertComment = new ExpertComment(null, user, toCommentUser, content, new Date(), null,
                commentLevel);
        expertComment.addExpertComment();

        return ServiceResult.success();
    }

    /**
     * 当前用户对指定服务进行评价
     * 
     * @param toCommentServiceId 指定服务Id
     * @param content 评价内容
     * @param commentLevel 评价等级
     * @return
     */
    public static ServiceResult commentToService(Long toCommentServiceId, String content, int commentLevel) {
        Service service = Service.queryServiceById(toCommentServiceId);
        if (null == service) {
            return ServiceResults.illegalParameters("服务不存在");
        }
        if (commentLevel <= 0 || commentLevel > 5) {
            return ServiceResults.illegalParameters("评价等级超出范围");
        }

        User user = User.getFromSession(Context.current().session());

        ServiceComment comment = new ServiceComment(null, user, service.getOwner(), service, content, new Date(), null,
                commentLevel);
        comment.addServiceComment();

        return ServiceResult.success();
    }

    /**
     * 当前用户对指定父评论进行回复
     * 
     * @param parentCommentId 父评论
     * @param content 回复内容
     * @return
     */
    public static ServiceVOResult<CommonVO> reply(Long parentCommentId, String content) {
        User me = User.getFromSession(Context.current().session());
        Comment p = Comment.getParentCommenById(parentCommentId);
        if (null == p) {
            return ServiceVOResult.error("100005", "父评论不存在");
        }

        ObjectNode resultNode = null;
        if (p.toCommentService == null) {
            ExpertComment comment = new ExpertComment(null, me, p.commentUser, content, new java.util.Date(), p, 0);
            comment.addExpertReplay();
            resultNode = Comment.getObjectNode(comment.commentTime, me);
        } else if (p.toCommentService != null) {
            ServiceComment comment = new ServiceComment(null, me, p.commentUser, p.toCommentService, content,
                    new java.util.Date(), p, 0);
            comment.addServiceReplay();
            resultNode = Comment.getObjectNode(comment.commentTime, me);
        }

        ServiceVOResult<CommonVO> serviceVOResult = ServiceVOResult.success();
        CommonVO vo = CommonVO.create();

        ObjectNode commentNode = Json.newObject();
        commentNode.put("commentTime", resultNode.path("commentTime").asText());
        commentNode.put("commentTimeDetail", resultNode.path("commentTimeDetail").asText());
        commentNode.put("headUrl", resultNode.path("headUrl").asText());
        commentNode.put("username", resultNode.path("username").asText());

        vo.set("comment", commentNode);
        serviceVOResult.setVo(vo);

        return serviceVOResult;
    }

    /**
     * 分页获取指定用户的评论列表
     * 
     * @param pageIndex 页数，从1开始。必须
     * @param pageSize 每页条数。必须
     * @param userId 目标用户Id。必须
     * @param levelList 评论等级筛选条件。必须
     * @return
     */
    public static MobilePage<CommentItem> getCommentPageByUserId(int pageIndex, int pageSize, long userId,
            List<Integer> levelList) {
        Comment comment = new Comment();
        Page<Comment> page = comment.getExpertParentComments(pageIndex, pageSize, userId, null, levelList);

        List<CommentItem> commentItemlist = new ArrayList<CommentItem>();
        for (Comment c : page.getList()) {
            commentItemlist.add(CommentItem.create(c));
        }

        MobilePage<CommentItem> result = new MobilePage<CommentItem>(page.getTotalRowCount(), commentItemlist);

        return result;
    }

    /**
     * 分页获取指定服务的评论列表
     * 
     * @param pageIndex 页数，从1开始。必须
     * @param pageSize 每页条数。必须
     * @param serviceId 目标服务Id。必须
     * @param levelList 评论等级筛选条件。必须
     * @return
     */
    public static MobilePage<CommentItem> getCommentPageByServiceId(int pageIndex, int pageSize, long serviceId,
            List<Integer> levelList) {
        Comment comment = new Comment();
        Page<Comment> page = comment.getExpertParentComments(pageIndex, pageSize, null, serviceId, levelList);

        List<CommentItem> commentItemlist = new ArrayList<CommentItem>();
        for (Comment c : page.getList()) {
            commentItemlist.add(CommentItem.create(c));
        }

        MobilePage<CommentItem> result = new MobilePage<CommentItem>(page.getTotalRowCount(), commentItemlist);

        return result;
    }

}
