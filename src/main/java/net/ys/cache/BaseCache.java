package net.ys.cache;

import net.ys.serialize.ISerialize;
import net.ys.serialize.KyRoSerialize;
import net.ys.storage.RedsExecutor;
import net.ys.storage.RedsRunner;
import net.ys.storage.RedsServer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: LiWenC
 * Date: 17-9-21
 */
@Repository
public class BaseCache {

    ISerialize serialize = KyRoSerialize.getInstance();

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
                    return "OK".equals(jedis.set(serialize.serialize(key), serialize.serialize(obj)));
                } catch (Exception e) {
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
                    byte[] data = jedis.get(serialize.serialize(key));
                    if (data != null) {
                        return serialize.deSerialize(data, clazz);
                    }
                } catch (Exception e) {
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
                    return jedis.del(serialize.serialize(key)) > 0;
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
                    return jedis.zadd(serialize.serialize(key), score, serialize.serialize(obj)) > 0;
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
    public boolean save(final String key, final double score, final Object obj) {
        RedsRunner<Boolean> rr = new RedsRunner<Boolean>() {
            @Override
            public Boolean run(Jedis jedis) throws JedisConnectionException {
                try {
                    jedis.zremrangeByScore(serialize.serialize(key), score, score);
                    return jedis.zadd(serialize.serialize(key), score, serialize.serialize(obj)) > 0;
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
     * @return
     */
    public <T> T get(final String key, final double score, final Class<T> clazz) {
        RedsRunner<T> rr = new RedsRunner<T>() {
            @Override
            public T run(Jedis jedis) throws JedisConnectionException {
                try {
                    Set<byte[]> data = jedis.zrangeByScore(serialize.serialize(key), score, score);
                    if (data.size() > 0) {
                        for (byte[] bytes : data) {
                            return serialize.deSerialize(bytes, clazz);
                        }
                    }
                } catch (Exception e) {
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
                    Set<byte[]> data = jedis.zrangeByScore(serialize.serialize(key), min, max, offset, count);
                    if (CollectionUtils.isNotEmpty(data)) {
                        for (byte[] bytes : data) {
                            list.add(serialize.deSerialize(bytes, clazz));
                        }
                    }
                } catch (Exception e) {
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
                    Set<byte[]> data = jedis.zrevrangeByScore(serialize.serialize(key), max, min, offset, count);
                    if (CollectionUtils.isNotEmpty(data)) {
                        for (byte[] bytes : data) {
                            list.add(serialize.deSerialize(bytes, clazz));
                        }
                    }
                } catch (Exception e) {
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
                    return jedis.zcard(serialize.serialize(key));
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
                    return jedis.zremrangeByScore(serialize.serialize(key), score, score) > 0;
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
