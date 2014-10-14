package controllers.user;

import org.apache.commons.lang3.StringUtils;

import models.Group;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Result;
import controllers.base.BaseApp;

public class UserRecordApp extends BaseApp {

	/**
	 * user会话记录list
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result user() {
		return ok(views.html.usercenter.conversation.userRecord.render());
	}

	/**
	 * user会话记录
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result userDetail() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String groupId = requestData.get("gid");
		String uid = requestData.get("uid");

		String userName = "";
		String groupName = "";
		if (StringUtils.isNotBlank(uid)) {
			User user = User.findById(Long.parseLong(uid));
			if (null == user) {
			    return ok(views.html.common.pagenotfound.render());
			}
			userName = user.getName();
		}
		if (StringUtils.isNotBlank(groupId)) {
			Group group = Group.queryGroupById(Long.parseLong(groupId));
			if (null == group) {
			    return ok(views.html.common.pagenotfound.render());
			}
			groupName = group.getGroupName();
		}

		return ok(views.html.usercenter.conversation.userRecordDetail.render(userName, groupName));
	}

	/**
	 * group会话记录list
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result group() {
		return ok(views.html.usercenter.conversation.groupRecord.render());
	}

	/**
	 * group会话记录
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result groupDetail() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String groupId = requestData.get("gid");
		String uid = requestData.get("uid");

		String userName = "";
		String groupName = "";
		if (StringUtils.isNotBlank(uid)) {
			User user = User.findById(Long.parseLong(uid));
			if (null == user) {
			    return ok(views.html.common.pagenotfound.render());
			}
			userName = user.getName();
		}
		if (StringUtils.isNotBlank(groupId)) {
			Group group = Group.queryGroupById(Long.parseLong(groupId));
			if (null == group) {
			    return ok(views.html.common.pagenotfound.render());
			}
			groupName = group.getGroupName();
		}
		return ok(views.html.usercenter.conversation.groupRecordDetail.render(userName, groupName));
	}



    /**
    * user会话记录search
    *
    * @return
    */
    @Transactional(readOnly = true)
    public static Result groupSearch() {
    return ok(views.html.usercenter.conversation.groupRecordSearch.render());
    }


    /**
    * group会话记录search
    *
    * @return
    */
    @Transactional(readOnly = true)
    public static Result userSearch() {
    return ok(views.html.usercenter.conversation.userRecordSearch.render());
    }
}
