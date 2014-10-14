package models.service;

import java.util.ArrayList;
import java.util.List;

import models.Expert;
import models.Friends;
import models.User;

import org.apache.commons.collections.CollectionUtils;

import common.Constants;
import vo.ExpertDetail;
import vo.page.Page;

/**
 * @ClassName: FriendsService
 * @Description: 圈子好友功能服务
 *
 */
public class FriendsService {
	
	/**
	 * 添加用户好友
	 * @param user 用户
	 * @param friend 好友
	 * @return
	 */
	public static Boolean addFriend(User user, User friend) {
		Boolean isFriend = Friends.isFriend(user.getId(), friend.getId());
		if (!isFriend) {
			Friends.addFriends(user, friend);
		}
		return Boolean.TRUE;
	}
	
	/**
	 * 删除用户对应的某个好友
	 * @param user
	 * @param friend
	 * @return
	 */
	public static Boolean deleteFriend(User user, User friend) {
		Boolean isFriend = Friends.isFriend(user.getId(), friend.getId());
		if (isFriend) {
			Friends.deleteByUserIdAndFriendId(user.getId(), friend.getId());
		}
		return Boolean.TRUE;
	}
	
	/**
	 * 用户对应的所有的好友
	 * @param user
	 * @return
	 */
	public static List<User> listFriend(User user){
		List<User> list = Friends.queryFriends(user.getId());
		return list;
	}
	
	/**
	 * 分页获取用户的好友信息
	 * @param page
	 * @param pageSize
	 * @param user
	 * @return
	 */
	public static Page<ExpertDetail> getFriendPage(int page, int pageSize, User user, String searchText){
		Page<Expert> expertPage = Friends.queryExpertByPage(page, pageSize, user, searchText, null);
		List<Expert> expertList = expertPage.getList();
		List<ExpertDetail> data = null;
		if (CollectionUtils.isNotEmpty(expertList)) {
			data = new ArrayList<ExpertDetail>();
			for (Expert e : expertList) {
				ExpertDetail expertDetail = new ExpertDetail();
				expertDetail.convert(e);
				data.add(expertDetail);
			}
		}

		return new Page<ExpertDetail>(Constants.SUCESS, expertPage.getTotalRowCount(), data);
	}

}
