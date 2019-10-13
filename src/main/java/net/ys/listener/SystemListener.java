package net.ys.listener;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import net.ys.threadpool.ThreadPoolManager;
import net.ys.util.Tools;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

/**
 * User: NMY
 * Date: 16-8-29
 */
@WebListener
public class SystemListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ThreadPoolManager.INSTANCE.destroy();
        try {//释放DB驱动并关闭线程
            Enumeration<Driver> enumeration = DriverManager.getDrivers();
            while (enumeration.hasMoreElements()) {
                DriverManager.deregisterDriver(enumeration.nextElement());
            }
            AbandonedConnectionCleanupThread.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("--- contextDestroyed ---");

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("--- contextInitialized ---");
        Tools.godBless();
    }
}