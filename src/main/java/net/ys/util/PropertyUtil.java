package net.ys.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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

    public static List<String> gets(String prefixKey) {
        List<String> values = new ArrayList<String>();
        Enumeration<?> names = properties.propertyNames();
        String name;
        while (names.hasMoreElements()) {
            name = String.valueOf(names.nextElement());
            if (name.startsWith(prefixKey)) {
                values.add(properties.getProperty(name));
            }
        }
        return values;
    }

    public static String reGet(String key) throws IOException {
        Properties properties = new Properties();
        String path = PropertyUtil.class.getClassLoader().getResource("config.properties").getPath();
        properties.load(new FileInputStream(path));
        return properties.getProperty(key);
    }

    public static List<String> reGets(String prefixKey) throws IOException {
        Properties properties = new Properties();
        String path = PropertyUtil.class.getClassLoader().getResource("config.properties").getPath();
        properties.load(new FileInputStream(path));

        List<String> values = new ArrayList<String>();
        Enumeration<?> names = properties.propertyNames();
        String name;
        while (names.hasMoreElements()) {
            name = String.valueOf(names.nextElement());
            if (name.startsWith(prefixKey)) {
                values.add(properties.getProperty(name));
            }
        }
        return values;
    }
}
