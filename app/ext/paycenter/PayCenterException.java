/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-5
 */
package ext.paycenter;

/**
 * 
 * 
 * @ClassName: PayCenterException
 * @Description: 支付中心异常
 * @date 2013-12-5 下午5:25:22
 * @author ShenTeng
 * 
 */
public class PayCenterException extends RuntimeException {

    private static final long serialVersionUID = 1338563315877241987L;

    public PayCenterException() {
        super();
    }

    public PayCenterException(String message) {
        super(message);
    }

    public PayCenterException(String message, Throwable cause) {
        super(message, cause);
    }

    public PayCenterException(Throwable cause) {
        super(cause);
    }
}
