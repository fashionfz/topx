/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-2-17
 */
package models.service.reminder;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import models.User;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import play.Logger;
import play.Logger.ALogger;
import play.libs.Yaml;
import utils.EmailUtil;
import utils.MessageUtil;
import vo.EmailInfo;
import ext.config.ConfigFactory;
import ext.msg.model.service.MessageService;

/**
 * 
 * 
 * @ClassName: RemindSender
 * @Description: 提醒发送类
 * @date 2014-2-17 下午6:24:55
 * @author ShenTeng
 * 
 */
public class RemindSender {

    private static VelocityEngine engine = new VelocityEngine();

    private static ALogger LOGGER = Logger.of(RemindSender.class);

    public static void remind(Item item, Option option, User oldUser, User newUser, Map<String, Object> params) {
        String template = getTemplate(item, option);
        String content = parseTemplate(template, oldUser, newUser, params);

        switch (option) {
        case BY_EMAIL:
            if (StringUtils.isNotBlank(oldUser.email)) {
                sendEmail(item, oldUser.email, content);
            }
            break;

        case BY_SMS:
            if (StringUtils.isNotBlank(oldUser.phoneNumber)) {
                sendSms(oldUser.phoneNumber, content);
            }
            break;

        case BY_MSG:
            sendMsg(newUser, content);
            break;
        }
    }

    private static void sendSms(String phoneNumber, String content) {
        int result = MessageUtil.batchSend(phoneNumber, content);
        if (result < 0) {
            LOGGER.error("发送短信失败，返回错误码：" + result);
        }
    }

    private static void sendMsg(User user, String content) {
        MessageService.pushMsgSys(user.id.toString(), user.getName(), content);
    }

    private static void sendEmail(Item item, String email, String content) {
        String title = "嗨啰·觅——安全提醒邮件";
        if (Item.CHANGE_PHONE_NUM == item) {
            title = "嗨啰·觅——绑定手机更改提醒";
        } else if (Item.CHANGE_PASSWORD == item) {
            title = "嗨啰·觅——密码修改提醒";
        } else if (Item.BOOKING_REMIND == item) {
            title = "嗨啰·觅——咨询预约提醒";
        } 
        
        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setSubject(title);
        emailInfo.setBody(content, "text/html;charset=utf-8");
        emailInfo.setTo(email);
        EmailUtil.pushEmail(emailInfo);
    }

    private static String parseTemplate(String template, User oldUser, User newUser, Map<String, Object> params) {
        VelocityContext context = new VelocityContext();
        context.put("oldUser", oldUser);
        context.put("newUser", newUser);

        DateTime now = new DateTime(new Date());
        context.put("now", now);

        context.put("cdnUrl", ConfigFactory.getString("upload.url"));

        if (MapUtils.isNotEmpty(params)) {
            for (Map.Entry<String, Object> e : params.entrySet()) {
                context.put(e.getKey(), e.getValue());
            }
        }

        StringWriter writer = new StringWriter();
        engine.evaluate(context, writer, "safetyRemindTemplate", template);

        return writer.toString();
    }

    private static String getTemplate(Item item, Option option) {
        @SuppressWarnings("unchecked")
        Map<String, String> load = (Map<String, String>) Yaml.load("safetyRemindTemplate.yml");

        return load.get(item.name() + "." + option.name());
    }

    public static class DateTime {
        private Date date;

        public DateTime(Date date) {
            this.date = date;
        }

        public String get(String pattern) {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            return formatter.format(date);
        }

    }

}
