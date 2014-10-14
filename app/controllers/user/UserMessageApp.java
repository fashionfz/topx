/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package controllers.user;

import models.User;
import play.db.jpa.Transactional;
import play.mvc.Result;
import controllers.base.BaseApp;
import ext.msg.model.Message;
import ext.msg.vo.MsgCount;

/**
 * 
 *
 *
 * @ClassName: UserMessageApp
 * @Description: 个人消息controller
 * @date 2013-11-5 上午10:46:56
 * @author ShenTeng
 *
 */
public class UserMessageApp extends BaseApp {

    @Transactional(readOnly = true)
    public static Result list() {
    	Boolean hasSysNum = false;
    	Boolean hasGroupNum = false;
    	Boolean hasFriendsNum = false;
		User currentUser = User.getFromSession(session());
    	MsgCount count = Message.newsMessageNum(currentUser);
    	if (count.commentMsgNum >0 
    			|| count.replyMsgNum > 0 
    			|| count.transferInMsgNum > 0
    			|| count.transferOutMsgNum > 0
    			|| count.resumeFinishNum > 0)
    		hasSysNum = true;
    	
    	if (count.invitGroupMemberNum > 0 
    			|| count.agreeGroupInvitNum >0 
    			|| count.rejectGroupIntvitNum > 0 
    			|| count.removeGroupMemberNum > 0
    			|| count.quitGroupNum > 0
    			|| count.applyGroupNum > 0
    			|| count.dismissGroupNum > 0)
    		hasGroupNum = true;
        	
    	if (count.addFriendsNum > 0 
    			|| count.agreeFriendsNum >0)
    		hasFriendsNum = true;
   
    	
    		
        return ok(views.html.usercenter.usermessage.render(hasSysNum, hasGroupNum, hasFriendsNum));
    }
    
}
