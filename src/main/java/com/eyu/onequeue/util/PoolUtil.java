package com.eyu.onequeue.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author solq
 **/
public abstract class PoolUtil {
    private static final Logger logger = LoggerFactory.getLogger(PoolUtil.class);

    public static class ThreadNamed implements ThreadFactory {

	public ThreadNamed(String threadName) {
	    name = threadName;
	    group = new ThreadGroup(name);
	}

	final AtomicInteger ai = new AtomicInteger(1);
	String name;
	ThreadGroup group;

	@Override
	public Thread newThread(Runnable r) {
	    Thread ret = new Thread(group, r, name + ":" + ai.getAndIncrement(), 0);
	    return ret;
	}

    }

    public static ExecutorService createPool(int maxCore, int timeOutSeconds, String threadName) {
	return new ThreadPoolExecutor(4, maxCore, timeOutSeconds, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadNamed(threadName));
    }

    public static ExecutorService createCachePool(String threadName) {
	return Executors.newCachedThreadPool(new ThreadNamed(threadName));
    }

    public static ScheduledExecutorService createScheduledPool(int corePoolSize, String threadName) {
	return Executors.newScheduledThreadPool(corePoolSize, new ThreadNamed(threadName));
    }

    public static void shutdown(ExecutorService pool, long awaitTime) {
	logger.error("开始关闭总线线程池");
	try {
	    pool.shutdown();
	    if (!pool.awaitTermination(awaitTime, TimeUnit.SECONDS)) {
		logger.error("无法在预计时间内完成事件总线线程池关闭,尝试强行关闭");
		pool.shutdownNow();
		if (!pool.awaitTermination(awaitTime, TimeUnit.SECONDS)) {
		    logger.error("总线线程池无法完成关闭");
		}
	    }
	} catch (InterruptedException e) {
	    logger.error("总线线程池关闭时线程被打断,强制关闭事件总线线程池");
	    try {
		pool.shutdownNow();
	    } catch (Exception e2) {
		logger.error("{}", e2);
	    }
	}
    }

}
