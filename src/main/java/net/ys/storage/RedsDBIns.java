package net.ys.storage;

import net.ys.utils.LogUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本类实现了使用Reds客户端API对Reds数据库的底层连接池的封装
 */
public enum RedsDBIns {

    INSTANCE;

    private volatile ConcurrentHashMap<RedsServer, JedisPool> redsPoolContainer;
    private final TreeMap<String, AtomicInteger> redsClientCounterMap;

    RedsDBIns() {
        this.redsPoolContainer = new ConcurrentHashMap<RedsServer, JedisPool>();
        this.redsClientCounterMap = new TreeMap<String, AtomicInteger>();
    }

    public synchronized void destroy() {
        RedsServer[] ss = RedsServer.values();
        for (RedsServer rs : ss) {
            JedisPool p = redsPoolContainer.get(rs);
            if (p != null) {
                p.destroy();
            }
        }
        redsPoolContainer.clear();
    }

    public synchronized void init() {
        RedsServer[] ss = RedsServer.values();
        for (RedsServer rs : ss) {
            final JedisPool p = new JedisPool(rs.getConfig(), rs.getHost(), rs.getPort(), rs.getTimeout());
            redsPoolContainer.put(rs, p);
        }
    }

    public Jedis getReds(RedsServer server) {
        Jedis j = null;
        try {
            JedisPool pool = redsPoolContainer.get(server);

            if (pool == null) {
                synchronized (this) {
                    pool = new JedisPool(server.getConfig(), server.getHost(), server.getPort(), server.getTimeout(), server.getPassword());
                    if (pool != null) {
                        j = pool.getResource();
                        redsPoolContainer.putIfAbsent(server, pool);
                    }
                }
            } else {
                j = pool.getResource();
            }

            if (j != null) {
                AtomicInteger counter = this.redsClientCounterMap.get(server.name());
                if (counter == null) {
                    synchronized (server) {
                        counter = new AtomicInteger(1);
                        this.redsClientCounterMap.put(server.name(), counter);
                    }
                } else {
                    this.redsClientCounterMap.get(server.name()).incrementAndGet();
                }
            }
            return j;
        } catch (JedisException e) {
            this.releaseBrokenReds(server, j);
            LogUtil.error(e);
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return null;
    }

    public void release(RedsServer server, Jedis jedis) {
        JedisPool pool = redsPoolContainer.get(server);
        if (pool != null && jedis != null) {
            pool.returnResource(jedis);
            AtomicInteger counter = this.redsClientCounterMap.get(server.name() + ".release");
            if (counter == null) {
                synchronized (server) {
                    counter = new AtomicInteger(1);
                    this.redsClientCounterMap.put(server.name() + ".release", counter);
                }
            } else {
                this.redsClientCounterMap.get(server.name() + ".release").incrementAndGet();
            }
        } else {
            LogUtil.info("[Reds Release], pool=" + pool + ";reds=" + jedis);
        }
    }

    public void releaseBrokenReds(RedsServer server, Jedis jedis) {
        JedisPool pool = redsPoolContainer.get(server);
        if (pool != null && jedis != null) {
            pool.returnBrokenResource(jedis);

            AtomicInteger counter = this.redsClientCounterMap.get(server.name() + ".broken");
            if (counter == null) {
                synchronized (server) {
                    counter = new AtomicInteger(1);
                    this.redsClientCounterMap.put(server.name() + ".broken", counter);
                }
            } else {
                this.redsClientCounterMap.get(server.name() + ".broken").incrementAndGet();
            }
        } else {
            LogUtil.info("[Reds Release Broken], pool=" + pool + ";reds=" + jedis);
        }
        LogUtil.info("[Reds Release Broken]Broken Reds released : " + jedis);
    }

    public Map<String, AtomicInteger> getRedsStatus() {
        return this.redsClientCounterMap;
    }
}
