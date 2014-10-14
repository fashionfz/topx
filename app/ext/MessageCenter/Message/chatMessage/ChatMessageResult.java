package ext.MessageCenter.Message.chatMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import models.Group;
import models.User;
import models.service.ChatService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import utils.Assets;
import vo.ChatMessageRecordVO;
import vo.ChatMsgRelationshipVO;
import vo.ChatMsgUnReadNumVO;
import vo.GroupMemberVO;
import vo.GroupVO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.jackjson.JackJsonUtil;
import ext.MessageCenter.Message.chatMessage.ChatMessageHttpClient.ChatType;
import ext.MessageCenter.Message.chatMessage.vo.ChatGroupVO;
import ext.msg.model.Message;
import ext.usercenter.UCClient;
import ext.usercenter.vo.UCUserVO;

/**
 * 
 * 
 * @ClassName: ChatMessageResult
 * @Description: 聊天记录中心方法执行结果
 */
public class ChatMessageResult<T> {
    
    /**
     * 返回的查询结果
     */
    public T data;

    /**
     * 错误的中文描述，错误信息的详细中文描述
     */
    public String errorinfo;

    /**
     * 接口请求的返回代码，从这个代码中可以识别接口调用是否成功，以及调用失败时的错误类型。具体的code定义参见用户中心接口文档
     */
    String responsecode;

    /**
     * 操作成功
     * 
     * @return true:操作成功
     */
    public boolean isSuccess() {
        return responsecode != null && responsecode.equals("_200");
    }

    /**
     * 没有查询到符合条件的数据
     * 
     * @return true:没有查询到符合条件的数据
     */
    public boolean noMatchData() {
        return responsecode != null && responsecode.equals("_404");
    }

    /**
     * 传入的参数项格式不符合规定
     * 
     * @return true:传入的参数项格式不符合规定
     */
    public boolean illegalParam() {
        return responsecode != null && responsecode.equals("_402");
    }
    
    /**
     * 规定的必传入项没有传入
     * 
     * @return true:规定的必传入项没有传入
     */
    public boolean lackInputParam() {
        return responsecode != null && responsecode.equals("_401");
    }

    /**
     * 查询到重复数据
     * 
     * @return true:查询到重复数据
     */
    public boolean duplicateData() {
        return responsecode != null && responsecode.equals("_405");
    }

    public String toString() {
        return "ChatMessageResult [data=" + data + ", errorinfo=" + errorinfo + ", responsecode=" + responsecode + "]";
    }
    
//    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
//    /**
//     * 从queryRelationship的响应json中解析Email的集合
//     * @param json queryRelationship的响应json
//     * @return Email集合
//     */
//	public static List<String> readUserEmailsFromQueryRelationshipJson(String json) {
//
//		List<String> userEmailList = new ArrayList<String>();
//		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
//		JsonNode node = null;
//		try {
//			node = mapper.readTree(json);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		if (node != null && node.path("responsecode").asText().equals("_200")) {
//
//			node = node.path("data");
//			Iterator<JsonNode> ite = node.elements();
//			while (ite.hasNext()) {
//				JsonNode item = ite.next();
//				String itemText = item.asText();
//				userEmailList.add(itemText);
//			}
//			return userEmailList;
//		}
//		return null;
//	}
	
//	/**
//     * 从queryRelationship的响应json中解析用户id的集合
//     * @param json queryRelationship的响应json
//     * @return 用户id集合
//     */
//	public static List<Long> readUserIdsFromQueryRelationshipJson(String json) {
//
////		List<Long> userIdList = new ArrayList<Long>();
//		Set<Long> userIdSet = new TreeSet<Long>(); // 改用TreeSet -> 排序、去重
//		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
//		JsonNode node = null;
//		try {
//			node = mapper.readTree(json);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		if (node != null && node.path("responsecode").asText().equals("_200")) {
//
//			node = node.path("data");
//			Iterator<JsonNode> ite = node.elements();
//			while (ite.hasNext()) {
//				JsonNode item = ite.next();
//				Long itemText = item.asLong();
////				userIdList.add(itemText);
//				userIdSet.add(itemText);
//			}
//			return new ArrayList<Long>(userIdSet);
//		}
//		return null;
//	}
	
