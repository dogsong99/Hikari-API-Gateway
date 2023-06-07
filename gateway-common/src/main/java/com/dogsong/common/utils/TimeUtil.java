package com.dogsong.common.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * 时间处理工具类
 *
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/6/7
 */
@Slf4j
public final class TimeUtil {

    private static volatile long currentTimeMillis;

    static {
        currentTimeMillis = System.currentTimeMillis();

        Thread daemon = new Thread(() -> {
            while (true) {
                currentTimeMillis = System.currentTimeMillis();
                try {
                    java.util.concurrent.TimeUnit.MILLISECONDS.sleep(1);
                } catch (Throwable e) {
                    log.error("", e);
                }
            }
        });
        daemon.setDaemon(true);
        daemon.setName("common-fd-time-tick-thread");
        daemon.start();
    }

    public static long currentTimeMillis() {
        return currentTimeMillis;
    }

}
