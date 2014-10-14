/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-12
 */
package ext.sns.openapi;

/**
 * 
 * 
 * @ClassName: OpenAPIResult
 * @Description: OpenAPI返回结果
 * @date 2014-6-12 下午3:06:08
 * @author ShenTeng
 * 
 */
public class OpenAPIResult<T> {

    public enum STATE {
        SUCCESS, INVALID_TOKEN, UNKNOWN_ERROR
    }

    private STATE state;

    private T result;

    public OpenAPIResult(STATE state, T result) {
        this.state = state;
        this.result = result;
    }

    public STATE getState() {
        return state;
    }

    public T getResult() {
        return result;
    }

    public boolean isSuccess() {
        return STATE.SUCCESS == state;
    }

}
