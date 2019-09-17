import net.ys.cache.BaseCache;
import net.ys.controller.CheckController;
import net.ys.util.InitObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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

    /**
     * 这种方式不能测试注入的方法
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    @Test
    public void testApi() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Class clazz = CheckController.class;
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Object o = clazz.newInstance();
        for (Method method : declaredMethods) {
            int parameterCount = method.getParameterCount();
            Object[] objects = new Object[parameterCount];
            int i = 0;

            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                objects[i] = InitObject.initParameterObj(parameter);
                i++;
            }
            Object invoke = method.invoke(o, objects);
            System.out.println(invoke);
        }
    }
}
