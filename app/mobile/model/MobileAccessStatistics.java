/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-4-25
 */
package mobile.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * 
 * @ClassName: MobileAccessStatistics
 * @Description: 移动端访问统计
 * @date 2014-4-25 下午8:55:13
 * @author ShenTeng
 * 
 */
@javax.persistence.Entity
@Table(name = "tb_mobile_access_statistics", uniqueConstraints = { @UniqueConstraint(columnNames = { "year", "month",
        "day", "hour", "minute" }) })
public class MobileAccessStatistics {

    /**
     * 主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 年
     */
    private Integer year;

    /**
     * 月
     */
    private Integer month;

    /**
     * 日
     */
    private Integer day;

    /**
     * 时
     */
    private Integer hour;

    /**
     * 分
     */
    private Integer minute;

    /**
     * 国内版安卓
     */
    private Integer android = 0;
    /**
     * 国内版ipad
     */
    private Integer ipad = 0;

    /**
     * 国内版iphone
     */
    private Integer iphone = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getAndroid() {
        return android;
    }

    public void setAndroid(Integer android) {
        this.android = android;
    }

    public Integer getIpad() {
        return ipad;
    }

    public void setIpad(Integer ipad) {
        this.ipad = ipad;
    }

    public Integer getIphone() {
        return iphone;
    }

    public void setIphone(Integer iphone) {
        this.iphone = iphone;
    }

}
