package net.ys.utils;

import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * 钉钉机器人
 * User: NMY
 * Date: 19-4-22
 */
public class AliMsgUtil {

    /**
     * 文本消息
     *
     * @param webHookToken 机器人 webHookToken
     * @param content      消息内容
     * @param atMobiles    被@人的手机号数组
     * @param isAtAll      是否@所有人
     * @throws Exception
     */
    public static void sendText(String webHookToken, String content, String[] atMobiles, boolean isAtAll) throws IOException {
        JSONObject contentObj = new JSONObject();
        contentObj.put("content", content);
        JSONObject msgObj = new JSONObject();
        msgObj.put("msgtype", "text");
        msgObj.put("text", contentObj);
        msgObj.put("isAtall", isAtAll);
        if (ArrayUtils.isNotEmpty(atMobiles)) {
            JSONObject atMobilesObj = new JSONObject();
            atMobilesObj.put("atMobiles", atMobiles);
            msgObj.put("at", atMobilesObj);
        }
        String textMsg = msgObj.toString();
        sendHttp(webHookToken, textMsg);
    }

    /**
     * markdown类型消息
     *
     * @param webHookToken 机器人 webHookToken
     * @param title        首屏会话透出的展示内容
     * @param text         markdown格式的消息
     */
    public static void sendMarkDown(String webHookToken, String title, String text) throws Exception {
        JSONObject markdownObj = new JSONObject();
        markdownObj.put("text", text);
        markdownObj.put("title", title);
        JSONObject msgObj = new JSONObject();
        msgObj.put("msgtype", "markdown");
        msgObj.put("markdown", markdownObj);
        String textMsg = msgObj.toString();
        sendHttp(webHookToken, textMsg);
    }


    private static boolean sendHttp(String webHookToken, String textMsg) {
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost httppost = new HttpPost(webHookToken);
            httppost.addHeader("Content-Type", "application/json; charset=UTF-8");
            StringEntity se = new StringEntity(textMsg, "UTF-8");
            httppost.setEntity(se);
            response = httpClient.execute(httppost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity());
                System.out.println(result);
                return true;
            }
        } catch (Exception e) {
            LogUtil.error("send message error!!", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    LogUtil.error(e);
                }
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        String token = "https://oapi.dingtalk.com/robot/send?access_token=7716ecc460ff1652d579be5423b6001abf4ecac241744d450339ddd7ffc71972";
        sendText(token, "hello", null, false);
    }
}
