/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-5-27
 */
package mobile.util;

import mobile.service.result.ServiceResult;

/**
 *
 *
 * @ClassName: ServiceResults
 * @Description: 移动端公用的服务返回结果
 * @date 2014年8月27日 下午3:54:37
 * @author ShenTeng
 *
 */
public class ServiceResults {

    /**
     * 系统错误
     * 
     * @return
     */
    public static ServiceResult systemError() {
        return ServiceResult.error("100001", "系统错误");
    }

    /**
     * 接口已经停止使用
     * 
     * @param errorMsg 其他描述信息
     * @return
     */
    public static ServiceResult deprecated(String errorMsg) {
        return ServiceResult.error("100004", "接口已经停止使用。" + errorMsg);
    }

    /**
     * 参数非法
     */
    public static ServiceResult illegalParameters() {
        return ServiceResult.error("100005", "传入参数不符合规范");
    }

    /**
     * 参数非法
     * 
     * @param errorMsg 其他描述信息
     * @return
     */
    public static ServiceResult illegalParameters(String errorMsg) {
        return ServiceResult.error("100005", errorMsg);
    }

}
