/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年7月4日
 */

package controllers.user;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.Group;
import models.SkillTag;
import models.User;
import models.service.ChatService;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.Assets;
import utils.HelomeUtil;
import utils.ZoomImage;
import vo.GroupVO;
import vo.page.Page;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.attachment.AttachmentApp;
import controllers.attachment.AttachUploadApp;
import controllers.base.BaseApp;
import controllers.base.ObjectNodeResult;
import exception.AvatarException;

/**
 * 
 *
 *
 * @ClassName: UserGroupsApp
 * @Description: 群组
 * @author zhiqiang.zhou
 *
 */
public class UserGroupsApp extends BaseApp {

    /**
     * 我建立的页面
     */
	public static Result list() {
		return ok(views.html.usercenter.ug.groupList.render());
	}
    /**
     * 创建普通群组页面
     */
    @Transactional(readOnly = true)
	public static Result writeInfo() {
    	List<SkillTag> tags = SkillTag.listCategories(100);
    	for (SkillTag skillTag : tags) {
			skillTag.industry = null;
		}
		return ok(views.html.usercenter.ug.writeInfo.render(tags));
	}
    /**
     * 编辑普通群组页面
     */
    @Transactional(readOnly = true)
    public static Result editInfo() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String groupId = requestData.get("gid");
        
        List<SkillTag> tags = SkillTag.listCategories(100);
        for (SkillTag skillTag : tags) {
            skillTag.industry = null;
        }
        GroupVO groupVO = new GroupVO();
        if (groupId != null) {
            Group group = Group.queryGroupById(Long.parseLong(groupId));
            if (group != null) {
                groupVO.convert(group);
            }
        }
        
