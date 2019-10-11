package net.ys.util;

import org.apache.log4j.Logger;

/**
 * User: NMY
 * Date: 18-4-26
 */
public class LogUtil {

    private static Logger log = Logger.getLogger(LogUtil.class);

    public static void error(Exception e) {
        log.error(e, e);
    }

    public static void error(Object msg, Exception e) {
        log.error(msg, e);
    }

    public static void info(Object msg) {
        log.info(msg);
    }

    public static void debug(Object... messages) {
        for (Object msg : messages) {
            log.debug(msg);
        }
    }
}
