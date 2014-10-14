/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-5-27
 */
package mobile.util;

import mobile.vo.result.MobileResult;

/**
 * 
 * 
 * @ClassName: MobileResults
 * @Description: 移动端公用的返回结果
 * @date 2014-5-27 上午11:34:36
 * @author ShenTeng
 * 
 */
public class MobileResults {

    /**
     * 系统错误
     * 
     * @return
     */
    public static MobileResult systemError() {
        return MobileResult.error("100001", "系统错误");
    }

    /**
     * 接口已经停止使用
     * 
     * @param errorMsg 其他描述信息
     * @return
     */
    public static MobileResult deprecated(String errorMsg) {
        return MobileResult.error("100004", "接口已经停止使用。" + errorMsg);
    }

    /**
     * 参数非法
     */
    public static MobileResult illegalParameters() {
        return MobileResult.error("100005", "传入参数不符合规范");
    }

    /**
     * 参数非法
     * 
     * @param errorMsg 其他描述信息
     * @return
     */
    public static MobileResult illegalParameters(String errorMsg) {
        return MobileResult.error("100005", errorMsg);
    }

}
