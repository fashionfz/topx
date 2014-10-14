/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-19
 */
package ext.usercenter;

/**
 *
 *
 * @ClassName: UserCenterException
 * @Description: 用户中心服务使用的异常。
 * @date 2013-11-19 下午4:27:58
 * @author ShenTeng
 * 
 */
public class UserCenterException extends RuntimeException {

    private static final long serialVersionUID = -3408508307589404630L;
    
    public UserCenterException() {
        super();
    }

    public UserCenterException(String message) {
        super(message);
    }

    public UserCenterException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserCenterException(Throwable cause) {
        super(cause);
    }

}
