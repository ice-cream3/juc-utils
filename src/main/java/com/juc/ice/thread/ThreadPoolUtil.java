package com.juc.ice.thread;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: ThreadPoolUtils
 * @Description: 线程池
 *  加上@Service注解,交给spring管理,直接ThreadExecutor.execute更简单
 *  不加@Service注解,直接调用ThreadExecutor.execut
 * @Author: ice
 * @Date: 2023/6/28 12:47
 */
@Data
public class ThreadPoolUtil {


    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolUtil.class);

    /**
     * 默认核心线程数
     */
    private static final int DEFAULT_CORE_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * 默认最大线程数
     */
    private static final int DEFAULT_MAX_SIZE = DEFAULT_CORE_SIZE * 2 + 1;

    /**
     * 默认空闲线程存活时间
     */
    private static final long DEFAULT_ALIVE_TIME = 60;

    /**
     * 默认队列数量
     */
    private static final int DEFAULT_QUEUE_SIZE = 1024;

    /**
     * 任务较多时暂存队列
     */
    private static final BlockingQueue<Runnable> DEFAULT_WORK_QUEUE = new LinkedBlockingQueue<>(DEFAULT_CORE_SIZE);

    /**
     * 线程池
     * ThreadPoolExecutor
     */
    private static ExecutorService pool;

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
     * 队列
     */
    private BlockingQueue<Runnable> workQueue;

    /**
     * 线程工厂
     */
    private ThreadFactory threadFactory;

    /**
     * 线程池名称
     */
    private String threadName;

    /**
     * 初始化线程池
     */
    public void init() {
        if (null != pool) {
            return;
        }
        if (null == workQueue) {
            queueSize = queueSize > 0 ? queueSize : DEFAULT_QUEUE_SIZE;
            workQueue = new LinkedBlockingQueue<>(queueSize);
        }
        if (null == threadFactory) {
            threadFactory = new DefaultThreadFactory(this.threadName);
        }
        coreSize = coreSize > 0 ? coreSize : DEFAULT_CORE_SIZE;
        maxSize = maxSize > 0 ? maxSize : DEFAULT_MAX_SIZE;
        aliveTime = aliveTime > 0 ? aliveTime : DEFAULT_ALIVE_TIME;
        pool = new java.util.concurrent.ThreadPoolExecutor(coreSize, maxSize, aliveTime, TimeUnit.SECONDS, workQueue, threadFactory);
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
     *
     * @return future
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

        DefaultThreadFactory(String threadName) {
            SecurityManager manager = System.getSecurityManager();
            group = (manager != null) ? manager.getThreadGroup() : Thread.currentThread().getThreadGroup();
            String name = StringUtils.isBlank(threadName) ? "thread" : threadName;
            namePrefix = "taskTool-" + poolNumber.getAndIncrement() + "-" + name + "-";
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
