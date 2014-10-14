/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author ZhouChun
 * @ClassName: DateUtils
 * @Description: 日期时间工具类
 * @date 13-11-13 下午4:31
 */
public class DateUtils {

    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORAMT_DATE_TIME = "yyyy-MM-dd HH:mm";
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE);
    /**
     * 时区转换，
     * @param src 需要转换的时间
     * @param timeZone 需要转换的时区
     * @return 转换后的时间
     */
    public static Date convertTimeZone(Date src, String timeZone) {
        return convertTimeZone(src, TimeZone.getDefault().getID(), timeZone);
    }

    /**
     * 时区转换
     * @param src 需要转换的时间
     * @param srcTimeZone 转换前的时区
     * @param timeZone 需要转换的时区
     * @return 转换后的时间
     */
    public static Date convertTimeZone(Date src, String srcTimeZone, String timeZone) {
        return convertTimeZone(src, TimeZone.getTimeZone(srcTimeZone).getRawOffset(), TimeZone.getTimeZone(timeZone).getRawOffset());
    }

    /**
     * 时区转换
     * @param src 需要转换的时间
     * @param srcTimezoneOffset 转换前的时区偏移量
     * @param timeZoneOffset  需要转换的时区偏移量
     * @return
     */
    public static Date convertTimeZone(Date src, int srcTimezoneOffset, int timeZoneOffset) {
        if (null == src) {
            return null;
        }
        if(srcTimezoneOffset == timeZoneOffset) return src;
        long srcTime = src.getTime();
        long targetTime = srcTime - (srcTimezoneOffset - timeZoneOffset);
        Date result = new Date(targetTime);
        return result;
    }

    /**
     * 日期格式化
     * @param date 需要格式化的时间对象
     * @param formatter 格式 DateUtils.FORMAT_DATETIME
     * @return
     */
    public static String format(Date date, String formatter) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        return sdf.format(date);
    }

    /**
     * 日期格式化
     * @param date 需要格式化的时间对象 默认格式 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String format(Date date) {
        return format(date, FORMAT_DATETIME);
    }

    /**
     * 字符串转换成日期
     * @param src
     * @param fmt
     * @return
     */
    public static Date convert2Date(String src, String fmt) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(fmt);
            return sdf.parse(src);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 字符串转换成日期
     * @param src
     * @return
     */
    public static Date convert2Date(String src) {
        return convert2Date(src, FORMAT_DATETIME);
    }

    /**
     * 获取当前日期字符串 格式: 2013-11-19
     * @return
     */
    public static String currentDate() {
        return format(Calendar.getInstance().getTime(), FORMAT_DATE);
    }

    /**
     * 获取当前时间字符串 格式: 2013-11-19 13:55:55
     * @return
     */
    public static String currentDateTime() {
        return format(Calendar.getInstance().getTime());
    }
    
    /**
     * 计算两个日期字符串相差的天数
     * @param d1 格式为 yyyy-MM-dd 的字符串
     * @param d2 格式为 yyyy-MM-dd 的字符串
     * @return
     * @throws ParseException 
     */
	public static int daysBetween(String d1, String d2) throws ParseException {
		Date date1 = dateFormat.parse(d1);
		Date date2 = dateFormat.parse(d2);
		Long quot = date1.getTime() - date2.getTime();
		if (quot < 0) {
			quot = 0 - quot;
		}
		quot = quot / 1000 / 60 / 60 / 24;
		return quot.intValue();
	}


    public static void main(String[] args) {

        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar now = Calendar.getInstance();
        System.out.println(sdf.format(now.getTime()) + "\t\t中国 Beijing 北京");*/
        
        System.out.println(DateUtils.format(new Date(), ",yyyy-MM-dd,HH:mm"));

        //long srcTime = now.getTimeInMillis();
        //[美国] Chicago 芝加哥 GMT-6
        //long targetTime = srcTime - (TimeZone.getDefault().getRawOffset() - TimeZone.getTimeZone("GMT-6").getRawOffset());
        //巴西 Sao Paulo 圣保罗 GMT -3
        //long targetTime = srcTime - (TimeZone.getDefault().getRawOffset() - TimeZone.getTimeZone("GMT-2").getRawOffset());
        //Date newdate = new Date(targetTime);
        //System.out.println(sdf.format(newdate));

        //巴西 Sao Paulo 圣保罗 GMT -3 (夏令时 GMT-2)
        /*System.out.println(DateUtils.format(DateUtils.convertTimeZone(now.getTime(), "GMT-2")) + "\t\t巴西 Sao Paulo 圣保罗");
        System.out.println(DateUtils.format(DateUtils.convertTimeZone(now.getTime(), "GMT-6")) + "\t\t美国 Chicago 芝加哥");
        System.out.println("==================================================================");

        Date saopaulo = DateUtils.convertTimeZone(now.getTime(), "GMT-2");
        System.out.println(DateUtils.format(saopaulo) + "\t\t巴西 Sao Paulo 圣保罗");
        System.out.println(DateUtils.format(DateUtils.convertTimeZone(saopaulo, "GMT-2", "GMT-6")) + "\t\t美国 Chicago 芝加哥");
        System.out.println("==================================================================");

        System.out.println(TimeZone.getTimeZone("GMT+08").getRawOffset());
        System.out.println(TimeZone.getTimeZone("GMT+08:30").getRawOffset());

        System.out.println(TimeZone.getTimeZone("UTC+08").getRawOffset());
        System.out.println(TimeZone.getTimeZone("UTC+08:30").getRawOffset());*/
    }
}
