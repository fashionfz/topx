/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-24
 */
package system.vo;

import java.util.List;

import play.libs.Json;

/**
 * 
 * 
 * @ClassName: Page
 * @Description: 分页VO，用于extjs grid分页
 * @date 2013-12-24 上午11:42:19
 * @author ShenTeng
 * 
 */
public class Page<T> {

    private Long total;

    private List<T> data;
    
    public Page() {
    	
    }
    
    public Page(Long total, List<T> data) {
    	this.total = total;
    	this.data = data;
    }

    public String toJson() {
        return Json.toJson(this).toString();
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

}
