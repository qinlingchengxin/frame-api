package net.ys.job;

import net.ys.util.LogUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    public void test() {
        LogUtil.debug("jobService:test");
    }

    /**
     * 通过注解实现，此种方式不可配
     */
    @Scheduled(cron = "0 * * * * ?")
    public void code() {
        System.out.println("job code:" + System.currentTimeMillis());
    }
}
