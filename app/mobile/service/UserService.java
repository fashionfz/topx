/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-4-23
 */
package mobile.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ext.MessageCenter.utils.MCMessageUtil;
import mobile.vo.result.CommonVO;
import mobile.vo.user.*;
import models.Attach;
import models.AttachOfIndustry;
import models.Expert;
import models.SkillTag;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import play.libs.Json;
import play.mvc.Http.Context;
import utils.Assets;
import vo.TopCate;
import vo.TopExpert;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ShenTeng
 * @ClassName: UserService
 * @Description: 用户服务类
 * @date 2014-4-23 上午10:11:03
 */
public class UserService {

    /**
     * 获取指定用户的基本信息
     *
     * @param userId 用户Id
     * @return UserBasicInfo VO
     */
    public static UserBasicInfo getUserBasicInfo(Long userId) {
        models.User user = models.User.findById(userId);
        return UserBasicInfo.create(user);
    }

    /**
     * 获取当前登录用户的基本信息
     *
     * @return UserBasicInfo VO
     */
    public static UserBasicInfo getUserBasicInfo() {
        models.User user = models.User.getFromSession(Context.current().session());
        return UserBasicInfo.create(user);
    }

    /**
     * 获取指定用户的详细信息
     *
     * @param userId 用户Id
     * @return UserBasicInfo VO
     */
    public static UserDetailInfo getUserDetailInfo(Long userId) {
        models.User user = models.User.findById(userId);
        Expert expert = Expert.findByUserId(userId);
        return UserDetailInfo.create(expert, user);
    }

    /**
     * 获取当前登录用户职业经历列表
     *
     * @return
     */
    public static List<JobExp> getJobExpList() {
        models.User user = models.User.getFromSession(Context.current().session());
        Expert newExpert = Expert.findByUserId(user.id);
        return JobExp.createList(newExpert);
    }

    /**
     * 获取当前登录用户教育经历列表
     *
     * @return
     */
    public static List<EducationExp> getEducationExpList() {
        models.User user = models.User.getFromSession(Context.current().session());
        Expert newExpert = Expert.findByUserId(user.id);
        return EducationExp.createList(newExpert);
    }

    /**
     * 获取置顶用户
     *
     * @param size 获取条数
     * @return
     */
    public static List<TopUser> getTopUserList(int size) {
        Set<TopExpert> tops = new LinkedHashSet<>();
        List<TopCate> cates = SkillTag.getTopCateWithCache();
        boolean isContinue = false;
        int counter = 0;
        do {
            isContinue = false;
            for (TopCate topCate : cates) {
                List<TopExpert> topExperts = topCate.getTopExperts();
                if (counter < topExperts.size()) {
                    tops.add(topExperts.get(counter));
                    if (tops.size() >= size) {
                        isContinue = false;
                        break;
                    } else {
                        isContinue = true;
                    }
                }
            }
            counter++;
        } while (isContinue);

        List<TopUser> list = new ArrayList<>();
        for (TopExpert expert : tops) {
            list.add(TopUser.create(expert));
        }

        return list;
    }

    /**
     * 获取用户的在线状态
     *
     * @return 0 - 在线；1 - 不在线
     */
    public static int getOnlineState(Long userId) {
        if (null == userId) {
            throw new IllegalArgumentException("userId can't be null.");
        }

        return MCMessageUtil.whetherOnline(userId) ? 0 : 1;
    }

    /**
     * 获取用户列表
     *
     * @param pageIndex  页数，从1开始，必须
     * @param pageSize   每页条数，必须
     * @param industryId 行业id，传null代表所有行业
     * @param skillsTag  标签名称，传null代表所有标签
     * @param country    国家，传null代表所有国家
     * @param payType    支付类型。0 - 免费，1 - 面议。传null代表所有类型
     * @param gender     性别。0 - 男，1 - 女。传null代表所有性别
     * @param orderBy    排序字段，均为降序排序。comment - 按评价次数排序；averageScore - 按平均评论分排序。传null代表按默认方式排序
     * @return
     */
    public static List<User> getUserList(int pageIndex, int pageSize, Long industryId, String skillsTag,
                                         String country, Integer payType, Integer gender, String orderBy) {
        String payTypeStr = payType == null ? null : Integer.toString(payType);
        String genderStr = gender == null ? null : Integer.toString(gender);

        List<Expert> experts = Expert.getPartExpert(Integer.toString(pageIndex), pageSize, industryId, skillsTag,
                country, null, payTypeStr, genderStr, orderBy, null);

        List<User> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(experts)) {
            for (Expert expert : experts) {
                list.add(User.create(expert));
            }
        }
        return list;
    }

    /**
     * 根据关键字查询技能标签
     *
     * @param keyword 关键字
     * @return
     */
    public static List<String> getSkillTagListByKeyword(String keyword) {
        List<SkillTag> skillTags = null;
        if (StringUtils.isNotBlank(keyword)) {
            skillTags = SkillTag.query(keyword, 10);
        } else {
            skillTags = new ArrayList<SkillTag>(0);
        }

        List<String> tagNameList = new ArrayList<String>(0);
        if (CollectionUtils.isNotEmpty(skillTags)) {
            for (SkillTag tag : skillTags) {
                tagNameList.add(tag.tagName);
            }
        }

        return tagNameList;
    }

    /**
     * 获取所有行业标签
     *
     * @return
     */
    public static List<CommonVO> getIndustryTagList() {
        List<SkillTag> skillTags = SkillTag.getCategoryTag(true);
        List<CommonVO> list = new ArrayList<CommonVO>();

        if (skillTags.size() > 0) {
            for (SkillTag tags : skillTags) {
                CommonVO commonVO = CommonVO.create();
                commonVO.set("id", tags.id);
                commonVO.set("tagName", tags.tagName);

                List<Attach> attachList = new AttachOfIndustry().queryByAttachId(tags.getId());

                ArrayNode arrayNode = Json.newObject().arrayNode();
                for (Attach attach : attachList) {
                    ObjectNode attachNode = Json.newObject();
                    attachNode.put("filename", attach.fileName);
                    attachNode.put("url", Assets.at(attach.path));
                    arrayNode.add(attachNode);
                }
                commonVO.set("attachs", arrayNode);

                list.add(commonVO);
            }
        }

        return list;
    }

    /**
     * 获取行业下的技能标签
     *
     * @param industryId 行业Id
     * @return
     */
    public static List<CommonVO> getSkillTagListByIndustry(Long industryId) {
        List<SkillTag> skillTags = SkillTag.getTagAll(industryId);
        List<CommonVO> list = new ArrayList<CommonVO>();

        if (skillTags.size() > 0) {
            for (SkillTag tags : skillTags) {
                CommonVO commonVO = CommonVO.create();
                commonVO.set("id", tags.id);
                commonVO.set("tagName", tags.tagName);

                list.add(commonVO);
            }
        }

        return list;
    }

    /**
     * 获取在线翻译者
     *
     * @return
     */
    public static List<User> getOnlineTranslatorList() {
        List<User> userList = new ArrayList<User>();

        List<Expert> expertList = Expert.queryCustomerServices("\"嗨啰在线翻译\"", true);
        for (Expert expert : expertList) {
            userList.add(User.create(expert));
        }

        return userList;
    }

}
