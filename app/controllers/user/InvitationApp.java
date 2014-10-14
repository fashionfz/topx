/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package controllers.user;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;
import ext.msg.model.service.MessageService;
import models.User;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Result;
import views.html.usercenter.invitation;

/**
 * 
 *
 *
 * @ClassName: FriendsApp
 * @Description: 邀请controller
 * @date 2014-09-25 下午10:46:56
 * @author tintao.li
 *
 */
public class InvitationApp extends BaseApp {

    @Transactional
    public static Result list() {
        User user = User.getFromSession(session());
        return ok(invitation.render(user));
    }
}
