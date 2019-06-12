package net.ys.storage;

import net.ys.util.LogUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class RedsExecutor<T> {

    public RedsExecutor() {
    }

    public T exe(RedsRunner<T> redsRunner, RedsServer redsServer) {
        T rst = null;
        Jedis j = null;
        try {
            j = RedsDBIns.INSTANCE.getReds(redsServer);
            if (j != null) {
                rst = redsRunner.run(j);
                RedsDBIns.INSTANCE.release(redsServer, j);
            } else {
                LogUtil.info("Stop process as Reds Connect Failed for " + redsServer.getHost() + ":" + redsServer.getPort() + ",timeout=" + redsServer.getTimeout());
            }
        } catch (JedisException e) {
            RedsDBIns.INSTANCE.releaseBrokenReds(redsServer, j);
            LogUtil.error(e);
        } catch (Exception e) {
            RedsDBIns.INSTANCE.release(redsServer, j);
            LogUtil.error(e);
        }
        return rst;
    }
}