        return ok(views.html.usercenter.ug.editInfo.render(tags,groupVO));
    }

    /**
     * 我加入的页面
     */
    public static Result join() {
        return ok(views.html.usercenter.ug.join.render());
    }

    /**
     * 多人会话页面
     */
    public static Result multi() {
        return ok(views.html.usercenter.ug.multi.render());
    }

    /**
     * 群成员页面
     */
    @Transactional(readOnly = true)
	public static Result memberList() {
		DynamicForm requestData = Form.form().bindFromRequest();
    	String gid = requestData.get("gid"); // 群组id

    	String groupName = "";
    	String countMem = "";
		if (StringUtils.isNotBlank(gid)) {
			Group group = Group.queryGroupById(Long.parseLong(gid));
			if (group != null) {
				groupName = group.getGroupName();
				countMem = group.getCountMem().toString();
			}
		}
		return ok(views.html.usercenter.ug.memberList.render(gid,groupName,countMem));
	}

    /**
     * 邀请圈好友页面
     */
    @Transactional(readOnly = true)
	public static Result addMember() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String gid = requestData.get("gid"); // 群组id
        String isNew = requestData.get("isNew"); // 群组id

        String groupName = "";
        if (StringUtils.isNotBlank(gid)) {
            Group group = Group.queryGroupById(Long.parseLong(gid));
            if (group != null) {
                groupName = group.getGroupName();
            }
        }
		return ok(views.html.usercenter.ug.addMember.render(gid,groupName,isNew));
	}

    /**
     * 邀请圈好友成功页面
     */
    public static Result addMemberSuccess() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String gid = requestData.get("gid"); // 群组id
        String inviteCount = requestData.get("num"); // 邀请数目
        return ok(views.html.usercenter.ug.addMemberSuccess.render(gid,inviteCount));
    }
    
    /**
     * 创建或修改普通群组
     */
    @Transactional
	public static Result createOrUpdateGroup() {
		JsonNode json = getJson();
		User currentUser = User.getFromSession(session());
		ObjectNodeResult result = ChatService.createOrUpdateGroup(currentUser, json);
		return ok(result.getObjectNode());
	}
    
    /**
     * 创建多人会话群组
     * @throws IOException 
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public static Result createMultiCommunicate() throws IOException{
    	JsonNode json = request().body().asJson();
    	Iterator<JsonNode> userIds = json.findPath("userIds").iterator();
    	List<Long> userIdList = new ArrayList<Long>();
    	if (!userIds.hasNext()) {
			return ok("{\"status\":\"0\",\"error\":\"传入的userIds参数不能为空。\"}");
		}
    	while(userIds.hasNext()){
    		Long userId = userIds.next().asLong();
    		userIdList.add(userId);
    	}
		Logger.info("userIds --------> " + userIds.toString());
		
		User me = User.getFromSession(session());
		ObjectNodeResult result = null;
		result = ChatService.createMultiCommunicateGroup(me, userIdList);
    	return ok(result.getObjectNode());
    }
    
    /**
     * 修改多人会话名称
     * @return
     */
    @Transactional
	public static Result updateMultiCommunicateName() {
    	DynamicForm requestData = Form.form().bindFromRequest();
    	String groupName = requestData.get("groupName");
    	String groupId = requestData.get("groupId"); // 多人会话群ID
    	User user = User.getFromSession(session());
    	ObjectNodeResult result = null;
    	try {
    		result = ChatService.updateMultiCommunicateName(user, groupName, Long.parseLong(groupId));
		} catch (IOException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("调用updateMultiCommunicateName()方法查询出错啦。", e);
			}
			return ok("{\"status\":\"0\",\"error\":\"获取服务端连接超时\"}");
		}
    	return ok(result.getObjectNode());
    }
    
    /**
     * 分类查询用户建立的群组（类别：公开自由加入、需要申请加入）
     * @return
     */
    @Transactional(readOnly = true)
    public static Result queryCreatedGroups(){
    	DynamicForm requestData = Form.form().bindFromRequest();
    	// 加入权限
    	String groupType = requestData.get("type"); // type:0（公开自由加入）、1（需要申请加入）、2（全部）
		if (StringUtils.isBlank(groupType)) {
    		return ok("{\"status\":\"0\",\"error\":\"参数type不能为空。\"}");
    	}
		String pageStr = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "10" : requestData.get("pageSize");
		String groupNameStr = requestData.get("groupName");
		String groupName = ""; // 群组名称
		if (StringUtils.isNotBlank(groupNameStr)) {
			groupName = groupNameStr;
		}
		Integer page = Integer.parseInt(pageStr);
		if (page != null && page < 0) {
			page = 0;
		}
		
		User me = User.getFromSession(session());
		Group.GroupPriv groupPriv = Group.GroupPriv.PUBLIC;
		if (StringUtils.equals(groupType, "1")) {
			groupPriv = Group.GroupPriv.APPLY;
		} else if (StringUtils.equals(groupType, "2")){
			groupPriv = null;
		}
		
		Page<vo.GroupVO> pageGroup = ChatService.queryGroupByUserAndGroupPriv(me, groupPriv, groupName, page, Integer.parseInt(pageSize));
		if (Logger.isDebugEnabled()) {
			Logger.debug("调用queryCreatedGroups()方法返回给前端的json  ---> "+ play.libs.Json.toJson(pageGroup));
		}
    	return ok(play.libs.Json.toJson(pageGroup));
    }
    
    /**
     * 分类查询用户加入的群组（类别：公开自由加入、需要申请加入）
     * @return
     */
    @Transactional(readOnly = true)
	public static Result queryJoinedGroups() {
    	DynamicForm requestData = Form.form().bindFromRequest();
    	// 加入权限
    	String groupType = requestData.get("type"); // type:0（公开自由加入）、1（需要申请加入）、2（全部）
		if (StringUtils.isBlank(groupType)) {
    		return ok("{\"status\":\"0\",\"error\":\"参数type不能为空。\"}");
    	}
		String pageStr = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "10" : requestData.get("pageSize");
		String groupNameStr = requestData.get("groupName");
		String groupName = ""; // 群组名称
		if (StringUtils.isNotBlank(groupNameStr)) {
			groupName = groupNameStr;
		}
		Integer page = Integer.parseInt(pageStr);
		if (page != null && page < 0) {
			page = 0;
		}
		User me = User.getFromSession(session());
		Group.GroupPriv groupPriv = Group.GroupPriv.PUBLIC;
		if (StringUtils.equals(groupType, "1")) {
			groupPriv = Group.GroupPriv.APPLY;
		} else if (StringUtils.equals(groupType, "2")){
			groupPriv = null;
		}
		
		Page<vo.GroupVO> pageGroup = ChatService.queryJoinedGroupByUserAndGroupPriv(me, groupPriv, groupName, page, Integer.parseInt(pageSize));
		if (Logger.isDebugEnabled()) {
			Logger.debug("调用queryJoinedGroups()方法返回给前端的json  ---> "+ play.libs.Json.toJson(pageGroup));
		}
    	return ok(play.libs.Json.toJson(pageGroup));
    }
    
    /**
     * 查询临时群组（包括翻译群组）
     * @return
     */
    @Transactional(readOnly = true)
    public static Result queryTempGroups(){
    	DynamicForm requestData = Form.form().bindFromRequest();
    	String pageStr = StringUtils.isBlank(requestData.get("page")) ? "0" : requestData.get("page");
		String pageSize = StringUtils.isBlank(requestData.get("pageSize")) ? "10" : requestData.get("pageSize");
		User me = User.getFromSession(session());
		String groupNameStr = requestData.get("groupName");
		String groupName = ""; // 群组名称
		if (StringUtils.isNotBlank(groupNameStr)) {
			groupName = groupNameStr;
		}
		Integer page = Integer.parseInt(pageStr);
		if (page != null && page < 0) {
			page = 0;
		}
		Page<vo.GroupVO> pageGroup = ChatService.queryTempGroups(me, groupName, page, Integer.parseInt(pageSize));
		if (Logger.isDebugEnabled()) {
			Logger.debug("调用queryTempGroups()方法返回给前端的json  ---> " + play.libs.Json.toJson(pageGroup));
		}
		return ok(play.libs.Json.toJson(pageGroup));
    }
    
    /**
     * 根据群组id查询群组
     * @param groupId
     * @return
     */
    @Transactional(readOnly = true)
    public static Result queryGroupById(Long groupId){
		if (groupId == null) {
			throw new IllegalArgumentException("参数groupId不能为空");
		}
    	Group group = Group.queryGroupById(groupId);
    	if (group == null) {
			return ok("{\"status\":\"0\",\"error\":\"没有找到该群组。\"}");
		}
    	vo.GroupVO vo = new vo.GroupVO();
    	vo.convert(group);
    	return ok(play.libs.Json.toJson(vo));
    }
    
    /**
     * 删除群组
     * <br/> 
     * 解散群组
     * @param groupId 群组id
     * @return
     */
    @Transactional
    public static Result deleteGroup(Long groupId) {
		if (groupId == null) {
			throw new IllegalArgumentException("参数groupId不能为空");
		}
    	ObjectNodeResult result = null;
    	User me = User.getFromSession(session());
    	result = ChatService.deleteGroup(me,groupId);
		return ok(result.getObjectNode());
    }
    
    /**
     * 移除群组成员
     * @return
     */
    @Transactional
	public static Result removeMember() {
		ObjectNodeResult result = new ObjectNodeResult();
		DynamicForm requestData = Form.form().bindFromRequest();
    	String groupId = requestData.get("groupId");
		String userId = requestData.get("userId");
		User user = User.findById(Long.parseLong(userId));
		result = ChatService.deleteMemberFromGroup(user, Long.parseLong(groupId),1);
		return ok(result.getObjectNode());
	}
	
	/**
	 * 退出
	 * @param groupId
	 * @return
	 */
    @Transactional
	public static Result quitGroup(Long groupId){
		if(groupId == null){
			return ok("{\"status\":\"0\",\"error\":\"参数groupId不能为空。\"}");
		}
		User currentUser = User.getFromSession(session());
		ObjectNodeResult result = new ObjectNodeResult();
		result = ChatService.deleteMemberFromGroup(currentUser, groupId,2);
		return ok(result.getObjectNode());
	}
	
    
    /**
     * 群组 - 上传头像
     * @return
     */
    @Transactional
    @BodyParser.Of(value = BodyParser.Raw.class, maxLength = 2 * 1024 * 1024)
	public static Result uploadAvatar() {
		User currentUser = User.getFromSession(session());
		ObjectNodeResult result = new ObjectNodeResult();
		// 文件大小不能超过2M
		if (request().body().isMaxSizeExceeded()) {
			result.error("图片大小不能超过2M", "900003");
			return ok(result.getObjectNode());
		}
		File avatarFile = request().body().asRaw().asFile();
		try {
			save(avatarFile, result, currentUser);
		} catch (AvatarException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("上传群组头像失败", e);
			}
			result.error("图片路径创建失败请检查权限或配置", "900004");
			return ok(result.getObjectNode());
		}
		return ok(result.getObjectNode());

	}
    
    /**
     * 群组 - 上传头部背景
     * <br/> 直接上传文件
     * @return
     */
    @Transactional
    public static Result uploadHeadBackGround(){
    	User currentUser = User.getFromSession(session());
		ObjectNodeResult result = new ObjectNodeResult();
		// 文件大小不能超过2M
		if (request().body().isMaxSizeExceeded()) {
			result.error("图片大小不能超过2M", "900003");
			return ok(result.getObjectNode());
		}
		
		Http.MultipartFormData body = request().body().asMultipartFormData();
		Http.MultipartFormData.FilePart uploadFile = body.getFile("headbackgroud");
		
		try {
			if (AttachUploadApp.uploadCheck(result, uploadFile)) {
				saveHeadBackGroundFile(uploadFile,result,currentUser);
			}
		} catch (AvatarException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("上传群组头部背景失败", e);
			}
			result.error("图片路径创建失败请检查权限或配置", "900004");
			return ok(result.getObjectNode());
		}
		return ok(result.getObjectNode());
    }
    
    /**
     * 保存头像文件
     * @param avatarFile
     * @param result
     * @param currentUser
     * @throws AvatarException
     */
    public static void save(File avatarFile,ObjectNodeResult result,User currentUser) throws AvatarException {
    	Long currentTimestamp = System.currentTimeMillis(); // 时间戳，用于解决同名文件上传的情况
    	StringBuffer sb = new StringBuffer(AttachmentApp.getUploadPath());
    	sb.append("groupAvatar").append(File.separator).append(currentUser.getId()).append(File.separator).append(currentTimestamp);
    	File avatarPath = new File(sb.toString());
		if (!avatarPath.exists()) {
			boolean results = avatarPath.mkdirs();
			if (!results) {
				throw new AvatarException("图片路径创建失败请检查权限或配置");
			}
		}
		String source = avatarFile.getAbsolutePath();
		ZoomImage zoomImage = new ZoomImage();
		// 190x190
		String avatar190URL = Group.getAvatarFileRelativePath(currentUser.getId(), 190,currentTimestamp);
		createZoomSizeImage(zoomImage, source, sb.toString() + File.separator + "avatar_190.jpg", 190, 190);
//    	result.getObjectNode().put("avatar_190", Assets.at(avatar190URL+"?"+"t="+time));
		result.getObjectNode().put("avatar_190", Assets.at(avatar190URL));
		result.getObjectNode().put("avatar_190_source", avatar190URL);
		
    	// 70x70
    	String avatar70URL = Group.getAvatarFileRelativePath(currentUser.getId(), 70,currentTimestamp);
    	createZoomSizeImage(zoomImage, source, sb.toString() + File.separator + "avatar_70.jpg", 70, 70);
    	result.getObjectNode().put("avatar_70", Assets.at(avatar70URL));
    	result.getObjectNode().put("avatar_70_source", avatar70URL);
    	// 22x22
    	String avatar22URL = Group.getAvatarFileRelativePath(currentUser.getId(), 22,currentTimestamp);
    	createZoomSizeImage(zoomImage, source, sb.toString() + File.separator + "avatar_22.jpg", 22, 22);
    	result.getObjectNode().put("avatar_22", Assets.at(avatar22URL));
    	result.getObjectNode().put("avatar_22_source", avatar22URL);
    	
    	avatarFile.delete();
    }
    
    /**
     * 上传头部背景文件
     * @param headBackGroundFile
     * @param result
     * @param currentUser
     * @throws AvatarException
     */
    public static void saveHeadBackGroundFile(Http.MultipartFormData.FilePart filePart,ObjectNodeResult result,User currentUser) throws AvatarException {
    	Long currentTimestamp = System.currentTimeMillis(); // 时间戳，用于解决同名文件上传的情况
    	StringBuffer sb = new StringBuffer(AttachmentApp.getUploadPath());
    	sb.append("groupHeadBackGround").append(File.separator).append(currentUser.getId()).append(File.separator).append(currentTimestamp);
    	File path = new File(sb.toString());
		if (!path.exists()) {
			boolean results = path.mkdirs();
			if (!results) {
				throw new AvatarException("图片路径创建失败请检查权限或配置");
			}
		}
		String suffix = getSuffix(filePart.getFilename());
		File target = new File(sb.toString() + File.separator + "headbackgroud_1920" + suffix);
		AttachmentApp.move(filePart.getFile(), target);
		
		// 1920x250
		String headbackgroud1920URL = Group.getHeadBackGroundFileRelativePath(currentUser.getId(), 1920,currentTimestamp,suffix);
		result.getObjectNode().put("headbackgroud_1920", Assets.at(headbackgroud1920URL));
		result.getObjectNode().put("headbackgroud_1920_source", headbackgroud1920URL);
    }
    
    /**
     * 获取文件后缀名
     * @param filename
     * @return
     */
    private static String getSuffix(String filename) {
        String s = HelomeUtil.trim(filename).toLowerCase();
        int index = s.lastIndexOf(".");
        if(index == -1) {
            return null;
        } else {
            return s.substring(index);
        }
    }
    
    /**
     * 创建指定大小的图片
     * @param zoomImage
     * @param source
     * @param target
     * @param width
     * @param height
     */
    private static void createZoomSizeImage(ZoomImage zoomImage, String source, String target, int width, int height) {
		try {
			zoomImage.createZoomSizeImage(source, target, width, height);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("user avatar zoomImage.createZoomSizeImage() error ", e);
		}
	}

}
