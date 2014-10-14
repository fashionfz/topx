/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年7月30日
 */
package mobile.service.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import mobile.model.MobileClientLog;
import mobile.vo.other.ClientLog;
import mobile.vo.result.MobilePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import play.db.jpa.JPA;
import utils.jpa.IndieTransactionFunc;
import utils.jpa.JPAUtil;

/**
 *
 *
 * @ClassName: ClientLogService
 * @Description: 移动端日志
 * @date 2014年7月30日 下午5:08:16
 * @author ShenTeng
 * 
 */
public class ClientLogService {

    /**
     * 上传移动端日志
     * 
     * @param from 来源,必须
     * @param sourceFile 日志文件，必须，length>0
     * @param description 描述,非必须
     */
    public static void uploadLog(final String from, File sourceFile, final String description) {
        if (sourceFile == null || StringUtils.isBlank(from)) {
            throw new IllegalArgumentException("illegal param. sourceFile = " + sourceFile + ", from = " + from);
        }
        if (sourceFile.length() <= 0) {
            throw new IllegalArgumentException("sourceFile.length() <= 0");
        }

        String absDestFilePath = JPAUtil.indieTransaction(new IndieTransactionFunc<String>() {

            @Override
            public String call(EntityManager em) {
                // 保存实体
                DateTime now = new DateTime();
                MobileClientLog log = new MobileClientLog();
                log.setCreateTime(now.toDate());
                log.setDescription(description);
                log.setDevice(from);
                JPA.em().persist(log);

                String destFileName = now.toString("yyyyMMdd-HHmmss") + "-" + log.getId() + ".log";
                String absDestFilePath = FileService.getMobileAbsUploadPath() + "clientLog/" + destFileName;
                String relDestFilePath = FileService.getMobileRelUploadPath() + "clientLog/" + destFileName;

                log.setLogFileUrl(relDestFilePath);
                JPA.em().merge(log);

                return absDestFilePath;
            }
        });

        File destFile = new File(absDestFilePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        FileService.copyFile(sourceFile.getAbsolutePath(), absDestFilePath);
    }

    /**
     * 分页获取ClientLog
     * 
     * @param pageIndex 页码，从1开始
     * @param pageSize 每页条数
     * @param from 请求来源
     * @return
     */
    public static MobilePage<ClientLog> getPage(int pageIndex, int pageSize, String from) {
        String contentHql = " from MobileClientLog where 1=1 ";
        if (StringUtils.isNotBlank(from)) {
            contentHql += " and device = :device ";
        }
        String countHql = " select count(id) " + contentHql;
        contentHql = contentHql + " order by id desc ";

        TypedQuery<Long> countTypedQuery = JPA.em().createQuery(countHql, Long.class);
        TypedQuery<MobileClientLog> contentTypedQuery = JPA.em().createQuery(contentHql, MobileClientLog.class)
                .setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize);

        if (StringUtils.isNotBlank(from)) {
            countTypedQuery.setParameter("device", from);
            contentTypedQuery.setParameter("device", from);
        }

        Long count = countTypedQuery.getSingleResult();
        List<MobileClientLog> contentList = contentTypedQuery.getResultList();

        List<ClientLog> voList = new ArrayList<ClientLog>();
        if (CollectionUtils.isNotEmpty(contentList)) {
            for (MobileClientLog po : contentList) {
                voList.add(ClientLog.create(po));
            }
        }

        MobilePage<ClientLog> page = new MobilePage<ClientLog>(count, voList);
        return page;
    }
}
