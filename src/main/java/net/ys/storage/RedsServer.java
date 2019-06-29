package net.ys.storage;

import net.ys.component.SysConfig;
import redis.clients.jedis.JedisPoolConfig;


public enum RedsServer {

    MASTER(SysConfig.redsHost, SysConfig.redsPort, 3000, SysConfig.redsPassword, SysConfig.redsDatabase);

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
        config.setMaxTotal(256);
        config.setMaxIdle(300);
        config.setMaxWaitMillis(1000);
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