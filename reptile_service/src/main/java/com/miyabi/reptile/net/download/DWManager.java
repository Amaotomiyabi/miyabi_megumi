package com.miyabi.reptile.net.download;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 下载任务管理者
 *
 * @author miyabi
 * @since 1.0
 **/


public class DWManager {
    private static DWManager dwManager = new DWManager();
    private BlockingQueue<Runnable> works = new ArrayBlockingQueue<>(10);
    private ThreadPoolExecutor workPool;
    private ThreadPoolExecutor filePool;

    private DWManager() {
        this.workPool = workPool();
    }

    public static DWManager getInstance() {
        return dwManager;
    }

    private ThreadPoolExecutor workPool() {
        return new ThreadPoolExecutor(1//Downloader.WORKER_SIZE
                , 1//Downloader.WORKER_SIZE
                , 5
                , TimeUnit.SECONDS
                , works
                , new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void execute(Runnable runnable) {
        workPool.execute(runnable);
    }
}
