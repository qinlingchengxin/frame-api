package net.ys.job;

import net.ys.threadpool.ThreadPoolManager;
import net.ys.util.LogUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    public void test() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.debug("jobService:test");
                } catch (Exception e) {
                    LogUtil.error(e);
                }
            }
        };
        ThreadPoolManager.INSTANCE.complexPool.doIt(r);
    }

    /**
     * 通过注解实现，此种方式不可配
     */
    @Scheduled(cron = "0 * * * * ?")
    public void code() {
        System.out.println("job code:" + System.currentTimeMillis());
    }
}
