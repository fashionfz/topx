package models.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import models.Group;
import models.Group.GroupPriv;
import models.Group.Type;
import models.GroupMember;
import models.GroupMember.Role;
import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.db.jpa.JPA;
import play.mvc.Http.Context;
import vo.ChatMsgRelationshipVO;
import vo.ExpertDetail;
import vo.GroupDetail;
import vo.GroupMemberVO;
import vo.GroupVO;
import vo.page.Page;

import com.fasterxml.jackson.databind.JsonNode;
import common.Constants;

import controllers.base.ObjectNodeResult;
import ext.MessageCenter.Message.chatMessage.ChatMessageHttpClient;
import ext.MessageCenter.Message.chatMessage.ChatMessageResult;
import ext.MessageCenter.Message.chatMessage.vo.ChatGroupVO;
import ext.MessageCenter.utils.MCMessageUtil;
import ext.msg.model.service.MessageService;
import ext.msg.vo.GroupInfoVO;

/**
 * @ClassName: ChatService
 * @Description: 聊天相关功能服务
 */
public class ChatService {
	
	/**
	 * 查询群组下面的成员
	 * <br/>
	 * 提供分页
	 * @param groupId
	 * @return
	 */
	public static Page<ExpertDetail> queryUserUnderGroup(Long groupId,int page,int pageSize) {
		List<User> userList = Group.queryUserUnderGroup(groupId,page,pageSize);
		List<ExpertDetail> gmVOList = new ArrayList<ExpertDetail>();
		if (CollectionUtils.isNotEmpty(userList)) {
			for (User u : userList) {
				ExpertDetail expertDetail = new ExpertDetail();
				expertDetail.convert(u.getExperts().iterator().next());
				gmVOList.add(expertDetail);
			}
		}
		Long total = Group.queryTotalCountOfUserUnderGroup(groupId);
		Page<ExpertDetail> pageGroup = new Page<ExpertDetail>(Constants.SUCESS, total, gmVOList);
		return pageGroup;
	}

    public static final String GROUP_PREFIX = "G";

