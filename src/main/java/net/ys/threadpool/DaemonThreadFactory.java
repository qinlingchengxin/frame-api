package net.ys.threadpool;

import java.util.concurrent.ThreadFactory;

public class DaemonThreadFactory implements ThreadFactory {

    private String threadFactoryName;

    public String getThreadName() {
        return this.threadFactoryName;
    }

    public DaemonThreadFactory(String name) {
        this.threadFactoryName = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName(this.threadFactoryName + "-" + t.getId());
        return t;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + ":" + this.threadFactoryName + "-";
    }

}