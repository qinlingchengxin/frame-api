package net.ys.component;

import net.ys.constant.X;
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

    public static boolean validApiParameter = false;//是否对api参数进行安全校验

    public static int enableWhiteList;

    public static List<String> backupServerIp;

    public static String redsHost;

    public static int redsPort;

    public static int redsDatabase;

    public static String redsPassword;

    public static String appName;

    public static String pushMasterSecret;

    public static String pushAppKey;

    public static String dingTalkUrl;

    public static String testPath;

    public static int apiAccessTimeLimit;

    public static int smsMaxNumDay;

    public static int smsEffectiveTime;

    public static List<String> testName;

    @Value("${enable.white.list}")
    public void setEnableWhiteList(int enableWhiteList) {
        this.enableWhiteList = enableWhiteList;
    }

    @Value("${valid.api.parameter}")
    public void setValidApiParameter(int validApiParameter) {
        this.validApiParameter = validApiParameter == 1;
    }

    @Value("${backupServerIp:backup.server.ip.*}")
    public void setBackupServerIp(String[] backupServerIp) {
        String prefix = backupServerIp[0];
        this.backupServerIp = PropertyUtil.gets(prefix.substring(0, prefix.length() - 1));
    }

    @Value("${app_name}")
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Value("${reds.host}")
    public void setRedsHost(String redsHost) {
        this.redsHost = redsHost;
    }

    @Value("${reds.password}")
    public void setRedsPassword(String redsPassword) {
        this.redsPassword = redsPassword;
    }

    @Value("${reds.port}")
    public void setRedsPort(int redsPort) {
        this.redsPort = redsPort;
    }

    @Value("${reds.database}")
    public void setRedsDatabase(int redsDatabase) {
        this.redsDatabase = redsDatabase;
    }

    @Value("${push_master_secret}")
    public void setPushMasterSecret(String pushMasterSecret) {
        this.pushMasterSecret = pushMasterSecret;
    }

    @Value("${push_app_key}")
    public void setPushAppKey(String pushAppKey) {
        this.pushAppKey = pushAppKey;
    }

    @Value("${api_access_time_limit}")
    public void setApiAccessTimeLimit(int apiAccessTimeLimit) {
        this.apiAccessTimeLimit = apiAccessTimeLimit * X.Time.MINUTE_SECOND;
    }

    @Value("${sms_max_num_day}")
    public void setSmsMaxNumDay(int smsMaxNumDay) {
        this.smsMaxNumDay = smsMaxNumDay;
    }

    @Value("${sms_effective_time}")
    public void setSmsEffectiveTime(int smsEffectiveTime) {
        this.smsEffectiveTime = smsEffectiveTime;
    }

    @Value("${ding_talk_access_token}")
    public void setDingTalkUrl(String dingTalkAccessToken) {
        this.dingTalkUrl = "https://oapi.dingtalk.com/robot/send?" + dingTalkAccessToken;
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
        this.testPath = absolutePath.replace('\\', '/') + "/";
    }

    @Value("${testName:test_name.*}")
    public void setTestName(String[] testName) {
        String prefix = testName[0];
        this.testName = PropertyUtil.gets(prefix.substring(0, prefix.length() - 1));
    }
}
