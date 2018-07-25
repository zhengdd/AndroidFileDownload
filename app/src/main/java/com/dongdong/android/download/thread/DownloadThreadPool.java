package com.dongdong.android.download.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadThreadPool {
    public static String TAG = "DownloadThreadPool";
    private int corePoolSize = 5;
    private int maxPoolSize = 5;
    private long keepAliveTime = 3000;
    private ThreadPoolExecutor mExecutor;

    private DownloadThreadPool() {


        TimeUnit unit = TimeUnit.MILLISECONDS;
        BlockingQueue workQueue = new LinkedBlockingDeque<>();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
        mExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue,
                threadFactory, handler);

    }

    public static class DownloadThreadPoolHolder {
        public final static DownloadThreadPool downloadThreadPool = new DownloadThreadPool();
    }

    public static DownloadThreadPool getInstance() {
        return DownloadThreadPoolHolder.downloadThreadPool;
    }

    public void execute(Runnable task) {

        mExecutor.execute(task);
    }

    public void remove(Runnable task) {

        mExecutor.remove(task);
    }

    public int getActiveCount() {
        return mExecutor.getActiveCount();
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }


    public boolean isIdle() {
        if (getActiveCount() < maxPoolSize) {
            return true;
        } else {
            return false;
        }

    }
}
