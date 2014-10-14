/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年6月4日
 */
package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import models.GroupMember.Role;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.db.jpa.JPA;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import utils.Assets;
import vo.page.Page;

import com.fasterxml.jackson.databind.JsonNode;
import common.Constants;

import controllers.base.ObjectNodeResult;
import ext.MessageCenter.Message.chatMessage.ChatMessageHttpClient;
import ext.MessageCenter.utils.MCMessageUtil;
import ext.search.GTransformer;
import ext.search.SearchHttpClient;

/**
 * @ClassName: Group
 * @Description: 群组实体
 * @date 2014年6月4日 下午3:41:26
 * @author RenYouchao
 * 
 */
@Entity
@Table(name = "tb_group")
public class Group {

	/** 主键自增 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	/** 群组名称 **/
	public String groupName;
	/** 成员个数 **/
	public Long countMem = 0L;
	/** 翻译者个数 **/
	public Long countTranslate = 0L;
	/** 创建日期 **/
	public Date createDate;
	/** 头像 **/
	public String headUrl;
	/** 群组背景图 **/
	public String backgroudUrl;
	
	@Column(name = "industryId", insertable = false, updatable = false)
	public Long industryId;
	/** 群组行业 **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "industryId")
	public SkillTag industry;
	/** 群组说明 **/
	@Column(length = 4000)
	public String groupInfo;
	/** 群主 */
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ownerId")
	public GroupMember owner;
	/** 群标签模式 ["ddd",""] */
	@Column(length = 4000)
	public String tags;
	/** 群组成员 **/
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "groupId")
	public Set<GroupMember> groupmembers;
	/** 群组类型 **/
	@Enumerated(EnumType.ORDINAL)
	@Column(length = 1)
	public Type type;
	/** 群组权限 **/
	@Enumerated(EnumType.ORDINAL)
	@Column(length = 1)
	public GroupPriv groupPriv;
	/** 群组成员最大数量限制 （默认100人，包括群主） */
	public Long maxMemberNum = 100L;

	/**
	 * @ClassName: Type
	 * @Description: 群组的类型 NORMAL：普通群组，TRANSLATE：翻译群组， MULTICOMMUNICATE：多人会话
	 */
	public enum Type {
		NORMAL, TRANSLATE, MULTICOMMUNICATE;
		
		/**
         * 根据名称获取枚举，忽略大小写
         * @return
         */
		public static Type getByName(String name) {
		    Type type = null;
		    for (Type e : Type.values()) {
		        if (e.name().equalsIgnoreCase(name)) {
		            type = e;
		            break;
	            }
	        }
		    return type;
		}
		
		public static Type getByOrdinal(int ordinal) {
			Type[] values = Type.values();
	        if (ordinal < 0 || ordinal > (values.length - 1)) {
	            return null;
	        } else {
	            return values[ordinal];
	        }
	    }
	}

	/**
	 * @ClassName: GroupCategory
	 * @Description: 群组的权限
	 */
	public enum GroupPriv {
		PUBLIC, APPLY;
		
		/**
		 * 根据名称获取枚举，忽略大小写
		 * @return
		 */
		public static GroupPriv getByName(String name) {
		    GroupPriv priv = null;
		    for (GroupPriv e : GroupPriv.values()) {
		        if (e.name().equalsIgnoreCase(name)) {
		            priv = e;
		            break;
		        }
		    }
		    
		    return priv;
		}
	}

	/**
	 * 群组成员最大数量限制（默认100人，包括群主）
	 */
	public static final long MAX_MEMBERNUM = 1 * 100L;

	public Group() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Group(String groupName, Long countMem, Long countTranslate,
			Date createDate, Set<GroupMember> groupmembers, Type type) {
		this.groupName = groupName;
		this.countMem = countMem;
		this.countTranslate = countTranslate;
		this.createDate = createDate;
		this.groupmembers = groupmembers;
		this.type = type;
	}

	public Group(String groupName, Long countMem, Long countTranslate,
			Date createDate, GroupMember owner, Set<GroupMember> groupmembers,
			Type type) {
		this.groupName = groupName;
		this.countMem = countMem;
		this.countTranslate = countTranslate;
		this.createDate = createDate;
		this.owner = owner;
		this.groupmembers = groupmembers;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Long getCountMem() {
		return countMem;
	}

	public void setCountMem(Long countMem) {
		this.countMem = countMem;
	}

	public Long getCountTranslate() {
		return countTranslate;
	}

	public void setCountTranslate(Long countTranslate) {
		this.countTranslate = countTranslate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public GroupMember getOwner() {
		return owner;
	}

	public void setOwner(GroupMember owner) {
		this.owner = owner;
	}

	public Set<GroupMember> getGroupmembers() {
		return groupmembers;
	}

	public void setGroupmembers(Set<GroupMember> groupmembers) {
		this.groupmembers = groupmembers;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getBackgroudUrl() {
		return backgroudUrl;
	}

	public void setBackgroudUrl(String backgroudUrl) {
		this.backgroudUrl = backgroudUrl;
	}

	public SkillTag getIndustry() {
		return industry;
	}

	public void setIndustry(SkillTag industry) {
		this.industry = industry;
	}

	public String getGroupInfo() {
		return groupInfo;
	}

	public void setGroupInfo(String groupInfo) {
		this.groupInfo = groupInfo;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Long getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Long industryId) {
        this.industryId = industryId;
    }

    public GroupPriv getGroupPriv() {
        return groupPriv;
    }

    public void setGroupPriv(GroupPriv groupPriv) {
        this.groupPriv = groupPriv;
    }

    public Long getMaxMemberNum() {
        return maxMemberNum;
    }

    public void setMaxMemberNum(Long maxMemberNum) {
        this.maxMemberNum = maxMemberNum;
    }

    /**
	 * 新增组
	 * 
	 * @param group
	 *            group对象
	 * @return
	 */
	public static Group saveGroup(Group group) {
		JPA.em().persist(group);
		return group;
	}

	/**
	 * 新增组
	 * 
	 * @return
	 */
	public Group saveGroup() {
		JPA.em().persist(this);
		return this;
	}

	/**
	 * 根据群组id获取对应的群组
	 */
	public static List<Group> queryGroupByIds(List<Long> s) {
		if (CollectionUtils.isNotEmpty(s)) {
			List<Group> groupList = JPA.em().createQuery("from Group where id in (:ids)", Group.class)
					.setParameter("ids", s).getResultList();
			if (CollectionUtils.isNotEmpty(groupList)) {
				return groupList;
			}
		}

		return new ArrayList<Group>();
	}
	
	public static Map<Long, Group> queryGroupMapByIds(List<Long> s) {
	    if (CollectionUtils.isNotEmpty(s)) {
	        List<Group> groupList = JPA.em().createQuery("from Group where id in (:ids)", Group.class)
	                .setParameter("ids", s).getResultList();
	        if (CollectionUtils.isNotEmpty(groupList)) {
	            HashMap<Long, Group> groupMap = new HashMap<Long, Group>();
	            for (Group group : groupList) {
	                groupMap.put(group.getId(), group);
                }
	            return groupMap;
	        }
	    }
	    
	    return new HashMap<Long, Group>();
	}

	/**
	 * 根据id获取对应的群组
	 * 
	 * @param groupId
	 * @return
	 */
	public static Group queryGroupById(Long groupId) {
		List<Group> groupList = JPA.em().createQuery("from Group g left join fetch g.owner go where g.id = :id", Group.class)
				.setParameter("id", groupId).getResultList();
		if (CollectionUtils.isNotEmpty(groupList)) {
			return groupList.get(0);
		}
		return null;
	}
	
	/**
	 * 根据群组名称查询用户Id的集合
	 * @param groupName
	 * @return
	 */
	public static List<Long> findGroupIdListByGroupName(String groupName) {
		List<Long> groupIdList = JPA.em().createQuery("select id from Group where groupName like :groupName",Long.class)
				.setParameter("groupName", "%" + groupName + "%").getResultList();
		if (CollectionUtils.isNotEmpty(groupIdList)) {
			return groupIdList;
		}
		return new ArrayList<Long>();
	}
	
	/**
	 * 查询某个用户对应的群组，群组名称包含groupName的值
	 * @param groupName
	 * @param userId
	 * @return
	 */
	public static List<Long> findGroupIdListByGroupNameAndUserId(String groupName,Long userId){
		List<Long> groupIdList = JPA.em().createQuery("select g.id from Group g,GroupMember gm where gm in elements(g.groupmembers) and g.groupName like :groupName and gm.userId = :userId",Long.class)
				.setParameter("groupName", "%" + groupName + "%").setParameter("userId", userId).getResultList();
		if (CollectionUtils.isNotEmpty(groupIdList)) {
			return groupIdList;
		}
		return new ArrayList<Long>();
	}
	
	/**
	 * 根据id删除
	 * @param groupId
	 */
	public static void deleteById(final Long groupId) {
		JPA.em().createQuery("delete from Group where id = :id").setParameter("id", groupId).executeUpdate();
		Akka.system().scheduler().scheduleOnce(Duration.create(100, TimeUnit.MILLISECONDS), new Runnable() {
			public void run() {
				GTransformer.tranDeleteNVP(groupId);
			}
		}, Akka.system().dispatcher());

	}

	/**
	 * 根据群组id查询群组的成员
	 */
	public static List<GroupMember> queryUserUnderGroup(Long groupId) {
		List<Group> groupList = JPA.em().createQuery("from Group where id = :id", Group.class)
				.setParameter("id", groupId).getResultList();
		List<GroupMember> gmList = new ArrayList<GroupMember>();
		if (CollectionUtils.isNotEmpty(groupList)) {
			gmList.addAll(groupList.get(0).groupmembers);
			return gmList;
		}
		return gmList;
	}
	
	/**
	 * 根据群组id查询群组的成员
	 * <br/>
	 * 提供分页
	 */
	public static List<User> queryUserUnderGroup(Long groupId,int page,int pageSize) {
//		List<User> userList = JPA.em().createQuery("select u from User u where u.id in (select gm.userId from Group g,GroupMember gm where gm in elements(g.groupmembers) and g.id = :id)", User.class)
//				.setParameter("id", groupId).setFirstResult(page * pageSize).setMaxResults(pageSize).getResultList();
		// 代码优化
		List<User> userList = JPA.em().createQuery("select u from User u where u.id in (select gm.userId from GroupMember gm where gm.group.id = :id)", User.class)
				.setParameter("id", groupId).setFirstResult(page * pageSize).setMaxResults(pageSize).getResultList();
		return userList;
	}
	
	/**
	 * 根据群组id查询群组的成员的总记录数
	 */
	public static Long queryTotalCountOfUserUnderGroup(Long groupId) {
//		Long total = JPA.em().createQuery("select count(gm.id) from Group g,GroupMember gm where gm in elements(g.groupmembers) and g.id = :id",Long.class).setParameter("id", groupId)
//				.getSingleResult();
		// 代码优化
		Long total = JPA.em().createQuery("select count(gm.userId) from GroupMember gm where gm.group.id = :id",Long.class).setParameter("id", groupId)
				.getSingleResult();
		if (total == null) {
			return 0L;
		}
		return total;
	}
	
	/**
	 * 根据群组下面的所有用户的list集合
	 */
	public static List<User> queryUserListOfGroup(Long groupId) {
		List<User> userList = JPA.em().createQuery("select u from User u where u.id in (select gm.userId from Group g,GroupMember gm where gm in elements(g.groupmembers) and g.id = :id)", User.class)
				.setParameter("id", groupId).getResultList();
		if (CollectionUtils.isEmpty(userList)) {
			return new ArrayList<User>();
		}
		return userList;
	}
	

	/**
	 * 分页查询Group集合
	 * 
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<Group> queryAllGroup(int page, int pageSize) {
		List<Group> groupList = JPA.em().createQuery("from Group order by id", Group.class)
				.setFirstResult(page * pageSize).setMaxResults(pageSize)
				.getResultList();
		if (CollectionUtils.isNotEmpty(groupList)) {
			return groupList;
		}
		return new ArrayList<Group>();
	}

	/**
	 * 查询总记录数
	 * 
	 * @return
	 */
	public static Long queryTotalCount() {
		Long total = (Long) JPA.em().createQuery("select count(id) from Group").getSingleResult();
		if (total != null) {
			return total;
		}
		return 0L;
	}

	/**
	 * 保存或更新群组信息
	 */
	public static ObjectNodeResult saveGroupByJson(User currentUser, JsonNode json) {
	    JPA.em().clear();
		ObjectNodeResult result = new ObjectNodeResult();
//		String[] notUseStrs = { "helome", "嗨啰" };

		Group group = new Group();
		Group dbGroup = null; // 从数据库中查询出来的Group对象
		// id
		if (json.has("id")) {
			Long id = json.findPath("id").asLong();
			if (id != null) {
				dbGroup = Group.queryGroupById(id);
				if (dbGroup != null) {
					if (dbGroup.getType() != Group.Type.NORMAL) {
						return result.error("该群组不是普通群组，不能在这里修改", "900001");
					}
					if (dbGroup.getType() == Group.Type.NORMAL && dbGroup.getOwner() != null
							&& dbGroup.getOwner().getUserId() - currentUser.getId() != 0) {
						return result.error("你不是该群组的创建者，修改失败", "-301");
					}
					group = dbGroup;
				}
			}
		}
		// 名称
		String oldGroupName = dbGroup == null ? "" : dbGroup.getGroupName();
        String groupName = oldGroupName;
        if (json.has("groupName")) {
			groupName = json.findPath("groupName").asText();
			if (StringUtils.isBlank(groupName)) {
				return result.error("名称不能为空", "900002");
			}
//			for (String item : notUseStrs) {
//				if (StringUtils.contains(groupName, item)) {
//					return result.error("不能以'" + item + "'作为群名称", "900003");
//				}
//			}
			if (StringUtils.isNotBlank(groupName)) {
				groupName = common.SensitiveWordsFilter.doFilter(groupName);
				groupName = common.ReplaceWordsFilter.doFilter(groupName);
			}
			group.setGroupName(groupName.trim());
		}
        // 头像
        String oldHeadUrl = dbGroup == null ? "" : dbGroup.getHeadUrl();
        String headUrl = oldHeadUrl;
        if (json.has("headUrl")) {
			headUrl = json.findPath("headUrl").asText();
			group.setHeadUrl(headUrl);
		}
		// 所属分类
		if (json.has("industry")) {
			Long industryId = json.findPath("industry").asLong();
			SkillTag sk = SkillTag.getTagById(industryId);
			group.setIndustry(sk);
			if (sk != null) {
				group.setIndustryId(sk.getId());
			}
		}
		// 群说明
		if (json.hasNonNull("groupInfo")) {
			String groupInfo = json.findPath("groupInfo").asText();
			if (groupInfo.length() > 500) {
				return result.error("群说明最多500个字符", "900004");
			}
			groupInfo = common.SensitiveWordsFilter.doFilter(groupInfo);
			groupInfo = common.ReplaceWordsFilter.doFilter(groupInfo);
			group.setGroupInfo(groupInfo.trim());
		}
		// 背景图片
		if (json.has("backgroudUrl")) {
			group.setBackgroudUrl(json.findPath("backgroudUrl").asText());
		}
		// 加入权限
		if (group.getGroupPriv() == null) {
		    group.setGroupPriv(GroupPriv.PUBLIC);
		}
		if (json.has("groupPriv")) {
			Integer groupPriv = json.findPath("groupPriv").asInt(-1);
			GroupPriv[] groupPrivs = GroupPriv.values();
			if (groupPriv >= 0 && groupPriv < groupPrivs.length ) {
				group.groupPriv = groupPrivs[groupPriv];
			}
		}
		// 群标签
		if (json.has("tags") && json.get("tags").isArray()) {
			String tags = json.findPath("tags").toString();
			if (StringUtils.isNotBlank(tags)) {
				tags = common.SensitiveWordsFilter.doFilter(tags);
				tags = common.ReplaceWordsFilter.doFilter(tags);
			}
			group.setTags(tags);
		}
		// 普通群组
		group.type = Type.NORMAL;
		if (group.getId() == null || dbGroup == null) {
			// 群主
			// 创建日期
			group.setCreateDate(new java.util.Date());
			// 群成员数
			group.setCountMem(1L);
			GroupMember owner = new GroupMember(currentUser.getId(), currentUser.getName(), currentUser.getEmail(), Role.MEMBER, group.getCreateDate()).saveGroupMember();
			owner.setGroup(group);
			group.setOwner(owner);
		}

		group.saveOrUpdate();
		
		if (group.getId() == null || dbGroup == null) {
			Date joinDate = new Date();
			// 调用知识库系统的接口 /relation/group/add ，添加用户与群组的关系
			boolean flag = ChatMessageHttpClient.addGroupMember(currentUser, models.service.ChatService.GROUP_PREFIX + group.getId(), joinDate);
			if (!flag) {
				ChatMessageHttpClient.addGroupMember(currentUser, models.service.ChatService.GROUP_PREFIX + group.getId(), joinDate);
			}
			
			String createGroupMessageId = UUID.randomUUID().toString().toUpperCase();
			// 调用MC的通知接口
			Logger.info("调用MC创建群的接口  ---> " + "群id:" + group.getId() + "，群主id:" + group.getOwner().getUserId() + "群名称：" + group.getGroupName());
			MCMessageUtil.pushCreateGroupMessage(group.getId(), group.getOwner().getUserId(), group.getGroupName());
			MCMessageUtil.pushGroupNotice(createGroupMessageId, 4, group.getId(), currentUser.getId(), group.getGroupName(), currentUser.getName(), currentUser.getName() + "创建群组：" + group.getGroupName(),0);
			
			// 发送群主创建群组的消息
			// type：消息类型 1（聊天记录），2（通知消息）....
	        String content = "{\"subType\":\"text\",\"type\":2,\"data\":\"" + currentUser.getName() + "创建群组：" + group.getGroupName() + "\",\"messageId\":\"" + createGroupMessageId + "\"}";
	        try {
				ChatMessageHttpClient.sendChatMessage(currentUser, models.service.ChatService.GROUP_PREFIX + group.getId(), joinDate, content, ChatMessageHttpClient.ChatType.MANY2MANY, 2);
			} catch (IOException e) {
				if (Logger.isErrorEnabled()) {
					Logger.error("发送创建群组的消息失败", e);
				}
			}
		}
		
		
		if (group.getId() != null && dbGroup != null) {
			if (!StringUtils.equals(groupName, oldGroupName) && !StringUtils.equals(headUrl, oldHeadUrl)) {
				MCMessageUtil.pushProfileNotice(UUID.randomUUID().toString().toUpperCase(), 6, group.getId(), currentUser.getId(), group.getGroupName(), currentUser.getName(), currentUser.getName() + "修改了群名称和群头像", 0, StringUtils.isBlank(group.getHeadUrl()) ? "" : Assets.at(group.getHeadUrl()));
			} else if(!StringUtils.equals(groupName, oldGroupName)) {
				MCMessageUtil.pushProfileNotice(UUID.randomUUID().toString().toUpperCase(), 6, group.getId(), currentUser.getId(), group.getGroupName(), currentUser.getName(), currentUser.getName() + "修改了群名称", 0, "");
			} else if(!StringUtils.equals(headUrl, oldHeadUrl)){
				MCMessageUtil.pushProfileNotice(UUID.randomUUID().toString().toUpperCase(), 6, group.getId(), currentUser.getId(), "", currentUser.getName(), currentUser.getName() + "修改了群头像", 0, StringUtils.isBlank(group.getHeadUrl()) ? "" : Assets.at(group.getHeadUrl()));
			}
		}
		
		result.put("groupId", group.getId());
		result.put("groupType", group.getType().toString().toLowerCase());
		return result;
	}
	
	/**
	 * 保存或更新当前对象
	 */
	public void saveOrUpdate() {
		if (id != null) {
			this.countMem = new Long(this.getGroupmembers() == null ? 0 : this.getGroupmembers().size());
			JPA.em().merge(this);
		} else {
			JPA.em().persist(this);
		}
		if (this.type.equals(Group.Type.NORMAL)){
			final Group thiz = this;
			Akka.system().scheduler().scheduleOnce(Duration.create(100, TimeUnit.MILLISECONDS), new Runnable() {
				public void run() {
					System.out.println(thiz.industryId);
					GTransformer gtf = new GTransformer(thiz);
					SearchHttpClient.createOrUpdateDocument(gtf.tranInputsNVP());
				}
			}, Akka.system().dispatcher());
		}
	}
	
	/**
     * 获取静态资源文件访问路径 - 头像
     * @param userId 群组创建者
     * @param size
     * @param currentTimestamp
     * @return
     */
    public static String getAvatarFileRelativePath(Long userId, int size,Long currentTimestamp) {
        StringBuilder avatar = new StringBuilder("topx/uploadfile/groupAvatar/");
        avatar.append(userId).append("/").append(currentTimestamp).append("/avatar_").append(size).append(".jpg");
        return avatar.toString();
    }
    
    /**
     * 获取静态资源文件访问路径 - 头部背景
     * @param userId 群组创建者
     * @param size
     * @param currentTimestamp
     * @return
     */
    public static String getHeadBackGroundFileRelativePath(Long userId, int size,Long currentTimestamp,String suffix) {
        StringBuilder avatar = new StringBuilder("topx/uploadfile/groupHeadBackGround/");
        avatar.append(userId).append("/").append(currentTimestamp).append("/headbackgroud_").append(size).append(suffix);
        return avatar.toString();
    }
    
    /**
     * 根据群加入权限分类查询某个用户建立的普通群组（公开自由加入的、需要申请加入的）
     * <br/> 
     * 分页查询
     * @param userId
     * @param groupPriv
     * @param page
     * @param pageSize
     * @return
     */
    public static List<Group> queryGroupByUserIdAndGroupPriv(Long userId,GroupPriv groupPriv,String groupName,int page, int pageSize) {
		List<Group> groupList = null;
		if (groupPriv != null) {
			groupList = JPA.em().createQuery("from Group where groupName like :groupName and owner.userId = :userId and groupPriv = :groupPriv and type = :type order by createDate desc", Group.class)
					.setParameter("groupName", "%" + groupName + "%").setParameter("userId", userId).setParameter("groupPriv", groupPriv).setParameter("type", Type.NORMAL)
					.setFirstResult(page * pageSize).setMaxResults(pageSize)
					.getResultList();
		} else { // groupPriv为空，查询全部的
			groupList = JPA.em().createQuery("from Group where groupName like :groupName and owner.userId = :userId and type = :type order by groupPriv desc,createDate desc", Group.class)
					.setParameter("groupName", "%" + groupName + "%").setParameter("userId", userId).setParameter("type", Type.NORMAL)
					.setFirstResult(page * pageSize).setMaxResults(pageSize)
					.getResultList();
		}
		
		if (CollectionUtils.isNotEmpty(groupList)) {
			return groupList;
		}
		return new ArrayList<Group>();
	}
    
    /**
	 * 查询总记录数
	 * 
	 * @return
	 */
	public static Long queryTotalCountOfGroupByUserIdAndGroupPriv(Long userId,GroupPriv groupPriv,String groupName) {
		Long total = null;
		if (groupPriv != null) {
			total = (Long) JPA.em().createQuery("select count(id) from Group where groupName like :groupName and owner.userId = :userId and groupPriv = :groupPriv and type = :type")
					.setParameter("groupName", "%" + groupName + "%").setParameter("userId", userId).setParameter("groupPriv", groupPriv).setParameter("type", Type.NORMAL)
					.getSingleResult();
		} else {
			total = (Long) JPA.em().createQuery("select count(id) from Group where groupName like :groupName and owner.userId = :userId and type = :type")
					.setParameter("groupName", "%" + groupName + "%").setParameter("userId", userId).setParameter("type", Type.NORMAL)
					.getSingleResult();
		}
		if (total != null) {
			return total;
		}
		return 0L;
	}
	
	/**
	 * 根据群加入权限分类查询某个用户加入的普通群组（公开自由加入的、需要申请加入的）
     * <br/> 
     * 分页查询
	 * @param userId
	 * @param groupPriv
	 * @return
	 */
	public static List<Group> queryJoinedGroupByUserIdAndGroupPriv(Long userId,GroupPriv groupPriv,String groupName,int page, int pageSize){
		List<Group> groupList = null;
		if (groupPriv != null) {
			groupList = JPA.em().createQuery("select g from Group g,GroupMember gm where gm in elements(g.groupmembers) and g.groupName like :groupName and gm.userId = :userId and g.owner.userId !=:userId and g.groupPriv = :groupPriv and g.type = :type order by g.createDate desc", Group.class)
					.setParameter("groupName", "%" + groupName + "%").setParameter("userId", userId).setParameter("groupPriv", groupPriv).setParameter("type", Type.NORMAL)
					.setFirstResult(page * pageSize).setMaxResults(pageSize)
					.getResultList();
		} else {
			groupList = JPA.em().createQuery("select g from Group g,GroupMember gm where gm in elements(g.groupmembers) and g.groupName like :groupName and gm.userId = :userId and g.owner.userId !=:userId and g.type = :type order by g.groupPriv desc,g.createDate desc", Group.class)
					.setParameter("groupName", "%" + groupName + "%").setParameter("userId", userId).setParameter("type", Type.NORMAL)
					.setFirstResult(page * pageSize).setMaxResults(pageSize)
					.getResultList();
		}
		
		if (CollectionUtils.isNotEmpty(groupList)) {
			return groupList;
		}
		return new ArrayList<Group>();
	}
	
	/**
	 * 查询用户对应的临时群组（包括临时群组）
	 * @return
	 */
	public static List<Group> queryTempGroupByUserId(Long userId,String groupName,int page,int pageSize) {
		List<Group> groupList = JPA.em().createQuery("select g from Group g,GroupMember gm where gm in elements(g.groupmembers) and g.groupName like :groupName and gm.userId = :userId and (g.type = :type or g.type = :type2) order by g.createDate desc",Group.class)
//		List<Group> groupList = JPA.em().createQuery("select g from Group g where g.id in (select gm.group.id from GroupMember gm where gm.userId = :userId) and g.type = :type order by g.id desc",Group.class)	
				.setParameter("groupName", "%" + groupName + "%").setParameter("userId", userId).setParameter("type", Type.TRANSLATE).setParameter("type2", Type.MULTICOMMUNICATE)
				.setFirstResult(page * pageSize).setMaxResults(pageSize)
				.getResultList();
		if (CollectionUtils.isNotEmpty(groupList)) {
			return groupList;
		}
		
		return new ArrayList<Group>();
	}
	
	/**
	 * 查询用户对应的临时群组的总记录数
	 * @return
	 */
	public static Long queryTotalCountOfTempGroupByUserId(Long userId,String groupName){
		Long total = null;
		total = (Long) JPA.em().createQuery("select count(g.id) from Group g,GroupMember gm where gm in elements(g.groupmembers) and g.groupName like :groupName and gm.userId = :userId and (g.type = :type or g.type = :type2)")
				.setParameter("groupName", "%" + groupName + "%").setParameter("userId", userId).setParameter("type", Type.TRANSLATE).setParameter("type2", Type.MULTICOMMUNICATE)
				.getSingleResult();
		if (total != null) {
			return total;
		}
		return 0L;
	}
	
	
	/**
	 * 查询用户加入的群组的id的集合
	 * @return
	 */
	public static List<Long> queryJoinedGroupIdByUserId(Long userId) {
		List<Long> groupIdList = JPA.em().createQuery("select g.id from Group g,GroupMember gm where gm in elements(g.groupmembers) and gm.userId = :userId and g.type = :type order by g.id desc", Long.class)
				.setParameter("userId", userId).setParameter("type", Type.NORMAL)
				.getResultList();
		if (CollectionUtils.isNotEmpty(groupIdList)) {
			return groupIdList;
		}
		return new ArrayList<Long>();
	}
	
	/**
	 * 查询根据群加入权限分类查询某个用户加入的普通群组的总记录数
	 * 
	 * @return
	 */
	public static Long queryTotalCountOfJoinedGroupByUserIdAndGroupPriv(Long userId,GroupPriv groupPriv,String groupName) {
		Long total = null;
		if (groupPriv != null) {
			total = (Long) JPA.em().createQuery("select count(g.id) from Group g,GroupMember gm where gm in elements(g.groupmembers) and g.groupName like :groupName and gm.userId = :userId and g.owner.userId !=:userId and g.groupPriv = :groupPriv and g.type = :type")
					.setParameter("groupName", "%" + groupName + "%").setParameter("userId", userId).setParameter("groupPriv", groupPriv).setParameter("type", Type.NORMAL)
					.getSingleResult();
		} else {
			total = (Long) JPA.em().createQuery("select count(g.id) from Group g,GroupMember gm where gm in elements(g.groupmembers) and g.groupName like :groupName and gm.userId = :userId and g.owner.userId !=:userId and g.type = :type")
					.setParameter("groupName", "%" + groupName + "%").setParameter("userId", userId).setParameter("type", Type.NORMAL)
					.getSingleResult();
		}
		if (total != null) {
			return total;
		}
		return 0L;
	}
	
	/**
     * 分页查询群组
     * 
     * @param page 页数，从1开始
     * @param pageSize 每页条数
     * @param industryId 行业Id，非必须
     * @param privacy 群组权限，非必须
     * @param skillTag 技能标签，非必须
     * @param groupName 群组名，非必须
     * @param memberUserId 指定群成员用户Id，非必须
     * @param isOwner 指定的群成员是否为群主（群主也是群成员），非必须
     * @param orderBy 排序字段，非必须，该字段必须做校验，只允许规定的值，以免SQL注入
     * @param isDesc 排序方式，非必须
     * @return Page对象
	 */
	public static Page<Group> queryByPage(int page, int pageSize, Long industryId, GroupPriv privacy, String skillTag, 
	        String groupName, List<Type> typeList, Long memberUserId, Boolean isOwner, String orderBy, Boolean isDesc) {
	    StringBuilder hql = new StringBuilder();
	    Map<String, Object> paramMap = new HashMap<>();
	    hql.append("from Group g ");
	    if (memberUserId != null) {
            hql.append(" left join g.groupmembers gm where gm.userId = :memberUserId ");
            paramMap.put("memberUserId", memberUserId);
        } else {
            hql.append(" where 1 = 1");
        }
	    if (memberUserId != null && isOwner != null) {
	        hql.append(" and ((g.owner.userId ").append(isOwner ? " = " : " <> ")
	        .append(" :ownerUserId and g.type = :hasOwnerType) or g.type <> :hasOwnerType) ");
	        paramMap.put("hasOwnerType", Type.NORMAL);
	        paramMap.put("ownerUserId", memberUserId);
	    }
	    if (CollectionUtils.isNotEmpty(typeList)) {
	        hql.append(" and g.type in (:typeList) ");
	        paramMap.put("typeList", typeList);
	    }
	    if (null != industryId) {
	        hql.append(" and g.industry.id = :industryId ");
	        paramMap.put("industryId", industryId);
	    }
	    if (null != privacy) {
	        hql.append(" and g.groupPriv = :privacy ");
	        paramMap.put("privacy", privacy);
	    }
	    if (StringUtils.isNotBlank(skillTag)) {
	        hql.append(" and g.tags like :skillTag ");
	        paramMap.put("skillTag", "%" + skillTag + "%");
	    }
	    if (StringUtils.isNotBlank(groupName)) {
	        hql.append(" and g.groupName like :groupName ");
	        paramMap.put("groupName", "%" + groupName + "%");
	    }
	    
	    String countHql = "select count(g.id) " + hql.toString();
	    
	    // order
	    if (StringUtils.isNotBlank(orderBy)) {
	        hql.append(" order by g.");
	        hql.append(orderBy);
	        hql.append(BooleanUtils.isFalse(isDesc) ? " asc " : " desc ");
	    } else {
	        hql.append(" order by countMem desc, id desc");
	    }
	    hql.insert(0, "select g ");
	    
	    TypedQuery<Group> listTypedQuery = JPA.em().createQuery(hql.toString(), Group.class).setFirstResult((page - 1) * pageSize)
	            .setMaxResults(pageSize);
	    TypedQuery<Long> countTypedQuery = JPA.em().createQuery(countHql, Long.class);
	    for (Entry<String, Object> e : paramMap.entrySet()) {
	        listTypedQuery.setParameter(e.getKey(), e.getValue());
	        countTypedQuery.setParameter(e.getKey(), e.getValue());
        }
	    
	    List<Group> resultList = listTypedQuery.getResultList();
	    Long count = countTypedQuery.getSingleResult();
	    
	    return new Page<Group>(Constants.SUCESS, count, resultList);
	}

}
