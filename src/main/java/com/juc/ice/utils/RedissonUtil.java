package com.juc.ice.utils;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedissonUtil
 * @Description Redisson操作工具类
 *
 *<!--redisson-->
 * <dependency>
 *     <groupId>org.redisson</groupId>
 *     <artifactId>redisson-spring-boot-starter</artifactId>
 *     <version>${redisson.springboot.starter.version}</version>
 * </dependency>
 */
@Component
public class RedissonUtil {

    private static final List<String> TIMEUNIT = Arrays.asList("DAYS", "HOURS", "MINUTES", "SECONDS", "MILLISECONDS", "MICROSECONDS", "NANOSECONDS");

    @Autowired
    private RedissonClient redissonClient;

    public void set(String key, Object value) {
        redissonClient.getBucket(key).set(value);
    }

    /**
     * 获取锁
     * 【启动锁续期】机制,默认加锁时长30秒
     * @param key key
     */
    public void lock(String key) {
        redissonClient.getLock(key).lock();
    }

    /**
     * 获取锁对象
     * @param key k
     * @return 锁对象
     */
    public RLock getLock(String key) {
        return redissonClient.getLock(key);
    }

    /**
     * 获取锁
     * 不启动锁续期机制,指定leaseTime时长后自动释放锁
     * @param key key
     * @param leaseTime 锁过期时间
     */
    public void lock(String key, long leaseTime) {
        redissonClient.getLock(key).lock(leaseTime, TimeUnit.SECONDS);
    }

    /**
     * 检查此锁是否被任何线程锁定
     * 返回：如果锁定则为 true，否则为 false
     * @param key key
     */
    public boolean isLocked(String key) {
        return redissonClient.getLock(key).isLocked();
    }

    /**
     * key是否存在
     * @param key key
     */
    public boolean isExists(String key) {
        return redissonClient.getLock(key).isExists();
    }

