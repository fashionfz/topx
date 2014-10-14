/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-4-22
 */
package mobile.util;

import java.util.Collection;

import mobile.vo.MobileVO;

import org.apache.commons.collections.CollectionUtils;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * @ClassName: MobileVOUtil
 * @Description: 移动端VO工具类
 * @date 2014-4-22 下午5:47:32
 * @author ShenTeng
 * 
 */
public class MobileVOUtil {

    public static JsonNode toJson(MobileVO vo) {
        if (null == vo) {
            return null;
        }

        return vo.toJson();
    }

    /**
     * 将VO对象set到ObjectNode的最外层
     * 
     * @param node 目标ObjectNode
     * @param vo MobileVO对象
     */
    public static void setToRoot(ObjectNode node, MobileVO vo) {
        if (null == node) {
            throw new IllegalArgumentException("node is null");
        }

        if (null != vo) {
            JsonNode json = toJson(vo);
            if (null != json) {
                node.setAll((ObjectNode) json);
            }
        }
    }

    /**
     * 将VO对象set到ObjectNode的某个属性中
     * 
     * @param node 目标ObjectNode
     * @param fieldName 属性名
     * @param vo MobileVO对象
     */
    public static void setToField(ObjectNode node, String fieldName, MobileVO vo) {
        if (null == node) {
            throw new IllegalArgumentException("node is null");
        }

        node.set(fieldName, toJson(vo));
    }

    /**
     * 将VO对象set到ObjectNode的某个属性中
     * 
     * @param node 目标ObjectNode
     * @param fieldName 属性名
     * @param c MobileVO对象集合
     */
    public static void setToField(ObjectNode node, String fieldName, Collection<?> c) {
        if (null == node) {
            throw new IllegalArgumentException("node is null");
        }

        ArrayNode arrayNode = Json.newObject().arrayNode();

        if (CollectionUtils.isNotEmpty(c)) {
            for (Object vo : c) {
                if (vo instanceof MobileVO) {
                    arrayNode.add(MobileVOUtil.toJson((MobileVO) vo));
                } else {
                    arrayNode.add(Json.toJson(vo));
                }
            }
        }

        node.set(fieldName, arrayNode);
    }

}
