/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年7月17日
 */
package vo.msg;

import models.Group.Type;

/**
 * @ClassName: MessageJsonInvitMember
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014年7月17日 上午10:51:38
 * @author RenYouchao
 * 
 */
public class MessageJsonInvitMember {
	
	/**邀请的群组Id**/
	public Long groupId;
	/**邀请的群组名**/
	public String groupName;
	/**群主id**/
	public Long hostsId;
	/**群主姓名**/
	public String hostsName;
	/**内容**/
	public String content;
	/**请描述**/
	public Type type;
	/**群头像**/
	public String headUrl;
	/**
	 * 请描述
	 * @param groupId
	 * @param groupName
	 * @param hostsId
	 * @param hostsName
	 */
	public MessageJsonInvitMember(Long groupId, String groupName, Long hostsId, String hostsName,String headUrl,Type type) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.hostsId = hostsId;
		this.hostsName = hostsName;
		this.headUrl = headUrl;
		this.type = type;
	}
	
	public MessageJsonInvitMember(Long groupId, String groupName, Long hostsId, String hostsName,String content,String headUrl,Type type) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.hostsId = hostsId;
		this.hostsName = hostsName;
		this.content = content;
	}


}
