/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年8月8日
 */
package mobile.controllers;

import java.io.File;
import java.util.List;

import mobile.service.GroupService;
import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.util.MobileResults;
import mobile.util.jsonparam.JsonParam;
import mobile.util.jsonparam.JsonParamConfig;
import mobile.util.jsonparam.config.item.ArrayItemType;
import mobile.util.jsonparam.config.item.CanEmpty;
import mobile.util.jsonparam.config.item.Require;
import mobile.vo.group.GroupDetailVO;
import mobile.vo.group.GroupMember;
import mobile.vo.group.GroupVO;
import mobile.vo.result.CommonVO;
import mobile.vo.result.MobilePage;
import mobile.vo.result.MobileResult;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Result;

/**
 *
 *
 * @ClassName: GroupApp
 * @Description: 群组相关App
 * @date 2014年8月8日 下午12:24:50
 * @author ShenTeng
 * 
 */
public class GroupApp extends MobileBaseApp {

    @Transactional
    public static Result getGroupMember(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.intField("pageIndex", Require.No);
        config.intField("pageSize", Require.No);
        config.longField("groupId", Require.Yes);
    
        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }
    
        int pageIndex = param.getInt("pageIndex", 1);
        int pageSize = param.getInt("pageSize", 99999);
        if (pageIndex <= 0 || pageSize < 1) {
            return illegalParameters("pageIndex或pageSize超出范围");
        }
    
        MobilePage<GroupMember> page = GroupService.getGroupMemberList(pageIndex, pageSize, param.getLong("groupId"));
        page.setListFieldName("groupMemberList");
    
        MobileResult result = MobileResult.success();
        result.setToRoot(page);
    
