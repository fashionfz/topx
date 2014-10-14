/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年12月17日
 */
package system.models.service;

import common.Constants;
import play.db.jpa.JPA;

/**
 * @ClassName: TopService
 * @Description: 专家置顶服务类
 * @date 2013年12月17日 下午6:13:58
 * @author RenYouchao
 * 
 */
public class TopService {

	/**
	 * 将专家置顶
	 * 
	 * @param userId
	 *            专家的账号id
	 * 
	 */
	public static void topExpert(Long userId,Long topIndustry) {
		int results = JPA.em().createQuery("update Expert set isTop=true,topIndustry=:topIndustry where userId=:userId")
				.setParameter("topIndustry", topIndustry).setParameter("userId", userId).executeUpdate();
		play.cache.Cache.remove(Constants.CACHE_EXPERT_TOPS);
	}

	/**
	 * 不将专家置顶
	 * 
	 * @param userId
	 *            专家的账号id
	 */
	public static void unTopExpert(Long userId) {
		int results = JPA.em().createQuery("update Expert set isTop=false,topIndustry=null where userId=:userId").setParameter("userId", userId)
				.executeUpdate();
		play.cache.Cache.remove(Constants.CACHE_EXPERT_TOPS);
	}

}
