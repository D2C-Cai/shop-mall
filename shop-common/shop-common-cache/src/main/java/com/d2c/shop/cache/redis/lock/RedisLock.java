package com.d2c.shop.cache.redis.lock;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Cai
 */
public class RedisLock implements AutoCloseable {

    public static final String REDIS_LOCK = "RedisLock:";
    private static final long DEFAULT_EXPIRE = 60;
    //
    private String key;
    private RedisTemplate redisTemplate;

    public RedisLock(RedisTemplate redisTemplate, String key) {
        this.redisTemplate = redisTemplate;
        this.key = key;
    }

    /**
     * 等待锁的时间，单位为s
     *
     * @param key
     * @param timeout s
     * @param seconds
     */
    public boolean lock(String key, long timeout, TimeUnit seconds) {
        String lockKey = generateLockKey(key);
        long nanoWaitForLock = seconds.toNanos(timeout);
        long start = System.nanoTime();
        try {
            while ((System.nanoTime() - start) < nanoWaitForLock) {
                if (redisTemplate.getConnectionFactory().getConnection().setNX(lockKey.getBytes(), new byte[0])) {
                    redisTemplate.expire(lockKey, DEFAULT_EXPIRE, TimeUnit.SECONDS); // 暂设置为60s过期，防止异常中断锁未释放
                    return true;
                }
                TimeUnit.MILLISECONDS.sleep(1000 + new Random().nextInt(100)); // 加随机时间防止活锁
            }
        } catch (Exception e) {
            e.printStackTrace();
            unlock();
        }
        return false;
    }

    /**
     * 注意在finally里边调用
     */
    public void unlock() {
        try {
            String lockKey = generateLockKey(key);
            RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
            connection.del(lockKey.getBytes());
            connection.del(key.getBytes());
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateLockKey(String key) {
        return String.format(REDIS_LOCK + "%s", key);
    }

    @Override
    public void close() {
        try {
            String lockKey = generateLockKey(key);
            RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
            connection.del(lockKey.getBytes());
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
