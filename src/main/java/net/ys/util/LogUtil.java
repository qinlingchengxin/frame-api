package net.ys.util;

/**
 * User: NMY
 * Date: 18-4-26
 */
public class LogUtil {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LogUtil.class);
    private static java.util.logging.Logger jdkLog = java.util.logging.Logger.getLogger(LogUtil.class.getName());

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
        jdkLog.info("\n");
        for (Object msg : messages) {
            jdkLog.info("debug-msg: " + msg.toString());
        }
    }
}
