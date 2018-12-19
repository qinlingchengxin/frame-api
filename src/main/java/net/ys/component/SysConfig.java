package net.ys.component;

import net.ys.utils.PropertyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统配置类
 * User: NMY
 * Date: 16-8-28
 */
@Component
public class SysConfig {

    public static String appName;

    public static String[] testName;

    @Value("${app.name}")
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Value("${testName:test.name.*}")
    public void setTestName(String[] testName) {
        String prefix = testName[0];
        List<String> nameList = PropertyUtil.gets(prefix.substring(0, prefix.length() - 1));
        String[] names = new String[nameList.size()];
        this.testName = nameList.toArray(names);
    }
}
