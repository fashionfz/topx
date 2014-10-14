/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-4-10
 */
package mobile.controllers;

import mobile.service.MessageService;
import mobile.service.result.ServiceResult;
import mobile.util.jsonparam.JsonParam;
import mobile.util.jsonparam.JsonParamConfig;
import mobile.util.jsonparam.config.item.ArrayItemType;
import mobile.util.jsonparam.config.item.CanEmpty;
import mobile.util.jsonparam.config.item.Require;
import mobile.vo.result.CommonVO;
import mobile.vo.result.MobilePage;
import mobile.vo.result.MobileResult;
import mobile.vo.user.ChatMessageItem;
import mobile.vo.user.NewMessageNum;
import mobile.vo.user.SystemMessageItem;
import play.db.jpa.Transactional;
import play.mvc.Result;

/**
 * 
 * 
 * @ClassName: MessageApp
 * @Description: 个人消息
 * @date 2014-4-10 下午3:06:07
 * @author ShenTeng
 * 
 */
public class MessageApp extends MobileBaseApp {

    /**
     * 查询会话记录
     */
    @Transactional
    public static Result getChatMessage(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.intField("pageIndex", Require.Yes);
        config.intField("pageSize", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        Integer pageSize = param.getInt("pageSize");
        Integer pageIndex = param.getInt("pageIndex");
        // pageSize范围0-200
        if (pageSize < 1 || pageSize > 200 || pageIndex < 1) {
            return illegalParameters("pageSize或pageIndex超出范围");
        }

        MobilePage<ChatMessageItem> page = MessageService.getChatMessagePage(pageIndex, pageSize);
        page.setListFieldName("chatMessageItemList");
        NewMessageNum newMessageNum = MessageService.getNewMessageNum();

        MobileResult result = MobileResult.success();
        result.setToRoot(page);
        result.setToField("newMessageNum", newMessageNum);

        return result.getResult();
    }

    /**
     * 查询系统消息
     */
    @Transactional
    public static Result getSystemMessage(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.intField("pageIndex", Require.Yes);
        config.intField("pageSize", Require.Yes);
        config.booleanField("autoRead", Require.Yes);
        config.booleanField("unReadFirst", Require.No);
        config.arrayField("messageType", Require.No, CanEmpty.Yes, ArrayItemType.String);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        Integer pageSize = param.getInt("pageSize");
        Integer pageIndex = param.getInt("pageIndex");
        // pageSize范围0-200
        if (pageSize < 1 || pageSize > 200 || pageIndex < 1) {
            return illegalParameters("pageSize或pageIndex超出范围");
        }

        MobilePage<SystemMessageItem> page = MessageService.getSystemMessagePage(pageIndex, pageSize,
                param.getBoolean("autoRead"), param.getStringList("messageType"),
                param.getBoolean("unReadFirst", false));
        page.setListFieldName("systemMessageItemList");

        NewMessageNum newMessageNum = MessageService.getNewMessageNum();

        MobileResult result = MobileResult.success();
        result.setToRoot(page);
        result.setToField("newMessageNum", newMessageNum);

        return result.getResult();
    }

    @Transactional
    public static Result newMsgNum(String from) {
        NewMessageNum newMessageNum = MessageService.getNewMessageNum();

        CommonVO vo = CommonVO.create();

        vo.set("systemMsgNum", newMessageNum.getSystemMsgNum());
        vo.set("commentMsgNum", newMessageNum.getCommentMsgNum());
        vo.set("replyMsgNum", newMessageNum.getReplyMsgNum());
        vo.set("transferInMsgNum", newMessageNum.getTransferInMsgNum());
        vo.set("transferOutMsgNum", newMessageNum.getTransferOutMsgNum());
        vo.set("chatMsgNum", newMessageNum.getChatMsgNum());

        return MobileResult.success().setToRoot(vo).getResult();
    }

    /**
     * 标记消息为已读
     */
    @Transactional
    public static Result markRead(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.arrayField("msgIds", Require.Yes, CanEmpty.No, ArrayItemType.String);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult result = MessageService.markRead(param.getStringList("msgIds"));

        return MobileResult.success().setToRoot(result).getResult();
    }

    /**
     * 标记消息为已读(无需登录)
     */
    @Transactional
    public static Result markRead2(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("userId", Require.Yes);
        config.arrayField("msgIds", Require.Yes, CanEmpty.No, ArrayItemType.String);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult result = MessageService.markRead(param.getStringList("msgIds"), param.getLong("userId"));

        return MobileResult.success().setToRoot(result).getResult();
    }

    /**
     * 信息删除
     */
    @Transactional
    public static Result deleteMessage(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.longField("id", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult result = MessageService.deleteMessage(param.getLong("id"));

        return MobileResult.success().setToRoot(result).getResult();
    }

    /**
     * 批量删除消息
     */
    @Transactional
    public static Result deleteMessages(String form) {
        JsonParamConfig config = new JsonParamConfig();
        config.arrayField("ids", Require.Yes, CanEmpty.No, ArrayItemType.Long);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceResult result = MessageService.batchDeleteMessage(param.getLongList("ids"));

        return MobileResult.success().setToRoot(result).getResult();
    }

}
