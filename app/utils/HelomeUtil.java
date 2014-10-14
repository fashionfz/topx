/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author ZhouChun
 * @ClassName: HelomeUtil
 * @Description: 工具类
 * @date 13-10-22 下午6:38
 */
public class HelomeUtil {

    private static final ALogger LOGGER = Logger.of(HelomeUtil.class);

    public static boolean isNull(Object o) {

        if (o == null)
            return true;

        return false;
    }

    /**
     * String Collection Map JsonNode ...
     * 
     * @param o
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object o) {

        if (isNull(o))
            return true;
        if (o instanceof String)
            return o.toString().length() == 0;
        if (o instanceof Collection)
            return ((Collection) o).size() == 0;
        if (o instanceof Map)
            return ((Map) o).size() == 0;
        if (o instanceof JsonNode)
            return ((JsonNode) o).size() == 0;

        return false;
    }

    public static String trim(Object o) {
        if (isEmpty(o))
            return "";
        return o.toString().trim();
    }

    /**
     * 检测邮箱地址是否合法
     * 
     * @param email
     * @return true合法 false不合法
     */
    public static boolean isEmail(String email) {
        if (isNull(email))
            return false;
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 判断Ip是否合法
     * 
     * @param ip
     * @return
     */
    public static boolean isIP(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }

        String num = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
        String regex = "^" + num + "\\." + num + "\\." + num + "\\." + num + "$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    /**
     * 获取UUID
     * 
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 转换成int 转换失败默认返回-1
     * 
     * @param s
     * @return
     */
    public static int parseInt(String s) {
        return parseInt(s, -1);
    }

    /**
     * 转换成int
     * 
     * @param s
     * @param def
     * @return
     */
    public static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 获取email,使用星号隐藏部分内容,例如:ice****@qq.com
     * 
     * @return 例如:ice****@qq.com
     */
    public static String getMaskEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return email;
        } else {
            String[] split = email.split("@");

            int prefixLength = split[0].length();
            int starCount = prefixLength < 4 ? prefixLength : 4;

            StringBuilder prefix = new StringBuilder();
            prefix.append(split[0].substring(0, prefixLength - starCount));
            for (int i = 0; i < starCount; i++) {
                prefix.append("*");
            }
            return prefix.toString() + "@" + split[1];
        }
    }

    /**
     * 获得视频URL
     * 
     * @return
     */
    public static String getVedioUrl() {
        return play.Play.application().configuration().getString("web.video.url");
    }

    public static Integer toInteger(String str, Integer defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static Long toLong(String str, Long defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * JsonNode Array排序。作为排序标准的字段必须为整数，且不超过Long型的范围。
     * 
     * @param sourceNode 待排序JsonNode Array
     * @param fieldName 作为排序标准的字段名
     * @param isDesc true - 降序， false - 升序
     * @return 已排序JsonNode
     */
    public static JsonNode sortJsonNode(JsonNode sourceNode, final String fieldName, final boolean isDesc) {
        if (null == sourceNode || !sourceNode.isArray() || sourceNode.isMissingNode()) {
            LOGGER.error("sort json node fail.");
            return sourceNode;
        }
        List<JsonNode> sortedList = new LinkedList<>();
        Iterator<JsonNode> iterator = sourceNode.elements();
        while (iterator.hasNext()) {
            JsonNode element = iterator.next();
            sortedList.add(element);
        }
        Collections.sort(sortedList, new Comparator<JsonNode>() {
            private int bigger = isDesc ? -1 : 1;
            private int smaller = -bigger;

            @Override
            public int compare(JsonNode o1, JsonNode o2) {
                long o1Key = o1.get(fieldName) == null ? 0 : o1.get(fieldName).asLong();
                long o2Key = o2.get(fieldName) == null ? 0 : o2.get(fieldName).asLong();
                return o1Key == o2Key ? 0 : o1Key > o2Key ? bigger : smaller;
            }
        });
        ObjectNode sortedNode = Json.newObject();
        ArrayNode arrayNode = sortedNode.arrayNode();
        arrayNode.addAll(sortedList);
        return arrayNode;
    }

    /**
     * 如果不包含指定的值，这返回默认值
     * 
     * @param src 原值
     * @param contain 指定值集合
     * @param defaultStr 默认值
     * @return 结果字符串
     */
    public static String defaultIfNotContain(String src, String[] contain, String defaultStr) {
        return ArrayUtils.contains(contain, src) ? src : defaultStr;
    }
    
    /**
     * 如果和给定值相等，那么返回默认值
     * @param src 原值
     * @param eq 给定值
     * @param defaultValue 默认值
     * @return
     */
    public static <T> T defaultIfEqual(T src, T eq, T defaultValue) {
        if (src == null) {
            return src == eq ? defaultValue : src;
        } else {
            return src.equals(eq) ? defaultValue : src;
        }
    }

}
