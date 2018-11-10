package net.ys.job;

import net.ys.threadpool.ThreadPoolManager;
import net.ys.utils.LogUtil;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    public void test() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.debug("JobService-->" + System.currentTimeMillis());
                } catch (Exception e) {
                    LogUtil.error(e);
                }
            }
        };
        ThreadPoolManager.INSTANCE.complexPool.doIt(r);
    }
}