        return result.getResult();
    }

    @Transactional
    public static Result createTranslateGroup(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("userId", Require.Yes);
        config.longField("transUserId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<CommonVO> createResult = GroupService.createTranslateGroup(param.getLong("userId"),
                param.getLong("transUserId"));

        MobileResult result = MobileResult.success().setToRoot(createResult);
        return result.getResult();
    }

    @Transactional
    public static Result createNormalGroup(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("groupName", Require.Yes);
        config.longField("industryId", Require.Yes);
        config.stringField("groupInfo", Require.No);
        config.stringField("groupPriv", Require.Yes);
        config.arrayField("skillsTags", Require.Yes, CanEmpty.No, ArrayItemType.String);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = GroupService.createGroup(param.getString("groupName"),
                param.getLong("industryId"), param.getString("groupInfo"), param.getString("groupPriv"),
                param.getStringList("skillsTags"));

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @Transactional
    public static Result createMultiCommunicate(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.arrayField("userIds", Require.Yes, CanEmpty.No, ArrayItemType.Long);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<CommonVO> sResult = GroupService.createMultiCommunicate(param.getLongList("userIds"));

        MobileResult result = MobileResult.success().setToRoot(sResult);
        return result.getResult();
    }

    @Transactional
    public static Result getGroupList(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.intField("pageIndex", Require.Yes);
        config.intField("pageSize", Require.Yes);
        config.longField("industryId", Require.No);
        config.stringField("privacy", Require.No);
        config.stringField("skillsTags", Require.No);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        int pageIndex = param.getInt("pageIndex");
        int pageSize = param.getInt("pageSize");
        if (pageIndex <= 0 || pageSize < 1 || pageSize > 30) {
            return illegalParameters("pageIndex或pageSize超出范围");
        }

        MobilePage<GroupVO> page = GroupService.getGroupPage(pageIndex, pageSize, param.getLong("industryId"),
                param.getString("privacy"), param.getString("skillsTags"));

        return MobileResult.success().setToRoot(page).getResult();
    }

    @Transactional
    public static Result getGroupDetail(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("groupId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<GroupDetailVO> serviceVOResult = GroupService.getGroupDetail(param.getLong("groupId"));

        return MobileResult.success().setToField("groupDetail", serviceVOResult).getResult();
    }

    @Transactional
    public static Result getMyGroupList(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.intField("pageIndex", Require.No);
        config.intField("pageSize", Require.No);
        config.arrayField("types", Require.Yes, CanEmpty.No, ArrayItemType.String);
        config.booleanField("isOwner", Require.No);
        config.stringField("groupName", Require.No);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        Integer pageIndex = param.getInt("pageIndex");
        Integer pageSize = param.getInt("pageSize");
        if (pageIndex == null || pageSize == null) {
            pageIndex = 1;
            pageSize = Integer.MAX_VALUE;
        } else if (pageIndex <= 0 || pageSize < 1 || pageSize > 30) {
            return illegalParameters("pageIndex或pageSize超出范围");
        }

        MobilePage<GroupVO> myGroupPage = GroupService.getMyGroupPage(pageIndex, pageSize,
                param.getStringList("types"), param.getBoolean("isOwner"), param.getString("groupName"));

        return MobileResult.success().setToRoot(myGroupPage).getResult();
    }

    @Transactional
    public static Result updateNormalGroup(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("groupId", Require.Yes);
        config.stringField("groupName", Require.No);
        config.longField("industryId", Require.No);
        config.stringField("groupInfo", Require.No);
        config.stringField("groupPriv", Require.No);
        config.arrayField("skillsTags", Require.No, CanEmpty.No, ArrayItemType.String);
        config.lessRequired(2);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = GroupService.updateGroup(param.getLong("groupId"), param.getString("groupName"),
                param.getLong("industryId"), param.getString("groupInfo"), param.getString("groupPriv"),
                param.getStringList("skillsTags"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    @BodyParser.Of(value = BodyParser.Raw.class, maxLength = 2 * 1024 * 1024)
    public static Result updateGroupAvatar(String from, Long groupId) {
        // 文件大小不能超过2M
        if (request().body().isMaxSizeExceeded()) {
            return MobileResult.error("100005", "图片大小不能超过2M").getResult();
        }

        File avatarFile = request().body().asRaw().asFile();
        if (null == avatarFile || avatarFile.length() <= 0) {
            return MobileResult.error("100005", "图片大小必须大于0").getResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = GroupService.updateGroupAvatar(groupId, avatarFile);

        return MobileResult.success().setToRoot(serviceVOResult).getResult();
    }

    @Transactional
    public static Result inviteJoinGroup(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("groupId", Require.Yes);
        config.arrayField("userIds", Require.Yes, CanEmpty.No, ArrayItemType.Long);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = GroupService.inviteJoinGroup(param.getLong("groupId"),
                param.getLongList("userIds"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result agreeJoinGroupInvite(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("messageId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = GroupService.agreeJoinGroupInvite(param.getLong("messageId"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result joinPublicGroup(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("groupId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = GroupService.joinPublicGroup(param.getLong("groupId"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result applyJoinGroup(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("groupId", Require.Yes);
        config.stringField("content", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = GroupService.applyJoinGroup(param.getLong("groupId"), param.getString("content"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result agreeJoinGroupApply(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("messageId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = GroupService.agreeJoinGroupApply(param.getLong("messageId"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result removeGroupMember(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("groupId", Require.Yes);
        config.arrayField("userIds", Require.Yes, CanEmpty.No, ArrayItemType.Long);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        List<Long> userIds = param.getLongList("userIds");
        if (userIds.size() > 50) {
            return MobileResults.illegalParameters("单次移除数量超过限制").getResult();
        }

        ServiceResult serviceResult = GroupService.removeGroupMember(param.getLong("groupId"), userIds);

        return MobileResult.success().setToRoot(serviceResult).getResult();

    }

    @Transactional
    public static Result quitGroup(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("groupId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = GroupService.quitGroup(param.getLong("groupId"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result deleteGroup(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("groupId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = GroupService.deleteGroup(param.getLong("groupId"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result updateGroupName(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("groupId", Require.Yes);
        config.stringField("newGroupName", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = GroupService.updateGroupName(param.getLong("groupId"),
                param.getString("newGroupName"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result inviteJoinMultiCommunicate(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("groupId", Require.Yes);
        config.arrayField("userIds", Require.Yes, CanEmpty.No, ArrayItemType.Long);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = GroupService.inviteJoinMultiCommunicate(param.getLong("groupId"),
                param.getLongList("userIds"));

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

}
