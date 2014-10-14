/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年8月8日
 */
package mobile.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.base.ObjectNodeResult;
import controllers.user.UserGroupsApp;
import exception.AvatarException;
import ext.msg.model.Message;
import ext.msg.model.Message.MsgType;
import ext.msg.model.service.MessageService;
import ext.usercenter.UCClient;
import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.vo.group.GroupDetailVO;
import mobile.vo.group.GroupMember;
import mobile.vo.group.GroupVO;
import mobile.vo.result.CommonVO;
import mobile.vo.result.MobilePage;
import models.Group;
import models.Group.GroupPriv;
import models.Group.Type;
import models.SkillTag;
import models.User;
import models.service.ChatService;
import models.service.ChatService.CreateTranslateGroupResult;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.util.CollectionUtils;
import play.Logger;
import play.Logger.ALogger;
import play.db.jpa.JPA;
import play.libs.Json;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import utils.Assets;
import vo.GroupMemberVO;
import vo.page.Page;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author ShenTeng
 * @ClassName: GroupService
 * @Description: 群组相关的服务
 * @date 2014年8月8日 下午2:39:53
 */
public class GroupService {

    private static final ALogger LOGGER = Logger.of(GroupService.class);

    /**
     * 创建翻译群组
     *
     * @param userId       聊天对方用户Id，必须
     * @param translatorId 翻译者用户Id，必须
     * @return
     */
    public static ServiceVOResult<CommonVO> createTranslateGroup(Long userId, Long translatorId) {
        Session session = Context.current().session();
        User me = User.getFromSession(session);
        User you = User.findById(userId);
        User translator = User.findById(translatorId);
        if (you == null) { // 从用户中心获取
            you = UCClient.queryUserById(userId);
        }
        if (translator == null) { // 从用户中心获取
            translator = UCClient.queryUserById(translatorId);
        }

        if (you == null || translator == null) {
            return ServiceVOResult.error("100005", "添加的用户或翻译者不存在");
        }
        if (you.getId() - translator.getId() == 0) {
            return ServiceVOResult.error("100005", "与自己聊天的用户和翻译者不能是同一个人");
        }
        if (me.getId().equals(userId) || me.getId().equals(translatorId)) {
            return ServiceVOResult.error("100005", "自己不能和自己聊天、自己不能作为翻译者");
        }

        CreateTranslateGroupResult result;
        try {
            result = models.service.ChatService.createTranslateGroup(me, you, translator);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!result.isExist()) {
            CommonVO vo = CommonVO.create();
            vo.set("groupId", result.getGroup().getId());
            vo.set("groupName", result.getGroup().getGroupName());

            ServiceVOResult<CommonVO> serviceVOResult = ServiceVOResult.success();
            serviceVOResult.setVo(vo);
            return serviceVOResult;
        } else {
            return ServiceVOResult.error("285001", "已经添加用户到同一个组了，请不要重复添加");
        }
    }

