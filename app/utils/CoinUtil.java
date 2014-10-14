/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年3月21日
 */
package utils;

/**
 * @ClassName: CoinUtil
 * @Description: 货币处理的工具类
 * @date 2014年3月21日 下午5:21:59
 * @author RenYouchao
 * 
 */
public class CoinUtil {
	
	public static String expensesRMB(Long expenses){
		return "￥" + String.valueOf(expenses) + "/分钟";
	}

}
