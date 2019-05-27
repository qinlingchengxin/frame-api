package net.ys.utils;

import net.sf.json.JSONObject;
import net.ys.constant.X;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信工具
 * User: LiWenC
 * Date: 17-5-22
 */

public class SmsUtil {

    public static boolean send(String phoneNumber, String code) throws Exception {

        String time = String.valueOf(System.currentTimeMillis());
        String content = String.format(X.SMS.CONTENT, code);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mobile", phoneNumber);
        map.put("sendMarkId", X.SMS.SEND_MARK_ID);
        map.put("timestamp", time);
        map.put("content", String.format(X.SMS.CONTENT, code));
        map.put("sign", X.SMS.SIGN);

        StringBuilder builder = new StringBuilder();
        builder.append("content=" + content).append("mobile=" + phoneNumber).append("sendMarkId=" + X.SMS.SEND_MARK_ID).append("sign=" + X.SMS.SIGN).append("timestamp=" + time).append(X.SMS.SUFFIX);
        String singStr = Tools.genMD5(builder.toString());
        map.put("signstr", singStr);

        String returnStr = HttpClientUtils.post(X.SMS.URL, map);
        JSONObject object = JSONObject.fromObject(returnStr);
        if (object.getInt("status") == 200) {
            return true;
        }
        return false;
    }
}
