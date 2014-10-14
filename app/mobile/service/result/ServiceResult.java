/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-5
 */
package mobile.service.result;

import controllers.base.ObjectNodeResult;

/**
 *
 *
 * @ClassName: ServiceResult
 * @Description: 服务的返回结果，不包含VO对象
 * @date 2014年8月11日 下午5:01:37
 * @author ShenTeng
 *
 */
public class ServiceResult {

    private String errorCode;

    private String errorContent;

    private Boolean isSuccess;

    public static ServiceResult success() {
        ServiceResult result = new ServiceResult();
        result.isSuccess = true;
        return result;
    }

    public static ServiceResult error(String errorCode, String errorContent) {
        ServiceResult result = new ServiceResult();
        result.isSuccess = false;
        result.errorCode = errorCode;
        result.errorContent = errorContent;
        return result;
    }

    public static ServiceResult create(ObjectNodeResult objectNodeResult) {
        ServiceResult result = new ServiceResult();
        if (objectNodeResult.isSuccess()) {
            result.isSuccess = true;
        } else {
            result.isSuccess = false;
            result.errorCode = objectNodeResult.getErrorCode();
            result.errorContent = objectNodeResult.getError();
        }

        return result;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorContent() {
        return errorContent;
    }
}
