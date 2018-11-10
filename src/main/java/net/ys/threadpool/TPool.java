package net.ys.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TPool extends ThreadPoolExecutor implements RejectedExecutionHandler {

    /**
     * @param corePoolSize    ： 线程池维护线程的最少数量
     * @param maximumPoolSize ：线程池维护线程的最大数量
     * @param keepAliveTime   ： 线程池维护线程所允许的空闲时间
     * @param unit            ： 线程池维护线程所允许的空闲时间的单位
     * @param workQueue       ： 线程池所使用的缓冲队列
     * @param handler         ： 线程池对拒绝任务的处理策略
     */
    public TPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public void doIt(Runnable r) {
        this.execute(r);
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        this.execute(r);
    }
}
