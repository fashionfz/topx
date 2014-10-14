/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-1-9
 */
package mobile.vo.comment;

import java.util.Map;

import mobile.vo.MobileVO;
import models.Expert;
import models.ExpertComment;
import models.Service;
import models.ServiceComment;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * 
 * @ClassName: CommentInfo
 * @Description: 评论信息
 * @date 2014-4-8 下午3:52:24
 * @author ShenTeng
 * 
 */
public class CommentInfo implements MobileVO {

    /** 评价次数 **/
    private Long commentNum = 0L;

    /** 平均分数 **/
    private Float averageScore = 0.0f;

    /** 5星评价数 **/
    private Long l5CommentNum = 0L;

    /** 4星评价数 **/
    private Long l4CommentNum = 0L;

    /** 3星评价数 **/
    private Long l3CommentNum = 0L;

    /** 2星评价数 **/
    private Long l2CommentNum = 0L;

    /** 1星评价数 **/
    private Long l1CommentNum = 0L;

    private CommentInfo() {
    };

    public static CommentInfo create(Expert expert) {
        CommentInfo info = new CommentInfo();
        if (null == expert) {
            return info;
        }
        info.setAverageScore(expert.getAverageScoreWithDefault());
        info.setCommentNum(expert.getCommentNumWithDefault());
        ExpertComment comment = new ExpertComment();
        Map<Integer, Long> commentNumByLevel = comment.getTotalRecordByLevel(expert.userId);
        info.setL1CommentNum(commentNumByLevel.get(1));
        info.setL2CommentNum(commentNumByLevel.get(2));
        info.setL3CommentNum(commentNumByLevel.get(3));
        info.setL4CommentNum(commentNumByLevel.get(4));
        info.setL5CommentNum(commentNumByLevel.get(5));

        return info;
    }

    public static CommentInfo create(Service service) {
        CommentInfo info = new CommentInfo();
        if (null == service) {
            return info;
        }
        info.setAverageScore(service.getAverageScore());
        info.setCommentNum(service.getCommentNum());

        ServiceComment comment = new ServiceComment();
        Map<Integer, Long> commentNumByLevel = comment.getTotalRecordByLevel(service.getOwnerId(), service.getId());
        info.setL1CommentNum(commentNumByLevel.get(1));
        info.setL2CommentNum(commentNumByLevel.get(2));
        info.setL3CommentNum(commentNumByLevel.get(3));
        info.setL4CommentNum(commentNumByLevel.get(4));
        info.setL5CommentNum(commentNumByLevel.get(5));

        return info;
    }

    @Override
    public JsonNode toJson() {
        return Json.toJson(this);
    }

    public Long getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Long commentNum) {
        this.commentNum = commentNum;
    }

    public Float getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Float averageScore) {
        this.averageScore = averageScore;
    }

    public Long getL5CommentNum() {
        return l5CommentNum;
    }

    public void setL5CommentNum(Long l5CommentNum) {
        this.l5CommentNum = l5CommentNum;
    }

    public Long getL4CommentNum() {
        return l4CommentNum;
    }

    public void setL4CommentNum(Long l4CommentNum) {
        this.l4CommentNum = l4CommentNum;
    }

    public Long getL3CommentNum() {
        return l3CommentNum;
    }

    public void setL3CommentNum(Long l3CommentNum) {
        this.l3CommentNum = l3CommentNum;
    }

    public Long getL2CommentNum() {
        return l2CommentNum;
    }

    public void setL2CommentNum(Long l2CommentNum) {
        this.l2CommentNum = l2CommentNum;
    }

    public Long getL1CommentNum() {
        return l1CommentNum;
    }

    public void setL1CommentNum(Long l1CommentNum) {
        this.l1CommentNum = l1CommentNum;
    }

}
