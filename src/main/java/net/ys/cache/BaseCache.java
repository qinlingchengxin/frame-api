package net.ys.cache;

import net.ys.storage.RedsExecutor;
import net.ys.storage.RedsRunner;
import net.ys.storage.RedsServer;
import org.apache.commons.collections.CollectionUtils;
import org.msgpack.MessagePack;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: LiWenC
 * Date: 17-9-21
 */
@Repository
public class BaseCache {

    MessagePack msgPack = new MessagePack();

    /**
     * k-v
     *
     * @param key
     * @param obj
     * @return
     */
    public boolean save(final String key, final Object obj) {
        RedsRunner<Boolean> rr = new RedsRunner<Boolean>() {
            @Override
            public Boolean run(Jedis jedis) throws JedisConnectionException {
                try {
                    return "OK".equals(jedis.set(msgPack.write(key), msgPack.write(obj)));
                } catch (IOException e) {
                }
                return false;
            }
        };
        return new RedsExecutor<Boolean>().exe(rr, RedsServer.MASTER);
    }

    /**
     * k-v
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(final String key, final Class<T> clazz) {
        RedsRunner<T> rr = new RedsRunner<T>() {
            @Override
            public T run(Jedis jedis) throws JedisConnectionException {
                try {
                    byte[] data = jedis.get(msgPack.write(key));
                    if (data != null) {
                        return msgPack.read(data, clazz);
                    }
                } catch (IOException e) {
                }
                return null;
            }
        };
        return new RedsExecutor<T>().exe(rr, RedsServer.MASTER);
    }

    /**
     * k-v
     *
     * @param key
     * @return
     */
    public boolean delete(final String key) {
        RedsRunner<Boolean> rr = new RedsRunner<Boolean>() {
            @Override
            public Boolean run(Jedis jedis) throws JedisConnectionException {
                try {
                    return jedis.del(msgPack.write(key)) > 0;
                } catch (Exception e) {
                }
                return false;
            }
        };
        return new RedsExecutor<Boolean>().exe(rr, RedsServer.MASTER);
    }

    /**
     * sorted set
     *
     * @param key
     * @param obj
     * @return
     */
    public boolean add(final String key, final double score, final Object obj) {
        RedsRunner<Boolean> rr = new RedsRunner<Boolean>() {
            @Override
            public Boolean run(Jedis jedis) throws JedisConnectionException {
                try {
                    return jedis.zadd(msgPack.write(key), score, msgPack.write(obj)) > 0;
                } catch (IOException e) {
                }
                return false;
            }
        };
        return new RedsExecutor<Boolean>().exe(rr, RedsServer.MASTER);
    }

    /**
     * sorted set
     *
     * @param key
     * @param obj
     * @return
     */
    public boolean save(final String key, final double score, final Object obj) {
        RedsRunner<Boolean> rr = new RedsRunner<Boolean>() {
            @Override
            public Boolean run(Jedis jedis) throws JedisConnectionException {
                try {
                    jedis.zremrangeByScore(msgPack.write(key), score, score);
                    return jedis.zadd(msgPack.write(key), score, msgPack.write(obj)) > 0;
                } catch (IOException e) {
                }
                return false;
            }
        };
        return new RedsExecutor<Boolean>().exe(rr, RedsServer.MASTER);
    }

    /**
     * sorted set
     *
     * @param key
     * @return
     */
    public <T> T get(final String key, final double score, final Class<T> clazz) {
        RedsRunner<T> rr = new RedsRunner<T>() {
            @Override
            public T run(Jedis jedis) throws JedisConnectionException {
                try {
                    Set<byte[]> data = jedis.zrangeByScore(msgPack.write(key), score, score);
                    if (data.size() > 0) {
                        for (byte[] bytes : data) {
                            return msgPack.read(bytes, clazz);
                        }
                    }
                } catch (IOException e) {
                }
                return null;
            }
        };
        return new RedsExecutor<T>().exe(rr, RedsServer.MASTER);
    }

    /**
     * sorted set
     *
     * @param key
     * @return
     */
    public <T> List<T> getsAsc(final String key, final double min, final double max, final int offset, final int count, final Class<T> clazz) {
        RedsRunner<List<T>> rr = new RedsRunner<List<T>>() {
            @Override
            public List<T> run(Jedis jedis) throws JedisConnectionException {
                List<T> list = new ArrayList<T>();
                try {
                    Set<byte[]> data = jedis.zrangeByScore(msgPack.write(key), min, max, offset, count);
                    if (CollectionUtils.isNotEmpty(data)) {
                        for (byte[] bytes : data) {
                            list.add(msgPack.read(bytes, clazz));
                        }
                    }
                } catch (IOException e) {
                }
                return list;
            }
        };
        return new RedsExecutor<List<T>>().exe(rr, RedsServer.MASTER);
    }

    /**
     * sorted set
     *
     * @param key
     * @return
     */
    public <T> List<T> getsDesc(final String key, final double min, final double max, final int offset, final int count, final Class<T> clazz) {
        RedsRunner<List<T>> rr = new RedsRunner<List<T>>() {
            @Override
            public List<T> run(Jedis jedis) throws JedisConnectionException {
                List<T> list = new ArrayList<T>();
                try {
                    Set<byte[]> data = jedis.zrevrangeByScore(msgPack.write(key), max, min, offset, count);
                    if (CollectionUtils.isNotEmpty(data)) {
                        for (byte[] bytes : data) {
                            list.add(msgPack.read(bytes, clazz));
                        }
                    }
                } catch (IOException e) {
                }
                return list;
            }
        };
        return new RedsExecutor<List<T>>().exe(rr, RedsServer.MASTER);
    }

    /**
     * sorted set
     *
     * @param key
     * @return
     */
    public long getKeyCount(final String key) {
        RedsRunner<Long> rr = new RedsRunner<Long>() {
            @Override
            public Long run(Jedis jedis) throws JedisConnectionException {
                try {
                    return jedis.zcard(msgPack.write(key));
                } catch (Exception e) {
                }
                return 0L;
            }
        };
        return new RedsExecutor<Long>().exe(rr, RedsServer.MASTER);
    }

    /**
     * sorted set
     *
     * @param key
     * @param score
     * @return
     */
    public boolean delete(final String key, final long score) {
        RedsRunner<Boolean> rr = new RedsRunner<Boolean>() {
            @Override
            public Boolean run(Jedis jedis) throws JedisConnectionException {
                try {
                    return jedis.zremrangeByScore(msgPack.write(key), score, score) > 0;
                } catch (Exception e) {
                }
                return false;
            }
        };
        return new RedsExecutor<Boolean>().exe(rr, RedsServer.MASTER);
    }

    /**
     * 获取访问次数
     *
     * @param cacheKey
     * @param timeLimit 秒
     * @return
     */
    public long getAccessCount(final String cacheKey, final int timeLimit) {
        RedsRunner<Long> rr = new RedsRunner<Long>() {
            @Override
            public Long run(Jedis jedis) throws JedisConnectionException {
                try {
                    long currVal = jedis.incr(cacheKey);//当前访问次数
                    if (currVal == 1) {//第一次访问的时候加个有效期
                        jedis.expire(cacheKey, timeLimit);
                    }
                    return currVal;
                } catch (Exception e) {
                }
                return 0L;
            }
        };
        return new RedsExecutor<Long>().exe(rr, RedsServer.MASTER);
    }
}
