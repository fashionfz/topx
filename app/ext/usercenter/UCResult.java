/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-10-29
 */
package ext.usercenter;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * 
 * @ClassName: UCResult
 * @Description: 用户中心方法执行结果
 * @date 2013-10-29 下午4:39:55
 * @author ShenTeng
 * 
 */
public class UCResult<T> {
    
    /**
     * 返回的查询结果
     */
    public T data;

    /**
     * 错误的中文描述，错误信息的详细中文描述
     */
    public String errorinfo;

    /**
     * 接口请求的返回代码，从这个代码中可以识别接口调用是否成功，以及调用失败时的错误类型。具体的code定义参见用户中心接口文档
     */
    String responsecode;

    /**
     * 操作成功
     * 
     * @return true:操作成功
     */
    public boolean isSuccess() {
        return responsecode != null && responsecode.equals("_200");
    }

    /**
     * 没有查询到符合条件的数据
     * 
     * @return true:没有查询到符合条件的数据
     */
    public boolean noMatchData() {
        return responsecode != null && responsecode.equals("_404");
    }

    /**
     * 传入的参数项格式不符合规定
     * 
     * @return true:传入的参数项格式不符合规定
     */
    public boolean illegalParam() {
        return responsecode != null && responsecode.equals("_402");
    }
    
    /**
     * 规定的必传入项没有传入
     * 
     * @return true:规定的必传入项没有传入
     */
    public boolean lackInputParam() {
        return responsecode != null && responsecode.equals("_401");
    }

    /**
     * 查询到重复数据
     * 
     * @return true:查询到重复数据
     */
    public boolean duplicateData() {
        return responsecode != null && responsecode.equals("_405");
    }

    /**
     * 解析用户中心系统返回的JSON结果串，返回UCResult对象<br>
     * 
     * @param jsonString 用户中心系统返回的JSON结果串
     * @param dataClz data属性期望类型
     * @param isParseData 是否解析data属性，不解析data为null
     * @return 返回UCResult对象
     */
    static <A> UCResult<A> fromJsonString(String jsonString, Class<A> dataClz, boolean isParseData) {
        JsonNode resultJson = null;
        try {
            resultJson  = Json.parse(jsonString);
        } catch (RuntimeException e) {
            throw new UserCenterException("用户中心返回错误的JSON。JSON内容：\n" + jsonString, e);
        }
        
        UCResult<A> result = new UCResult<>();

        result.responsecode = resultJson.has("responsecode") ? resultJson.get("responsecode").asText() : null;
        result.errorinfo = resultJson.has("errorinfo") ? resultJson.get("responsecode").asText() : null;

        // 解析data属性
        if (!Void.class.equals(dataClz) && isParseData && resultJson.has("data")) {
            result.data = Json.fromJson(resultJson.get("data"), dataClz);
        }

        return result;
    }

    @Override
    public String toString() {
        return "UCResult [data=" + data + ", errorinfo=" + errorinfo + ", responsecode=" + responsecode + "]";
    }

}
