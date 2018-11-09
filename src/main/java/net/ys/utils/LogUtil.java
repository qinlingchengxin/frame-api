package net.ys.utils;

import org.apache.log4j.Logger;

/**
 * User: LiWenC
 * Date: 18-4-26
 */
public class LogUtil {

    private static Logger log = Logger.getLogger(LogUtil.class);

    public static void error(Exception e) {
        System.out.println("\r\n" + System.currentTimeMillis() + " error-msg: " + e.getMessage());
        log.error(e, e);
    }

    public static void info(Object msg) {
        System.out.println("\r\n" + System.currentTimeMillis() + " info-msg: " + msg);
        log.info(msg);
    }

    public static void debug(Object... messages) {
        if (log.isDebugEnabled()) {
            System.out.println();
            for (Object msg : messages) {
                System.out.println(System.currentTimeMillis() + " debug-msg: " + msg);
            }
        }
    }
}
