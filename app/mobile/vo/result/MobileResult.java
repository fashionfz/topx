/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-5-19
 */
package mobile.vo.result;

import java.util.Collection;

import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.util.MobileVOUtil;
import mobile.vo.MobileVO;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * @ClassName: MobileResult
 * @Description: 移动端返回结果
 * @date 2014-5-19 下午4:15:37
 * @author ShenTeng
 * 
 */
public class MobileResult {

    private static final String STATUS = "status";

    private static final String ERROR_CODE = "errorCode";

    private static final String ERROR = "error";

    private static final int STATUS_SUCCESS = 1;

    private static final int STATUS_FAILED = 0;

    private ObjectNode node = null;

    private MobileResult() {
    }

    public static MobileResult success() {
        MobileResult result = new MobileResult();
        result.node = Json.newObject();
        result.node.put(STATUS, STATUS_SUCCESS);
        return result;
    }

    public static MobileResult error(String errorCode, String errorContent) {
        MobileResult result = new MobileResult();
        result.node = Json.newObject();
        result.node.put(STATUS, STATUS_FAILED);
        result.node.put(ERROR_CODE, errorCode);
        result.node.put(ERROR, errorContent);
        return result;
    }

    public boolean isSuccess() {
        return STATUS_SUCCESS == node.get(STATUS).asInt();
    }

    public MobileResult setToRoot(MobileVO vo) {
        MobileVOUtil.setToRoot(node, vo);
        return this;
    }

    public MobileResult setToRoot(ServiceVOResult<?> sResult) {
        if (!sResult.isSuccess() && STATUS_SUCCESS == this.node.get(STATUS).asInt(Integer.MIN_VALUE)) {
            this.node.put(STATUS, STATUS_FAILED);
            this.node.put(ERROR_CODE, sResult.getErrorCode());
            this.node.put(ERROR, sResult.getErrorContent());
        }

        MobileVOUtil.setToRoot(node, sResult.getVo());
        return this;
    }

    public MobileResult setToRoot(ServiceResult sResult) {
        if (!sResult.isSuccess() && STATUS_SUCCESS == this.node.get(STATUS).asInt(Integer.MIN_VALUE)) {
            this.node.put(STATUS, STATUS_FAILED);
            this.node.put(ERROR_CODE, sResult.getErrorCode());
            this.node.put(ERROR, sResult.getErrorContent());
        }

        return this;
    }

    public MobileResult setToField(String fieldName, MobileVO vo) {
        MobileVOUtil.setToField(node, fieldName, vo);
        return this;
    }

    public MobileResult setToField(String fieldName, ServiceVOResult<?> sResult) {
        if (!sResult.isSuccess() && STATUS_SUCCESS == this.node.get(STATUS).asInt(Integer.MIN_VALUE)) {
            this.node.put(STATUS, STATUS_FAILED);
            this.node.put(ERROR_CODE, sResult.getErrorCode());
            this.node.put(ERROR, sResult.getErrorContent());
        }

        MobileVOUtil.setToField(node, fieldName, sResult.getVo());
        return this;
    }

    public MobileResult setToField(String fieldName, Collection<?> c) {
        MobileVOUtil.setToField(node, fieldName, c);
        return this;
    }

    /**
     * 在结果中加入ObjectNode，会覆盖同名字段。该方法通常用于兼容旧接口的定义
     * 
     * @param other ObjectNode对象
     * @return this
     */
    public MobileResult setObjectNode(ObjectNode other) {
        this.node.setAll(other);
        return this;
    }

    /**
     * 获取play.mvc.Result
     * 
     * @return play.mvc.Result实现
     */
    public Result getResult() {
        return Results.ok(node);
    }

}
