import net.ys.cache.BaseCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * User: NMY
 * Date: 19-4-7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class SpringTest {

    @Resource
    BaseCache baseCache;

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 200; i++) {
            Thread.sleep(5);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    baseCache.save("key:" + System.currentTimeMillis(), System.currentTimeMillis());

                }
            }).start();
        }
    }
}
