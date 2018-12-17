package net.ys.storage;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

/**
 * 这个类不是多线程，不要看到run方法就是多线程，命名比较逗逼 ...
 *
 * @param <T>
 */
public interface RedsRunner<T> {
    T run(Jedis jedis) throws JedisException;
}
