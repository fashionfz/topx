package models.service;

import java.util.ArrayList;
import java.util.List;

import models.Expert;
import models.Favorite;
import models.User;
import vo.ExpertDetail;
import vo.page.Page;

import org.apache.commons.collections.CollectionUtils;

import common.Constants;

/**
 * @ClassName: FavoriteService
 * @Description: 收藏专家服务
 */
public class FavoriteService {
	
	/**
	 * 添加某个用户收藏的专家
	 */
	public static Boolean addFavorite(User user,Expert expert){
		Boolean isFavorited = Favorite.isFavorited(user.getId(), expert.getId());
		if (!isFavorited) {
			Favorite.addFavorite(user, expert);
		}
		return Boolean.TRUE;
	}
	
	/**
	 * 删除某个用户收藏的某个专家
	 */
	public static Boolean deleteFavorite(User user, Expert expert) {
		Boolean isFavorited = Favorite.isFavorited(user.getId(), expert.getId());
		if (isFavorited) {
			Favorite.deleteByUserIdAndExpertId(user.getId(), expert.getId());
		}
		return Boolean.TRUE;
	}
	
	/**
	 * 用户收藏的专家列表
	 */
	public static List<Expert> listFavorite(User user) {
		List<Expert> list = Favorite.queryFavoriteExpert(user.getId());
		if (CollectionUtils.isEmpty(list)) {
			return new ArrayList<Expert>();
		}
		return list;
	}
	
	/**
	 * 分页获取用户的收藏信息
	 * @param page 页号
	 * @param pageSize 每页展示行数
	 * @param user 用户对象
	 * @return Page<vo.ExpertDetail>对象
	 */
	public static Page<ExpertDetail> getFavoritePage(int page, int pageSize,User user){
		List<Expert> expertList = Favorite.queryExpertOfCurrentPage(page, pageSize, user);
		
		Long total = Favorite.queryTotalCount(user);
		List<ExpertDetail> data = null;
		if (CollectionUtils.isNotEmpty(expertList)) {
			data = new ArrayList<ExpertDetail>();
			for (Expert expert : expertList) {
				ExpertDetail expertDetail = new ExpertDetail();
				expertDetail.convert(expert);
				data.add(expertDetail);
			}
		}
		
		return new Page<ExpertDetail>(Constants.SUCESS, total, data);
	}
	
}