    /**
     * 创建翻译群组
     *
     * @param userIdList 用户Id列表
     * @return
     */
    public static ServiceVOResult<CommonVO> createMultiCommunicate(List<Long> userIdList) {
        User me = User.getFromSession(Context.current().session());

        if (userIdList.contains(me.getId())) {
            return ServiceVOResult.error("100005", "邀请的人不能够包含自己");
        }

        ObjectNodeResult objectNodeResult = null;
        try {
            objectNodeResult = ChatService.createMultiCommunicateGroup(me, userIdList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!objectNodeResult.isSuccess()) {
            Logger.error(objectNodeResult.getObjectNode().toString());
            return ServiceVOResult.error("100001", "系统错误");
        }

        CommonVO vo = CommonVO.create();
        vo.set("groupId", objectNodeResult.getObjectNode().path("groupId").asLong());
        vo.set("groupName", objectNodeResult.getObjectNode().path("groupName").asText());

        return ServiceVOResult.success(vo);
    }

    /**
     * 获取群组成员
     *
     * @param pageIndex 页数，从1开始。必须
     * @param pageSize  每页条数。必须
     * @param groupId   群组Id。必须
     * @return
     */
    public static MobilePage<GroupMember> getGroupMemberList(int pageIndex, int pageSize, Long groupId) {
        Page<GroupMemberVO> page = models.service.ChatService
                .queryGroupMemberByPage(pageIndex, pageSize, groupId, null);

        MobilePage<GroupMember> mobilePage = new MobilePage<GroupMember>(page.getTotalRowCount(),
                GroupMember.createList(page.getList()));

        return mobilePage;
    }

    /**
     * 分页查询群组
     *
     * @param pageIndex  页数，从1开始，必须
     * @param pageSize   每页条数，必须
     * @param industryId 行业Id，非必须
     * @param privacy    群组权限，非必须
     * @param skillTag   技能标签，非必须
     * @return Page对象
     */
    public static MobilePage<GroupVO> getGroupPage(int pageIndex, int pageSize, Long industryId, String privacy,
                                                   String skillTag) {
        GroupPriv groupPriv = GroupPriv.getByName(privacy);
        Page<vo.GroupVO> groupPage = ChatService.queryGroupByPage(true, pageIndex, pageSize, industryId, groupPriv,
                skillTag, null, null);

        List<GroupVO> list = new ArrayList<GroupVO>(groupPage.getList().size());
        for (vo.GroupVO source : groupPage.getList()) {
            list.add(GroupVO.create(source));
        }

        MobilePage<GroupVO> page = new MobilePage<GroupVO>(groupPage.getTotalRowCount(), list);
        return page;
    }

    /**
     * 获取群组详细
     *
     * @param groupId 群组Id，必须
     * @return
     */
    public static ServiceVOResult<GroupDetailVO> getGroupDetail(Long groupId) {
        Group group = Group.queryGroupById(groupId);
        if (null == group) {
            return ServiceVOResult.error("2001", "群组不存在");
        }

        vo.GroupVO webVo = new vo.GroupVO();
        webVo.convert(group);

        GroupDetailVO groupDetailVO = GroupDetailVO.create(webVo);

        return ServiceVOResult.success(groupDetailVO);
    }

    /**
     * 分页获取我的群组
     *
     * @param pageIndex   页数，从1开始。必须
     * @param pageSize    每页条数。必须
     * @param typeStrList 群组类型集合， NORMAL - 普通群组，TRANSLATE - 翻译群组，MULTICOMMUNICATE - 多人会话。必须
     * @param isOwner     是否为群主（仅对普通群组有效），非必须
     * @return
     */
    public static MobilePage<GroupVO> getMyGroupPage(int pageIndex, int pageSize, List<String> typeStrList,
                                                     Boolean isOwner, String groupName) {
        User me = User.getFromSession(Context.current().session());

        List<Type> typeList = new ArrayList<Group.Type>();
        for (String typeStr : typeStrList) {
            Type type = Type.getByName(typeStr);
            if (null != type) {
                typeList.add(type);
            }
        }

        Page<Group> poPage = Group.queryByPage(pageIndex, pageSize, null, null, null, groupName, typeList, me.getId(),
                isOwner, "id", true);

        List<GroupVO> list = new ArrayList<GroupVO>();
        for (Group po : poPage.getList()) {
            vo.GroupVO webVO = new vo.GroupVO();
            webVO.convert(po);
            GroupVO vo = GroupVO.create(webVO);
            vo.setIsJoin(true);
            list.add(vo);
        }

        MobilePage<GroupVO> page = new MobilePage<GroupVO>(poPage.getTotalRowCount(), list);
        return page;
    }

    /**
     * 创建群组
     *
     * @param groupName  群组名称，必须
     * @param industryId 群组所属行业Id，必须
     * @param groupInfo  群组说明，非必须
     * @param groupPriv  群组权限设置，必须
     * @param skillsTags 群组标签，必须
     * @return
     */
    public static ServiceVOResult<CommonVO> createGroup(String groupName, Long industryId, String groupInfo,
                                                        String groupPriv, List<String> skillsTags) {
        GroupPriv priv = GroupPriv.getByName(groupPriv);
        if (null == priv) {
            return ServiceVOResult.error("100005", "未知的群组权限：" + groupPriv);
        }
        if (StringUtils.isBlank(groupName)) {
            return ServiceVOResult.error("100005", "群组名不能为空");
        }
        SkillTag tag = SkillTag.getTagById(industryId);
        if (null == tag || tag.getTagType() != SkillTag.TagType.CATEGORY) {
            return ServiceVOResult.error("100005", "未知的群组所属行业Id：" + industryId);
        }

        ObjectNode data = Json.newObject();
        data.put("groupName", groupName);
        data.put("industry", industryId);
        data.put("groupPriv", priv.ordinal());
        data.put("groupInfo", groupInfo);
        data.set("tags", Json.toJson(skillsTags));

        User me = User.getFromSession(Context.current().session());

        ObjectNodeResult objectNodeResult = Group.saveGroupByJson(me, data);

        if (!objectNodeResult.isSuccess()) {
            if ("900003".equals(objectNodeResult.getErrorCode())) {
                return ServiceVOResult.error("294001", objectNodeResult.getError());
            }
            Logger.error(objectNodeResult.getObjectNode().toString());
            return ServiceVOResult.error("100001", "系统错误");
        }

        CommonVO vo = CommonVO.create();
        vo.set("groupId", objectNodeResult.getObjectNode().path("groupId").asLong(-1));

        return ServiceVOResult.success(vo);
    }

    /**
     * 更新我的群组
     *
     * @param groupId    群组Id，必须
     * @param groupName  群组名称，非必须
     * @param industryId 群组所属行业Id，非必须
     * @param groupInfo  群组说明，非必须
     * @param groupPriv  群组权限设置，非必须
     * @param skillsTags 群组标签，非必须
     * @return
     */
    public static ServiceResult updateGroup(Long groupId, String groupName, Long industryId, String groupInfo,
                                            String groupPriv, List<String> skillsTags) {
        ObjectNode data = Json.newObject();
        User me = User.getFromSession(Context.current().session());

        Group group = Group.queryGroupById(groupId);
        if (null != group && group.getType() != Group.Type.NORMAL) {
            return ServiceResult.error("100005", "该接口只能用于普通群组");
        }
        if (null == group) {
            return ServiceResult.error("2001", "群组不存在");
        } else if (!Objects.equals(group.getOwner().getUserId(), me.getId())) {
            return ServiceResult.error("2002", "你不是群组所有者，操作失败");
        }
        data.put("id", groupId);

        if (null != groupName) {
            if (StringUtils.isBlank(groupName)) {
                return ServiceResult.error("100005", "群组名不能为空字符串");
            }
            data.put("groupName", groupName);
        }

        if (null != industryId) {
            SkillTag tag = SkillTag.getTagById(industryId);
            if (null == tag || tag.getTagType() != SkillTag.TagType.CATEGORY) {
                return ServiceResult.error("100005", "未知的群组所属行业Id：" + industryId);
            }
            data.put("industry", industryId);
        }

        if (null != groupPriv) {
            GroupPriv priv = GroupPriv.getByName(groupPriv);
            if (null == priv) {
                return ServiceResult.error("100005", "未知的群组权限：" + groupPriv);
            }

            data.put("groupPriv", priv.ordinal());
        }

        if (null != skillsTags) {
            data.set("tags", Json.toJson(skillsTags));
        }

        if (null != groupInfo) {
            data.put("groupInfo", groupInfo);
        }

        ObjectNodeResult objectNodeResult = Group.saveGroupByJson(me, data);

        if (!objectNodeResult.isSuccess()) {
            if ("900003".equals(objectNodeResult.getErrorCode())) {
                return ServiceResult.error("295001", objectNodeResult.getError());
            }
            Logger.error(objectNodeResult.getObjectNode().toString());
            return ServiceResult.error("100001", "系统错误");
        }

        return ServiceResult.success();
    }

    /**
     * 上传群组头像
     *
     * @param groupId    群组Id，必须
     * @param avatarFile 头像文件，必须，length>0
     * @return
     */
    public static ServiceVOResult<CommonVO> updateGroupAvatar(Long groupId, File avatarFile) {
        if (null == groupId || null == avatarFile || avatarFile.length() <= 0) {
            throw new IllegalArgumentException("groupId、avatarFile is null or avatarFile.length() <= 0. groupId = "
                    + groupId + ", avatarFile = " + avatarFile);
        }

        User me = User.getFromSession(Context.current().session());
        Group group = Group.queryGroupById(groupId);
        if (null != group && group.getType() != Group.Type.NORMAL) {
            return ServiceVOResult.error("100005", "该接口只能用于普通群组");
        }
        if (null == group) {
            return ServiceVOResult.error("2001", "群组不存在");
        } else if (!Objects.equals(group.getOwner().getUserId(), me.getId())) {
            return ServiceVOResult.error("2002", "你不是群组所有者，操作失败");
        }

        ObjectNodeResult nodeResult = new ObjectNodeResult();
        try {
            UserGroupsApp.save(avatarFile, nodeResult, me);
        } catch (AvatarException e) {
            LOGGER.error("上传群组头像失败", e);
            return ServiceVOResult.error("100001", "上传群组头像失败");
        }

        String headUrl = nodeResult.getObjectNode().path("avatar_190_source").asText();
        ObjectNode data = Json.newObject();
        data.put("headUrl", headUrl);
        data.put("id", groupId);

        ObjectNodeResult objectNodeResult = Group.saveGroupByJson(me, data);

        if (!objectNodeResult.isSuccess()) {
            Logger.error(objectNodeResult.getObjectNode().toString());
            return ServiceVOResult.error("100001", "系统错误");
        }

        CommonVO vo = CommonVO.create();
        vo.set("headUrl", Assets.at(headUrl));

        ServiceVOResult<CommonVO> result = ServiceVOResult.success();
        result.setVo(vo);

        return result;
    }

    /**
     * 邀请加入群组<br>
     * 该接口只能用于普通群组
     *
     * @param groupId    群组Id，必须
     * @param userIdList 要求用户Id，必须，size>0
     * @return
     */
    public static ServiceResult inviteJoinGroup(Long groupId, List<Long> userIdList) {
        if (null == groupId || CollectionUtils.isEmpty(userIdList)) {
            throw new IllegalArgumentException("groupId is null or userIdList is empty. groupId = " + groupId
                    + ", userIdList = " + userIdList);
        }

        User me = User.getFromSession(Context.current().session());
        Group group = Group.queryGroupById(groupId);
        if (null != group && group.getType() != Group.Type.NORMAL) {
            return ServiceResult.error("100005", "该接口只能用于普通群组");
        }
        if (null == group) {
            return ServiceResult.error("2001", "群组不存在");
        }
        List<Long> groupIdList = Arrays.asList(groupId);
        Map<Long, Boolean> checkJoinGroup = models.GroupMember.checkJoinGroup(me.getId(), groupIdList);
        if (checkJoinGroup.get(groupId) != true) {
            return ServiceResult.error("297001", "你还没有加入或已被踢出群组，不能进行邀请");
        }

        Map<Long, Boolean> checkJoinGroupUser = models.GroupMember.checkJoinGroup(userIdList, groupId);

        for (Map.Entry<Long, Boolean> e : checkJoinGroupUser.entrySet()) {
            if (!e.getValue()) {
                User recevierUser = User.findById(e.getKey());
                if (null != recevierUser) {
                    MessageService.pushMsgInvitMember(me, recevierUser, group);
                }
            }
        }

        return ServiceResult.success();
    }

    /**
     * 邀请加入多人会话<br>
     * 该接口只能用于多人会话
     *
     * @param groupId    群组Id，必须
     * @param userIdList 要求用户Id，必须，size>0
     * @return
     */
    public static ServiceResult inviteJoinMultiCommunicate(Long groupId, List<Long> userIdList) {
        if (null == groupId || CollectionUtils.isEmpty(userIdList)) {
            throw new IllegalArgumentException("groupId is null or userIdList is empty. groupId = " + groupId
                    + ", userIdList = " + userIdList);
        }

        User me = User.getFromSession(Context.current().session());
        Group group = Group.queryGroupById(groupId);
        if (null != group && group.getType() != Group.Type.MULTICOMMUNICATE) {
            return ServiceResult.error("100005", "该接口只能用于多人会话");
        }
        if (null == group) {
            return ServiceResult.error("2001", "群组不存在");
        }
        List<Long> groupIdList = Arrays.asList(groupId);
        Map<Long, Boolean> checkJoinGroup = models.GroupMember.checkJoinGroup(me.getId(), groupIdList);
        if (checkJoinGroup.get(groupId) != true) {
            return ServiceResult.error("309001", "你没有加入该多人会话，不能进行邀请");
        }

        for (Long userId : userIdList) {
            Map<Long, Boolean> isJoinGroup = models.GroupMember.checkJoinGroup(userId, Arrays.asList(groupId));
            if (!isJoinGroup.get(groupId)) {
                try {
                    ChatService.appendMemberToGroup(groupId, userId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return ServiceResult.success();
    }

    /**
     * 同意加入群组邀请
     *
     * @param messageId 消息Id
     * @return
     */
    public static ServiceResult agreeJoinGroupInvite(Long messageId) {
        User me = User.getFromSession(Context.current().session());

        Message message = Message.queryById(messageId);
        if (null == message || message.msgType != MsgType.INVIT_GROUP_MEMBER
                || !Objects.equals(message.consumeOnly, me.id.toString())) {
            return ServiceResult.error("100005", "非法的messageId = " + messageId);
        }


        User receiverUser = User.findById(NumberUtils.toLong(message.consumeOnly, -1));
        if (null == receiverUser) {
            LOGGER.error("invalid receiverId. message.content = " + message.content);
            return ServiceResult.error("100001", "系统错误");
        }

        JsonNode content = Json.parse(message.content);

        long groupId = content.get("groupId").asLong(-1);
        Group group = Group.queryGroupById(groupId);
        if (null == group) {
            LOGGER.error("invalid groupId. message.content = " + message.content);
            return ServiceResult.error("2001", "群组不存在");
        }
        List<Long> groupIdList = Arrays.asList(groupId);
        Map<Long, Boolean> checkJoinGroup = models.GroupMember.checkJoinGroup(receiverUser.getId(), groupIdList);
        if (checkJoinGroup.get(groupId) == true) {
            MessageService.handlerMessage(messageId);
            return ServiceResult.error("2003", "你已经是该群组成员");
        }

        User senderUser = User.findById(NumberUtils.toLong(message.senderOnly, -1));
        if (null == senderUser) {
            LOGGER.error("invalid senderId. message.content = " + message.content);
            return ServiceResult.error("100001", "系统错误");
        }

        try {
            ChatService.appendMemberToGroup(groupId, receiverUser.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MessageService.pushMsgInvitAgree(me, senderUser, group);
        MessageService.handlerMessage(messageId);
        return ServiceResult.success();
    }

    /**
     * 加入公开群组
     *
     * @param groupId 群组Id
     * @return
     */
    public static ServiceResult joinPublicGroup(Long groupId) {
        User me = User.getFromSession(Context.current().session());
        Group group = Group.queryGroupById(groupId);
        if (null != group && group.getType() != Group.Type.NORMAL) {
            return ServiceResult.error("100005", "该接口只能用于普通群组");
        }
        if (null == group) {
            return ServiceResult.error("2001", "群组不存在");
        }
        if (group.getGroupPriv() != GroupPriv.PUBLIC) {
            return ServiceResult.error("299002", "该群组需要申请才能加入");
        }
        List<Long> groupIdList = Arrays.asList(groupId);
        Map<Long, Boolean> checkJoinGroup = models.GroupMember.checkJoinGroup(me.getId(), groupIdList);
        if (checkJoinGroup.get(groupId) == true) {
            return ServiceResult.error("299001", "你已经是该群组成员，无需加入");
        }

        ObjectNodeResult result;
        try {
            result = models.service.ChatService.appendMemberToGroup(groupId, me.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ServiceResult.create(result);
    }

    /**
     * 申请加入非公开群组
     *
     * @param groupId 群组Id
     * @param content 内容，<=250个字符
     * @return
     */
    public static ServiceResult applyJoinGroup(Long groupId, String content) {
        if (content.length() > 250) {
            return ServiceResult.error("100005", "内容超过长度限制");
        }

        User me = User.getFromSession(Context.current().session());
        Group group = Group.queryGroupById(groupId);
        if (null != group && group.getType() != Group.Type.NORMAL) {
            return ServiceResult.error("100005", "该接口只能用于普通群组");
        }
        if (null == group) {
            return ServiceResult.error("2001", "群组不存在");
        }
        List<Long> groupIdList = Arrays.asList(groupId);
        Map<Long, Boolean> checkJoinGroup = models.GroupMember.checkJoinGroup(me.getId(), groupIdList);
        if (checkJoinGroup.get(groupId) == true) {
            return ServiceResult.error("300001", "你已经是该群组成员，无需加入");
        }
        User recevierUser = User.findById(group.owner.userId);
        MessageService.pushMsgApply(me, recevierUser, group, content);
        return ServiceResult.success();
    }

    /**
     * 移除群组成员
     *
     * @param groupId 群组Id，必须
     * @param userIds 待移除成员用户Id集合，必须
     * @return
     */
    public static ServiceResult removeGroupMember(Long groupId, List<Long> userIds) {
        User me = User.getFromSession(Context.current().session());

        Group group = Group.queryGroupById(groupId);
        if (null != group && group.getType() != Group.Type.NORMAL) {
            return ServiceResult.error("100005", "该接口只能用于普通群组");
        }
        if (null == group) {
            return ServiceResult.error("2001", "群组不存在");
        } else if (!Objects.equals(group.getOwner().getUserId(), me.getId())) {
            return ServiceResult.error("2002", "你不是群组所有者，操作失败");
        }

        JPA.em().clear();

        for (Long userId : userIds) {
            User user = User.findById(userId);
            if (null != user) {
                ObjectNodeResult objectNodeResult = ChatService.deleteMemberFromGroup(user, groupId, 1);
                if (!objectNodeResult.isSuccess() && !"900011".equals(objectNodeResult.getErrorCode())) {
                    Logger.error(objectNodeResult.getObjectNode().toString());
                    return ServiceResult.error("100001", "系统错误");

                }
            }
        }

        return ServiceResult.success();
    }

    /**
     * 退出群组<br>
     * 需要登录
     *
     * @param groupId 群组Id，必须
     * @return
     */
    public static ServiceResult quitGroup(Long groupId) {
        User me = User.getFromSession(Context.current().session());

        ObjectNodeResult objectNodeResult = ChatService.deleteMemberFromGroup(me, groupId, 2);

        if (!objectNodeResult.isSuccess()) {
            switch (objectNodeResult.getErrorCode()) {
                case "900010":
                    return ServiceResult.error("100005", "未知的群组，groupId = " + groupId);
                case "900011":
                    return ServiceResult.error("303001", "该用户已不在该群组中");
                case "900105":
                    return ServiceResult.error("303002", "群主不能退出自己建立的群组");
                default:
                    Logger.error(objectNodeResult.getObjectNode().toString());
                    return ServiceResult.error("100001", "系统错误");
            }

        }

        return ServiceResult.success();
    }

    /**
     * 解散群组
     *
     * @param groupId 群组Id
     * @return
     */
    public static ServiceResult deleteGroup(Long groupId) {
        User me = User.getFromSession(Context.current().session());

        ObjectNodeResult objectNodeResult = ChatService.deleteGroup(me, groupId);

        if (!objectNodeResult.isSuccess()) {
            switch (objectNodeResult.getErrorCode()) {
                case "900103":
                    return ServiceResult.error("100005", "未知的群组，groupId = " + groupId);
                case "900104":
                    return ServiceResult.error("100005", "该接口只能用于普通群组");
                case "900105":
                    return ServiceResult.error("304001", "你没有权限解散群组");
                default:
                    Logger.error(objectNodeResult.getObjectNode().toString());
                    return ServiceResult.error("100001", "系统错误");
            }

        }

        return ServiceResult.success();
    }

    /**
     * 同意加入群组申请
     *
     * @param messageId 消息Id
     * @return
     */
    public static ServiceResult agreeJoinGroupApply(Long messageId) {
        User me = User.getFromSession(Context.current().session());

        Message message = Message.queryById(messageId);
        if (null == message || message.msgType != MsgType.APPLY_GROUP
                || !Objects.equals(message.consumeOnly, me.id.toString())) {
            return ServiceResult.error("100005", "非法的messageId = " + messageId);
        }

        User senderUser = User.findById(NumberUtils.toLong(message.senderOnly, -1));
        if (null == senderUser) {
            LOGGER.error("invalid senderId. message.content = " + message.content);
            return ServiceResult.error("100001", "系统错误");
        }

        JsonNode content = Json.parse(message.content);

        long groupId = content.get("groupId").asLong(-1);
        Group group = Group.queryGroupById(groupId);
        if (null == group) {
            return ServiceResult.error("2001", "群组不存在");
        }
        List<Long> groupIdList = Arrays.asList(groupId);
        Map<Long, Boolean> checkJoinGroup = models.GroupMember.checkJoinGroup(senderUser.getId(), groupIdList);
        if (checkJoinGroup.get(groupId) == true) {
            MessageService.handlerMessage(messageId);
            return ServiceResult.error("2004", "该用户已经是该群组成员");
        }


        try {
            ChatService.appendMemberToGroup(groupId, senderUser.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MessageService.pushMsgApplyAgree(me, senderUser, group);
        MessageService.handlerMessage(messageId);
        return ServiceResult.success();
    }

    /**
     * 更新群组名称，只适用于翻译群和多人会话
     *
     * @param groupId      群组Id
     * @param newGroupName 新群组名称
     * @return
     */
    public static ServiceResult updateGroupName(Long groupId, String newGroupName) {
        User me = User.getFromSession(Context.current().session());

        Group group = Group.queryGroupById(groupId);
        if (null != group && group.getType() != Group.Type.TRANSLATE && group.getType() != Group.Type.MULTICOMMUNICATE) {
            return ServiceResult.error("100005", "该接口只能用于多人会话、翻译群");
        }
        if (null == group) {
            return ServiceResult.error("2001", "群组不存在");
        }
        List<Long> groupIdList = Arrays.asList(groupId);
        Map<Long, Boolean> checkJoinGroup = models.GroupMember.checkJoinGroup(me.getId(), groupIdList);
        if (checkJoinGroup.get(groupId) != true) {
            return ServiceResult.error("100005", "未加入的群组，groupId = " + groupId);
        }

        ObjectNodeResult objectNodeResult;
        try {
            objectNodeResult = ChatService.updateMultiCommunicateName(me, newGroupName, groupId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!objectNodeResult.isSuccess()) {
            if ("900002".equals(objectNodeResult.getErrorCode())) {
                return ServiceResult.error("308001", objectNodeResult.getError());
            }
            Logger.error(objectNodeResult.getObjectNode().toString());
            return ServiceResult.error("100001", "系统错误");
        }
        return ServiceResult.success();
    }

}
