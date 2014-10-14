/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package utils;

import java.util.ArrayList;
import java.util.List;

import play.libs.Yaml;
import play.mvc.Http;
import vo.TimeZone;

/**
 * @author ZhouChun
 * @ClassName: TimeZoneUtils
 * @Description: 时区工具类
 * @date 13-11-14 下午5:05
 */
public class TimeZoneUtils {

    public static final String CUSTOMER_TIMEZONE_UID = "SESSION_CUSTOMER_TIMEZONE_UID";

    private static List<TimeZone> timezones = new ArrayList<TimeZone>();

    @SuppressWarnings("unchecked")
    public static void load(String timezonefile) {
        timezones = (List<TimeZone>) Yaml.load(timezonefile);
    }

    public static List<TimeZone> getTimezones() {
        return timezones;
    }

    public static String render(String uid) {
        TimeZone tz = get(uid);
        if (tz != null)
            return tz.getDisplayName();
        return uid;
    }

    public static TimeZone get(String uid) {
        for (TimeZone timeZone : timezones) {
            if (String.valueOf(timeZone.getUid()).equals(uid)) {
                return timeZone;
            }
        }
        return null;
    }

    public static TimeZone getFromSession(Http.Session session) {
        return TimeZoneUtils.get(session.get(CUSTOMER_TIMEZONE_UID));
    }

    public static String getUid(int offset) {
        for (TimeZone timeZone : timezones) {
            if (timeZone.equalsOffset(String.valueOf(offset))) {
                return timeZone.getUid();
            }
        }
        return "B61F76930B16D90B06FE68E38A61219E";// 北京时间
    }

    public static void setTimeZoneOffset2Session(Http.Session session, Integer offset) {
        String uid = TimeZoneUtils.getUid(offset);
        session.put(CUSTOMER_TIMEZONE_UID, uid);
    }

    public static void main(String[] args) {

        for (int i = 12; i >= -12; i--) {
            System.out.println(-(i * 60) + ": GMT" + (i > 0 ? "+" : "") + i + " " + (i > 0 ? "东" : "西")
                    + (new CnUpperCaser(String.valueOf(i)).getCnString()).trim() + "区");
        }

    }

}
