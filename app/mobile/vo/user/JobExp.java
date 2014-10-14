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
 * @ClassName: JobExp
 * @Description: 职业经历
 * @date 2014-5-27 上午11:45:31
 * @author ShenTeng
 * 
 */
public class JobExp implements MobileVO {

    private ObjectNode exp;

    private JobExp() {
    }

    public static List<JobExp> createList(Expert expert) {
        List<JobExp> list = new ArrayList<>();

        if (null != expert && StringUtils.isNotBlank(expert.jobExp)) {
            JsonNode expertJobExp = Json.parse(expert.jobExp);
            if (expertJobExp.isArray()) {
                Iterator<JsonNode> elements = expertJobExp.elements();
                while (elements.hasNext()) {
                    JsonNode next = elements.next();
                    list.add(create(next));
                }
            }
        }

        return list;
    }

    public static JobExp create(JsonNode jobExpNode) {
        JobExp exp = new JobExp();
        exp.exp = Json.newObject();

        exp.exp.put("beginYear", jobExpNode.hasNonNull("beginYear") ? jobExpNode.get("beginYear").asText() : "");
        exp.exp.put("beginMonth", jobExpNode.hasNonNull("beginMonth") ? jobExpNode.get("beginMonth").asText() : "");

        String endYear = jobExpNode.hasNonNull("endYear") ? jobExpNode.get("endYear").asText() : "";
        exp.exp.put("endYear", endYear.equals("至今") ? "-1" : endYear);
        exp.exp.put("endMonth", jobExpNode.hasNonNull("endMonth") ? jobExpNode.get("endMonth").asText() : "");

        exp.exp.put("company", jobExpNode.hasNonNull("company") ? jobExpNode.get("company").asText() : "");
        exp.exp.put("duty", jobExpNode.hasNonNull("duty") ? jobExpNode.get("duty").asText() : "");
        exp.exp.put("workInfo", jobExpNode.hasNonNull("workInfo") ? jobExpNode.get("workInfo").asText() : "");

        return exp;
    }

    @Override
    public JsonNode toJson() {
        return exp;
    }

}
