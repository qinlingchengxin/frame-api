package net.ys.cache;

import net.ys.serialize.ISerialize;
import net.ys.serialize.KyRoSerialize;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: NMY
 * Date: 17-9-21
 */
@Repository
public class BaseCache {

    ISerialize serialize = KyRoSerialize.getInstance();

    @Resource
    private JedisPool redsPool;

    /**
     * k-v
     *
     * @param key
     * @param obj
     * @return
     */
    public boolean save(String key, Object obj) {
        Jedis reds = null;
        try {
            reds = redsPool.getResource();
            return "OK".equals(reds.set(serialize.serialize(key), serialize.serialize(obj)));
        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return false;
    }

    /**
     * k-v
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        Jedis reds = null;
        try {
            reds = redsPool.getResource();
            byte[] data = reds.get(serialize.serialize(key));
            if (data != null) {
                return serialize.deSerialize(data, clazz);
            }
        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return null;
    }

    /**
     * k-v
     *
     * @param key
     * @return
     */
    public boolean delete(String key) {
        Jedis reds = null;
        try {
            reds = redsPool.getResource();
            return reds.del(serialize.serialize(key)) > 0;
        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return false;
    }

    /**
     * sorted set
     *
     * @param key
     * @param obj
     * @return
     */
    public boolean add(String key, double score, Object obj) {
        Jedis reds = null;
        try {
            reds = redsPool.getResource();
            return reds.zadd(serialize.serialize(key), score, serialize.serialize(obj)) > 0;
        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return false;
    }

    /**
     * sorted set
     *
     * @param key
     * @param obj
     * @return
     */
    public boolean save(String key, double score, Object obj) {
        Jedis reds = null;
        try {
            reds = redsPool.getResource();
            reds.zremrangeByScore(serialize.serialize(key), score, score);
            return reds.zadd(serialize.serialize(key), score, serialize.serialize(obj)) > 0;
        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return false;
    }

    /**
     * sorted set
     *
     * @param key
     * @return
     */
    public <T> T get(String key, double score, Class<T> clazz) {
        Jedis reds = null;
        try {
            reds = redsPool.getResource();
            Set<byte[]> data = reds.zrangeByScore(serialize.serialize(key), score, score);
            if (data.size() > 0) {
                for (byte[] bytes : data) {
                    return serialize.deSerialize(bytes, clazz);
                }
            }

        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return null;
    }

    /**
     * sorted set
     *
     * @param key
     * @return
     */
    public <T> List<T> getsAsc(String key, double min, double max, int offset, int count, Class<T> clazz) {
        Jedis reds = null;
        List<T> list = new ArrayList<T>();
        try {
            reds = redsPool.getResource();
            Set<byte[]> data = reds.zrangeByScore(serialize.serialize(key), min, max, offset, count);
            if (CollectionUtils.isNotEmpty(data)) {
                for (byte[] bytes : data) {
                    list.add(serialize.deSerialize(bytes, clazz));
                }
            }
        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return list;
    }

    /**
     * sorted set
     *
     * @param key
     * @return
     */
    public <T> List<T> getsDesc(String key, double min, double max, int offset, int count, Class<T> clazz) {

        Jedis reds = null;
        List<T> list = new ArrayList<T>();
        try {
            reds = redsPool.getResource();
            Set<byte[]> data = reds.zrevrangeByScore(serialize.serialize(key), max, min, offset, count);
            if (CollectionUtils.isNotEmpty(data)) {
                for (byte[] bytes : data) {
                    list.add(serialize.deSerialize(bytes, clazz));
                }
            }
        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return list;
    }

    /**
     * sorted set
     *
     * @param key
     * @return
     */
    public long getKeyCount(String key) {
        Jedis reds = null;
        try {
            reds = redsPool.getResource();
            return reds.zcard(serialize.serialize(key));
        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return 0L;
    }

    /**
     * sorted set
     *
     * @param key
     * @param score
     * @return
     */
    public boolean delete(String key, long score) {
        Jedis reds = null;
        try {
            reds = redsPool.getResource();
            return reds.zremrangeByScore(serialize.serialize(key), score, score) > 0;
        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return false;
    }

    /**
     * 获取访问次数
     *
     * @param cacheKey
     * @param timeLimit 秒
     * @return
     */
    public long getAccessCount(String cacheKey, int timeLimit) {
        Jedis reds = null;
        try {
            reds = redsPool.getResource();
            long currVal = reds.incr(cacheKey);//当前访问次数
            if (currVal == 1) {//第一次访问的时候加个有效期
                reds.expire(cacheKey, timeLimit);
            }
            return currVal;
        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return 0L;
    }

    /**
     * 释放连接
     *
     * @param reds
     */
    public void close(Jedis reds) {
        if (reds != null) {
            reds.close();
            if (reds.isConnected()) {
                try {
                    reds.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
