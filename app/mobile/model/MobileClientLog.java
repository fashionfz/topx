/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年7月30日
 */
package mobile.model;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 *
 * @ClassName: MobileClientLog
 * @Description: 移动端日志
 * @date 2014年7月30日 下午5:04:03
 * @author ShenTeng
 * 
 */
@javax.persistence.Entity
@Table(name = "tb_mobile_client_log")
public class MobileClientLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String device;

    private Date createTime;

    private String description;

    private String logFileUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogFileUrl() {
        return logFileUrl;
    }

    public void setLogFileUrl(String logFileUrl) {
        this.logFileUrl = logFileUrl;
    }

}
