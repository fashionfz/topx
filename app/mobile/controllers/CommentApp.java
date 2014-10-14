/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-1-10
 */
package mobile.controllers;

import mobile.service.CommentService;
import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.util.jsonparam.JsonParam;
import mobile.util.jsonparam.JsonParamConfig;
import mobile.util.jsonparam.config.item.ArrayItemType;
import mobile.util.jsonparam.config.item.CanEmpty;
import mobile.util.jsonparam.config.item.Require;
import mobile.vo.comment.CommentDetail;
import mobile.vo.comment.CommentInfo;
import mobile.vo.comment.CommentItem;
import mobile.vo.result.CommonVO;
import mobile.vo.result.MobilePage;
import mobile.vo.result.MobileResult;
import play.db.jpa.Transactional;
import play.mvc.Result;

/**
 * 
 * 
 * @ClassName: CommentApp
 * @Description: 评论相关接口
 * @date 2014-1-10 下午6:26:12
 * @author ShenTeng
 * 
 */
public class CommentApp extends MobileBaseApp {

    @Transactional
    public static Result getCommentInfo(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("userId", Require.No);
        config.longField("serviceId", Require.No);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        Long userId = param.getLong("userId");
        Long serviceId = param.getLong("serviceId");
        if ((null == userId && null == serviceId) || (null != userId && null != serviceId)) {
            return illegalParameters("userId、serviceId能且只能使用一个");
        }

        CommentInfo commentInfo = null;
        if (null != userId) {
            commentInfo = CommentService.getCommentInfoByUserId(userId);
        } else {
            commentInfo = CommentService.getCommentInfoByServiceId(serviceId);
        }

        MobileResult result = MobileResult.success().setToField("commentInfo", commentInfo);
        return result.getResult();
    }

    /**
     * 专家详细页面评论列表
     * 
     * @return
     */
    @Transactional
    public static Result getCommentList(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.intField("pageIndex", Require.Yes);
        config.intField("pageSize", Require.Yes);
        config.longField("userId", Require.No);
        config.longField("serviceId", Require.No);
        config.arrayField("level", Require.Yes, CanEmpty.Yes, ArrayItemType.Int);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        Integer pageIndex = param.getInt("pageIndex");
        Integer pageSize = param.getInt("pageSize");
        if (pageSize < 0 || pageSize > 20 || pageIndex <= 0) {
            return illegalParameters("pageSize 或 pageIndex超出范围");
        }

        Long userId = param.getLong("userId");
        Long serviceId = param.getLong("serviceId");
        if ((null == userId && null == serviceId) || (null != userId && null != serviceId)) {
            return illegalParameters("userId、serviceId能且只能使用一个");
        }

        MobilePage<CommentItem> commentPage = null;
        if (null != userId) {
            commentPage = CommentService.getCommentPageByUserId(pageIndex, pageSize, userId, param.getIntList("level"));
        } else {
            commentPage = CommentService.getCommentPageByServiceId(pageIndex, pageSize, serviceId,
                    param.getIntList("level"));
        }

        commentPage.setTotalFieldName("total");
        commentPage.setListFieldName("comments");

        MobileResult result = MobileResult.success().setToRoot(commentPage);

        return result.getResult();
    }

    @Transactional
    public static Result getCommentDetail(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("commentId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<CommentDetail> serviceVOResult = CommentService.getCommentDetail(param.getLong("commentId"));

        return MobileResult.success().setToField("commentDetail", serviceVOResult).getResult();
    }

    /**
     * 回复(专家或者用户)
     */
    @Transactional
    public static Result reply(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("parentCommentId", Require.Yes);
        config.stringField("content", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = CommentService.reply(param.getLong("parentCommentId"),
                param.getString("content"));

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    /**
     * 发布评论
     * 
     * @throws Exception
     */
    @Transactional
    public static Result comment(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("toCommentServiceId", Require.No);
        config.longField("toCommentUserId", Require.No);
        config.stringField("content", Require.Yes);
        config.intField("level", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        Long toCommentUserId = param.getLong("toCommentUserId");
        Long toCommentServiceId = param.getLong("toCommentServiceId");
        if ((null == toCommentUserId && null == toCommentServiceId)
                || (null != toCommentUserId && null != toCommentServiceId)) {
            return illegalParameters("toCommentUserId、toCommentServiceId能且只能使用一个");
        }

        ServiceResult serviceResult = null;
        if (null != toCommentUserId) {
            serviceResult = CommentService.commentToUser(toCommentUserId, param.getString("content"),
                    param.getInt("level"));
        } else {
            serviceResult = CommentService.commentToService(toCommentServiceId, param.getString("content"),
                    param.getInt("level"));
        }

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }
}
