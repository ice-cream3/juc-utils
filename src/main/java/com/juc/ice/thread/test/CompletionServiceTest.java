package com.juc.ice.thread.test;

import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @ClassName: CompletionService
 * @Description:
 * @Author: ice
 * @Date: 2023/12/5 16:03
 */
@Service
public class CompletionServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CompletionServiceTest.class);

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    public static void main(String[] args) {

    }

    private boolean getReceiveStatus(User user, List<TaskClass> TaskClasss) {
        String username = user.getUsername();
        StopWatch stopWatch = new StopWatch("业务计时");
        stopWatch.start("获取领取任务状态开始");
        //根据任务类型进行分组
        java.util.concurrent.CompletionService<Boolean> service = new ExecutorCompletionService<>(threadPoolExecutor);
        Map<Integer, List<TaskClass>> typeMap = TaskClasss.stream().collect(Collectors.groupingBy(TaskClass::getType, Collectors.toList()));
        List<Future<Boolean>> futures = Arrays.stream(TaskEnum.values()).map(taskEnum -> service.submit(new Receive(taskEnum, user, typeMap))).collect(Collectors.toList());
        try {
            for (int i = 0; i < futures.size(); i++) {
                if (service.take().get()) break;
            }
            stopWatch.stop();
            logger.info("任务执行累计用时:{}", stopWatch.getTotalTimeMillis());
            return true;
        } catch (Exception e) {
            logger.info("任务状态异常{}, e:{}", username, e.getMessage(), e);
        } finally {
            futures.forEach(f -> f.cancel(true));
        }
        return false;
    }


    class Receive implements Callable<Boolean> {

        private final TaskEnum taskEnum;
        private final User user;
        private final Map<Integer, List<TaskClass>> typeMap;

        public Receive(TaskEnum taskEnum, User user, Map<Integer, List<TaskClass>> typeMap) {
            this.taskEnum = taskEnum;
            this.user = user;
            this.typeMap = typeMap;
        }

        @Override
        public Boolean call() throws Exception {
            logger.info("线程执行:{}, 任务类型:{}", Thread.currentThread().getName(), taskEnum.name());
            switch (taskEnum) {
                case TASK1: {
                    return getTask1(user, typeMap);
                }
                case TASK2: {
                    return getTask2(user, typeMap);
                }
                case TASK3: {
                    return getTask3(user, typeMap);
                }
                case TASK4: {
                    return getTask4(user, typeMap);
                }
                case TASK5: {
                    return getTask5(user, typeMap);
                }
                case TASK6: {
                    return getTask6(user, typeMap);
                }
                default:
                    return false;
            }
        }
    }

    private boolean getTask1(User user, Map<Integer, List<TaskClass>> typeMap) {
        logger.info("任务状态:任务1执行完成,user:{}", user.getUsername());
        typeMap.forEach((key, value) -> System.out.println("任务1信息:" + key + value.size()));
        return !CollectionUtils.isEmpty(typeMap.get(TaskEnum.TASK1.getType()));
    }

    private boolean getTask3(User user, Map<Integer, List<TaskClass>> typeMap) {
        logger.info("任务状态:任务3执行完成,user:{}", user.getUsername());
        typeMap.forEach((key, value) -> System.out.println("任务3信息:" + key + value.size()));
        return !CollectionUtils.isEmpty(typeMap.get(TaskEnum.TASK3.getType()));
    }

    private boolean getTask2(User user, Map<Integer, List<TaskClass>> typeMap) throws Exception {
        logger.info("任务状态:任务2执行完成,user:{}", user.getUsername());
        typeMap.forEach((key, value) -> System.out.println("任务2信息:" + key + value.size()));
        return !CollectionUtils.isEmpty(typeMap.get(TaskEnum.TASK2.getType()));
    }

    private boolean getTask4(User user, Map<Integer, List<TaskClass>> typeMap) {
        logger.info("任务状态:任务4执行完成,user:{}", user.getUsername());
        typeMap.forEach((key, value) -> System.out.println("任务4信息:" + key + value.size()));
        return !CollectionUtils.isEmpty(typeMap.get(TaskEnum.TASK4.getType()));
    }

    private boolean getTask5(User user, Map<Integer, List<TaskClass>> typeMap) {
        logger.info("任务状态:任务5执行完成,user:{}", user.getUsername());
        typeMap.forEach((key, value) -> System.out.println("任务5信息:" + key + value.size()));
        return !CollectionUtils.isEmpty(typeMap.get(TaskEnum.TASK5.getType()));
    }

    private boolean getTask6(User user, Map<Integer, List<TaskClass>> typeMap) {
        logger.info("任务状态:任务6执行完成,user:{}", user.getUsername());
        typeMap.forEach((key, value) -> System.out.println("任务6信息:" + key + value.size()));
        return !CollectionUtils.isEmpty(typeMap.get(TaskEnum.TASK6.getType()));
    }

}
