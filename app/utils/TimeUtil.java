/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-28
 */
package utils;

import java.util.Date;

/**
 *
 *
 * @ClassName: TimeUtil
 * @Description: TimeUtil
 * @date 2013-11-28 下午4:31:43
 * @author YangXuelin
 * 
 */
public class TimeUtil {
	
	/**
	 * 将秒转成分钟
	 * @param seconds
	 * @return
	 */
	public static String secondsToMinutesStr(int seconds) {
		if(seconds % 60 == 0) {
			return seconds/60 + "分钟";
		}
		return seconds/60 + "分" + seconds%60 + "秒";
	}
	
	/**
	 * 将秒转成整数分钟
	 * @param seconds
	 * @return
	 */
	public static int secondsToMinutes(int seconds) {
		int temp = seconds % 60 == 0 ? 0 : 1;
		if(temp == 0) {
			return seconds/60;
		}
		return seconds/60 + temp;
	}
	
	/**
	 * 比较两个时间间隔的分钟数
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public static int compareDate(Date beginDate, Date endDate) {
		if(beginDate == null || null == endDate) {
			return 0;
		}
		long begin = beginDate.getTime();
		long end = endDate.getTime();
		//return (int)Math.ceil((double)(end - begin)/(double)(1000 * 60));
		return (int) ((end - begin)/(1000 * 60));
	}
	
	/*public static void main(String[] args){// throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date begin = sdf.parse("2013-12-10 12:00");
		Date end = sdf.parse("2013-12-10 12:10");
		System.out.println(compareDate(begin, end));
		System.out.println(secondsToMinutesStr(421)); 
	}*/

}
