/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-7-4
 */
package mobile.controllers;

import java.io.File;
import java.util.regex.Pattern;

import mobile.service.ChatService;
import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.util.MobileResults;
import mobile.util.jsonparam.JsonParam;
import mobile.util.jsonparam.JsonParamConfig;
import mobile.util.jsonparam.config.item.Require;
import mobile.vo.result.CommonVO;
import mobile.vo.result.MobileResult;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Result;
import utils.HelomeUtil;

/**
 * 
 * 
 * @ClassName: ChatApp
 * @Description: 聊天相关App
 * @date 2014-7-4 上午11:44:21
 * @author ShenTeng
 * 
 */
public class ChatApp extends MobileBaseApp {

    public static Result autoTranslate(String from) {
        JsonParamConfig config = new JsonParamConfig();
        config.stringField("content", Require.Yes);

        JsonParam param = new JsonParam(getJson(), config);
        if (param.isInvalid()) {
            return param.getErrorResult();
        }

        ServiceVOResult<CommonVO> serviceVOResult = ChatService.translate(param.getString("content"));

        MobileResult result = MobileResult.success().setToRoot(serviceVOResult);
        return result.getResult();
    }

    /**
     * 聊天发送文件
     */
    @Transactional
    @BodyParser.Of(value = BodyParser.Raw.class, maxLength = 100 * 1024 * 1024)
    public static Result sendChatFile(String from, String filename, String receiverIdStr, String type,
            String multimediaLengthStr) {
        if (request().body().isMaxSizeExceeded()) {
            return MobileResult.error("280001", "发送文件大小不能超过100M").getResult();
        }
        if (null == type) {
            type = "file";
        }
        Long multimediaLength = null;
        if ("multimedia".equals(type)) {
            multimediaLength = HelomeUtil.toLong(multimediaLengthStr, null);
            if (null == multimediaLength) {
                return MobileResult.error("100005", "非法的multimediaLength").getResult();
            }
        }

        File uploadFile = request().body().asRaw().asFile();
        if (null == uploadFile || uploadFile.length() <= 0) {
            return MobileResult.error("100005", "图片大小必须大于0").getResult();
        }

        Long receiverId = null;
        String receiverType = null;
        if (Pattern.matches("\\d+", receiverIdStr)) {
            receiverId = HelomeUtil.toLong(receiverIdStr, null);
            receiverType = "user";
        } else if (Pattern.matches("G\\d+", receiverIdStr)) {
            receiverId = HelomeUtil.toLong(receiverIdStr.substring("G".length()), null);
            receiverType = "group";
        }
        if (null == receiverId) {
            return MobileResults.illegalParameters("非法的receiverId").getResult();
        }

        ServiceResult serviceResult = ChatService.sendChatFile(uploadFile, filename, receiverId, receiverType, type,
                multimediaLength);

        return MobileResult.success().setToRoot(serviceResult).getResult();
    }

}
