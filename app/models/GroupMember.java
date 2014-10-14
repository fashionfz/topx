/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年6月5日
 */
package models;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;

import play.db.jpa.JPA;
import vo.page.Page;

import common.Constants;

/**
 * @ClassName: GroupMember
 * @Description: 群成员
 * @date 2014年6月5日 上午10:58:22
 * @author RenYouchao
 * 
 */

@Entity
@Table(name = "tb_group_member")
public class GroupMember {
	
	/** 主键自增 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id; 
	/**用户id**/
	public Long userId;
	/**用户姓名**/
	public String userName;
	/**用户email**/
	public String email;
    /**角色**/
    @Enumerated(EnumType.ORDINAL)
    @Column(length = 1)
	public Role role;
    /**加入群的时间*/
    public Date joinDate;
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="groupId")
    public Group group;
    
	/**
	 * @ClassName: Role
	 * @Description: 群主角色
	 * @date 2014年6月5日 上午11:08:45
	 * @author RenYouchao
	 */
	public enum Role {
	    MEMBER, TRANSLATE;
	}

	
	public GroupMember() {
	}
	
	public GroupMember(User user, Role role) {
		this.userId = user.getId();
		this.userName = user.userName;
		this.email = user.getEmail();
		this.role = role;
	}
	
	public GroupMember(Long userId, String userName, String email, Role role) {
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		this.role = role;
	}
	
	public GroupMember(Long userId, String userName, String email, Role role,Date joinDate) {
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		this.role = role;
		this.joinDate = joinDate;
	}
	
	public GroupMember(Long userId, String userName, String email, Role role,Date joinDate,Group group) {
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		this.role = role;
		this.joinDate = joinDate;
		this.group = group;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * 新增组员
	 * @return
	 */
	public GroupMember saveGroupMember(){
		JPA.em().persist(this);
		return this;
	}

	/**
	 * 新增组员
	 */
	public static void addGropMember(User user, GroupMember.Role role) {
		if (user == null || role == null) {
			throw new IllegalArgumentException("传入的user对象或者role对象不能为空。");
		}
		JPA.em().persist(new GroupMember(user.getId(), user.getName(), user.getEmail(), role));
	}
	
	/**
	 * 查询用户对应有哪些群成员
	 * @param userId
	 * @return
	 */
	public static List<GroupMember> queryGroupMember(Long userId) {
		if (userId == null) {
			throw new IllegalArgumentException("传入的userId不能为空。");
		}
		List<GroupMember> list = JPA.em().createQuery("from GroupMember where userId=:userId")
				.setParameter("userId", userId).getResultList();
		if (CollectionUtils.isEmpty(list)) {
			return new ArrayList<GroupMember>();
		}
		return list;
	}
	
	/**
	 * 根据用户id和群组id查询对应的GroupMember
	 * @return
	 */
	public static GroupMember queryGroupMemberByUserIdAndGroupId(Long userId, Long groupId) {
		List<GroupMember> list = JPA.em().createQuery("from GroupMember where userId=:userId and group.id=:groupId", GroupMember.class)
				.setParameter("userId", userId).setParameter("groupId", groupId).getResultList();
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}
	
	
	/**
	 * 查询给定的用户id的集合对应的所有的群成员的集合
	 * @param userIdList
	 * @return
	 */
	public static List<GroupMember> queryGroupMemberByUserIds(List<Long> userIdList){
		if (CollectionUtils.isNotEmpty(userIdList)) {
			List<GroupMember> gmList = JPA.em().createQuery("from GroupMember where userId in (:userIds) and group is not null", GroupMember.class)
					.setParameter("userIds", userIdList).getResultList();
			if (CollectionUtils.isNotEmpty(gmList)) {
				return gmList;
			}
		}
		return new ArrayList<GroupMember>();
		
	}
	
	/**
	 * 根据id删除
	 * @param id
	 */
	public static void deleteById(Long id) {
		JPA.em().createQuery("delete from GroupMember where id = :id").setParameter("id", id).executeUpdate();
	}
	
	/**
	 * 根据groupId删除
	 */
	public static void deleteByGroupId(Long groupId) {
		JPA.em().createQuery("update Group set owner = null where id = :id").setParameter("id", groupId).executeUpdate();
		JPA.em().createQuery("delete from GroupMember where group.id = :groupId").setParameter("groupId", groupId).executeUpdate();
	}
	
	/**
	 * 根据群组Id查询指定个数群成员，随机排序
	 */
	public static List<GroupMember> queryByGroupIdWithRandomOrder(Long groupId, Integer num) {
	    String hql = "from GroupMember where group.id = :groupId order by RAND()";
	    List<GroupMember> resultList = JPA.em().createQuery(hql, GroupMember.class).setParameter("groupId", groupId)
	            .setFirstResult(0).setMaxResults(num).getResultList();
	    return resultList;
	}
	
	
    /**
     * 分页查询
     * @param pageIndex 页数，从1开始
     * @param pageSize 每页条数
     * @param groupId 群组Id
     * @param excludeId 排除GroupMember Id,为null则不排除
     * @return
     */
    public static Page<GroupMember> queryPageByGroupId(int start, int pageSize, Long groupId, Long excludeId) {
        String hql = " from GroupMember where group.id = :groupId ";
        if (null != excludeId) {
            hql += " and id <> :excludeId";
        }
        String countHql = "select count(id) " + hql;
    
        TypedQuery<Long> countTypedQuery = JPA.em().createQuery(countHql, Long.class).setParameter("groupId", groupId);
        TypedQuery<GroupMember> contentTypedQuery = JPA.em().createQuery(hql, GroupMember.class)
                .setParameter("groupId", groupId).setFirstResult((start - 1) * pageSize).setMaxResults(pageSize);
   
        if (null != excludeId) {
            countTypedQuery.setParameter("excludeId", excludeId); 
            contentTypedQuery.setParameter("excludeId", excludeId); 
        }
   
        Long count = countTypedQuery.getSingleResult();
        List<GroupMember> resultList = contentTypedQuery.getResultList();
        return new Page<GroupMember>(Constants.SUCESS, count, resultList);
    }
	
	
	/**
	 * 批量判断用户是否加入某些群组
	 * @param userId
	 * @param groupIdList
	 * @return Map<GroupId, isJoined>
	 */
	public static Map<Long, Boolean> checkJoinGroup(Long userId, List<Long> groupIdList) {
	    if (CollectionUtils.isEmpty(groupIdList) || null == userId) {
	        return new HashMap<Long, Boolean>();
	    }
	    String hql = "select group.id from GroupMember where userId = :userId and group.id in (:groupIdList)";
	    List<Long> matchList = JPA.em().createQuery(hql, Long.class).setParameter("userId", userId)
	            .setParameter("groupIdList", groupIdList).getResultList();
	    
	    Map<Long, Boolean> result = new HashMap<Long, Boolean>();
	    for (Long groupId : groupIdList) {
	        result.put(groupId, matchList.contains(groupId));
	    }
	    return result;
	}
	
	public static Map<Long, Boolean> checkJoinGroup(List<Long> userIdList, Long groupId) {
	    if (CollectionUtils.isEmpty(userIdList) || null == groupId) {
	        return new HashMap<Long, Boolean>();
	    }
	    String hql = "select userId from GroupMember where userId in (:userIdList) and group.id = :groupId";
	    List<Long> matchList = JPA.em().createQuery(hql, Long.class).setParameter("userIdList", userIdList)
	            .setParameter("groupId", groupId).getResultList();
	    
	    Map<Long, Boolean> result = new HashMap<Long, Boolean>();
	    for (Long userId : userIdList) {
	        result.put(userId, matchList.contains(userId));
	    }
	    return result;
	}
	
	public static void updateUserNameByUserId(String newName, Long userId) {
	    String hql = "update GroupMember set userName = :newName where userId = :userId and userName != :newName";
	    JPA.em().createQuery(hql).setParameter("newName", newName).setParameter("userId", userId).executeUpdate();
	}
	
}
