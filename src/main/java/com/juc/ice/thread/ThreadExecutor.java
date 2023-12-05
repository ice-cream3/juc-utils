package com.juc.ice.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: TaskToolExecutor
 * @Description: 线程工具
 * @Author: ice
 * @Date: 2023/6/27 17:20
 */
public class ThreadExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ThreadExecutor.class);

    /**
     * 默认核心线程数
     */
    private static final int DEFAULT_CORE_SIZE = 10;

    /**
     * 默认最大线程数
     */
    private static final int DEFAULT_MAX_SIZE = 20;

    /**
     * 默认空闲线程存活时间
     */
    private static final long DEFAULT_ALIVE_TIME = 60;

    /**
     * 默认队列数量
     */
    private static final int DEFAULT_QUEUE_SIZE = 1024;

    /**
     * 线程池
     */
    private static final ExecutorService pool;

    /**
     * 线程工厂
     */
    private static final ThreadFactory threadFactory = new DefaultThreadFactory();

    /**
     * 任务较多时暂存队列
     */
    private static final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_SIZE);

    /**
     * 核心线程数
     */
    private int coreSize;

    /**
     * 最大线程数
     */
    private int maxSize;

    /**
     * 空闲线程存活时间
     */
    private long aliveTime;

    /**
     * 队列数量
     */
    private int queueSize;

    /**
     * 线程池名称
     */
    private String name;

    static {
        // 初始化线程池
        final Runtime JRE = Runtime.getRuntime();
        Integer logicCores = JRE.availableProcessors();
        pool = new ThreadPoolExecutor(logicCores, logicCores, DEFAULT_ALIVE_TIME, TimeUnit.SECONDS, workQueue, threadFactory);
        JRE.addShutdownHook(new Thread(pool::shutdown));
    }

    /**
     * 销毁线程池
     */
    public void destroy() {
        pool.shutdown();
    }

    /**
     * 执行Task
     */
    public static void execute(Runnable task) {
        pool.execute(task);
    }

    /**
     * 提交Task，可获取线程返回结果
     */
    public static <T> Future<T> submit(Callable<T> task) {
        return pool.submit(task);
    }


    /**
     * 默认线程工厂
     */
    static class DefaultThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "taskTool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0);
            // 守护线程
            if (thread.isDaemon())
                thread.setDaemon(false);
            // 线程优先级
            if (thread.getPriority() != Thread.NORM_PRIORITY)
                thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
}
