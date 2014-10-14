/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-7-3
 */
package mobile.vo.result;

import mobile.vo.MobileVO;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * @ClassName: CommonVO
 * @Description: 通用VO,适用于极其简单的返回结果，不需要建立专门的VO对象，结果会直接合并到最外层JSON
 * @date 2014-7-3 下午5:06:34
 * @author ShenTeng
 * 
 */
public class CommonVO implements MobileVO {

    private ObjectNode node = Json.newObject();

    private CommonVO() {
    }

    /**
     * 创建VO
     * 
     * @param resultName vo在返回结果中的name
     * @return CommonVO对象
     */
    public static CommonVO create() {
        CommonVO vo = new CommonVO();
        return vo;
    }

    public void set(String name, String value) {
        node.put(name, value);
    }

    public void set(String name, Long value) {
        node.put(name, value);
    }

    public void set(String name, Integer value) {
        node.put(name, value);
    }

    public void set(String name, Boolean value) {
        node.put(name, value);
    }

    public void set(String name, JsonNode value) {
        node.set(name, value);
    }

    public String getString(String name) {
        return node.hasNonNull(name) ? node.get(name).asText() : null;
    }

    public Long getLong(String name) {
        return node.hasNonNull(name) ? node.get(name).asLong() : null;
    }

    @Override
    public JsonNode toJson() {
        return node;
    }

}
