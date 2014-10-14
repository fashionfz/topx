/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-26
 */
package ext.schedule;

import java.util.concurrent.TimeUnit;

import play.libs.Akka;
import scala.concurrent.duration.Duration;
import ext.thread.EmailThread;

/**
 * 
 * 
 * @ClassName: ScheduleCenter
 * @Description: 调度中心
 * @date 2013-11-26 下午4:58:43
 * @author YangXuelin
 * 
 */
public class ScheduleCenter {


    public static void emailSchedule() {
        Runnable thread = new EmailThread();
        Akka.system()
                .scheduler()
                .schedule(Duration.create(30, TimeUnit.SECONDS), Duration.create(5, TimeUnit.SECONDS), thread,
                        Akka.system().dispatcher());
    }

}
