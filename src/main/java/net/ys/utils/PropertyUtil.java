package net.ys.utils;

import java.util.Properties;

/**
 * User: NMY
 * Date: 17-5-11
 */
public class PropertyUtil {

    static Properties properties;

    static {
        try {
            properties = new Properties();
            properties.load(PropertyUtil.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (Exception e) {
            throw new ExceptionInInitializerError("load properties error!");
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