	/**
     * 解析queryLast返回的json字符串
     * @param json queryLast返回的json字符串
     * @param cmrvoList ChatMessageRecordVO的list集合
     * @param totalSB 总记录数
     * @return ChatMessageRecordVO的list集合
     */
    public static List<ChatMessageRecordVO> parseJsonForPage(String json,List<ChatMessageRecordVO> cmrvoList,User fromUser,StringBuffer totalSB){
    	if(cmrvoList==null){
    		cmrvoList = new ArrayList<ChatMessageRecordVO>();
    	}
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		JsonNode node = null;
		try {
			node=mapper.readTree(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (node != null && node.path("responsecode").asText().equals("_200")) {
			node = node.path("data");
			String maxCount = node.path("maxCount").asText() == null ? "" : node.path("maxCount").asText().trim();
			totalSB.append(maxCount); // 设置总记录数
			node = node.path("results");
			jsonToChatMessageRecordVOList(node,cmrvoList,fromUser);
		}
    	return cmrvoList;
    }
    
	private static void jsonToChatMessageRecordVOList(JsonNode jsonNode, List<ChatMessageRecordVO> cmrvoList, User currentUser) {
    	Iterator<JsonNode> resultIte = jsonNode.iterator();
		while (resultIte.hasNext()) {
    		JsonNode node = resultIte.next();
    		Long type = node.path("type").asLong();
			if (type != null) {
				String content = node.path("content").asText() == null ? null : node.path("content").asText().trim();
				// 聊天记录内容过长的后台优化处理：聊天记录查询内容超过200个字符的只截取200个字符加......
				if (StringUtils.isNotBlank(content)) {
					String result = "";
					int strLength = 200;
					if (StringUtils.contains(content, "\"data\"") && content.length() > "{'subType':'text','data':'".length() + strLength){
						result = StringUtils.substring(content, 0, "{'subType':'text','data':'".length() + strLength);
						result = result + "......\"}";
						content = result;
					}
				}
				java.util.Date sendTime = null;
				if (StringUtils.isNotBlank(node.path("sendTime").asText())) {
					sendTime = new Date(node.path("sendTime").asLong());
				}
				if (type - 2 == 0) { // 群组聊天
					String from = node.path("from").asText();
					String to = node.path("to").asText();
					if (StringUtils.isNotBlank(content)) { // content为null，前端转json会报错
						if (StringUtils.contains(from, "G")) {
							from = StringUtils.substring(from, 1);
						}
						if (StringUtils.contains(to, "G")) {
							to = StringUtils.substring(to, 1);
						}
						if(StringUtils.isNotEmpty(from)) {
							cmrvoList.add(new ChatMessageRecordVO(new Long(from), content, sendTime, "me", new Long(to)));
						}
					}
				} else { // 个人聊天
					Long from = node.path("from").asLong();
					Long to = node.path("to").asLong();
					if (StringUtils.isNotBlank(content)) { // content为null，前端转json会报错
						if (StringUtils.equals(currentUser.getId().toString(), from.toString())) {
							cmrvoList.add(new ChatMessageRecordVO(to, content, sendTime, "me"));
						} else {
							cmrvoList.add(new ChatMessageRecordVO(from, content, sendTime, "opposite"));
						}
					}
				}
    		}
    	}
    }
    
    
//    /**
//     * 解析queryLast返回的json字符串
//     * @deprecated 不使用
//     * @param json queryLast返回的json字符串
//     * @param cmrvoList ChatMessageRecordVO的list集合
//     * @return ChatMessageRecordVO的list集合
//     */
//    public static List<ChatMessageRecordVO> getPageListFromJson(String json,List<ChatMessageRecordVO> cmrvoList,User fromUser){
//    	if(cmrvoList==null){
//    		cmrvoList = new ArrayList<ChatMessageRecordVO>();
//    	}
//		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
//		JsonNode node = null;
//		try {
//			node=mapper.readTree(json);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		if (node != null && node.path("responsecode").asText().equals("_200")) {
//			node = node.path("data");
//			Long from = node.path("from").asLong();
//			Long to = node.path("to").asLong();
//			String content = node.path("content").asText() == null ? null : node.path("content").asText().trim();
//			if (StringUtils.isNotBlank(content)) { // content为null，转json会报错
//				java.util.Date sendTime = null;
//				if (StringUtils.isNotBlank(node.path("sendTime").asText())) {
////					sendTime = dateFormat.parse(node.path("sendTime").asText());
//					sendTime = new Date(node.path("sendTime").asLong());
//				}
//				if (StringUtils.equals(fromUser.getId().toString(), from.toString())) {
//					cmrvoList.add(new ChatMessageRecordVO(to, content,sendTime,"me"));
//				} else {
//					cmrvoList.add(new ChatMessageRecordVO(from, content,sendTime,"opposite"));
//				}
//			}
//		}
//    	return cmrvoList;
//    }
    
    /**
	 * 更新List<ChatMessageRecordVO>集合的userName
	 * 
	 * @param userIdList
	 *            userId的list集合
	 * @param cmrvoList
	 *            ChatMessageRecordVO的list集合
	 */
	public static void updateUserNameOfChatMessageRecordVOList(List<ChatMessageRecordVO> cmrvoList,User fromUser) {
		List<Long> userIdList = new ArrayList<Long>();
		List<Long> groupIdList = new ArrayList<Long>();
		for (ChatMessageRecordVO vo : cmrvoList) {
			userIdList.add(vo.getUserId());
			if (vo.getGroupId() != null && vo.getGroupId() != 0) {
				groupIdList.add(vo.getGroupId());
			}
		}
		
		List<User> userList = User.queryUserByIds(userIdList);
		List<Group> groupList = Group.queryGroupByIds(groupIdList);
		for (ChatMessageRecordVO vo : cmrvoList) {
			if (CollectionUtils.isNotEmpty(userList)) {
				for (User u : userList) {
					if (vo.getUserId() - u.getId() == 0) {
						vo.setUserName(u.getUserName());
						vo.setEmail(u.getEmail());
						vo.setAvatar(u.getAvatar(70));
						Long unReadMsgNum = Message.getUnReadMsgNum(u.getId(),fromUser.getId());
						vo.setMsgNum(unReadMsgNum);
						break;
					}
				}
			}
			if (vo.getGroupId() != null && vo.getGroupId() != 0) {
				if (CollectionUtils.isNotEmpty(groupList)) {
					for (Group group : groupList) {
						if (vo.getGroupId() - group.getId() == 0) {
							vo.setGroupName(group.getGroupName());
							vo.setGroupType(group.getType() == null ? Group.Type.NORMAL.toString().toLowerCase() : group.getType().toString().toLowerCase());
							//vo.setGroupAvatar(StringUtils.isBlank(group.getHeadUrl()) ? "" : Assets.at(group.getHeadUrl()));
							if (StringUtils.isBlank(group.getHeadUrl()) && (group.getType() == null || group.getType() == Group.Type.NORMAL)) {
                                vo.setGroupAvatar(Assets.getDefaultGroupHeadUrl(false));
                            } else {
                                vo.setGroupAvatar(Assets.at(group.getHeadUrl()));
                            }
							Long unReadMsgNum = Message.getUnReadMsgNum(fromUser.getId(), ChatService.GROUP_PREFIX + group.getId());
							vo.setMsgNum(unReadMsgNum); // 更新个人未读的群消息个数
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * 更新用户名为空的ChatMessageRecordVO的userName
	 * <br/> 根据用户id查询用户中心用户
	 * @param cmrvoList
	 */
	private static void updateUserNameOfuserNameIsEmpty(List<ChatMessageRecordVO> cmrvoList) {
		List<Long> userIdList = new ArrayList<Long>();
		List<ChatMessageRecordVO> chatMessageRecordVOList = new ArrayList<ChatMessageRecordVO>(); // 用户名为空的ChatMessageRecordVO
		if (CollectionUtils.isNotEmpty(cmrvoList)) {
			for (ChatMessageRecordVO vo : cmrvoList) {
//				if (vo.getUserId() != null && StringUtils.isEmpty(vo.getUserName())) {
//					User userOfUserCenter = UCClient.queryUserById(vo.getUserId());
//					vo.setUserName(userOfUserCenter.getName());
//					vo.setEmail(userOfUserCenter.getEmail());
//				}
				
				if(vo.getUserId() != null && StringUtils.isEmpty(vo.getUserName())) {
					userIdList.add(vo.getUserId());
					chatMessageRecordVOList.add(vo);
				}
			}
		}
		
		List<UCUserVO> ucUserVOList = UCClient.queryUserListByIds(userIdList);
		if(CollectionUtils.isNotEmpty(ucUserVOList)) {
			for(UCUserVO ucUserVO : ucUserVOList) {
				for(ChatMessageRecordVO cmrVO : chatMessageRecordVOList) {
					if(cmrVO.getUserId() - ucUserVO.getUserId() == 0) {
						cmrVO.setUserName(StringUtils.isNotBlank(ucUserVO.getUserName()) ? ucUserVO.getUserName() : ucUserVO.getEnglishName());
						break;
					}
				}
			}
		}
	}
	
	/**
	 * 更新用户名为空的ChatMsgRelationshipVO的userName
	 * <br/> 根据用户id查询用户中心用户
	 * @param cmrvoList
	 */
	private static void updateUserNameOfChatMsgRelationshipVO(List<ChatMsgRelationshipVO> cmrvoList) {
		List<Long> userIdList = new ArrayList<Long>();
		List<ChatMsgRelationshipVO> chatMsgRelationshipVOList = new ArrayList<ChatMsgRelationshipVO>(); // 用户名为空的ChatMsgRelationshipVO
		if (CollectionUtils.isNotEmpty(cmrvoList)) {
			for (ChatMsgRelationshipVO vo : cmrvoList) {
//				if (vo.getUserId() != null && StringUtils.isEmpty(vo.getUserName())) {
//					User userOfUserCenter = UCClient.queryUserById(vo.getUserId());
//					vo.setUserName(userOfUserCenter.getName());
//				}
				
				if(vo.getUserId() != null && StringUtils.isEmpty(vo.getUserName())) {
					userIdList.add(vo.getUserId());
					chatMsgRelationshipVOList.add(vo);
				}
			}
		}
		
		List<UCUserVO> ucUserVOList = UCClient.queryUserListByIds(userIdList);
		if(CollectionUtils.isNotEmpty(ucUserVOList)) {
			for(UCUserVO ucUserVO : ucUserVOList) {
				for(ChatMsgRelationshipVO cmrVO : chatMsgRelationshipVOList) {
					if(cmrVO.getUserId() - ucUserVO.getUserId() == 0) {
						cmrVO.setUserName(StringUtils.isNotBlank(ucUserVO.getUserName()) ? ucUserVO.getUserName() : ucUserVO.getEnglishName());
						break;
					}
				}
			}
		}
	}
	
	/**
	 * 聊天列表中，更新List<ChatMessageRecordVO>集合的userName
	 * 
	 * @param userIdList
	 *            userId的list集合
	 * @param cmrvoList
	 *            ChatMessageRecordVO的list集合
	 */
	public static void updateUserDataOfChatMessageRecordVOList(List<ChatMessageRecordVO> cmrvoList,User fromUser) {
		List<Long> userIdList = new ArrayList<Long>();
		List<Long> groupIdList = new ArrayList<Long>();
		for (ChatMessageRecordVO vo : cmrvoList) {
			userIdList.add(vo.getUserId());
			if (vo.getGroupId() != null && vo.getGroupId() != 0) {
				groupIdList.add(vo.getGroupId());
			}
		}
		
		List<User> userList = User.queryUserByIds(userIdList);
		List<Group> groupList = Group.queryGroupByIds(groupIdList);
		for (ChatMessageRecordVO vo : cmrvoList) {
			if (CollectionUtils.isNotEmpty(userList)) {
				for (User u : userList) {
					if (vo.getUserId() - u.getId() == 0) {
						vo.setUserName(u.getUserName());
						vo.setEmail(u.getEmail());
						vo.setAvatar(u.getAvatar(70));
						Long unReadMsgNum = Message.getUnReadMsgNum(u.getId(),fromUser.getId());
						vo.setMsgNum(unReadMsgNum);
						break;
					}
				}
			}
			if (vo.getGroupId() != null && vo.getGroupId() != 0) {
				if (CollectionUtils.isNotEmpty(groupList)) {
					for (Group group : groupList) {
						if (vo.getGroupId() - group.getId() == 0) {
							vo.setGroupName(group.getGroupName());
							vo.setGroupAvatar(StringUtils.isBlank(group.getHeadUrl()) ? "" : Assets.at(group.getHeadUrl()));
							vo.setGroupType(group.getType() == null ? Group.Type.NORMAL.toString().toLowerCase() : group.getType().toString().toLowerCase());
							ext.MessageCenter.utils.MCMessageUtil.cleanGroupChatNum(group.getId(),fromUser.getId()); // 清除群聊聊天数目
							if (StringUtils.isNotBlank(vo.getMessageId())) {
								ext.MessageCenter.utils.MCMessageUtil.cleanTranslateGroupChatNum(group.getId(), fromUser.getId(), vo.getMessageId());
							}
//							Long unReadMsgNum = Message.getUnReadMsgNum(fromUser.getId(), ChatService.GROUP_PREFIX + group.getId());
//							vo.setMsgNum(unReadMsgNum);
							break;
						}
					}
				}
			}
		}
	}
    
//    /**
//	 * 更新List<ChatMessageRecordVO>集合的userName
//	 * 
//	 * @param userIdList
//	 *            userId的list集合
//	 * @param cmrvoList
//	 *            ChatMessageRecordVO的list集合
//	 */
//	public static void updateUserNameOfChatMessageRecordVOList(List<Long> userIdList, List<ChatMessageRecordVO> cmrvoList,User fromUser) {
//		List<User> userList = User.queryUserByIds(userIdList);
//		Iterator<User> ite = userList.iterator();
////		Random r = new Random();
//		while (ite.hasNext()) {
//			User u = ite.next();
//			for (ChatMessageRecordVO vo : cmrvoList) {
//				if (vo.getUserId().equals(u.getId())) {
//					vo.setUserName(u.getUserName());
//					vo.setEmail(u.getEmail());
//					vo.setAvatar(u.getAvatar(70));
//					// 暂时先生成随机的消息总数 (模拟使用，后面修改 )
////					vo.setMsgNum(new Long(r.nextInt(900)));
////					Long unReadMsgNum = new Long(MCMessageUtil.getUnReadChatMsgNum(fromUser.getId(), u.getId()));
//					Long unReadMsgNum = Message.getUnReadMsgNum(u.getId(),fromUser.getId());
//					vo.setMsgNum(unReadMsgNum);
//					break;
//				}
//			}
//		}
//	}
	
	/**
     * 解析list返回的json字符串
     * @param json list返回的json字符串
     * @param cmrvoList ChatMessageRecordVO的list集合
     * @return ChatMessageRecordVO的list集合
     */
    public static List<ChatMessageRecordVO> getOne2OneMsgListFromJson(String json,List<ChatMessageRecordVO> cmrvoList,User fromUser,User toUser){
    	if(cmrvoList==null){
    		cmrvoList = new ArrayList<ChatMessageRecordVO>();
    	}
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		JsonNode node = null;
		try {
			node=mapper.readTree(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (node != null && node.path("responsecode").asText().equals("_200")) {
			node = node.path("data");
			Iterator<JsonNode> ite = node.elements();
			while (ite.hasNext()) {
				node = ite.next();
				String from = node.path("from").asText();
				java.util.Date sendTime = null;
				if (StringUtils.isNotBlank(node.path("sendTime").asText())) {
					sendTime = new Date(node.path("sendTime").asLong());
				}
				String content = node.path("content").asText() == null ? null : node.path("content").asText().trim();
				if (StringUtils.isNotBlank(content)) { // content为null 转json会报错
					ChatMessageRecordVO vo = null;
					if (StringUtils.equals(fromUser.getId().toString(), from)) {
						vo = new ChatMessageRecordVO(fromUser.getId(), fromUser.getName(), fromUser.getEmail(), content, sendTime,"me");
					} else {
						vo = new ChatMessageRecordVO(toUser.getId(), toUser.getName(), toUser.getEmail(), content, sendTime,"opposite");
					}
					cmrvoList.add(vo);
				}
			}

		}
    	return cmrvoList;
    }
    
    /**
	 * 获取聊天记录的list集合
	 * @param page 页面序号
	 * @param pageSize 页面行数
	 * @param fromUser 当前用户对象
	 * @param totalSB 总记录数
	 * @return
	 * @throws Exception
	 */
    public static List<ChatMessageRecordVO> getChatMessageList(int page, int pageSize, User fromUser,StringBuffer totalSB) throws IOException{
    	String jsonStr = null;
		try {
			jsonStr = ChatMessageHttpClient.queryLastMsgData((page*pageSize),pageSize,fromUser,null); // 查询出所有的人或者群组最后的聊天记录信息
		} catch (IOException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("查询出错啦。",e);
			}
		}
		if (jsonStr == null) {
    		throw new IOException("获取连接超时");
    	}
    	
    	List<ChatMessageRecordVO> cmrvoList = new ArrayList<ChatMessageRecordVO>();
		if (StringUtils.isNotEmpty(jsonStr)) {
			ChatMessageResult.parseJsonForPage(jsonStr,cmrvoList,fromUser,totalSB);
		}
    	ChatMessageResult.updateUserNameOfChatMessageRecordVOList(cmrvoList,fromUser);
    	
//    	//按照消息发送时间降序排序
//    	sortChatMessageRecordVOList(cmrvoList);
    	
    	return cmrvoList;
    }
    
    /**
	 * 获取聊天记录的list集合
	 * @param page 页面序号
	 * @param pageSize 页面行数
	 * @param fromUser 当前用户对象
	 * @param totalSB 总记录数
	 * @param chatType 聊天类型
	 * @return
	 * @throws Exception
	 */
    public static List<ChatMessageRecordVO> getChatMessageList(int page, int pageSize, User fromUser,StringBuffer totalSB,ChatType chatType) throws IOException{
    	String jsonStr = null;
		try {
			jsonStr = ChatMessageHttpClient.queryLastMsgData((page*pageSize),pageSize,fromUser,chatType); // 根据chatType查询聊天记录信息
		} catch (IOException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("查询出错啦。",e);
			}
		}
		if (jsonStr == null) {
    		throw new IOException("获取连接超时");
    	}
    	
    	List<ChatMessageRecordVO> cmrvoList = new ArrayList<ChatMessageRecordVO>();
		if (StringUtils.isNotEmpty(jsonStr)) {
			ChatMessageResult.parseJsonForPage(jsonStr,cmrvoList,fromUser,totalSB);
		}
    	ChatMessageResult.updateUserNameOfChatMessageRecordVOList(cmrvoList,fromUser);
    	updateUserNameOfuserNameIsEmpty(cmrvoList); // 更新用户名为空的ChatMessageRecordVO的userName，很可能是国际组的用户
//    	//按照消息发送时间降序排序
//    	sortChatMessageRecordVOList(cmrvoList);
    	updateGroupNameOfChatMessageRecordVOList(cmrvoList,fromUser); // 获取不到群组名称的从知识库系统获取
    	return cmrvoList;
    }
    
    /**
     * 更新ChatMessageRecordVO中groupName为空的对象
     * @param voList
     * @throws IOException 
     */
    private static void updateGroupNameOfChatMessageRecordVOList(List<ChatMessageRecordVO> voList,User user) throws IOException {
    	List<ChatMessageRecordVO> cmrVOList = new ArrayList<ChatMessageRecordVO>(); // 群组名为空的ChatMessageRecordVO
    	List<Long> groupIdList = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(voList)) {
			for (ChatMessageRecordVO vo : voList) {
				if(vo.getGroupId() != null && StringUtils.isEmpty(vo.getGroupName())) {
					groupIdList.add(vo.getGroupId());
					cmrVOList.add(vo);
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(cmrVOList)) {
			List<ChatGroupVO> groupVOList = queryGroupListByIds(groupIdList);
			if(CollectionUtils.isEmpty(groupVOList)) {
				for(ChatGroupVO vo : groupVOList) {
					for(ChatMessageRecordVO crv : cmrVOList) {
						if (vo.getId() - crv.getGroupId() == 0) {
							crv.setGroupName(vo.getGroupName());
							crv.setGroupType(Group.Type.getByOrdinal(vo.getType()).toString().toLowerCase());
						}
					}
				}
			}
		}
		
    }
    
    public static List<ChatGroupVO> queryGroups(User user) throws IOException {
    	String json = ChatMessageHttpClient.queryGroups(user, Group.Type.TRANSLATE.ordinal());
		List<ChatGroupVO> gVOList = new ArrayList<ChatGroupVO>();
		parseGroupListForqueryGroup(gVOList, json);
		return gVOList;
    }
	
//	/**
//	 * 获取聊天记录的list集合
//	 * @deprecated 不使用，改用getChatMessageList()
//	 * @param fromUser
//	 * @return
//	 * @throws Exception 
//	 */
//	public static List<ChatMessageRecordVO> getChatMessageVOList(User fromUser) throws IOException{
//    	String jsonStr = null;
//		try {
//			jsonStr = ChatMessageHttpClient.queryRelationship(fromUser,ChatMessageHttpClient.ChatType.ONE2ONE);
//		} catch (IOException e) {
//			if (Logger.isErrorEnabled()) {
//				Logger.error("查询出错啦。",e);
//			}
//		}
//		if (jsonStr == null) {
//    		throw new IOException("获取连接超时");
//    	}
////    	List<String> userEmailList = ChatMessageResult.readUserEmailsFromQueryRelationshipJson(jsonStr);
//    	List<Long> userIdList = ChatMessageResult.readUserIdsFromQueryRelationshipJson(jsonStr);
//    	
//    	User toUser = new User();
//    	List<ChatMessageRecordVO> cmrvoList = new ArrayList<ChatMessageRecordVO>();
//		if (userIdList != null && userIdList.size() > 0) {
//			for(Long item:userIdList){
////				toUser.setEmail(item);
//				toUser.setId(item);
//				String currentResponseJson = null;
//				try {
//					currentResponseJson = ChatMessageHttpClient.queryLastMessage(fromUser,toUser,ChatMessageHttpClient.ChatType.ONE2ONE);
//				} catch (IOException e) {
//					if (Logger.isErrorEnabled()) {
//						Logger.error("查询出错啦。", e);
//					}
//				}
//				if (StringUtils.isNotEmpty(currentResponseJson)) {
//					ChatMessageResult.getPageListFromJson(currentResponseJson,cmrvoList,fromUser);
//				}
//			}
//		}
//    	ChatMessageResult.updateUserNameOfChatMessageRecordVOList(userIdList,cmrvoList,fromUser);
//    	
//    	//按照消息发送时间降序排序
//    	sortChatMessageRecordVOList(cmrvoList);
//    	
//    	return cmrvoList;
//    }
	
	/**
	 * 获取当前用户的未读消息的VO的集合
	 * @param currentUser
	 * @return
	 * @throws IOException
	 */
	public static List<ChatMsgUnReadNumVO> getChatMsgUnReadNumVOList(User currentUser) throws IOException {
		String jsonStr = null;
		try {
			jsonStr = ChatMessageHttpClient.queryRelationship(currentUser, null);
		} catch (IOException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("查询出错啦。", e);
			}
		}
		if (jsonStr == null) {
    		throw new IOException("获取连接超时");
    	}
		
		Set<Long> userIdSet = new TreeSet<Long>(); // 用户id的集合
		Set<Long> groupIdSet = new TreeSet<Long>(); // 群组id的集合
		List<ChatMsgUnReadNumVO> cmurnvoList = new ArrayList<ChatMsgUnReadNumVO>(); // 未读消息不为0的ChatMsgUnReadNumVO的集合
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		JsonNode node = null;
		try {
			node = mapper.readTree(jsonStr);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 获取所有未读消息数不为0的ChatMsgUnReadNumVO
		if (node != null && node.path("responsecode").asText().equals("_200")) {
			node = node.path("data");
			Iterator<JsonNode> ite = node.elements();
			while (ite.hasNext()) {
				JsonNode item = ite.next();
				String itemText = item.asText();
				
				if (StringUtils.contains(itemText, "G")) { // 群组的情况
					Long unReadMsgNum = Message.getUnReadMsgNum(currentUser.getId(), itemText);
					if (unReadMsgNum != null && unReadMsgNum > 0) {
						itemText = StringUtils.substring(itemText, 1);
						Long groupId = new Long(itemText);
						groupIdSet.add(groupId);
						cmurnvoList.add(new ChatMsgUnReadNumVO(null, groupId, ChatMsgUnReadNumVO.Type.GROUP.toString().toLowerCase(), unReadMsgNum));
					}
				} else { // 个人用户的情况
					Long userId = new Long(itemText);
					Long unReadMsgNum = Message.getUnReadMsgNum(userId,currentUser.getId());
					if (unReadMsgNum != null && unReadMsgNum > 0) {
						userIdSet.add(userId);
						cmurnvoList.add(new ChatMsgUnReadNumVO(userId, null, ChatMsgUnReadNumVO.Type.PERSON.toString().toLowerCase(), unReadMsgNum));
					}
				}
			}
		}
		
		// 更新用户信息或者是群组信息
		if (CollectionUtils.isNotEmpty(cmurnvoList)) {
			List<Long> userIdList = new ArrayList<Long>(userIdSet);
			List<Long> groupIdList = new ArrayList<Long>(groupIdSet);
			List<User> userList = User.queryUserByIds(userIdList);
			List<Group> groupList = Group.queryGroupByIds(groupIdList);
			for (ChatMsgUnReadNumVO vo : cmurnvoList) {
				if (StringUtils.equals(vo.getType(), ChatMsgUnReadNumVO.Type.PERSON.toString().toLowerCase())) {
					for (User u : userList) {
						if (vo.getUserId() - u.getId() == 0) {
							vo.setUserName(u.getUserName());
							break;
						}
					}
				} else {
					for (Group group : groupList) {
						if (vo.getGroupId() - group.getId() == 0) {
							vo.setGroupName(group.getGroupName());
							break;
						}
					}
				}
			}
		}
		
		// 更新userName为空的数据，从用户中心获取
		updateUserNameOfChatMsgUnReadNumVOIsEmpty(cmurnvoList);
		// 更新ChatMsgUnReadNumVO中群组名称为空的对象
		updateGroupNameOfChatMsgUnReadNumVOList(cmurnvoList); 
		return cmurnvoList;
	}
	
	/**
	 * 更新用户名为空的ChatMsgUnReadNumVO的userName
	 * <br/> 根据用户id查询用户中心用户
	 * @param cmrvoList
	 */
	private static void updateUserNameOfChatMsgUnReadNumVOIsEmpty(List<ChatMsgUnReadNumVO> cmrvoList) {
		List<Long> userIdList = new ArrayList<Long>();
		List<ChatMsgUnReadNumVO> chatMessageRecordVOList = new ArrayList<ChatMsgUnReadNumVO>(); // 用户名为空的ChatMessageRecordVO
		if (CollectionUtils.isNotEmpty(cmrvoList)) {
			for (ChatMsgUnReadNumVO vo : cmrvoList) {
				if(vo.getUserId() != null && StringUtils.isEmpty(vo.getUserName())) {
					userIdList.add(vo.getUserId());
					chatMessageRecordVOList.add(vo);
				}
			}
		}
		
		List<UCUserVO> ucUserVOList = UCClient.queryUserListByIds(userIdList);
		if(CollectionUtils.isNotEmpty(ucUserVOList)) {
			for(UCUserVO ucUserVO : ucUserVOList) {
				for(ChatMsgUnReadNumVO cmrVO : chatMessageRecordVOList) {
					if(cmrVO.getUserId() - ucUserVO.getUserId() == 0) {
						cmrVO.setUserName(StringUtils.isNotBlank(ucUserVO.getUserName()) ? ucUserVO.getUserName() : ucUserVO.getEnglishName());
						break;
					}
				}
			}
		}
	}
	
	/**
     * 更新ChatMsgUnReadNumVO中groupName为空的对象
     * @param voList
     * @throws IOException 
     */
    private static void updateGroupNameOfChatMsgUnReadNumVOList(List<ChatMsgUnReadNumVO> voList) throws IOException {
    	List<ChatMsgUnReadNumVO> cmrVOList = new ArrayList<ChatMsgUnReadNumVO>(); // 群组名为空的ChatMsgUnReadNumVO
    	List<Long> groupIdList = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(voList)) {
			for (ChatMsgUnReadNumVO vo : voList) {
				if(vo.getGroupId() != null && StringUtils.isEmpty(vo.getGroupName())) {
					groupIdList.add(vo.getGroupId());
					cmrVOList.add(vo);
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(cmrVOList)) {
			List<ChatGroupVO> groupVOList = queryGroupListByIds(groupIdList);
			if(CollectionUtils.isEmpty(groupVOList)) {
				for(ChatGroupVO vo : groupVOList) {
					for(ChatMsgUnReadNumVO crv : cmrVOList) {
						if (vo.getId() - crv.getGroupId() == 0) {
							crv.setGroupName(vo.getGroupName());
						}
					}
				}
			}
		}
    }
	
	/**
	 * 获取当前用户的最近联系人的VO的集合
	 * @param currentUser
	 * @return
	 * @throws IOException
	 */
	public static List<ChatMsgRelationshipVO> getRelationshipVOList(User currentUser) throws IOException {
		String jsonStr = null;
		try {
			jsonStr = ChatMessageHttpClient.queryRelationship(currentUser, null);
		} catch (IOException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("查询出错啦。", e);
			}
		}
		if (jsonStr == null) {
    		throw new IOException("获取连接超时");
    	}
		
		Set<Long> userIdSet = new HashSet<Long>(); // 用户id的集合
		Set<Long> groupIdSet = new HashSet<Long>(); // 群组id的集合
		List<ChatMsgRelationshipVO> voList = new ArrayList<ChatMsgRelationshipVO>(); // 联系人的集合
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		JsonNode node = null;
		try {
			node = mapper.readTree(jsonStr);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (node != null && node.path("responsecode").asText().equals("_200")) {
			node = node.path("data");
			Iterator<JsonNode> ite = node.elements();
			while (ite.hasNext()) {
				JsonNode item = ite.next();
				String itemText = item.asText();
				
				if (StringUtils.contains(itemText, "G")) { // 群组的情况
					itemText = StringUtils.substring(itemText, 1);
					Long groupId = new Long(itemText);
					groupIdSet.add(groupId);
					voList.add(new ChatMsgRelationshipVO(null, groupId, ChatMsgRelationshipVO.Type.GROUP.toString().toLowerCase()));
				} else { // 个人用户的情况
					Long userId = new Long(itemText);
					userIdSet.add(userId);
					voList.add(new ChatMsgRelationshipVO(userId, null, ChatMsgRelationshipVO.Type.PERSON.toString().toLowerCase()));
				}
			}
		}
		
		// 更新用户信息或者是群组信息
		if (CollectionUtils.isNotEmpty(voList)) {
			List<Long> userIdList = new ArrayList<Long>(userIdSet);
			List<Long> groupIdList = new ArrayList<Long>(groupIdSet);
			List<User> userList = User.queryUserByIds(userIdList);
			List<Group> groupList = Group.queryGroupByIds(groupIdList);
			for (ChatMsgRelationshipVO vo : voList) {
				if (StringUtils.equals(vo.getType(), ChatMsgUnReadNumVO.Type.PERSON.toString().toLowerCase())) {
					for (User u : userList) {
						if (vo.getUserId() - u.getId() == 0) {
							vo.setUserName(u.getUserName());
							break;
						}
					}
				} else {
					for (Group group : groupList) {
						if (vo.getGroupId() - group.getId() == 0) {
							vo.setGroupName(group.getGroupName());
							break;
						}
					}
				}
			}
			Iterator<ChatMsgRelationshipVO> ite = voList.iterator();
			while (ite.hasNext()) {
				ChatMsgRelationshipVO vo = ite.next();
				if (StringUtils.equals(vo.getType(), ChatMsgUnReadNumVO.Type.PERSON.toString().toLowerCase()) && StringUtils.isEmpty(vo.getUserName())) { // 将数据库中已不存在的用户移除
					ite.remove();
				}
				if (StringUtils.equals(vo.getType(), ChatMsgUnReadNumVO.Type.GROUP.toString().toLowerCase()) && StringUtils.isEmpty(vo.getGroupName())) { // 将数据库中已不存在的群组移除
					ite.remove();
				}
			}
		}
		updateUserNameOfChatMsgRelationshipVO(voList); // 更新用户名为空的ChatMsgRelationshipVO的userName，很可能是国际组的用户
		
		return voList;
	}

	
//	/**
//	 * 按照聊天消息发送时间排序（降序）
//	 * @param cmrvoList ChatMessageRecordVO的list集合
//	 * @return ChatMessageRecordVO的list集合
//	 */
//	private static List<ChatMessageRecordVO> sortChatMessageRecordVOList(List<ChatMessageRecordVO> cmrvoList) {
//		if (CollectionUtils.isNotEmpty(cmrvoList)) {
//			Collections.sort(cmrvoList, new Comparator<ChatMessageRecordVO>() {
//				public int compare(ChatMessageRecordVO o1,ChatMessageRecordVO o2) {
//					if (o2 != null && o1 != null && o2.getMsgTime() != null && o1.getMsgTime() != null) {
//						return (int) (o2.getMsgTime().getTime() - o1.getMsgTime().getTime());
//					}
//					return 0;
//				}
//			});
//		}
//		return cmrvoList;
//	}

	
	/**
	 * 获取两个人一对一聊天记录的list集合
	 * @param containCurrent 是否包含给定时间的这条记录
	 * @return
	 */
	public static List<ChatMessageRecordVO> getOne2OneChatMessageVOList(User fromUser,User toUser,int rowCount,java.util.Date queryDate,Boolean containCurrent){
		String jsonStr = null;
		try {
			jsonStr = ChatMessageHttpClient.preListChatMessage(fromUser, toUser.getId().toString(), queryDate, rowCount, ChatMessageHttpClient.ChatType.ONE2ONE);
		} catch (IOException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("查询出错啦。", e);
			}
		}
		List<ChatMessageRecordVO> cmrvoList = new ArrayList<ChatMessageRecordVO>();
		if (StringUtils.isNotEmpty(jsonStr)) {
			getOne2OneMsgListFromJson(jsonStr, cmrvoList, fromUser, toUser);
		}
		ChatMessageResult.updateUserDataOfChatMessageRecordVOList(cmrvoList,fromUser);
		updateUserNameOfuserNameIsEmpty(cmrvoList); // 更新用户名为空的ChatMessageRecordVO的userName，很可能是国际组的用户
		return cmrvoList;
	}
	
	/**
	 * 获取当前用户与群组的聊天记录的list集合
	 * @param containCurrent 是否包含给定时间的这条记录
	 * @return
	 */
	public static List<ChatMessageRecordVO> getGroupChatMessageVOList(User fromUser,Long groupId,int rowCount,java.util.Date queryDate,Boolean containCurrent){
		String jsonStr = null;
		try {
			jsonStr = ChatMessageHttpClient.preListChatMessage(fromUser, models.service.ChatService.GROUP_PREFIX + groupId, queryDate, rowCount, ChatMessageHttpClient.ChatType.MANY2MANY);
		} catch (IOException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("查询出错啦。", e);
			}
		}
		List<ChatMessageRecordVO> cmrvoList = new ArrayList<ChatMessageRecordVO>();
		if (StringUtils.isNotEmpty(jsonStr)) {
			getGroupMsgListFromJson(jsonStr, cmrvoList, fromUser, groupId);
		}
		ChatMessageResult.updateUserDataOfChatMessageRecordVOList(cmrvoList,fromUser);
		updateUserNameOfuserNameIsEmpty(cmrvoList); // 更新用户名为空的ChatMessageRecordVO的userName，很可能是国际组的用户
		return cmrvoList;
	}
	
	/**
	 * 两个人一对一聊天记录的上下文查询
	 * @param containCurrent 是否包含给定时间的这条记录
	 * @return
	 */
	public static List<ChatMessageRecordVO> queryOne2OneChatMessageContext(User fromUser,User toUser,int preRows,int nextRows,java.util.Date queryDate,Boolean containCurrent) {
		String jsonStr = null;
		try {
			jsonStr = ChatMessageHttpClient.queryContext(fromUser, toUser.getId().toString(), ChatMessageHttpClient.ChatType.ONE2ONE, queryDate, preRows, nextRows, containCurrent);
		} catch (IOException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("查询出错啦。", e);
			}
		}
		List<ChatMessageRecordVO> cmrvoList = new ArrayList<ChatMessageRecordVO>();
		if (StringUtils.isNotEmpty(jsonStr)) {
			getOne2OneMsgListFromJson(jsonStr, cmrvoList, fromUser, toUser);
		}
		ChatMessageResult.updateUserDataOfChatMessageRecordVOList(cmrvoList,fromUser);
		updateUserNameOfuserNameIsEmpty(cmrvoList); // 更新用户名为空的ChatMessageRecordVO的userName，很可能是国际组的用户
		return cmrvoList;
	}
	
	/**
	 * 当前用户与群组的聊天记录的上下文查询
	 * @param containCurrent 是否包含给定时间的这条记录
	 * @return
	 */
	public static List<ChatMessageRecordVO> queryGroupChatMessageContext(User fromUser,Long groupId,int preRows,int nextRows,java.util.Date queryDate,Boolean containCurrent) {
		String jsonStr = null;
		try {
			jsonStr = ChatMessageHttpClient.queryContext(fromUser, models.service.ChatService.GROUP_PREFIX + groupId, ChatMessageHttpClient.ChatType.MANY2MANY, queryDate, preRows, nextRows, containCurrent);
		} catch (IOException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("查询出错啦。", e);
			}
		}
		List<ChatMessageRecordVO> cmrvoList = new ArrayList<ChatMessageRecordVO>();
		if (StringUtils.isNotEmpty(jsonStr)) {
			getGroupMsgListFromJson(jsonStr, cmrvoList, fromUser, groupId);
		}
		ChatMessageResult.updateUserDataOfChatMessageRecordVOList(cmrvoList,fromUser);
		updateUserNameOfuserNameIsEmpty(cmrvoList); // 更新用户名为空的ChatMessageRecordVO的userName，很可能是国际组的用户
		return cmrvoList;
	}
	
	/**
     * 解析list返回的json字符串
     * @param json list返回的json字符串
     * @param cmrvoList ChatMessageRecordVO的list集合
     * @return ChatMessageRecordVO的list集合
     */
    public static List<ChatMessageRecordVO> getGroupMsgListFromJson(String json,List<ChatMessageRecordVO> cmrvoList,User fromUser,Long groupId){
    	if(cmrvoList==null){
    		cmrvoList = new ArrayList<ChatMessageRecordVO>();
    	}
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		JsonNode node = null;
		try {
			node=mapper.readTree(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (node != null && node.path("responsecode").asText().equals("_200")) {
			node = node.path("data");
			Iterator<JsonNode> ite = node.elements();
			while (ite.hasNext()) {
				node = ite.next();
				String from = node.path("from").asText();
				java.util.Date sendTime = null;
				if (StringUtils.isNotBlank(node.path("sendTime").asText())) {
					sendTime = new Date(node.path("sendTime").asLong());
				}
				String content = node.path("content").asText() == null ? null : node.path("content").asText().trim();
				if (StringUtils.isNotBlank(content)) { // content为null 前端转json会报错
					JsonNode contentNode = null;
					try {
						contentNode = mapper.readTree(content);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					String messageId = "";
					if (contentNode != null && contentNode.has("messageId")) {
						messageId = contentNode.path("messageId").asText();
					}
					ChatMessageRecordVO vo = null;
					if (new Long(from) - fromUser.getId() == 0) {
						vo = new ChatMessageRecordVO(new Long(from), content, sendTime, "me", groupId, messageId);
					} else {
						vo = new ChatMessageRecordVO(new Long(from), content, sendTime, null, groupId, messageId);
					}
					cmrvoList.add(vo);
				}
			}

		}
    	return cmrvoList;
    }
    
//    /**
//     * 根据查询条件查询聊天记录
//     * @param currentUser 当前用户
//     * @param dateType 条件中的时间范围1：天为单位；2：月为单位；3：年为单位（必填）
//     * @param dateNumber 时间范围值，例如dateType=2
//     * @param endDate 结束日期（包括此日期的记录）,查询此日期之前（由dateType和dateNumber决定）的记录（必填）
//     * @param keyWords 查询的关键字
//     * @param contentType 内容类型 1-图片 2-文件 3-文本
//     * @param isIncludeGroup 是否查询群聊记录
//     * @param isOnlyIncludeGroup 是否只包含群聊信息
//     * @return
//     * @throws IOException
//     */
//    public static List<ChatMessageRecordVO> queryChatMsgRecordByCondition(User currentUser,DateType dateType,int dateNumber,Date endDate,String keyWords,String contentType,List<Long> userIdList,List<Long> groupIdList,boolean isIncludeGroup,boolean isOnlyIncludeGroup) throws IOException{
//    	String jsonStr = null;
//    	try {
//    		jsonStr = ChatMessageHttpClient.standardQuery(currentUser, dateType, dateNumber, endDate, keyWords, isIncludeGroup, isOnlyIncludeGroup);
//		} catch (IOException e) {
//			if (Logger.isErrorEnabled()) {
//				Logger.error("查询出错啦。", e);
//			}
//		}
//		if (jsonStr == null) {
//			throw new IOException("获取连接超时");
//		}
//		
//		List<ChatMessageRecordVO> cmrvoList = new ArrayList<ChatMessageRecordVO>();
//		if (StringUtils.isNotEmpty(jsonStr)) {
//			ChatMessageResult.parseJsonForStandardQuery(jsonStr, cmrvoList, currentUser, keyWords, contentType, userIdList, groupIdList);
//		}
//    	ChatMessageResult.updateUserDateOfStandardQueryResult(cmrvoList,currentUser);
//    	
//    	return cmrvoList;
//    }
    
    /**
     * 根据查询条件查询聊天记录
     * @param currentUser 当前用户
     * @throws IOException 
     */
    public static List<ChatMessageRecordVO> queryChatMsgRecordByCondition(User currentUser, String keyWords, String contentType,List<Long> userIdList,List<Long> groupIdList, Date startDate, Date endDate, boolean queryAll,int start,int rows,StringBuffer totalSB,ChatType chatType) throws IOException{
    	String jsonStr = null;
    	try {
    		if(CollectionUtils.isEmpty(userIdList)&&CollectionUtils.isEmpty(groupIdList)){
    			try {
    				jsonStr = ChatMessageHttpClient.queryRelationship(currentUser, null);
    			} catch (IOException e) {
    				if (Logger.isErrorEnabled()) {
    					Logger.error("查询出错啦。", e);
    				}
    			}
    			if (jsonStr == null) {
    	    		throw new IOException("获取连接超时");
    	    	}
    			ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
    			JsonNode node = null;
    			try {
    				node = mapper.readTree(jsonStr);
    			} catch (JsonProcessingException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			if (node != null && node.path("responsecode").asText().equals("_200")) {
    				node = node.path("data");
    				Iterator<JsonNode> ite = node.elements();
    				List<String> dataList = new ArrayList<String>();
					while (ite.hasNext()) {
						JsonNode item = ite.next();
						String itemText = item.asText();
						dataList.add(itemText);
    				}
    				String[] from = {currentUser.getId().toString()};
    				String[] to = dataList.toArray(new String[]{});
    				jsonStr = ChatMessageHttpClient.seniorQuery(from, to, chatType, queryAll, startDate, endDate, keyWords, false, start, rows);
    			}
    		}else{
    			String[] from = {currentUser.getId().toString()};
    			String[] to = {currentUser.getId().toString()};
				if (CollectionUtils.isNotEmpty(userIdList)) {
					to = new String[userIdList.size()];
					for (int i = 0; i < userIdList.size(); i++) {
						to[i] = userIdList.get(i).toString();
    				}
    				jsonStr = ChatMessageHttpClient.seniorQuery(from, to, ChatType.ONE2ONE, queryAll, startDate, endDate, keyWords, false, start, rows);
				} else if (CollectionUtils.isNotEmpty(groupIdList)) {
    				to = new String[groupIdList.size()];
    				for(int i=0;i<groupIdList.size();i++){
    					to[i] = ChatService.GROUP_PREFIX + groupIdList.get(i);
    				}
    				jsonStr = ChatMessageHttpClient.seniorQuery(from, to, ChatType.MANY2MANY, queryAll, startDate, endDate, keyWords, false, start, rows);
    			}
    		}

		} catch (IOException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("查询出错啦。", e);
			}
		}
		if (jsonStr == null) {
			throw new IOException("获取连接超时");
		}
		
		List<ChatMessageRecordVO> cmrvoList = new ArrayList<ChatMessageRecordVO>();
		if (StringUtils.isNotEmpty(jsonStr)) {
			ChatMessageResult.parseJsonForSeniorQuery(jsonStr, cmrvoList, currentUser, userIdList, groupIdList, totalSB);
		}
    	ChatMessageResult.updateUserDataOfStandardQueryResult(cmrvoList,currentUser);
    	updateUserNameOfuserNameIsEmpty(cmrvoList); // 更新用户名为空的ChatMessageRecordVO的userName，很可能是国际组的用户    	
    	
    	return cmrvoList;
    }
    
    /**
     * 解析standardQuery返回的json字符串
     * @param json queryLast返回的json字符串
     * @param cmrvoList ChatMessageRecordVO的list集合
     * @param keyWords 查询的关键字
     * @param contentType 内容类型 1-图片 2-文件 3-文本
     * @return ChatMessageRecordVO的list集合
     * @deprecated
     */
    public static List<ChatMessageRecordVO> parseJsonForSeniorQuery(String json,List<ChatMessageRecordVO> cmrvoList,User currentUser,String keyWords,String contentType,List<Long> userIdList,List<Long> groupIdList,StringBuffer totalSB){
		if (cmrvoList == null) {
			cmrvoList = new ArrayList<ChatMessageRecordVO>();
		}
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		JsonNode node = null;
		try {
			node = mapper.readTree(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Set<ChatMessageRecordVO> smrVOSet = new HashSet<ChatMessageRecordVO>(); // 使用set集合便于去重
		Long total =  0L;
		if (node != null && node.path("responsecode").asText().equals("_200")) {
			node = node.path("data");
			String maxCount = node.path("maxCount").asText() == null ? "" : node.path("maxCount").asText().trim();
			totalSB.append(maxCount); // 设置总记录数
			total = new Long (totalSB.toString());
			node = node.path("results");
			Iterator<JsonNode> resultIte = node.iterator();
			while (resultIte.hasNext()) {
	    		JsonNode jsonNode = resultIte.next();
	    		Long type = jsonNode.path("type").asLong();
				if (type != null) {
					String content = jsonNode.path("content").asText() == null ? null : jsonNode.path("content").asText().trim();
					if (StringUtils.isNotBlank(content)) {
						JsonNode contentNode = null;
						try {
							contentNode = mapper.readTree(content);
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						if(contentNode!=null){
							String subType = contentNode.path("subType").asText(); // subType text，file
							String data = contentNode.path("data").asText();
							// type 1-图片 2-文件 3-文本 4-多媒体 	text picture file multimedia
							if ((StringUtils.equals(contentType, "3") && StringUtils.equals(subType, "text"))
									|| (StringUtils.equals(contentType, "2") && StringUtils.equals(subType, "file"))
									|| (StringUtils.equals(contentType, "1") && StringUtils.equals(subType, "picture"))
									|| (StringUtils.equals(contentType, "4") && StringUtils.equals(subType, "multimedia"))) {
								if (StringUtils.isEmpty(keyWords)
										|| (StringUtils.isNotEmpty(keyWords) && (!StringUtils.equals(keyWords, "subType") || !StringUtils.equals(keyWords, "data")))) { // 关键词不为空时，关键词不等于subType、data
									if (StringUtils.isNotBlank(keyWords) && !data.contains(keyWords)) {
										--total;
										continue;
									}
									java.util.Date sendTime = null;
									if (StringUtils.isNotBlank(jsonNode.path("sendTime").asText())) {
										sendTime = new Date(jsonNode.path("sendTime").asLong());
									}
									if (type - 2 == 0) { // 群组聊天
										String from = jsonNode.path("from").asText();
										String to = jsonNode.path("to").asText();
//										if (StringUtils.contains(from, "G")) {
//											from = StringUtils.substring(from, 1);
//										}
										if (StringUtils.contains(to, "G")) {
											to = StringUtils.substring(to, 1);
										}
										if (StringUtils.isNotBlank(content)) { // content为null，前端转json会报错
											if (StringUtils.equals(from,currentUser.getId().toString())) {
												smrVOSet.add(new ChatMessageRecordVO(new Long(from), content, sendTime, "me", new Long(to)));
											} else {
												smrVOSet.add(new ChatMessageRecordVO(new Long(from), content, sendTime, null, new Long(to)));
											}
										}
									} else { // 个人聊天
										Long from = jsonNode.path("from").asLong();
										Long to = jsonNode.path("to").asLong();
										if (StringUtils.isNotBlank(content)) { // content为null，前端转json会报错
											if (StringUtils.equals(currentUser.getId().toString(), from.toString())) {
												smrVOSet.add(new ChatMessageRecordVO(to, content, sendTime, "me"));
											} else {
												smrVOSet.add(new ChatMessageRecordVO(from, content, sendTime, "opposite"));
											}
										}
									}
								} else {
									--total;
								}
							} else {
								--total;
							}
						}
					}
	    		}
	    	}
			// 重新设置总记录数
			totalSB.delete(0, totalSB.length());
			if (total < 0) {
				totalSB.append(0);
			} else {
				totalSB.append(total);
			}
		}
		cmrvoList.addAll(smrVOSet);
    	return cmrvoList;
    }
    /**
     * 解析standardQuery返回的json字符串
     * @param json queryLast返回的json字符串
     * @param cmrvoList ChatMessageRecordVO的list集合
     * @return ChatMessageRecordVO的list集合
     */
    public static List<ChatMessageRecordVO> parseJsonForSeniorQuery(String json,List<ChatMessageRecordVO> cmrvoList,User currentUser,List<Long> userIdList,List<Long> groupIdList,StringBuffer totalSB){
		if (cmrvoList == null) {
			cmrvoList = new ArrayList<ChatMessageRecordVO>();
		}
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		JsonNode node = null;
		try {
			node = mapper.readTree(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Set<ChatMessageRecordVO> smrVOSet = new HashSet<ChatMessageRecordVO>(); // 使用set集合便于去重
		if (node != null && node.path("responsecode").asText().equals("_200")) {
			node = node.path("data");
			String maxCount = node.path("maxCount").asText() == null ? "" : node.path("maxCount").asText().trim();
			totalSB.append(maxCount); // 设置总记录数
			node = node.path("results");
			Iterator<JsonNode> resultIte = node.iterator();
			while (resultIte.hasNext()) {
	    		JsonNode jsonNode = resultIte.next();
	    		Long type = jsonNode.path("type").asLong();
				if (type != null) {
					String content = jsonNode.path("content").asText() == null ? null : jsonNode.path("content").asText().trim();
					if (StringUtils.isNotBlank(content)) {
						JsonNode contentNode = null;
						try {
							contentNode = mapper.readTree(content);
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (contentNode != null) {
							String subType = contentNode.path("subType").asText(); // subType text，file
							String data = contentNode.path("data").asText();
							// type 1-图片 2-文件 3-文本 4-多媒体 	text picture file multimedia
							java.util.Date sendTime = null;
							if (StringUtils.isNotBlank(jsonNode.path("sendTime").asText())) {
								sendTime = new Date(jsonNode.path("sendTime").asLong());
							}
							if (type - 2 == 0) { // 群组聊天
								String from = jsonNode.path("from").asText();
								String to = jsonNode.path("to").asText();
								if (StringUtils.contains(to, "G")) {
									to = StringUtils.substring(to, 1);
								}
								if (StringUtils.isNotBlank(content)) { // content为null，前端转json会报错
									if (StringUtils.equals(from,currentUser.getId().toString())) {
										smrVOSet.add(new ChatMessageRecordVO(new Long(from), content, sendTime, "me", new Long(to)));
									} else {
										smrVOSet.add(new ChatMessageRecordVO(new Long(from), content, sendTime, null, new Long(to)));
									}
								}
							} else { // 个人聊天
								Long from = jsonNode.path("from").asLong();
								Long to = jsonNode.path("to").asLong();
								if (StringUtils.isNotBlank(content)) { // content为null，前端转json会报错
									if (StringUtils.equals(currentUser.getId().toString(), from.toString())) {
										smrVOSet.add(new ChatMessageRecordVO(to, content, sendTime, "me"));
									} else {
										smrVOSet.add(new ChatMessageRecordVO(from, content, sendTime, "opposite"));
									}
								}
							}
						}
					}
	    		}
	    	}
		}
		cmrvoList.addAll(smrVOSet);
    	return cmrvoList;
    }
    
    /**
	 * 更新StandardQuery查询结果的List<ChatMessageRecordVO>集合的用户或群组信息
	 * 
	 * @param userIdList
	 *            userId的list集合
	 * @param cmrvoList
	 *            ChatMessageRecordVO的list集合
	 */
	public static void updateUserDataOfStandardQueryResult(List<ChatMessageRecordVO> cmrvoList,User fromUser) {
		List<Long> userIdList = new ArrayList<Long>();
		List<Long> groupIdList = new ArrayList<Long>();
		for (ChatMessageRecordVO vo : cmrvoList) {
			userIdList.add(vo.getUserId());
			if (vo.getGroupId() != null && vo.getGroupId() != 0) {
				groupIdList.add(vo.getGroupId());
			}
		}
		
		List<User> userList = User.queryUserByIds(userIdList);
		List<Group> groupList = Group.queryGroupByIds(groupIdList);
		for (ChatMessageRecordVO vo : cmrvoList) {
			if (CollectionUtils.isNotEmpty(userList)) {
				for (User u : userList) {
					if (vo.getUserId() - u.getId() == 0) {
						vo.setUserName(u.getUserName());
						vo.setEmail(u.getEmail());
						vo.setAvatar(u.getAvatar(70));
//						Long unReadMsgNum = Message.getUnReadMsgNum(u.getId(),fromUser.getId());
//						vo.setMsgNum(unReadMsgNum);
						break;
					}
				}
			}
			if (vo.getGroupId() != null && vo.getGroupId() != 0) {
				if (CollectionUtils.isNotEmpty(groupList)) {
					for (Group group : groupList) {
						if (vo.getGroupId() - group.getId() == 0) {
							vo.setGroupName(group.getGroupName());
							vo.setGroupAvatar(StringUtils.isBlank(group.getHeadUrl()) ? "" : Assets.at(group.getHeadUrl()));
							vo.setGroupType(group.getType() == null ? Group.Type.NORMAL.toString().toLowerCase() : group.getType().toString().toLowerCase());
//							Long unReadMsgNum = Message.getUnReadMsgNum(fromUser.getId(), ChatService.GROUP_PREFIX + group.getId());
//							vo.setMsgNum(unReadMsgNum); // 更新个人未读的群消息个数
							break;
						}
					}
				}
			}
		}
	}
	
	public static List<GroupMemberVO> queryGroupMembers(Long groupId) throws IOException {
		String json = ChatMessageHttpClient.queryMembers(groupId, Group.Type.TRANSLATE.ordinal());
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		JsonNode node = null;
		try {
			node = mapper.readTree(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<GroupMemberVO> gmVOList = new ArrayList<GroupMemberVO>();
		List<Long> userIdList = new ArrayList<Long>();
		
		if (node != null && node.path("responsecode").asText().equals("_200")) {
			node = node.path("data");
			Iterator<JsonNode> ite =node.iterator();
			while(ite.hasNext()){
				Long userId = ite.next().asLong();
				GroupMemberVO vo = new GroupMemberVO();
				vo.setUserId(userId);
				userIdList.add(userId);
				gmVOList.add(vo);
			}
		}
		ChatGroupVO groupVO = queryGroupById(groupId);
		if (groupVO != null && groupVO.getTranslatorId() != null) {
			for (GroupMemberVO gmVO : gmVOList) {
				if (gmVO.getUserId() - groupVO.getTranslatorId() == 0) {
					gmVO.setRole(Group.Type.TRANSLATE.toString().toUpperCase());
					break;
				}
			}
		}
		
		if(CollectionUtils.isNotEmpty(userIdList)) {
			List<User> userList = User.queryUserByIds(userIdList);
			for(User user : userList) {
				for (GroupMemberVO vo : gmVOList) {
					if (vo.getUserId() - user.getId() == 0) {
						vo.setUserName(user.getName());
						break;
					}
				}
			}
		}
		updateUserNameOfMemberuserNameIsEmpty(gmVOList);
		return gmVOList;
	}
	
	/**
	 * 根据群组的id查询群信息
	 * @param groupId
	 * @return
	 * @throws IOException
	 */
	public static ChatGroupVO queryGroupById(Long groupId) throws IOException {
		List<String> groupIdList = new ArrayList<String>();
		groupIdList.add(ChatService.GROUP_PREFIX + groupId);
		String json = ChatMessageHttpClient.queryGroups(groupIdList);
		
		List<ChatGroupVO> groupList = new ArrayList<ChatGroupVO>();
		
		parseGroupListForqueryGroup(groupList, json);
		
		if (CollectionUtils.isNotEmpty(groupList)) {
			if (groupList.size() == 1) {
				return groupList.get(0);
			}
		}
		return null;
	}
	
	/**
	 * 根据群组的id查询群信息
	 * @param groupIdList 群组id的集合
	 * @return
	 * @throws IOException
	 */
	public static List<ChatGroupVO> queryGroupListByIds(List<Long> groupIdList) throws IOException {
		List<String> groupIdStrList = new ArrayList<String>();
		for (Long groupId : groupIdList) {
			groupIdStrList.add(ChatService.GROUP_PREFIX + groupId);
		}
		String json = ChatMessageHttpClient.queryGroups(groupIdStrList);
		
		List<ChatGroupVO> groupList = new ArrayList<ChatGroupVO>();
		parseGroupListForqueryGroup(groupList, json);
		
		return groupList;
	}
	
	/**
	 * 解析群组数据
	 * <br/> 解析群组的集合
	 * @param groupList
	 * @param json
	 * @return
	 */
	private static List<ChatGroupVO> parseGroupListForqueryGroup(List<ChatGroupVO> groupList,String json) {
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		JsonNode node = null;
		try {
			node = mapper.readTree(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (node != null && node.path("responsecode").asText().equals("_200")) {
			node = node.path("data");
			Iterator<JsonNode> ite = node.iterator();
			while (ite.hasNext()) {
				JsonNode jsonNode = ite.next();
				String id = jsonNode.path("id").asText();
				if (StringUtils.contains(id, "G")) {
					id = StringUtils.substring(id, 1);
				}
				String name = jsonNode.path("name").asText();
				Integer type = jsonNode.path("type").asInt();
				Long ownerId = jsonNode.path("owner").asLong();
				Long translatorId = jsonNode.path("translator").asLong();
				if (StringUtils.isNotEmpty(id)) {
					ChatGroupVO group = new ChatGroupVO(new Long(id), name, ownerId, type, translatorId);
					groupList.add(group);
				}
			}
		}
		return groupList;
	}
	
	
	/**
	 * 更新用户名为空的GroupMemberVO的userName
	 * <br/> 根据用户id查询用户中心用户
	 * @param voList
	 */
	private static void updateUserNameOfMemberuserNameIsEmpty(List<GroupMemberVO> voList) {
		List<Long> userIdList = new ArrayList<Long>();
		List<GroupMemberVO> gmVOList = new ArrayList<GroupMemberVO>(); // 用户名为空的ChatMessageRecordVO
		if (CollectionUtils.isNotEmpty(voList)) {
			for (GroupMemberVO vo : voList) {
				if(vo.getUserId() != null && StringUtils.isEmpty(vo.getUserName())) {
					userIdList.add(vo.getUserId());
					gmVOList.add(vo);
				}
			}
		}
		List<UCUserVO> ucUserVOList = UCClient.queryUserListByIds(userIdList);
		if(CollectionUtils.isNotEmpty(ucUserVOList)) {
			for(UCUserVO ucUserVO : ucUserVOList) {
				for(GroupMemberVO cmrVO : gmVOList) {
					if(cmrVO.getUserId() - ucUserVO.getUserId() == 0) {
						cmrVO.setUserName(StringUtils.isNotBlank(ucUserVO.getUserName()) ? ucUserVO.getUserName() : ucUserVO.getEnglishName());
						break;
					}
				}
			}
		}
	}
}
