package net.ys.component;

import net.ys.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * 系统配置类
 * User: NMY
 * Date: 16-8-28
 */
@Component
public class SysConfig {

    public static String appName;

    public static String pushMasterSecret;

    public static String pushAppKey;

    public static String dingTalkUrl;

    public static String testPath;

    public static int apiAccessTimeLimit;

    public static int smsMaxNumDay;

    public static int smsEffectiveTime;

    public static String[] testName;

    @Value("${app.name}")
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Value("${push.master.secret}")
    public void setPushMasterSecret(String pushMasterSecret) {
        this.pushMasterSecret = pushMasterSecret;
    }

    @Value("${push.app.key}")
    public void setPushAppKey(String pushAppKey) {
        this.pushAppKey = pushAppKey;
    }

    @Value("${api.access.time.limit}")
    public void setApiAccessTimeLimit(int apiAccessTimeLimit) {
        this.apiAccessTimeLimit = apiAccessTimeLimit;
    }

    @Value("${sms_max_num_day}")
    public void setSmsMaxNumDay(int smsMaxNumDay) {
        this.smsMaxNumDay = smsMaxNumDay;
    }

    @Value("${sms_effective_time}")
    public void setSmsEffectiveTime(int smsEffectiveTime) {
        this.smsEffectiveTime = smsEffectiveTime;
    }

    @Value("${ding_talk_url}")
    public void setDingTalkUrl(String dingTalkUrl) {
        this.dingTalkUrl = dingTalkUrl;
    }

    @Value("${test_path}")
    public void setTestPath(String testPath) {
        this.testPath = testPath;
        File file = new File(this.testPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("testPath is invalid");
            }
        }
        String absolutePath = file.getAbsolutePath();
        this.testPath = absolutePath.replaceAll("\\\\", "/") + "/";
    }

    @Value("${testName:test.name.*}")
    public void setTestName(String[] testName) {
        String prefix = testName[0];
        List<String> nameList = PropertyUtil.gets(prefix.substring(0, prefix.length() - 1));
        String[] names = new String[nameList.size()];
        this.testName = nameList.toArray(names);
    }
}
