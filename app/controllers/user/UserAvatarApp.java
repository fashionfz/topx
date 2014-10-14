/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package controllers.user;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.Expert;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import utils.Assets;
import utils.ZoomImage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.attachment.AttachmentApp;
import controllers.base.ObjectNodeResult;
import exception.AvatarException;

/**
 * @author ZhouChun
 * @ClassName: UserAvatarApp
 * @Description: 用户头像
 * @date 13-11-7 下午3:21
 */
public class UserAvatarApp extends AttachmentApp {

	/**
	 * flash 上传头像
	 * 
	 * @return
	 */
	@Transactional
	public static Result upload() {
		User current = null;
		String userId = request().getQueryString("userId");
		if (StringUtils.isNotBlank(userId)){
			current = User.findById(new Long(userId));
		} else {
		    current = User.getFromSession(session());
		}
		ObjectNodeResult result = new ObjectNodeResult();
		File avatarFile = request().body().asRaw().asFile();
		try {
			save(avatarFile, result, current);
		} catch (AvatarException e) {
			e.printStackTrace();
			result.errorkey("useravatar.error.upload");
			return ok(result.getObjectNode());
		}
		return ok(result.getObjectNode());
	}
	

	/**
	 * 保存头像
	 * 
	 * @param avatarFile
	 * @param result
	 * @param user
	 * @throws AvatarException
	 */
	public static void save(File avatarFile, ObjectNodeResult result, User user) throws AvatarException {
		StringBuilder sb = new StringBuilder(getUploadPath());
		sb.append("avatar").append(File.separator).append(user.id);
		Long time = System.currentTimeMillis();
		File avatarPath = new File(sb.toString());
		if (!avatarPath.exists()) {
			boolean results = avatarPath.mkdirs();
			if (!results)
				throw new AvatarException("图片路径创建失败请检查权限或配置");
		}
		String source = avatarFile.getAbsolutePath();
		ZoomImage zoomImage = new ZoomImage();
		// 190x190
		String avatar190URL = User.getAvatarFileRelativePath(user.id, 190);
		createZoomSizeImage(zoomImage, source, sb.toString() + File.separator + "avatar_190.jpg", 190, 190);
		result.getObjectNode().put("avatar_190", Assets.at(avatar190URL + "?" + "t=" + time));
		// 70x70
		createZoomSizeImage(zoomImage, source, sb.toString() + File.separator + "avatar_70.jpg", 70, 70);
		result.getObjectNode().put("avatar_70", Assets.at(User.getAvatarFileRelativePath(user.id, 70) + "?" + "t=" + time));
		// 22x22
		String avatar22URL = User.getAvatarFileRelativePath(user.id, 22);
		createZoomSizeImage(zoomImage, source, sb.toString() + File.separator + "avatar_22.jpg", 22, 22);
		result.getObjectNode().put("avatar_22", Assets.at(avatar22URL + "?" + "t=" + time));
		Expert.updateAvatarByUserId(avatar190URL + "?" + "t=" + time, user);
		User.updateAvatarById(avatar22URL + "?" + "t=" + time, user.id, session());
		avatarFile.delete();
	}

	private static void createZoomSizeImage(ZoomImage zoomImage, String source, String target, int width, int height) {
		try {
			zoomImage.createZoomSizeImage(source, target, width, height);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("user avatar zoomImage.createZoomSizeImage() error ", e);
		}
	}

	@Transactional(readOnly = true)
	public static Result show(long userid, int size) {
		User user = User.findById(userid);
		if (user != null) {
			return redirect(user.getAvatar(size));
		} else {
			return redirect(Assets.getDefaultAvatar());
		}
	}

	@Deprecated
	public static Result render(File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] b = new byte[2048];
			int len = -1;
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			in.close();
			return ok(out.toByteArray());
		} catch (Exception e) {
			Logger.error("render user avatar error ", e);
		}
		return ok();
	}

	/**
	 * 根据email获取用户对应id
	 * 
	 * @return
	 */
	@Transactional
	public static Result queryIdByEmail() {
		JsonNode jsonNode = getJson();
		ObjectNodeResult result = new ObjectNodeResult();
		Iterator<JsonNode> emails = jsonNode.get("emails").elements();
		List<String> s = new ArrayList<String>();
		while (emails.hasNext()) {
			String email = emails.next().asText();
			s.add(email);
		}
		List<User> list = User.queryIdByEmail(s);
		ObjectNode objectNode = Json.newObject();
		for (User user : list) {
			objectNode.put(user.email, user.id);
		}
		result.put("user", objectNode);
		return ok(result.getObjectNode());
	}
}
