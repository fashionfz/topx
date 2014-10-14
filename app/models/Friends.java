package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import common.Constants;
import play.db.jpa.JPA;
import vo.page.Page;

/**
 * @ClassName: Friends
 * @Description: 朋友圈子
 */
@Entity
@Table(name = "tb_friends")
public class Friends {
	
	/**
     * 主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    /**
     * 用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    public User user;
    
    /**
     * 好友
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friendId")
    public User friend;
    
    public Friends(){
    	
    }
    
	public Friends(User user, User friend) {
		this.user = user;
		this.friend = friend;
	}
	
	/**
	 * 新增圈子
	 * @param friends
	 */
	public static Friends saveFriends(Friends friends) {
		JPA.em().persist(friends);
		return friends;
	}

	/**
	 * 新增圈子
	 * @param user
	 * @param friend
	 */
	public static void addFriends(User user, User friend) {
		if (user == null || friend == null) {
			throw new IllegalArgumentException("传入的user对象或者friend对象不能为空。");
		}
		JPA.em().persist(new Friends(user, friend));
	}

	/**
	 * 查询某个用户对应的所有好友
	 * @param userId
	 * @return
	 */
	public static List<User> queryFriends(Long userId) {
		if (userId == null) {
			throw new IllegalArgumentException("传入的userId不能为空。");
		}
		List<User> userList = JPA.em().createQuery("select f.friend from Friends f where f.user.id=:userId",User.class)
				.setParameter("userId", userId).getResultList();
		if (CollectionUtils.isNotEmpty(userList)) {
			return userList;
		}
		return new ArrayList<User>();
	}
	
	/**
	 * 获取某页面的数据
	 * @param page
	 * @param pageSize
	 * @param user
	 * @return
	 */
	public static List<User> queryUserOfCurrentPage(int page, int pageSize, User user) {
		if (user == null) {
			throw new IllegalArgumentException("传入的user对象不能为空。");
		}
		List<User> data = JPA.em().createQuery("select f.friend from Friends f where f.user.id=:userId order by f.friend.id asc",User.class)
				.setParameter("userId", user.getId())
				.setFirstResult(page*pageSize).setMaxResults(pageSize).getResultList();
		return data;
	}
	
	/**
	 * 获取某个用户的好友总数
	 * @param user
	 * @return
	 */
	public static Long queryTotalCount(User user) {
		if (user == null) {
			throw new IllegalArgumentException("传入的user不能为空。");
		}
		return (Long) JPA.em().createQuery("select count(f.friend) from Friends f where f.user.id=:userId")
				.setParameter("userId", user.getId()).getSingleResult();
	}
	
	/**
	 * 获取某页面的好友数据的Page页面数据
	 * @param page
	 * @param pageSize
	 * @param user
	 * @param excludeGroupId 排除指定群组Id的群组成员
	 * @return
	 */
	public static Page<Expert> queryExpertByPage(int page, int pageSize, User user, String searchText, 
	        Long excludeGroupId) {
		if (user == null) {
			throw new IllegalArgumentException("传入的user对象不能为空。");
		}
		
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		queryHql.append("from Expert e where e.userId in (select f.friend.id from Friends f where f.user.id=:userId)");
		countHql.append("select count(e.id) from Expert e where e.userId in (select f.friend.id from Friends f where f.user.id=:userId)");
		paramMap.put("userId", user.getId());
		
		if (StringUtils.isNotBlank(searchText)) {
			queryHql.append(" and (e.userName like :searchTextLike or e.skillsTags like :searchTextLike or e.job like :searchTextLike or e.personalInfo like :searchTextLike)");
			countHql.append(" and (e.userName like :searchTextLike or e.skillsTags like :searchTextLike or e.job like :searchTextLike or e.personalInfo like :searchTextLike)");
			paramMap.put("searchTextLike", "%" + searchText + "%");
		}
		
		if (excludeGroupId != null) {
		    queryHql.append(" and e.userId not in (select gm.userId from GroupMember gm where gm.group.id = :excludeGroupId) ");
		    countHql.append(" and e.userId not in (select gm.userId from GroupMember gm where gm.group.id = :excludeGroupId) ");
		    paramMap.put("excludeGroupId", excludeGroupId);
		}
		
		queryHql.append(" order by e.user.id asc");
		
		TypedQuery<Expert> listQuery = JPA.em().createQuery(queryHql.toString(),Expert.class);
		TypedQuery<Long> countQuery = JPA.em().createQuery(countHql.toString(),Long.class);
		
		for (Entry<String, Object> e : paramMap.entrySet()) {
			listQuery.setParameter(e.getKey(), e.getValue());
			countQuery.setParameter(e.getKey(), e.getValue());
        }
		
		List<Expert> data = listQuery.setFirstResult(page * pageSize).setMaxResults(pageSize).getResultList();
		Long count = countQuery.getSingleResult();
		return new Page<Expert>(Constants.SUCESS,count,data);
	}
	
	/**
	 * 根据id删除
	 * @param id
	 */
	public static void deleteById(Long id) {
		JPA.em().createQuery("delete from Friends where id=:id").setParameter("id", id).executeUpdate();
	}
	
	/**
	 * 根据userId和friendId删除
	 * @param userId 用户id
	 * @param friendId 好友id
	 */
	public static void deleteByUserIdAndFriendId(Long userId, Long friendId) {
		JPA.em().createQuery("delete from Friends where user.id=:userId and friend.id=:friendId")
				.setParameter("userId", userId).setParameter("friendId", friendId).executeUpdate();
	}
	
	/**
	 * 判断两个用户是否已经是好友关系
	 * @param userId 用户id
	 * @param friendId 好友id
	 * @return true： 已经是好友，false：还不是好友
	 */
	public static Boolean isFriend(Long userId,Long friendId){
		List<Friends> list = JPA.em().createQuery("from Friends where user.id=:userId and friend.id=:friendId",Friends.class)
				.setParameter("userId", userId).setParameter("friendId", friendId).getResultList();
		if (CollectionUtils.isEmpty(list)) {
			return false;
		}
		return true;
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getFriend() {
		return friend;
	}

	public void setFriend(User friend) {
		this.friend = friend;
	}

}