    /**
     * 添加成员到组 组名自动生成:用户名、下一个用户名、 ...
     * <br/> 创建翻译群
     * @param one 成员一
     * @param two 成员二
     * @param translator 翻译者
     * @return
     * @throws IOException
     */
    public static CreateTranslateGroupResult createTranslateGroup(User one, User two, User translator) throws IOException {
        // 判断两个用户是否已经在同一个群组中
        List<Long> userIdList = new ArrayList<Long>();
        userIdList.add(one.getId());
        userIdList.add(two.getId());
        userIdList.add(translator.getId());
        List<GroupMember> gmList = GroupMember.queryGroupMemberByUserIds(userIdList);
        if (CollectionUtils.isNotEmpty(gmList) && gmList.size() > 1) {
            List<Long> onegroupIdList = new ArrayList<Long>();
            List<Long> twogroupIdList = new ArrayList<Long>();
            List<Long> translatorgroupIdList = new ArrayList<Long>();
            for (GroupMember gm : gmList) {
                if (gm.getGroup() != null) {

                    if (gm.getUserId() - one.getId() == 0
                            && StringUtils.equals(gm.getRole().toString(), GroupMember.Role.MEMBER.toString())) {
                        onegroupIdList.add(gm.getGroup().getId());
                    }
                    if (gm.getUserId() - two.getId() == 0
                            && StringUtils.equals(gm.getRole().toString(), GroupMember.Role.MEMBER.toString())) {
                        twogroupIdList.add(gm.getGroup().getId());
                    }
                    if (gm.getUserId() - translator.getId() == 0
                            && StringUtils.equals(gm.getRole().toString(), GroupMember.Role.TRANSLATE.toString())) {
                        translatorgroupIdList.add(gm.getGroup().getId());
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(onegroupIdList) && CollectionUtils.isNotEmpty(twogroupIdList)
                    && CollectionUtils.isNotEmpty(translatorgroupIdList)) {
                for (Long item : translatorgroupIdList) {
                    if (onegroupIdList.contains(item) && twogroupIdList.contains(item)) { // 包含相同的元素
                    	// 添加翻译者时，翻译者已经在该群组中，返回群组的信息。
                        return new CreateTranslateGroupResult(true, Group.queryGroupById(item));
                    }
                }
            }
        }

        // 数据库中写数据
        GroupMember oneMember = new GroupMember(one.getId(), one.getName(), one.getEmail(), Role.MEMBER)
                .saveGroupMember(); // 群主
        GroupMember twoMember = new GroupMember(two.getId(), two.getName(), two.getEmail(), Role.MEMBER)
                .saveGroupMember();
        GroupMember translatorMember = new GroupMember(translator.getId(), translator.getName(), translator.getEmail(),
                Role.TRANSLATE).saveGroupMember();
        String groupName = one.getName() + "、" + two.getName() + "、" + translator.getName(); // 群组名称
        Long countMem = 3L; // 成员个数
        Long countTranslate = 1L; // 翻译者个数
        Set<GroupMember> gmSet = new HashSet<GroupMember>();
        gmSet.add(oneMember);
        gmSet.add(twoMember);
        gmSet.add(translatorMember);
        Date createDate = new Date(); // 群组的创建时间
        Date oneJoinDate = new Date(); // one加入群组的时间
        Group group = new Group(groupName, countMem, countTranslate, createDate, oneMember, gmSet, Group.Type.TRANSLATE);
        group.saveGroup();

        // 调用知识库系统的接口 /relation/group/add ，添加用户与群组的关系
        long beginMillis = System.currentTimeMillis();
        
        Date twoJoinDate = new Date(); // two加入群组的时间
        Date translatorJoinDate = new Date(); // translator加入群组的时间
        boolean flag = ChatMessageHttpClient.addGroupMember(one, GROUP_PREFIX + group.getId(), oneJoinDate);
		if (!flag) { // 如果失败，补发一次
        	ChatMessageHttpClient.addGroupMember(one, GROUP_PREFIX + group.getId(), oneJoinDate);
        }
        flag = ChatMessageHttpClient.addGroupMember(two, GROUP_PREFIX + group.getId(), twoJoinDate);
        if(!flag) { // 如果失败，补发一次
        	ChatMessageHttpClient.addGroupMember(two, GROUP_PREFIX + group.getId(), twoJoinDate);
        }
        flag = ChatMessageHttpClient.addGroupMember(translator, GROUP_PREFIX + group.getId(), translatorJoinDate);
		if (!flag) { // 如果失败，补发一次
			ChatMessageHttpClient.addGroupMember(translator, GROUP_PREFIX + group.getId(), translatorJoinDate);
        }

        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        Logger.info("调用知识库系统的接口/relation/group/add共耗时：" + callTime + "ms");
        // 调用MC的通知接口
        beginMillis = System.currentTimeMillis();

        Logger.info("调用MC创建群的接口  ---> " + "群id:" + group.getId() + "，群主id:" + one.getId() + "群名称：" + groupName);
        MCMessageUtil.pushCreateGroupMessage(group.getId(), one.getId(), groupName);
        Logger.info("调用MC添加成员的接口  ---> " + "群id:" + group.getId() + "，群成员id:" + two.getId() + "，翻译者id:"
                + translator.getId());
        MCMessageUtil.pushAddGroupMemberMessage(group.getId(), two.getId());
        MCMessageUtil.pushAddGroupMemberMessage(group.getId(), translator.getId());
        String createGroupMessageId = UUID.randomUUID().toString().toUpperCase();
        String twoJoinGroupMessageId = UUID.randomUUID().toString().toUpperCase();
        String translatorJoinGroupMessageId = UUID.randomUUID().toString().toUpperCase();
		MCMessageUtil.pushGroupNotice(createGroupMessageId, 4, group.getId(), one.getId(), groupName, one.getName(), one.getName() + "邀请"
						+ two.getName() + "和" + translator.getName() + "加入群组：" + groupName,2);
		MCMessageUtil.pushGroupNotice(twoJoinGroupMessageId, 5, group.getId(), two.getId(), groupName, two.getName(), two.getName() + "加入该群组",2);
		MCMessageUtil.pushGroupNotice(translatorJoinGroupMessageId, 5,group.getId(), translator.getId(), groupName, translator.getName(), translator.getName() + "加入该群组",2);

        endMillis = System.currentTimeMillis();
        callTime = endMillis - beginMillis;
        Logger.info("调用MC相关接口共耗时：" + callTime + "ms");

        // 发送组员已经加入聊天群组的消息
        beginMillis = System.currentTimeMillis();

        // type：消息类型 1（聊天记录），2（通知消息）....
        String content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + one.getName() + "邀请" + two.getName() + "和"
                + translator.getName() + "加入群组：" + groupName +"\",\"messageId\":\"" + createGroupMessageId + "\"}";
        ChatMessageHttpClient.sendChatMessage(one, GROUP_PREFIX + group.getId(), oneJoinDate, content,
                ChatMessageHttpClient.ChatType.MANY2MANY, 2);
        content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + two.getName() + "加入该群组" +"\",\"messageId\":\"" + twoJoinGroupMessageId + "\"}";
        ChatMessageHttpClient.sendChatMessage(two, GROUP_PREFIX + group.getId(), twoJoinDate, content,
                ChatMessageHttpClient.ChatType.MANY2MANY, 2);
        content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + translator.getName() + "加入该群组" +"\",\"messageId\":\"" + translatorJoinGroupMessageId + "\"}";
        ChatMessageHttpClient.sendChatMessage(translator, GROUP_PREFIX + group.getId(), translatorJoinDate, content,
                ChatMessageHttpClient.ChatType.MANY2MANY, 2);

        endMillis = System.currentTimeMillis();
        callTime = endMillis - beginMillis;
        Logger.info("调用知识库系统的接口/chatmessage/send共耗时：" + callTime + "ms");
        //添加群信息（保存群的基本信息）到知识库系统
        try {
        	boolean result = ChatMessageHttpClient.createGroup(group, translator);
			if (!result) { // 如果失败，补发一次
        		ChatMessageHttpClient.createGroup(group, translator);
        	}
		} catch (Exception e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("添加翻译群信息到知识库系统出错。", e);
			}
		}
        
        return new CreateTranslateGroupResult(false, group);
    }
    
    /**
     * 创建多人会话群组
     * <br/>
     * @param ownerUser 群主
     * @param userIdList 其他成员id的list集合
     * @return
     * @throws IOException 
     */
    public static ObjectNodeResult createMultiCommunicateGroup(User ownerUser,List<Long> userIdList) throws IOException {
    	ObjectNodeResult result = new ObjectNodeResult();
		if (userIdList.contains(ownerUser.getId())) { // 邀请的人包含自己
			return result.error("邀请的人不能够包含自己，请检查。", "900201");
		}
		List<User> userList = User.queryUserByIds(userIdList);
		if (CollectionUtils.isEmpty(userList)) {
			return result.error("邀请的人都没有被找到，请重新邀请人员加入。", "900202");
		}
		StringBuffer groupNameSB = new StringBuffer();
		StringBuffer invitedPersonSB = new StringBuffer();
		Date createDate = new Date(); // 群组的创建时间
		Set<GroupMember> gmSet = new HashSet<GroupMember>();
		GroupMember owner = new GroupMember(ownerUser.getId(),ownerUser.getName(),ownerUser.getEmail(),Role.MEMBER,createDate).saveGroupMember();
		gmSet.add(owner);
		String groupName = ""; // 群组名称
		String invitedPersonStr = ""; // 被邀请人字符串
		groupNameSB.append(ownerUser.getName().trim()).append("、");
		for (User user : userList) {
			groupNameSB.append(user.getName().trim()).append("、");
			invitedPersonSB.append(user.getName().trim()).append("、");
			GroupMember member = new GroupMember(user.getId(),user.getName(),user.getEmail(),Role.MEMBER,createDate).saveGroupMember();
			gmSet.add(member);
		}
		groupNameSB.deleteCharAt(groupNameSB.length() - 1);
		invitedPersonSB.deleteCharAt(invitedPersonSB.length() - 1);
		if (groupNameSB.length() > 20) { // 群组名称最多20个字符长度
			groupName = StringUtils.substring(groupNameSB.toString(), 0, 20) + "...";
		} else {
			groupName = groupNameSB.toString();
		}
		if (invitedPersonSB.length() > 50) { // 被邀请人字符串最多50个字符长度
			invitedPersonStr = StringUtils.substring(invitedPersonSB.toString(), 0, 50) + "...";
		} else {
			invitedPersonStr = invitedPersonSB.toString();
		}

		Long countMem = new Long(userList.size()) + 1; // 群成员数
		Long countTranslate = 0L;
		Group group = new Group(groupName, countMem, countTranslate, createDate, gmSet, Group.Type.MULTICOMMUNICATE);
        group.saveGroup();
        
        Date joinDate = new Date(); // 成员关系添加的时间
        // 调用MC和知识库系统接口
        String createGroupMessageId = UUID.randomUUID().toString().toUpperCase();
        long beginMillis = System.currentTimeMillis();
        Logger.info("调用MC创建群的接口  ---> " + "群id:" + group.getId() + "，群主id:" + ownerUser.getId() + "群名称：" + groupName);
        MCMessageUtil.pushCreateGroupMessage(group.getId(), ownerUser.getId(), groupName);
        Logger.info("调用知识库系统的接口/relation/group/join  ---> " + "群:" + GROUP_PREFIX + group.getId() + "群成员id：" + ownerUser.getId());
    	boolean flag = ChatMessageHttpClient.addGroupMember(ownerUser, GROUP_PREFIX + group.getId(), joinDate);
        if(!flag) { // 如果失败，补发一次
        	ChatMessageHttpClient.addGroupMember(ownerUser, GROUP_PREFIX + group.getId(), joinDate);
        }
    	
        for (User user : userList) {
        	Logger.info("调用知识库系统的接口/relation/group/join  ---> " + "群:" + GROUP_PREFIX + group.getId() + "群成员id：" + user.getId());
        	flag = ChatMessageHttpClient.addGroupMember(user, GROUP_PREFIX + group.getId(), joinDate);
        	if(!flag) { // 如果失败，补发一次
        		ChatMessageHttpClient.addGroupMember(user, GROUP_PREFIX + group.getId(), joinDate);
        	}
        	
        	Logger.info("调用MC添加成员的接口  ---> " + "群id:" + group.getId() + "，群成员id:" + user.getId());
            MCMessageUtil.pushAddGroupMemberMessage(group.getId(), user.getId());
        }
        
        // 发送邀请消息
        MCMessageUtil.pushGroupNotice(createGroupMessageId, 4, group.getId(), ownerUser.getId(), group.getGroupName(), ownerUser.getName(), ownerUser.getName() + "邀请了" + invitedPersonStr + "加入该多人会话群",1);
        // type：消息类型 1（聊天记录），2（通知消息）....
        String content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + ownerUser.getName() + "邀请了" + invitedPersonStr + "加入该多人会话群" + "\",\"messageId\":\"" + createGroupMessageId + "\"}";
        ChatMessageHttpClient.sendChatMessage(ownerUser, GROUP_PREFIX + group.getId(), joinDate, content,
                ChatMessageHttpClient.ChatType.MANY2MANY, 2);
		for (User user : userList) {
			Date userJoinDate = new Date();
			String messageId = UUID.randomUUID().toString().toUpperCase();
        	MCMessageUtil.pushGroupNotice(messageId, 5, group.getId(), user.getId(), groupName, user.getName(), user.getName() + "加入该群组",1);
        	content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + user.getName() + "加入该群组" + "\",\"messageId\":\"" + messageId + "\"}";
            ChatMessageHttpClient.sendChatMessage(ownerUser, GROUP_PREFIX + group.getId(), userJoinDate, content,
                    ChatMessageHttpClient.ChatType.MANY2MANY, 2);
        }
        
        long endMillis = System.currentTimeMillis();
        long callTime = endMillis - beginMillis;
        Logger.info("调用接口共耗时：" + callTime + "ms");

        result.put("groupId", group.getId());
        result.put("groupName", group.getGroupName());
		result.put("groupType", group.getType().toString().toLowerCase());
		return result;
    }
    
    /**
     * 多人会话名称修改
     * @param user 修改者
     * @param groupName 新群组名称
     * @param groupId 群组id
     * @return
     * @throws IOException 
     */
	public static ObjectNodeResult updateMultiCommunicateName(User user, String groupName, Long groupId) throws IOException {
    	ObjectNodeResult result = new ObjectNodeResult();
    	String[] notUseStrs = { "helome", "嗨啰" };
    	if (StringUtils.isBlank(groupName)) {
			return result.error("名称不能为空", "900001");
		}
		for (String item : notUseStrs) {
			if (StringUtils.contains(groupName, item)) {
				return result.error("不能以'" + item + "'作为多人会话群名称", "900002");
			}
		}
		if (StringUtils.isNotBlank(groupName)) {
			groupName = common.SensitiveWordsFilter.doFilter(groupName);
		}
		Group group = Group.queryGroupById(groupId);
		String oldGroupName = group.getGroupName();
		group.setGroupName(groupName);
		JPA.em().merge(group);
		
		String updateGroupMessageId = UUID.randomUUID().toString().toUpperCase();
//		MCMessageUtil.pushGroupNotice(updateGroupMessageId, 6, group.getId(), user.getId(), group.getGroupName(), user.getName(), user.getName() + "将该群名称修改为：" + groupName,1);
		if (!StringUtils.equals(oldGroupName, groupName)) {
			MCMessageUtil.pushProfileNotice(updateGroupMessageId, 6, group.getId(), user.getId(), groupName, user.getName(), user.getName() + "将该群名称修改为：" + groupName, group.getType() == Group.Type.NORMAL ? 0 : (group.getType() == Group.Type.MULTICOMMUNICATE ? 1 : 2), "");
			// 给知识库发送消息
			// type：消息类型 1（聊天记录），2（通知消息）....
//			String content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + user.getName() + "将该群名称修改为：" + groupName + "\",\"messageId\":\"" + updateGroupMessageId + "\"}";
//			ChatMessageHttpClient.sendChatMessage(user, GROUP_PREFIX + group.getId(), new Date(), content,
//					ChatMessageHttpClient.ChatType.MANY2MANY, 2);
		}
		
		return result;
    }

    /**
     * 为群组新增成员
     * @deprecated
     * @param groupId
     * @param userId
     * @return
     * @throws IOException
     */
    public static ObjectNodeResult appendMembersToGroup(Long groupId, List<Long> userIdList) throws IOException {
        ObjectNodeResult result = new ObjectNodeResult();
        Group group = Group.queryGroupById(groupId);
        if (group == null) {
            return result.error("要加入的群组没有找到", "900101");
        }
        if (CollectionUtils.isNotEmpty(userIdList)) {
            Set<GroupMember> gmSet = group.getGroupmembers();
			if (CollectionUtils.isEmpty(gmSet)) {
				gmSet = new HashSet<GroupMember>();
			}

            for (Long userId : userIdList) {
                User user = User.findById(userId);
                Date joinDate = new Date(); // 加入群组的时间
                if (user != null) {
                    List<Long> groupIdList = Group.queryJoinedGroupIdByUserId(userId);
                    if (groupIdList.contains(group.getId())) {
                        continue;
                    }

                    GroupMember oneMember = new GroupMember(user.getId(), user.getName(), user.getEmail(), Role.MEMBER,
                            joinDate, group).saveGroupMember();
                    gmSet.add(oneMember);

                    try { // 加入try-catch，确保后续代码执行完成
						// 调用知识库系统的接口 /relation/group/add ，添加用户与群组的关系
						long beginMillis = System.currentTimeMillis();
						ChatMessageHttpClient.addGroupMember(user, GROUP_PREFIX + group.getId(), joinDate);
						long endMillis = System.currentTimeMillis();

						long callTime = endMillis - beginMillis;
						Logger.info("调用知识库系统的接口/relation/group/add共耗时：" + callTime + "ms");

						// 调用MC的通知接口
						beginMillis = System.currentTimeMillis();
						Logger.info("调用MC添加成员的接口  ---> " + "群id:" + group.getId() + "，群成员id:" + user.getId());
						MCMessageUtil.pushAddGroupMemberMessage(group.getId(), user.getId());

						endMillis = System.currentTimeMillis();
						callTime = endMillis - beginMillis;
						Logger.info("调用MC相关接口共耗时：" + callTime + "ms");

						// 发送组员已经加入聊天群组的消息
						beginMillis = System.currentTimeMillis();
						// type：消息类型 1（聊天记录），2（通知消息）....
						String content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + user.getName() + "加入该群组" + "\"}";
						ChatMessageHttpClient.sendChatMessage(user, GROUP_PREFIX + group.getId(), joinDate, content,
						        ChatMessageHttpClient.ChatType.MANY2MANY, 2);

						endMillis = System.currentTimeMillis();
						callTime = endMillis - beginMillis;
						Logger.info("调用知识库系统的接口/chatmessage/send共耗时：" + callTime + "ms");
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
            }
            group.setCountMem(new Long(gmSet.size())); // 成员个数
            group.setGroupmembers(gmSet); // 群成员
            group.saveOrUpdate(); // 更新群组信息
        }

        return result;
    }
    
    /**
     * 为群组新增成员
     * <br/>
     * 单个加入
     * @param groupId 群组id
     * @param userId 用户id
     * @return
     * @throws IOException
     */
    public static ObjectNodeResult appendMemberToGroup(Long groupId, Long userId) throws IOException {
        ObjectNodeResult result = new ObjectNodeResult();
        Group group = Group.queryGroupById(groupId);
        if (group == null) { // 没有找到则从知识库系统获取
    		ChatGroupVO chatGroupVO = null;
    		try {
    			chatGroupVO = ChatMessageResult.queryGroupById(groupId);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (chatGroupVO != null) {
				group = chatGroupVO.convert();
			}
    	}
		if (group.getType() == Group.Type.TRANSLATE) {
        	return result.error("翻译群不能再加人", "900100");
        }
        if (group == null) {
            return result.error("要加入的群组没有找到", "900101");
        }
        Set<GroupMember> gmSet = group.getGroupmembers();
		if (CollectionUtils.isEmpty(gmSet)) {
			gmSet = new HashSet<GroupMember>();
		}

        User user = User.findById(userId);
        List<User> userList = Group.queryUserListOfGroup(groupId); // 查询群组下面的所有用户
        Date joinDate = new Date(); // 加入群组的时间
        if (user != null && !userList.contains(user)) {
            List<Long> groupIdList = Group.queryJoinedGroupIdByUserId(userId);
            if (groupIdList.contains(group.getId())) {
            	return result;
            }

            GroupMember oneMember = new GroupMember(user.getId(), user.getName(), user.getEmail(), Role.MEMBER,
                    joinDate, group).saveGroupMember();
            gmSet.add(oneMember);
            
            group.setCountMem(group.getCountMem() + 1); // 成员个数
            group.setGroupmembers(gmSet); // 群成员
    		group.saveOrUpdate(); // 更新群组信息

            try { // 加入try-catch，确保后续代码执行完成
				// 调用知识库系统的接口 /relation/group/add ，添加用户与群组的关系
				long beginMillis = System.currentTimeMillis();
				boolean flag = ChatMessageHttpClient.addGroupMember(user, GROUP_PREFIX + group.getId(), joinDate);
				if(!flag) { // 如果失败，补发一次
					ChatMessageHttpClient.addGroupMember(user, GROUP_PREFIX + group.getId(), joinDate);
				}
				long endMillis = System.currentTimeMillis();

				long callTime = endMillis - beginMillis;
				Logger.info("调用知识库系统的接口/relation/group/add共耗时：" + callTime + "ms");

				// 调用MC的通知接口
				String joinGroupMessageId = UUID.randomUUID().toString().toUpperCase();
				beginMillis = System.currentTimeMillis();
				Logger.info("调用MC添加成员的接口  ---> " + "群id:" + group.getId() + "，群成员id:" + user.getId());
				MCMessageUtil.pushAddGroupMemberMessage(group.getId(), user.getId());
				// 非普通群或者是公开加入的普通群发送MC邀请消息
				if(group.getType() != Group.Type.NORMAL || (group.getType() == Group.Type.NORMAL && group.getGroupPriv() == Group.GroupPriv.PUBLIC)) {
					MCMessageUtil.pushGroupNotice(joinGroupMessageId, 5, group.getId(), user.getId(), group.getGroupName(), user.getName(), user.getName() + "加入该群组",group.getType() == Group.Type.NORMAL ? 0 : (group.getType() == Group.Type.MULTICOMMUNICATE ? 1 : 2));
				}

				endMillis = System.currentTimeMillis();
				callTime = endMillis - beginMillis;
				Logger.info("调用MC相关接口共耗时：" + callTime + "ms");

				
				// 发送组员已经加入聊天群组的消息
				beginMillis = System.currentTimeMillis();
				// 非普通群或者是公开加入的普通群发送邀请消息
				if(group.getType() != Group.Type.NORMAL || (group.getType() == Group.Type.NORMAL && group.getGroupPriv() == Group.GroupPriv.PUBLIC)) {
				    // type：消息类型 1（聊天记录），2（通知消息）....
				    String content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + user.getName() + "加入该群组" + "\",\"messageId\":\"" + joinGroupMessageId + "\"}";
				    ChatMessageHttpClient.sendChatMessage(user, GROUP_PREFIX + group.getId(), joinDate, content,
				            ChatMessageHttpClient.ChatType.MANY2MANY, 2);
				}
				endMillis = System.currentTimeMillis();
				callTime = endMillis - beginMillis;
				Logger.info("调用知识库系统的接口/chatmessage/send共耗时：" + callTime + "ms");
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
		
        return result;
    }

    /**
     * 查询群组下面的成员
     * 
     * @param groupId
     * @return
     */
    public static List<GroupMemberVO> queryUserUnderGroup(Long groupId,User currentUser) {
    	List<GroupMemberVO> gmVOList = new ArrayList<GroupMemberVO>();
    	Group group = Group.queryGroupById(groupId);
		if (group.getType() == Type.TRANSLATE) {
			try {
				gmVOList = ChatMessageResult.queryGroupMembers(groupId);
			} catch (IOException e) {
				if (Logger.isErrorEnabled()) {
					Logger.error("调用知识库系统出错了。", e);
				}
			}
			return gmVOList;
		}
    	List<GroupMember> gmList = Group.queryUserUnderGroup(groupId);
		if (CollectionUtils.isNotEmpty(gmList)) {
			for (GroupMember gm : gmList) {
				GroupMemberVO vo = new GroupMemberVO();
				vo.convert(gm);
				if (group.type == Type.NORMAL && group.getOwner()!=null && (group.getOwner().getId() - gm.getId() == 0)) {
					vo.setRole("OWNER");
					vo.setDisplayPriority(4);
				}
				if(group.getOwner()!=null && (group.getOwner().getUserId()-currentUser.getId()!=0) && (currentUser.getId()-vo.getUserId()==0)) {
					vo.setDisplayPriority(2);
				}
				gmVOList.add(vo);
			}
		}
		// 对结果集进行排序
		sortGroupMemberVOList(gmVOList);
        
        return gmVOList;
    }
    
    /**
     * 对GroupMemberVO的list集合进行排序
     * <br/> IM上群成员排序：群主在最前面，翻译者在前面，在线的在前面
     * @param gmList
     * @return
     */
	public static List<GroupMemberVO> sortGroupMemberVOList(List<GroupMemberVO> gmList) {
		if (CollectionUtils.isNotEmpty(gmList)) {
    		Collections.sort(gmList,new Comparator<GroupMemberVO>() {
				public int compare(GroupMemberVO o1, GroupMemberVO o2) {
					if (o1 != null && o2 != null) {
						return o2.getDisplayPriority() - o1.getDisplayPriority();
					}
					return 0;
				}
    		});
    	}
		return gmList;
    }

    /**
     * 查询所有的群组信息
     * 
     * @return
     */
    public static List<GroupInfoVO> queryAllGroupInfo(int page, int pageSize) {
        List<GroupInfoVO> giVOList = new ArrayList<GroupInfoVO>();
        List<Group> groupList = Group.queryAllGroup(page, pageSize);
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (Group group : groupList) {
                Set<GroupMember> gmSet = group.getGroupmembers();
                List<Long> groupMembers = new ArrayList<Long>();
                if (CollectionUtils.isNotEmpty(gmSet)) {
                    for (GroupMember gm : gmSet) {
                        groupMembers.add(gm.getUserId());
                    }
                }
                giVOList.add(new GroupInfoVO(group.getId(), group.getGroupName(), group.getOwner() == null ? null
                        : group.getOwner().getUserId(), groupMembers));
            }
        }
        return giVOList;
    }

    /**
     * 查询某个用户的聊天的联系人
     * 
     * @param user 用户对象
     * @return
     */
    public static List<ChatMsgRelationshipVO> queryRelationship(User user) {
        try {
            List<ChatMsgRelationshipVO> voList = ChatMessageResult.getRelationshipVOList(user);
            // 排序
            if (CollectionUtils.isNotEmpty(voList)) {
                Collections.sort(voList, new Comparator<ChatMsgRelationshipVO>() {
                    public int compare(ChatMsgRelationshipVO o1, ChatMsgRelationshipVO o2) {
                        if (o2 != null && o1 != null && o2.getType() != null && o1.getType() != null) {
                            return (o2.getType().length() - o1.getType().length());
                        }
                        return 0;
                    }
                });
            }
            return voList;
        } catch (IOException e) {
            if (Logger.isErrorEnabled()) {
                Logger.error("获取最近联系人出错啦。", e);
            }
        }
        return new ArrayList<ChatMsgRelationshipVO>();
    }

    /**
     * 创建或修改群组
     * 
     * @param currentUser 当前用户
     * @param json json字符串
     * @return
     */
    public static ObjectNodeResult createOrUpdateGroup(User currentUser, JsonNode json) {
        return Group.saveGroupByJson(currentUser, json);
    }

    /**
     * 根据用户和群加入权限分类查询用户创建的群组 <br/>
     * 分页查询
     * 
     * @return
     */
    public static Page<vo.GroupVO> queryGroupByUserAndGroupPriv(User user, Group.GroupPriv groupPriv, String groupName, int page,
            int pageSize) {
        Long total = 0L;
        List<Group> groupList = Group.queryGroupByUserIdAndGroupPriv(user.getId(), groupPriv, groupName, page, pageSize);
        List<GroupVO> groupVOList = new ArrayList<GroupVO>();
        for (Group group : groupList) {
            GroupVO vo = new GroupVO();
            vo.convert(group);
            groupVOList.add(vo);
        }
        total = Group.queryTotalCountOfGroupByUserIdAndGroupPriv(user.getId(), groupPriv, groupName);
        Page<GroupVO> pageGroup = new Page<GroupVO>(Constants.SUCESS, total, groupVOList);
        return pageGroup;
    }

    /**
     * 根据用户和群加入权限分类查询用户加入的群组 <br/>
     * 分页查询
     * 
     * @return
     */
    public static Page<vo.GroupVO> queryJoinedGroupByUserAndGroupPriv(User user, Group.GroupPriv groupPriv, String groupName, int page,
            int pageSize) {
        Long total = 0L;
        List<Group> groupList = Group.queryJoinedGroupByUserIdAndGroupPriv(user.getId(), groupPriv, groupName, page, pageSize);
        List<GroupVO> groupVOList = new ArrayList<GroupVO>();
        for (Group group : groupList) {
            GroupVO vo = new GroupVO();
            vo.convert(group);
            groupVOList.add(vo);
        }
        total = Group.queryTotalCountOfJoinedGroupByUserIdAndGroupPriv(user.getId(), groupPriv, groupName);
        Page<GroupVO> pageGroup = new Page<GroupVO>(Constants.SUCESS, total, groupVOList);
        return pageGroup;
    }

    /**
     * 查询临时群组（包括翻译群组） <br/>
     * 分页查询
     * 
     * @return
     */
    public static Page<vo.GroupVO> queryTempGroups(User user, String groupName, int page, int pageSize) {
        Long total = 0L;
        List<Group> groupList = Group.queryTempGroupByUserId(user.getId(), groupName, page, pageSize);
        List<GroupVO> groupVOList = new ArrayList<GroupVO>();
        for (Group group : groupList) {
            GroupVO vo = new GroupVO();
            vo.convert(group);
            groupVOList.add(vo);
        }
        total = Group.queryTotalCountOfTempGroupByUserId(user.getId(),groupName);
        Page<GroupVO> pageGroup = new Page<GroupVO>(Constants.SUCESS, total, groupVOList);
        return pageGroup;
    }

    /**
     * 删除群组
     * 
     * @param groupId 群组id
     * @return
     */
    public static ObjectNodeResult deleteGroup(User currentUser,Long groupId) {
    	ObjectNodeResult result = new ObjectNodeResult();
    	Group group = Group.queryGroupById(groupId);
    	if (group == null) { // 没有找到则从知识库系统获取
    		ChatGroupVO chatGroupVO = null;
    		try {
    			chatGroupVO = ChatMessageResult.queryGroupById(groupId);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (chatGroupVO != null) {
				group = chatGroupVO.convert();
			}
    	}
		if (group == null) {
			result = result.error("该群组没有找到。", "900103");
			return result;
		}
		
		if (group.getType() != Group.Type.NORMAL) {
			result = result.error("普通群组才能被解散。", "900104");
			return result;
		}
		if (currentUser.getId() - group.getOwner().getUserId() != 0) {
			result = result.error("只有群主才能解散群组。", "900105");
			return result;
		}
    	
    	List<User> userList = Group.queryUserListOfGroup(groupId);
    	GroupMember.deleteByGroupId(groupId); // 删除群成员
    	Group.deleteById(groupId); // 删除群组
    	// 发送群主解散该群群消息
		try { // 加入try-catch，确保后续代码执行完成
			Date quitDate = new Date();
			if (CollectionUtils.isNotEmpty(userList)) {
				for (User user : userList) {
					String deleteGroupMessageId = UUID.randomUUID().toString().toUpperCase();
					// 给每个成员发送群解散的聊天记录
					// type：消息类型 1（聊天记录），2（通知消息）....
					String content = "{\"subType\":\"text\",\"type\":2,\"data\":\"群组" + group.getGroupName() + "被群主" + currentUser.getName() + "解散" + "\",\"messageId\":\"" + deleteGroupMessageId + "\"}";
					try {
						ChatMessageHttpClient.sendChatMessage(currentUser, user.getId().toString(), quitDate, content, ChatMessageHttpClient.ChatType.ONE2ONE, 2);
					} catch (IOException e) {
						if (Logger.isErrorEnabled()) {
							Logger.error("退群时，调用知识库发送消息的接口出错。", e);
						}
					}
					
					boolean flag = ChatMessageHttpClient.quitGroupMember(user, GROUP_PREFIX + groupId, quitDate);
					if (!flag) { // 失败则再发一次
						ChatMessageHttpClient.quitGroupMember(user, GROUP_PREFIX + groupId, quitDate);
					}
				}
			}
			// 确保群主退出群关系
			MessageService.pushMsgDismiss(group);
			boolean flag = ChatMessageHttpClient.quitGroupMember(currentUser, GROUP_PREFIX + groupId, quitDate);
			if(!flag) { // 失败则再发一次
				ChatMessageHttpClient.quitGroupMember(currentUser, GROUP_PREFIX + groupId, quitDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result;
    }
    
    /**
     * 将用户从对应的群组中移除
     * @param user
     * @param groupId
     * @param type 类型：1-移除，2-退出
     * @return
     */
    public static ObjectNodeResult deleteMemberFromGroup(User user, Long groupId,int type){
    	ObjectNodeResult result = new ObjectNodeResult();
    	Group group = Group.queryGroupById(groupId);
		if (group == null) { // 没有找到则从知识库系统获取
    		ChatGroupVO chatGroupVO = null;
    		try {
    			chatGroupVO = ChatMessageResult.queryGroupById(groupId);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (chatGroupVO != null) {
				group = chatGroupVO.convert();
			}
    	}
		if (group == null) {
    		result.error("该群组没有找到", "900010");
    	}
    	GroupMember gm = GroupMember.queryGroupMemberByUserIdAndGroupId(user.getId(),groupId);
		if (gm != null) {
			if (type == 1) { //移除
				User owner = User.findById(group.getOwner().getUserId());
				String messageId = MessageService.pushMsgRemove(owner, user, group);
				// 发送组员退群的消息
				Date quitDate = new Date();
	            // type：消息类型 1（聊天记录），2（通知消息）....
	            String content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + user.getName() + "被群主移出该群组" + "\",\"messageId\":\"" + messageId + "\"}";
	            try {
					ChatMessageHttpClient.sendChatMessage(user, GROUP_PREFIX + group.getId(), quitDate, content, ChatMessageHttpClient.ChatType.MANY2MANY, 2);
					boolean flag = ChatMessageHttpClient.quitGroupMember(user, GROUP_PREFIX + groupId, quitDate);
					if (!flag) { // 如果失败则补发一次
						ChatMessageHttpClient.quitGroupMember(user, GROUP_PREFIX + groupId, quitDate);
					}
				} catch (IOException e) {
					if (Logger.isErrorEnabled()) {
						Logger.error("退群时，调用知识库退群接口出错。", e);
					}
				}
	            group.getGroupmembers().remove(gm);
	            group.setCountMem(group.getCountMem() - 1);
	            group.saveOrUpdate();
	            GroupMember.deleteById(gm.getId());
	            // 移除群成员的消息
	            MCMessageUtil.removeGroupMember(groupId, user.getId());
			} else if(type == 2) { // 退出
				if (group.getType() != null && group.getType() == Group.Type.NORMAL) { // 普通群组
					User owner = User.findById(group.getOwner().getUserId());
					if (group.getOwner().getUserId() - user.getId() == 0) {
						result = result.error("群主可以解散自己建立的群组，不能退出自己建立的群组。", "900105");
						return result;
					}
					// 移除群成员的消息
					try {
						MCMessageUtil.removeGroupMember(groupId, user.getId());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					String messageId = MessageService.pushMsgQuit(user, owner, group);
					// 发送组员退群的消息
					Date quitDate = new Date();
		            // type：消息类型 1（聊天记录），2（通知消息）....
		            String content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + user.getName() + "退出该群组" + "\",\"messageId\":\"" + messageId + "\"}";
		            try {
						ChatMessageHttpClient.sendChatMessage(user, GROUP_PREFIX + group.getId(), quitDate, content, ChatMessageHttpClient.ChatType.MANY2MANY, 2);
						boolean flag = ChatMessageHttpClient.quitGroupMember(user, GROUP_PREFIX + groupId, quitDate);
						if (!flag) { // 如果失败则补发一次
							ChatMessageHttpClient.quitGroupMember(user, GROUP_PREFIX + groupId, quitDate);
						}
					} catch (IOException e) {
						if (Logger.isErrorEnabled()) {
							Logger.error("退群时，调用知识库退群接口出错。", e);
						}
					}
		            group.getGroupmembers().remove(gm);
		            group.setCountMem(group.getCountMem() - 1);
		            group.saveOrUpdate();
		            GroupMember.deleteById(gm.getId());
				}
				if(group.getType() != null && (group.getType() == Group.Type.MULTICOMMUNICATE || group.getType() == Group.Type.TRANSLATE)) { // 多人会话
					List<User> userList = Group.queryUserListOfGroup(groupId);
					// 移除群成员的消息
		            try {
						MCMessageUtil.removeGroupMember(groupId, user.getId());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (CollectionUtils.isNotEmpty(userList)) {
						String messageId = UUID.randomUUID().toString().toUpperCase();
						// 发送组员退群的消息
						Date quitDate = new Date();
			            // type：消息类型 1（聊天记录），2（通知消息）....
			            String content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + user.getName() + "退出该群组" + "\",\"messageId\":\"" + messageId + "\"}";
			            try {
							ChatMessageHttpClient.sendChatMessage(user, GROUP_PREFIX + group.getId(), quitDate, content, ChatMessageHttpClient.ChatType.MANY2MANY, 2);
							boolean flag = ChatMessageHttpClient.quitGroupMember(user, GROUP_PREFIX + groupId, quitDate);
							if (!flag) { // 如果失败则补发一次
								ChatMessageHttpClient.quitGroupMember(user, GROUP_PREFIX + groupId, quitDate);
							}
							MCMessageUtil.pushGroupNotice(messageId, 7, group.getId(), user.getId(), group.getGroupName(), user.getName(), user.getName() + "退出该群组",group.getType() == Group.Type.NORMAL ? 0 : (group.getType() == Group.Type.MULTICOMMUNICATE ? 1 : 2));
						} catch (IOException e) {
							if (Logger.isErrorEnabled()) {
								Logger.error("退群时，调用接口出错。", e);
							}
						}
					}
		            group.getGroupmembers().remove(gm);
		            group.setCountMem(group.getCountMem() - 1);
		            if(group.getOwner() != null && (group.getOwner().getUserId() - user.getId() == 0)) {
		            	group.setOwner(null);
		            }
		            group.saveOrUpdate();
		            GroupMember.deleteById(gm.getId());
					// 如果群组没有成员，删除该群组
					if (group.getCountMem() == 0 && CollectionUtils.isEmpty(group.getGroupmembers())) {
						Group.deleteById(group.getId());
						MCMessageUtil.pushDeleteGroupMessage(groupId);
					}
				}
				
			}
		} else {
			boolean flag = ChatMessageHttpClient.quitGroupMember(user, GROUP_PREFIX + groupId, new Date());
			if (!flag) { // 如果失败则补发一次
				ChatMessageHttpClient.quitGroupMember(user, GROUP_PREFIX + groupId, new Date());
			}
			//result.error("该用户已不在该群组中", "900011");
		}
    	return result;
    }

    /**
     * 分页查询群组
     * 
     * @param isCheckJoin 是否对当前用户是否加入群组进行检查。如果检查了，输出结果会包含对于的标识
     * @param pageIndex 页数，从1开始
     * @param pageSize 每页条数
     * @param industryId 行业Id，非必须
     * @param privacy 群组权限，非必须
     * @param skillTag 技能标签，非必须
     * @param orderBy 排序字段，非必须
     * @param isDesc 排序方式，非必须
     * @return Page对象
     */
    public static Page<vo.GroupVO> queryGroupByPage(boolean isCheckJoin, int pageIndex, int pageSize, Long industryId, 
            GroupPriv privacy, String skillTag, String orderBy, Boolean isDesc) {
        Page<Group> groupPage = Group.queryByPage(pageIndex, pageSize, industryId, privacy, skillTag, null, 
                Arrays.asList(Type.NORMAL), null, null, orderBy, isDesc);
        
        List<GroupVO> voList = new ArrayList<GroupVO>(groupPage.getList().size());
        
        for (Group group : groupPage.getList()) {
            GroupVO vo = new GroupVO();
            vo.convert(group);
            voList.add(vo);
        }
        
        // check join
        User user = User.getFromSession(Context.current().session());
        if (isCheckJoin && null != user) {
            List<Long> groupIdList = new ArrayList<Long>();
            for (GroupVO groupVo : voList) {
                groupIdList.add(groupVo.getId());
            }
            
            Map<Long, Boolean> checkJoinGroupMap = GroupMember.checkJoinGroup(user.getId(), groupIdList);
            for (GroupVO groupVo : voList) {
                groupVo.setIsJoin(checkJoinGroupMap.get(groupVo.getId()));
            }
            
        }
        
        Page<vo.GroupVO> voPage = new Page<GroupVO>(groupPage.getStatus(), groupPage.getTotalRowCount(), voList);
        
        return voPage;
    }
    
    /**
     * 根据群组Id查询群组详细,其中包含指定个数的群组成员
     * 
     * @param groupId 群组Id
     * @param groupMemberNum 群组成员个数
     * @param isIncludeOwner群成员是否包含群主
     * @return 群组详细VO
     */
    public static GroupDetail queryGroupDetailById(Long groupId, Integer groupMemberNum, boolean isIncludeOwner) {
        Group group = Group.queryGroupById(groupId);
        if (null == group) {
            return null;
        }
        GroupDetail groupDetail = new GroupDetail();
        groupDetail.convert(group, 10, isIncludeOwner);
        return groupDetail;
    }
    
    /**
     * 分页查询群成员
     * @param pageIndex 页数，从1开始
     * @param pageSize 每页条数
     * @param groupId 群组Id
     * @param excludeId 排除GroupMember Id,为null则不排除
     * @return
     */
    public static Page<GroupMemberVO> queryGroupMemberByPage(int pageIndex, int pageSize, Long groupId
            , Long exculdeId) {
        Page<GroupMember> poPage = GroupMember.queryPageByGroupId(pageIndex, pageSize, groupId, exculdeId);
        
        List<GroupMemberVO> voList = new ArrayList<GroupMemberVO>();
        for (GroupMember gm : poPage.getList()) {
            GroupMemberVO vo = new GroupMemberVO();
            vo.convertWithDetailInfo(gm);
            voList.add(vo);
        }
        
        return new Page<GroupMemberVO>(poPage.getStatus(), poPage.getTotalRowCount(), voList);
    }
    
    public static class CreateTranslateGroupResult {
        
        private boolean isExist;
        
        private Group group;
        
        public CreateTranslateGroupResult(boolean isExist, Group group) {
            super();
            this.isExist = isExist;
            this.group = group;
        }
        
        public boolean isExist() {
            return isExist;
        }
        
        public void setExist(boolean isExist) {
            this.isExist = isExist;
        }
        
        public Group getGroup() {
            return group;
        }
        
        public void setGroup(Group group) {
            this.group = group;
        }
        
    }

}
