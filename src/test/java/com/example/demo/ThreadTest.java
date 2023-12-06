package com.example.demo;

import com.juc.ice.DemoApplication;
import com.juc.ice.thread.ThreadPoolUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ThreadPoolExecutor;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class ThreadTest {


    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource(name = "commonThreadExecutor")
    private ThreadPoolUtil commonThreadExecutor;
    @Resource(name = "calcThreadExecutor")
    private ThreadPoolUtil calcThreadExecutor;
    @Test
    public void testThreadUtil() {
        System.out.println("1111111111");
        ThreadPoolUtil.execute(() -> {
            try {
                System.out.println("线程1");
                Thread.sleep(3000);
                String name = Thread.currentThread().getName();
                System.out.println("线程1-结束" + name);
                ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
                int activeCount = threadGroup.activeCount();
                int activeGroupCount = threadGroup.activeGroupCount();
                System.out.println("总线程1:"+activeGroupCount+"活动线程:"+activeCount);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("22222222222");
        ThreadPoolUtil.execute(() -> {
            try {
                System.out.println("线程2");
                Thread.sleep(3000);
                String name = Thread.currentThread().getName();
                System.out.println("线程2-结束" + name);
                ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
                int activeCount = threadGroup.activeCount();
                int activeGroupCount = threadGroup.activeGroupCount();
                System.out.println("总线程2:"+activeGroupCount+"活动线程:"+activeCount);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("333333333333");
        ThreadPoolUtil.execute(() -> {
            try {
                System.out.println("线程3");
                Thread.sleep(3000);
                String name = Thread.currentThread().getName();
                System.out.println("线程3-结束" + name);
                ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
                int activeCount = threadGroup.activeCount();
                int activeGroupCount = threadGroup.activeGroupCount();
                System.out.println("总线程3:"+activeGroupCount+"活动线程:"+activeCount);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("4444444444444");
    }

    /**
     * ThreadPoolUtil 删除 pool 的static声明和方法声明
     */
    @Test
    public void testThread() {
        System.out.println("1111111111");
        commonThreadExecutor.execute(() -> {
            try {
                System.out.println("线程1");
                Thread.sleep(3000);
                String name = Thread.currentThread().getName();
                System.out.println("线程1-结束" + name);
                ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
                int activeCount = threadGroup.activeCount();
                int activeGroupCount = threadGroup.activeGroupCount();
                System.out.println("总线程1:"+activeGroupCount+"活动线程:"+activeCount);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("22222222222");
        commonThreadExecutor.execute(() -> {
            try {
                System.out.println("线程2");
                Thread.sleep(3000);
                String name = Thread.currentThread().getName();
                System.out.println("线程2-结束" + name);
                ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
                int activeCount = threadGroup.activeCount();
                int activeGroupCount = threadGroup.activeGroupCount();
                System.out.println("总线程2:"+activeGroupCount+"活动线程:"+activeCount);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("333333333333");
        commonThreadExecutor.execute(() -> {
            try {
                System.out.println("线程3");
                Thread.sleep(3000);
                String name = Thread.currentThread().getName();
                System.out.println("线程3-结束" + name);
                ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
                int activeCount = threadGroup.activeCount();
                int activeGroupCount = threadGroup.activeGroupCount();
                System.out.println("总线程3:"+activeGroupCount+"活动线程:"+activeCount);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        calcThreadExecutor.execute(() -> {
            try {
                System.out.println("线程4");
                Thread.sleep(3000);
                String name = Thread.currentThread().getName();
                System.out.println("线程4-结束" + name);
                ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
                int activeCount = threadGroup.activeCount();
                int activeGroupCount = threadGroup.activeGroupCount();
                System.out.println("总线程4:"+activeGroupCount+"活动线程:"+activeCount);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        calcThreadExecutor.execute(() -> {
            try {
                System.out.println("线程5");
                Thread.sleep(3000);
                String name = Thread.currentThread().getName();
                System.out.println("线程5-结束" + name);
                ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
                int activeCount = threadGroup.activeCount();
                int activeGroupCount = threadGroup.activeGroupCount();
                System.out.println("总线程5:"+activeGroupCount+"活动线程:"+activeCount);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("4444444444444");
    }

    public void testTimeZone() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置源时区
            Date sourceDate = formatter.parse("2023-03-15 10:00:00");
            formatter.setTimeZone(TimeZone.getTimeZone("America/New_York")); // 设置目标时区
            String targetDate = formatter.format(sourceDate);
            System.out.println(targetDate); // 输出：2023-03-14 21:00:00


            LocalDateTime sourceDateTime = LocalDateTime.parse("2023-03-15T10:00:00");
            ZoneId sourceTimeZone = ZoneId.of("Asia/Shanghai");
            ZonedDateTime sourceZonedDateTime = ZonedDateTime.of(sourceDateTime, sourceTimeZone);
            ZoneId targetTimeZone = ZoneId.of("America/New_York");
            ZonedDateTime targetZonedDateTime = sourceZonedDateTime.withZoneSameInstant(targetTimeZone);
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String targetDateTime = formatter2.format(targetZonedDateTime);
            System.out.println(targetDateTime); // 输出：2023-03-14 21:00:00
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void testRedis() {
        try {
            String key = "icelock";
            //redissonClient.getLock(key).lock(10, TimeUnit.SECONDS);
//        redissonClient.getLock(key).tryLockAsync();
//        redissonClient.getLock(key).lockAsync();
//          redissonClient.getLock(key).tryLock(15, TimeUnit.SECONDS);
            //redissonUtil.lock(key, 10L);
            RLock lock = redissonClient.getLock(key);
            RLock lock2 = redissonClient.getLock(key);
            //lock.lock();
            boolean locked = lock.isLocked();
            System.out.println(locked);
            boolean locked1 = lock2.isLocked();
            System.out.println(locked + "==" + locked1);
            boolean exists = lock.isExists();
            boolean exists1 = lock2.isExists();
            System.out.println(exists + "==" + exists1);
            lock2.unlock();
            System.out.println(666);
//            lock.unlock();
        /*redissonUtil.tryLock(key, 15);
        System.out.println(redissonUtil.isLocked(key));
        redissonUtil.forceUnlock(key);
        Thread.sleep(15 * 1000);
        redissonUtil.unlock(key);*/
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
