package net.ys.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 系统配置类
 * User: NMY
 * Date: 16-8-28
 */
@Component
public class SysConfig {

    public static String appName;

    @Value("${app.name}")
    public void setAppName(String appName) {
        this.appName = appName;
    }
}
