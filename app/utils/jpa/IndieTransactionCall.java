/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-19
 */
package utils.jpa;

import javax.persistence.EntityManager;

/**
 * 
 * 
 * @ClassName: IndieTransactionCall
 * @Description: 回调接口
 * @date 2014-3-19 上午10:58:15
 * @author ShenTeng
 * 
 */
public interface IndieTransactionCall {
    void call(EntityManager em);
}
