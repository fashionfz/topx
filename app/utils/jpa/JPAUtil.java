/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-19
 */
package utils.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import play.db.jpa.JPA;

/**
 * 
 * 
 * @ClassName: JPAUtil
 * @Description: jpa工具类
 * @date 2014-3-19 上午10:49:08
 * @author ShenTeng
 * 
 */
public class JPAUtil {

    /**
     * 开启一个独立事务，不影响外部事务。调用无返回值。
     * 
     * @param c 回调接口，包含要在事务中执行的代码
     */
    public static void indieTransaction(final IndieTransactionCall c) {
        indieTransaction(new IndieTransactionFunc<Void>() {
            @Override
            public Void call(EntityManager em) {
                c.call(em);
                return null;
            }
        });
    }

    /**
     * 开启一个独立事务，不影响外部事务。调用有返回值。
     * 
     * @param c 回调接口，包含要在事务中执行的代码
     */
    public static <T> T indieTransaction(IndieTransactionFunc<T> f) {
        EntityManager currentEM = null;
        EntityManager em = null;
        EntityTransaction tx = null;

        try {

            try {
                currentEM = JPA.em();
            } catch (Exception e1) {
            }

            em = JPA.em("default");
            JPA.bindForCurrentThread(em);
            tx = em.getTransaction();
            tx.begin();

            T result = f.call(em);

            if (tx != null) {
                if (tx.getRollbackOnly()) {
                    tx.rollback();
                } else {
                    tx.commit();
                }
            }

            return result;

        } catch (Throwable t) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Throwable e) {
                }
            }
            throw t;
        } finally {
            JPA.bindForCurrentThread(currentEM);
            if (em != null) {
                em.close();
            }
        }
    }

}
