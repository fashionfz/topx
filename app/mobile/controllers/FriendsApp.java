/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年7月11日
 */
package mobile.controllers;

import java.util.List;

import mobile.service.FriendService;
import mobile.service.result.ServiceResult;
import mobile.util.MobileResults;
import mobile.util.jsonparam.JsonParam;
import mobile.util.jsonparam.JsonParamConfig;
import mobile.util.jsonparam.config.item.ArrayItemType;
import mobile.util.jsonparam.config.item.CanEmpty;
import mobile.util.jsonparam.config.item.Require;
import mobile.vo.result.MobilePage;
import mobile.vo.result.MobileResult;
import mobile.vo.user.User;
import play.db.jpa.Transactional;
import play.mvc.Result;

/**
 *
 *
 * @ClassName: FriendsApp
 * @Description: 移动端好友App
 * @date 2014年7月11日 下午3:58:22
 * @author ShenTeng
 * 
 */
public class FriendsApp extends MobileBaseApp {

    @Transactional
    public static Result inviteFriend(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("inviteeUserId", Require.Yes);
        config.stringField("content", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult result = FriendService.inviteFriend(param.getLong("inviteeUserId"), param.getString("content"));

        return MobileResult.success().setToRoot(result).getResult();
    }

    @Transactional
    public static Result batchInviteFriend(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.arrayField("inviteeUserIds", Require.Yes, CanEmpty.No, ArrayItemType.Long);
        config.stringField("content", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        List<Long> inviteeUserIdList = param.getLongList("inviteeUserIds");
        if (inviteeUserIdList.size() > 50) {
            return MobileResults.illegalParameters("被邀请人过多，不能超过50个").getResult();
        }

        ServiceResult result = FriendService.batchInviteFriend(inviteeUserIdList, param.getString("content"));

        return MobileResult.success().setToRoot(result).getResult();
    }

    @Transactional
    public static Result acceptInvite(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("messageId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult serviceResult = FriendService.acceptInvite(param.getLong("messageId"));
        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

    @Transactional
    public static Result getFriends(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.intField("pageIndex", Require.No);
        config.intField("pageSize", Require.No);
        config.stringField("searchText", Require.No);
        config.longField("excludeGroupId", Require.No);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        Integer pageSize = param.getInt("pageSize");
        Integer pageIndex = param.getInt("pageIndex");

        if (null == pageSize || null == pageIndex) {
            pageIndex = 1;
            pageSize = Integer.MAX_VALUE;
        } else if (pageSize < 1 || pageSize > 200 || pageIndex < 1) {// pageSize范围0-200
            return illegalParameters("pageSize或pageIndex超出范围");
        }

        MobilePage<User> page = FriendService.getFriendPage(pageIndex, pageSize, param.getString("searchText"),
                param.getLong("excludeGroupId"));
        page.setListFieldName("userList");

        MobileResult result = MobileResult.success().setToRoot(page);

        return result.getResult();
    }

    @Transactional
    public static Result deleteFriend(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("friendUserId", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult result = FriendService.deleteFriend(param.getLong("friendUserId"), true);

        return MobileResult.success().setToRoot(result).getResult();
    }

}
