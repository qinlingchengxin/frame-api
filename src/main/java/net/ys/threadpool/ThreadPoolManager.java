/**
 *
 */
package net.ys.threadpool;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author sujg
 */
public enum ThreadPoolManager {

    INSTANCE;

    /**
     * 综合线程池
     */
    public TPool complexPool = null;
    /**
     * 静默后台专用调度线程
     */
    public volatile ScheduledExecutorService daemonBgScheduledPool = null;

    /**
     * 本地后台专用调度线程
     */
    public volatile ScheduledExecutorService localBgScheduledPool = null;
    /**
     * 综合调度线程池
     */
    public volatile ScheduledExecutorService complexScheduledPool = null;
    /**
     * 静默统计线程池
     */
    public volatile ScheduledExecutorService daemonStatsBgScheduledPool = null;

    ThreadPoolManager() {
        /**
         * @param corePoolSize ： 线程池维护线程的最少数量
         * @param maximumPoolSize ：线程池维护线程的最大数量
         * @param keepAliveTime ： 线程池维护线程所允许的空闲时间
         * @param unit ： 线程池维护线程所允许的空闲时间的单位
         * @param workQueue ：线程池所使用的缓冲队列
         * @param  handler ： 线程池对拒绝任务的处理策略
         * */
        complexPool = new TPool(100, 500, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000), null);
        /**
         * 20个线程
         */
        daemonBgScheduledPool = Executors.newScheduledThreadPool(20, new DaemonThreadFactory("daemonBgScheduledPool"));
        /**
         * 20个线程
         */
        localBgScheduledPool = Executors.newScheduledThreadPool(20, new DaemonThreadFactory("localBgScheduledPool"));
        /**
         * 20个线程
         */
        complexScheduledPool = Executors.newScheduledThreadPool(20, new DaemonThreadFactory("complexScheduledPool"));
        /**
         * 100
         */
        daemonStatsBgScheduledPool = Executors.newScheduledThreadPool(100, new DaemonThreadFactory("daemonStatsBgScheduledPool"));

    }

    /**
     * 线程池销毁
     */
    public void destroy() {
        if (null != complexPool) {
            complexPool.shutdown();
            complexPool.shutdownNow();
            complexPool = null;
        }
        if (daemonBgScheduledPool != null) {
            daemonBgScheduledPool.shutdown();
            daemonBgScheduledPool.shutdownNow();
            daemonBgScheduledPool = null;
        }
        if (localBgScheduledPool != null) {
            localBgScheduledPool.shutdown();
            localBgScheduledPool.shutdownNow();
            localBgScheduledPool = null;
        }
        if (complexScheduledPool != null) {
            complexScheduledPool.shutdown();
            complexScheduledPool.shutdownNow();
            complexScheduledPool = null;
        }
        if (daemonStatsBgScheduledPool != null) {
            daemonStatsBgScheduledPool.shutdown();
            daemonStatsBgScheduledPool.shutdownNow();
            daemonStatsBgScheduledPool = null;
        }
    }
}
