/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-5-27
 */
package mobile.vo.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mobile.vo.MobileVO;
import models.Expert;

import org.apache.commons.lang3.StringUtils;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * @ClassName: EducationExp
 * @Description: 教育经历
 * @date 2014-5-27 上午11:45:54
 * @author ShenTeng
 * 
 */
public class EducationExp implements MobileVO {

    private ObjectNode exp;

    private EducationExp() {
    }

    public static List<EducationExp> createList(Expert expert) {
        List<EducationExp> exp = new ArrayList<>();

        if (null != expert && StringUtils.isNotBlank(expert.educationExp)) {
            JsonNode eduExpNode = Json.parse(expert.educationExp);
            if (eduExpNode.isArray()) {
                Iterator<JsonNode> elements = eduExpNode.elements();
                while (elements.hasNext()) {
                    JsonNode next = elements.next();
                    exp.add(create(next));
                }
            }
        }

        return exp;
    }

    public static EducationExp create(JsonNode jobExpNode) {
        EducationExp exp = new EducationExp();
        exp.exp = Json.newObject();

        exp.exp.put("year", jobExpNode.hasNonNull("year") ? jobExpNode.get("year").asText() : "");
        exp.exp.put("yearEnd",
                jobExpNode.hasNonNull("yearEnd") ? jobExpNode.get("yearEnd").asText() : jobExpNode.get("year").asText());
        exp.exp.put("school", jobExpNode.hasNonNull("school") ? jobExpNode.get("school").asText() : "");
        exp.exp.put("major", jobExpNode.hasNonNull("major") ? jobExpNode.get("major").asText() : "");
        exp.exp.put("academicDegree", jobExpNode.hasNonNull("academicDegree") ? jobExpNode.get("academicDegree")
                .asText() : "");
        exp.exp.put("eduInfo", jobExpNode.hasNonNull("eduInfo") ? jobExpNode.get("eduInfo").asText() : "");

        return exp;
    }

    @Override
    public JsonNode toJson() {
        return exp;
    }

}