    /**
     * 尝试获取锁
     *  不启动锁续期机制,指定leaseTime时长后自动释放锁
     * @param key key
     * @param waitTime 获取锁等待时间,超时如果没获取到锁返回false
     * @param leaseTime 锁过期时间
     * @return 是否获取
     */
    public boolean tryLock(String key, long waitTime, long leaseTime) {
        try {
            return redissonClient.getLock(key).tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 尝试获取锁
     *  【启动锁续期】机制,默认加锁时长30秒
     * @param key 锁key
     * @param time 尝试等待时间,超时如果没获取到锁返回false
     * @return 是否获取锁
     */
    public boolean tryLock(String key, long time) {
        try {
            return redissonClient.getLock(key).tryLock(time, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 尝试获取锁
     *  【启动锁续期】机制,默认加锁时长30秒
     * @param key 锁key
     * @return 是否获取锁
     */
    public boolean tryLock(String key) {
        return redissonClient.getLock(key).tryLock();
    }

    /**
     * 强制解锁：当其他线程持有锁时，使用此方法可以立即释放锁，而不需要等待锁的自然过期或等待解锁操作
     * @param key key
     * @return 是否成功
     */
    public boolean forceUnlock(String key) {
        return redissonClient.getLock(key).forceUnlock();
    }

    /**
     * 释放锁
     * @param key 锁key
     */
    public void unlock(String key) {
        redissonClient.getLock(key).unlock();
    }

    /**
     * 缓存
     *
     * @param time 默认秒
     */
    public void set(String key, Object value, long time) {
        redissonClient.getBucket(key).set(value, time, TimeUnit.SECONDS);
    }

    /**
     * @param timeUnit DAYS 天
     *                 HOURS 小时
     *                 MINUTES 分钟
     *                 SECONDS 秒
     *                 MILLISECONDS 毫秒
     *                 MICROSECONDS 微秒
     *                 NANOSECONDS 纳秒
     */
    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        redissonClient.getBucket(key).set(value, time, timeUnit);
    }

    public Object get(String key) {
        return redissonClient.getBucket(key).get();
    }

    public Boolean del(String key) {
        return redissonClient.getBucket(key).delete();
    }

    public Boolean expire(String key, long time) {
        return redissonClient.getBucket(key).expire(time, TimeUnit.SECONDS);
    }

    public Boolean expire(String key, long time, TimeUnit timeUnit) {
        return redissonClient.getBucket(key).expire(time, timeUnit);
    }

    /**
     * 获取过期时间
     *
     * @return 返回毫秒
     */
    public Long getMillisecondExpire(String key) {
        long remained = redissonClient.getBucket(key).remainTimeToLive();
        if (remained <= 0) {
            return 0L;
        }
        return remained;
    }

    /**
     * 获取过期时间
     *
     * @return 返回秒
     */
    public Long getExpire(String key) {
        long remained = redissonClient.getBucket(key).remainTimeToLive();
        if (remained <= 0) {
            return 0L;
        }
        return (remained / 1000);
    }

    public Boolean hasKey(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    /**
     * 先+1后返回:++i
     * @param key k
     * @return v
     */
    public Long incrAndGet(String key) {
        return redissonClient.getAtomicLong(key).incrementAndGet();
    }

    /**
     * 先返回再+1:i++
     * @param key k
     * @return v
     */
    public Long getAndIncr(String key) {
        return redissonClient.getAtomicLong(key).getAndIncrement();
    }

    public Long incrAndGet(String key, long val) {
        return redissonClient.getAtomicLong(key).addAndGet(val);
    }

    public Long getAndIncr(String key, long val) {
        return redissonClient.getAtomicLong(key).getAndAdd(val);
    }

    public Long decrAndGet(String key, long val) {
        return redissonClient.getAtomicLong(key).addAndGet(-val);
    }

    public Long getAndDecr(String key, long val) {
        return redissonClient.getAtomicLong(key).getAndAdd(-val);
    }

    /**
     * 自增操作
     *
     * @param key  自增key
     * @param val  自增val
     * @param time 过期时间
     * @return 自增后的val
     */
    public Long incr(String key, long val, long time) {
        Long result = redissonClient.getAtomicLong(key).addAndGet(val);
        redissonClient.getBucket(key).expire(time, TimeUnit.SECONDS);
        return result;
    }

    /**
     * 自减操作
     *
     * @param key  自减key
     * @param val  自减val
     * @param time 过期时间
     * @return 自减后的val
     */
    public Long decr(String key, long val, long time) {
        long result = redissonClient.getAtomicLong(key).addAndGet(-val);
        redissonClient.getBucket(key).expire(time, TimeUnit.SECONDS);
        return result;
    }

    public Object hGet(String key, String hashKey) {
        return redissonClient.getMap(key).get(hashKey);
    }

    public Boolean hSet(String key, String hashKey, Object value, long time) {
        redissonClient.getMap(key).put(hashKey, value);
        return redissonClient.getMap(key).expire(time, TimeUnit.SECONDS);
    }

    public Object hSet(String key, String hashKey, Object value) {
        return redissonClient.getMap(key).put(hashKey, value);
    }

    public Map<Object, Object> hGetAll(String key) {
        return redissonClient.getMap(key).readAllMap();
    }

    public Boolean hSetAll(String key, Map<String, Object> map, long time) {
        redissonClient.getMap(key).putAll(map);
        return redissonClient.getMap(key).expire(time, TimeUnit.SECONDS);
    }

    public void hSetAll(String key, Map<String, ?> map) {
        redissonClient.getMap(key).putAll(map);
    }

    public void hDel(String key, String hashKey) {
        redissonClient.getMap(key).remove(hashKey);
    }

    public long hDel(String key, String... hashKey) {
        return redissonClient.getMap(key).fastRemove(hashKey);
    }

    public Boolean hHasKey(String key, String hashKey) {
        return redissonClient.getMap(key).containsKey(hashKey);
    }

    public Set<Object> sMembers(String key) {
        return redissonClient.getSet(key).readAll();
    }

    public boolean sAdd(String key, Object value) {
        return redissonClient.getSet(key).add(value);
    }

    public boolean sAddAll(String key, Set<Object> vals) {
        return redissonClient.getSet(key).addAll(vals);
    }

    public boolean sAdd(String key, Object val, long time) {
        redissonClient.getSet(key).add(val);
        return redissonClient.getSet(key).expire(time, TimeUnit.SECONDS);
    }

    public boolean sAddAll(String key, Set<Object> vals, long time) {
        redissonClient.getSet(key).addAll(vals);
        return redissonClient.getSet(key).expire(time, TimeUnit.SECONDS);
    }

    public Boolean sIsMember(String key, Object value) {
        return redissonClient.getSet(key).contains(value);
    }

    public int sSize(String key) {
        return redissonClient.getSet(key).size();
    }

    public boolean sRemove(String key, Object val) {
        return redissonClient.getSet(key).remove(val);
    }

    public boolean sRemoveAll(String key, Set<Object> values) {
        return redissonClient.getSet(key).removeAll(values);
    }

    public List<Object> lReadAll(String key) {
        return redissonClient.getList(key).readAll();
    }

    public int lSize(String key) {
        return redissonClient.getList(key).size();
    }

    public Object lIndex(String key, int index) {
        return redissonClient.getList(key).get(index);
    }

    public List<Object> lSubList(String key, int start, int end) {
        return redissonClient.getList(key).subList(start, end);
    }

    public boolean lContains(String key, Object obj) {
        return redissonClient.getList(key).contains(obj);
    }

    public boolean lContainsAll(String key, List<Object> objs) {
        return redissonClient.getList(key).containsAll(objs);
    }

    public int lIndexOf(String key, Object obj) {
        return redissonClient.getList(key).indexOf(obj);
    }

    public int lLastIndexOf(String key, Object obj) {
        return redissonClient.getList(key).lastIndexOf(obj);
    }

    public boolean lAdd(String key, Object value) {
        return redissonClient.getList(key).add(value);
    }

    public boolean lAdd(String key, Object value, long time) {
        redissonClient.getList(key).add(value);
        return redissonClient.getList(key).expire(time, TimeUnit.SECONDS);
    }

    public boolean lAddAll(String key, List<Object> values) {
        return redissonClient.getList(key).addAll(values);
    }

    public int lAddAfter(String key, Object elementToFind, Object element) {
        return redissonClient.getList(key).addAfter(elementToFind, element);
    }

    public int lAddBefore(String key, Object elementToFind, Object element) {
        return redissonClient.getList(key).addBefore(elementToFind, element);
    }

    public void lAddIndex(String key, int index, Object element) {
        redissonClient.getList(key).add(index, element);
    }

    public boolean lRemove(String key, Object value) {
        return redissonClient.getList(key).remove(value);
    }

    public boolean lRemoveAll(String key, List<Object> values) {
        return redissonClient.getList(key).removeAll(values);
    }

}