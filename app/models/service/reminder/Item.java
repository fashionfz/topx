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
 * @ClassName: Item
 * @Description: 提醒设置项
 * @date 2013-11-12 下午5:39:15
 * @author ShenTeng
 * 
 */
public enum Item {
    // 预约提醒
    BOOKING_REMIND("bookingRemind", false, new Option[] { Option.BY_SMS }),
    // 修改手机号
    CHANGE_PHONE_NUM("changePhoneNum", true, null),
    // 修改密码
    CHANGE_PASSWORD("changePassword", true, null),
    // 异地登录
    DIFFERENT_PLACE_LOGIN("differentPlaceLogin", true, null);

    private String val;
    Boolean isEnable;
    private Option[] defaultOptions;

    Item(String val, boolean isEnable, Option[] defaultOptions) {
        this.val = val;
        this.isEnable = isEnable;
        this.defaultOptions = defaultOptions;
    }

    public String getVal() {
        return val;
    }

    public Boolean getIsEnable() {
        return isEnable;
    }

    public Option[] getDefaultOptions() {
        return defaultOptions;
    }

    /**
     * 根据val获取Item
     * 
     * @param val
     * @return Item枚举, null:未找到
     */
    public static Item getByVal(String val) {
        Item e = null;
        for (Item o : Item.values()) {
            if (o.getVal().equals(val)) {
                e = o;
                break;
            }
        }

        return e;
    }
}