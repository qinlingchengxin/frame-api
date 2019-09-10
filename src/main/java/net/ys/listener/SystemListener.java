package net.ys.listener;

import net.ys.threadpool.ThreadPoolManager;
import net.ys.util.Tools;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * User: NMY
 * Date: 16-8-29
 */
@WebListener
public class SystemListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ThreadPoolManager.INSTANCE.destroy();
        System.out.println("contextDestroyed");
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Tools.godBless();
    }
}