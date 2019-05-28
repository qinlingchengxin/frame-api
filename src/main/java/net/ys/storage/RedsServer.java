package net.ys.storage;

import net.ys.utils.PropertyUtil;
import redis.clients.jedis.JedisPoolConfig;


public enum RedsServer {

    MASTER(PropertyUtil.get("reds.host"), Integer.parseInt(PropertyUtil.get("reds.port")), Integer.parseInt(PropertyUtil.get("reds.timeout")), PropertyUtil.get("reds.password"), Integer.parseInt(PropertyUtil.get("reds.database")));

    private String host;
    private int port;
    private int timeout;
    private String password;
    private int database;
    private JedisPoolConfig config;

    RedsServer(String hostName, int port, int timeout, String password, int database) {
        this.host = hostName;
        this.port = port;
        this.timeout = timeout;
        this.password = password;
        this.database = database;
        this.config = new JedisPoolConfig();
        config.setMaxTotal(Integer.parseInt(PropertyUtil.get("reds.maxTotal")));
        config.setMaxIdle(Integer.parseInt(PropertyUtil.get("reds.maxIdle")));
        config.setMaxWaitMillis(Integer.parseInt(PropertyUtil.get("reds.maxWait")));
        config.setTestOnBorrow(true);
        config.setTestWhileIdle(true);
        config.setTestOnReturn(true);
        config.setMinEvictableIdleTimeMillis(30000);
        config.setTimeBetweenEvictionRunsMillis(30000);
        config.setNumTestsPerEvictionRun(-1);

    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public String getPassword() {
        return password;
    }

    public JedisPoolConfig getConfig() {
        return this.config;
    }

    public int getDatabase() {
        return database;
    }
}