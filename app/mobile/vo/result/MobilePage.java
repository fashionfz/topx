/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-4-29
 */
package mobile.vo.result;

import java.util.List;

import mobile.util.MobileVOUtil;
import mobile.vo.MobileVO;

import org.apache.commons.collections.CollectionUtils;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * @ClassName: Page
 * @Description: 分页实体
 * @date 2014-4-29 下午7:31:12
 * @author ShenTeng
 * 
 */
public class MobilePage<T extends MobileVO> implements MobileVO {

    /** 总条数 */
    private Long totalRowCount;

    /** 数据集合 */
    private List<T> list;

    private String listFieldName = "list";

    private String totalFieldName = "totalRowCount";

    public MobilePage(Long totalRowCount, List<T> list) {
        this.totalRowCount = totalRowCount;
        this.list = list;
    }

    @Override
    public JsonNode toJson() {
        ObjectNode objectNode = Json.newObject();
        ArrayNode arrayNode = Json.newObject().arrayNode();
        if (CollectionUtils.isNotEmpty(list)) {
            for (T t : list) {
                JsonNode node = MobileVOUtil.toJson(t);
                arrayNode.add(node);
            }
        }

        objectNode.put(totalFieldName, totalRowCount);
        objectNode.set(listFieldName, arrayNode);

        return objectNode;
    }

    public Long getTotalRowCount() {
        return totalRowCount;
    }

    public void setTotalRowCount(Long totalRowCount) {
        this.totalRowCount = totalRowCount;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getListFieldName() {
        return listFieldName;
    }

    public void setListFieldName(String listFieldName) {
        this.listFieldName = listFieldName;
    }

    public String getTotalFieldName() {
        return totalFieldName;
    }

    public void setTotalFieldName(String totalFieldName) {
        this.totalFieldName = totalFieldName;
    }

}
