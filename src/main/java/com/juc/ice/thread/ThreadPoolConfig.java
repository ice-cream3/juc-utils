package com.juc.ice.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: ThreadPoolConfig
 * @Description: 线程配置
 * @Author: ice
 * @Date: 2023/12/6 11:42
 */
@Slf4j
@SpringBootConfiguration
public class ThreadPoolConfig extends ThreadPoolTaskExecutor {

    private static final long serialVersionUID = -229520689464167590L;

    // 从配置文件(nacos,applo)中获取线程配置参数
    // @Value(value = "${thread.core}")
    // private String threadCore;
     private int threadCore = 0;
     private int threadMaxCore = 0;

    /**
     * 通用工具类线程池
     * ThreadPoolUtil 删除 pool 的static声明和方法声明
     */
    @Bean(name = "commonThreadExecutor", initMethod = "init", destroyMethod = "destroy")
    public ThreadPoolUtil commonExecutor() {
        ThreadPoolUtil commonExecutor = new ThreadPoolUtil();
        commonExecutor.setThreadName("commonThread");
        commonExecutor.setCoreSize(15);
        commonExecutor.setMaxSize(32);
        commonExecutor.setQueueSize(2048);
        return commonExecutor;
    }

    /**
     * IO密集型线程池
     * ThreadPoolUtil 删除 pool 的static声明和方法声明
     */
    @Bean(name = "calcThreadExecutor", initMethod = "init", destroyMethod = "destroy")
    public ThreadPoolUtil calcExecutor() {
        ThreadPoolUtil calcExecutor = new ThreadPoolUtil();
        calcExecutor.setThreadName("calcThread");
        int cpuCount = Runtime.getRuntime().availableProcessors();
        if (threadCore != 0) {
            calcExecutor.setCoreSize(threadCore);
        } else {
            calcExecutor.setCoreSize(cpuCount * 2);
        }
        if (threadMaxCore != 0) {
            calcExecutor.setMaxSize(threadMaxCore);
        } else {
            calcExecutor.setMaxSize(cpuCount * 3);
        }
        calcExecutor.setQueueSize(1024);
        return calcExecutor;
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        int processors = Runtime.getRuntime().availableProcessors();
        if (processors <= 0) {
            processors = 10;
        }
        ThreadPoolExecutor poolExecutor = this.getThreadPoolExecutor();
        this.setQueueCapacity(1024);
        this.setAllowCoreThreadTimeOut(true);
        poolExecutor.setCorePoolSize(processors);
        poolExecutor.setMaximumPoolSize(processors * 2);
        poolExecutor.setKeepAliveTime(60, TimeUnit.SECONDS);
        poolExecutor.setThreadFactory(new DefaultThreadFactory());
        poolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        log.info("{}, coreSize:{}, max:{}, taskCount:{}, completedTaskCount:{}, activeCount:{}, queueSize:{}",
                this.getThreadNamePrefix(),
                poolExecutor.getCorePoolSize(),
                poolExecutor.getMaximumPoolSize(),
                poolExecutor.getTaskCount(),
                poolExecutor.getCompletedTaskCount(),
                poolExecutor.getActiveCount(),
                poolExecutor.getQueue().size());
        return poolExecutor;
    }

    /**
     * spring异步线程池配置
     * 方法Async使用
     * @return Executor
     */
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //获取到cpu内核数
        int i = Runtime.getRuntime().availableProcessors();
        // 设置线程池核心容量
        executor.setCorePoolSize(i);
        // 设置线程池最大容量
        executor.setMaxPoolSize(i * 2);
        // 设置任务队列长度
        executor.setQueueCapacity(1000);
        // 设置线程超时时间
        executor.setKeepAliveSeconds(60);
        // 设置线程名称前缀
        executor.setThreadNamePrefix("springAsyncPool-");
        // 设置任务丢弃后的处理策略,当poolSize已达到maxPoolSize，如何处理新任务（是拒绝还是交由其它线程处理）,
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 默认线程工厂
     */
    static class DefaultThreadFactory implements ThreadFactory {
        final ThreadGroup group;
        final String namePrefix;
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        final AtomicInteger threadNumber = new AtomicInteger(1);

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "taskTool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0);
            // 守护线程
            if (thread.isDaemon()) thread.setDaemon(false);
            // 线程优先级
            if (thread.getPriority() != Thread.NORM_PRIORITY) thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
}
