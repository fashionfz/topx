/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-4-25
 */
package mobile.service.core;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import mobile.model.MobileAccessStatistics;
import mobile.util.MobileUtil;
import play.db.jpa.JPA;
import play.libs.Akka;
import play.libs.F;
import scala.concurrent.duration.Duration;

/**
 * 
 * 
 * @ClassName: AccessStatisticsService
 * @Description: 访问统计服务
 * @date 2014-4-25 下午8:02:51
 * @author ShenTeng
 * 
 */
public class AccessStatisticsService {

    /** 统计数据写入数据库间隔，单位：ms */
    private static final int WRITE_TO_DB_INTERVAL = 60000;

    private static volatile ConcurrentHashMap<Long, ConcurrentHashMap<String, AtomicInteger>> counter = new ConcurrentHashMap<>();

    /**
     * 记录访问,仅记录在本地缓存中，需要调用{@link AccessStatisticsService#startStatisticsThread()}开启写入线程写入数据库
     * 
     * @param url 访问URL
     */
    public static void recordAccess(String url) {
        if (MobileUtil.isMobileUrlPrefixAndDevice(url)) {
            String from = MobileUtil.getFromByUrl(url);

            writeStatistics(from);
        }
    }

    private static void writeStatistics(String from) {
        Long nowTime = System.currentTimeMillis() / 60000 * 60000; // 时间精度转化为1分钟

        ConcurrentHashMap<String, AtomicInteger> nowCounter = counter.get(nowTime);
        if (null == nowCounter) {
            nowCounter = counter.putIfAbsent(nowTime, new ConcurrentHashMap<String, AtomicInteger>());
            if (null == nowCounter) {
                nowCounter = counter.get(nowTime);
            }
        }

        AtomicInteger nowNum = nowCounter.get(from);
        if (null == nowNum) {
            nowNum = nowCounter.putIfAbsent(from, new AtomicInteger());
            if (null == nowNum) {
                nowNum = nowCounter.get(from);
            }
        }

        nowNum.incrementAndGet();
    }

    /**
     * 强制写入统计数据到数据库，需要保证当前没有访问，否则可能造成部分数据丢失
     */
    public static void forceWriteStatistics() {
        recordToDB(System.currentTimeMillis());
    }

    /**
     * 开启统计数据写入线程
     */
    public static void startStatisticsThread() {
        Runnable thread = new StatisticsThread();
        Akka.system()
                .scheduler()
                .schedule(Duration.create(WRITE_TO_DB_INTERVAL, TimeUnit.MILLISECONDS),
                        Duration.create(WRITE_TO_DB_INTERVAL, TimeUnit.MILLISECONDS), thread,
                        Akka.system().dispatcher());
    }

    private static class StatisticsThread implements Runnable {

        @Override
        public void run() {
            recordToDB(System.currentTimeMillis() - 70000);
        }

    }

    private static void recordToDB(Long timeLimit) {
        Iterator<Entry<Long, ConcurrentHashMap<String, AtomicInteger>>> iterator = counter.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<Long, ConcurrentHashMap<String, AtomicInteger>> e = iterator.next();
            if (e.getKey() <= timeLimit) {
                ConcurrentHashMap<String, AtomicInteger> v = e.getValue();
                insertOrUpdateStatistics(e.getKey(), getValue(v, MobileUtil.FROM_ANDROID),
                        getValue(v, MobileUtil.FROM_IPAD), getValue(v, MobileUtil.FROM_IPHONE));

                iterator.remove();
            }
        }
    }

    private static Integer getValue(ConcurrentHashMap<String, AtomicInteger> e, String from) {
        AtomicInteger num = e.get(from);
        if (null == num) {
            return null;
        }

        return num.get();
    }

    private static void insertOrUpdateStatistics(long recordTime, Integer androidCount, Integer ipadCount,
            Integer iphoneCount) {
        if ((androidCount == null || androidCount <= 0) && (ipadCount == null || ipadCount <= 0)
                && (iphoneCount == null || iphoneCount <= 0)) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(recordTime);

        final MobileAccessStatistics statistics = new MobileAccessStatistics();
        statistics.setYear(calendar.get(Calendar.YEAR));
        statistics.setMonth(calendar.get(Calendar.MONTH) + 1);
        statistics.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        statistics.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        statistics.setMinute(calendar.get(Calendar.MINUTE));

        final StringBuilder updateJPQL = new StringBuilder("update MobileAccessStatistics s set ");
        if (androidCount != null && androidCount > 0) {
            statistics.setAndroid(androidCount);
            updateJPQL.append(" s.android = s.android + " + androidCount + " ,");
        }

        if (ipadCount != null && ipadCount > 0) {
            statistics.setIpad(ipadCount);
            updateJPQL.append(" s.ipad = s.ipad + " + ipadCount + " ,");
        }

        if (iphoneCount != null && iphoneCount > 0) {
            statistics.setIphone(iphoneCount);
            updateJPQL.append(" s.iphone = s.iphone + " + iphoneCount + " ,");
        }

        updateJPQL.deleteCharAt(updateJPQL.length() - 1);
        updateJPQL.append(" where s.year = :year and s.month = :month and s.day = :day "
                + "and s.hour = :hour and s.minute = :minute ");

        Boolean updateResult;
        try {
            updateResult = JPA.withTransaction(new F.Function0<Boolean>() {

                @Override
                public Boolean apply() throws Throwable {
                    int executeUpdate = JPA.em().createQuery(updateJPQL.toString())
                            .setParameter("year", statistics.getYear()).setParameter("month", statistics.getMonth())
                            .setParameter("day", statistics.getDay()).setParameter("hour", statistics.getHour())
                            .setParameter("minute", statistics.getMinute()).executeUpdate();
                    return executeUpdate > 0;
                }
            });
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        if (!updateResult) {
            try {
                JPA.withTransaction(new F.Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        JPA.em().persist(statistics);
                    }
                });
            } catch (Exception e) {
                JPA.withTransaction(new F.Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        JPA.em().createQuery(updateJPQL.toString()).setParameter("year", statistics.getYear())
                                .setParameter("month", statistics.getMonth()).setParameter("day", statistics.getDay())
                                .setParameter("hour", statistics.getHour())
                                .setParameter("minute", statistics.getMinute()).executeUpdate();
                    }
                });
            }
        }
    }
}
