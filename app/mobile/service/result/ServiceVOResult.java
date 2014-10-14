/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-5
 */
package mobile.service.result;

import mobile.vo.MobileVO;
import controllers.base.ObjectNodeResult;

/**
 * 
 * 
 * @ClassName: ServiceVOResult
 * @Description: 服务的返回结果，附带状态信息
 * @date 2014-6-5 下午6:21:07
 * @author ShenTeng
 * 
 */
public class ServiceVOResult<T extends MobileVO> {

    private String errorCode;

    private String errorContent;

    private Boolean isSuccess;

    private T vo;

    public static <T extends MobileVO> ServiceVOResult<T> success() {
        ServiceVOResult<T> result = new ServiceVOResult<>();
        result.isSuccess = true;
        return result;
    }

    public static <T extends MobileVO> ServiceVOResult<T> success(T vo) {
        ServiceVOResult<T> result = new ServiceVOResult<>();
        result.isSuccess = true;
        result.vo = vo;
        return result;
    }

    public static <T extends MobileVO> ServiceVOResult<T> error(String errorCode, String errorContent) {
        ServiceVOResult<T> result = new ServiceVOResult<>();
        result.isSuccess = false;
        result.errorCode = errorCode;
        result.errorContent = errorContent;
        return result;
    }

    public static <T extends MobileVO> ServiceVOResult<T> create(ServiceResult serviceResult) {
        if (serviceResult.isSuccess()) {
            return success();
        } else {
            return error(serviceResult.getErrorCode(), serviceResult.getErrorContent());
        }
    }

    public static <T extends MobileVO> ServiceVOResult<T> create(ObjectNodeResult result) {
        if (result.isSuccess()) {
            return success();
        } else {
            return error(result.getErrorCode(), result.getError());
        }
    }

    public ServiceVOResult<T> setVo(T vo) {
        this.vo = vo;
        return this;
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

    public T getVo() {
        return vo;
    }

}
