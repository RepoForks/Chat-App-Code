package com.tubiapp.demochatxmpp.apis;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Copyright © 2015 AsianTech inc.
 * Created by Justin on 5/29/15.
 */
public class ExecutorManager {
    private static Executor serviceExecutor;
    private static Executor dbExecutor;
    private static MainExecutor mainExecutor;
    private static int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();

    public static Executor getServiceExecutor() {
        if (serviceExecutor == null) {
            synchronized (ExecutorManager.class) {
                if (serviceExecutor == null) {
                    serviceExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES * 3, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(64));
                }
            }
        }
        return serviceExecutor;
    }

    public static Executor getDbExecutor() {
        if (dbExecutor == null) {
            synchronized (ExecutorManager.class) {
                if (dbExecutor == null) {
                    dbExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES * 3, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(64));
                }
            }
        }
        return dbExecutor;
    }

    public static MainExecutor getMainExecutor() {

        if (mainExecutor == null) {
            synchronized (ExecutorManager.class) {
                if (mainExecutor == null) {
                    mainExecutor = new MainExecutor();
                }
            }
        }
        return mainExecutor;
    }

}
