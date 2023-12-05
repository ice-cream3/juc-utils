package com.juc.ice.thread.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @ClassName: TaskFutureTest
 * @Description:
 * @Author: ice
 * @Date: 2023/6/28 12:50
 */
public class TaskFutureTest {
    public static final Logger logger = LoggerFactory.getLogger(TaskFutureTest.class);
    public static void main(String[] args) {
        taskSerial();
    }

    /**
     * 多任务串行、利用Future计算任务执行结果的和
     * Future.get()会阻塞获取返回值
     */
    private static void taskSerial() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 100,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.AbortPolicy()); //拒绝策略
        try {
            long start = System.currentTimeMillis();
            // 阻塞获取
            int sum = 0;
            for (int i = 1; i < 4; i++) {
                sum += executor.submit(new MyTask(i)).get();
            }
            System.out.println("多任务计算的和：" + sum);
            System.out.println("总共消耗的时间：" + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 多任务并行、利用invokeAll计算任务执行结果的和
     * 这里需要注意的是，在res.get(i).get()时，会按list中任务添加顺序获取返回结果。
     * 也就是说即使最后提交的任务最先执行完毕，也只能最后获取
     */
    private static void invokeAllParallel() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 100,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.AbortPolicy()); //拒绝策略
        try {
            Long start = System.currentTimeMillis();

            /**
             * 利用invokeAll同时执行所有线程
             */
            List<Callable<Integer>> list = new ArrayList<>();
            for (int i = 3; i > 0; i--) {
                list.add(new MyTask(i));
            }
            List<Future<Integer>> res = executor.invokeAll(list);
            int sum = 0;
            for (int i = 0; i < 3; i++) {
                int value = res.get(i).get();
                System.out.println("第" + i + "个返回的任务id是：" + value);
                sum += value;
            }
            System.out.println("多任务计算的和：" + sum);
            System.out.println("总共消耗的时间：" + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 多任务并行、利用CompletionService计算任务执行结果的和，并优先获取最快执行完毕的任务返回值
     * 多任务，用时最小，利用completionService实现
     * 与 invokeAll不同的是，
     * CompletionService.take().get()通过消息队列生产-消费模式获取最先执行完的结果，不会按提交顺序获取结果
     * <p>
     * 可以看到CompletionService通过.take().get()可以不用考虑提交任务的顺序，会优先获取任务执行结束返回的值。
     * 因为CompletionService原理上是消息队列中的生产-消费模式，任务执行结束便将结果生产到队列中，然后利用.take().get()消费队列中的数据，
     * 从而优先获取已经执行完的任务返回值
     */
    private static void completionParallel() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 100,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.AbortPolicy()); //拒绝策略
        try {
            StopWatch stopWatch = new StopWatch("业务计时");
            stopWatch.start("第一步开始");
            int taskCount = 3;
            CompletionService<Integer> service = new ExecutorCompletionService<>(executor);
            List<Future<Integer>> futures = new ArrayList<Future<Integer>>(taskCount);
            long start = System.currentTimeMillis();
            for (int i = 0; i < taskCount; i--) {
                futures.add(service.submit(new MyTask(i)));
            }
            stopWatch.stop();
            logger.info("第一步执行完成,time={}", stopWatch.getLastTaskTimeMillis());
            stopWatch.start("第二步开始");
            try {
                int sum = 0;
                for (int i = 0; i < futures.size(); i++) {
                    try {
                        int value = service.take().get();
                        System.out.println("第" + i + "个返回的任务id是：" + value);
                        sum += value;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                stopWatch.stop();
                logger.info("任务执行完成,sum={},总time={}", sum, stopWatch.getTotalTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 如果获取其中任何一个线程结果后直接返回,可以用cancel方法取消其它任务
                futures.forEach(f -> f.cancel(true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
