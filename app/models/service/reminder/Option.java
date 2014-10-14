/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-12
 */
package models.service.reminder;

/**
 * 
 * 
 * @ClassName: Option
 * @Description: 提醒设置项选项
 * @date 2013-11-12 下午5:39:15
 * @author ShenTeng
 * 
 */
public enum Option {
    BY_SMS("sms"), BY_MSG("msg"), BY_EMAIL("email");

    private String val;

    Option(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    /**
     * 该枚举是否包含对应的val
     * 
     * @param val
     * @return true:包含,false:不包含
     */
    public static boolean isContainVal(String val) {
        return getByVal(val) != null;
    }

    /**
     * 根据val获取Option
     * 
     * @param val
     * @return Option枚举, null:未找到
     */
    public static Option getByVal(String val) {
        Option op = null;
        for (Option o : Option.values()) {
            if (o.getVal().equals(val)) {
                op = o;
                break;
            }
        }

        return op;
    }
}