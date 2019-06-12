package net.ys.util;

import net.ys.component.SysConfig;
import net.ys.util.req.HttpUtilProxy;

/**
 * 钉钉机器人
 * User: NMY
 * Date: 19-4-22
 */
public class AliMsgUtil {

    /**
     * @param content 发送消息
     */
    public static void sendText(String content) {
        String msg = "{\"msgtype\":\"text\",\"text\":{\"content\":\"" + content + "\"},\"at\":{\"isAtAll\":true}}";
        HttpUtilProxy.doPostJson(SysConfig.dingTalkUrl, msg);
    }
}
