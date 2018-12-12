package net.ys.utils;

import net.sf.json.JSONObject;
import net.ys.constant.X;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;

/**
 * User: NMY
 * Date: 16-9-8
 */
public class Tools {

    static MessageDigest md5;
    private static Random rand;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
            rand = new Random();
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    /**
     * 判断多个字符串是否为空
     *
     * @param strings
     * @return
     */
    public static boolean isNotEmpty(String... strings) {
        if (strings == null || strings.length == 0) {
            return false;
        }
        for (String str : strings) {
            if (str == null || "".equals(str.trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取随机数字
     *
     * @return
     */
    public static int randomInt() {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int result = 0;
        for (int i = 0; i < 6; i++) {
            if (i == 0 && array[i] == 0) {
                array[i] = 1;
            }
            result = result * 10 + array[i];
        }
        return result;
    }

    /**
     * 获取指定长度随机字符串
     *
     * @param len
     * @return
     */
    public static String randomString(int len) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, len);
    }

    /**
     * 获取随机数字
     *
     * @return
     */
    public static int randomInt(int num) {
        return rand.nextInt(num);
    }

    /**
     * MD5加密
     *
     * @param str
     * @return
     */
    public static String genMD5(String str) {
        try {
            if (str == null || "".equals(str.trim())) {
                return "";
            }
            byte[] bs = md5.digest((str).getBytes(X.Code.U));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bs.length; i++) {
                sb.append(Character.forDigit((bs[i] >>> 4) & 0x0F, 16)).append(Character.forDigit(bs[i] & 0x0F, 16));
            }
            return sb.toString();
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 生成问号字符串
     *
     * @param size
     * @return
     */
    public static String genMark(int size) {
        StringBuffer sb = new StringBuffer("?");
        for (int i = 1; i < size; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }

    /**
     * 转换文件大小
     *
     * @param fileSize 字节
     * @return
     */
    public static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String result;
        if (fileSize <= 0) {
            result = "0B";
        } else if (fileSize < 1024) {
            result = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            result = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            result = df.format((double) fileSize / 1048576) + "MB";
        } else {
            result = df.format((double) fileSize / 1073741824) + "GB";
        }
        return result;
    }

    public static String genShortUrl(String url) throws IOException {
        HttpClient httpclient = new HttpClient();
        PostMethod postMethod = new PostMethod("https://dwz.cn/admin/create");
        RequestEntity entity = new StringRequestEntity("{\"url\":\"" + url + "\"}", "application/json", X.Code.U);
        postMethod.setRequestEntity(entity);
        postMethod.setRequestHeader("Content-Type", X.ContentType.JSON);

        int statusCode = httpclient.executeMethod(postMethod);
        if (statusCode == 200) {
            String result = postMethod.getResponseBodyAsString();
            JSONObject object = JSONObject.fromObject(result);
            int code = object.optInt("Code");
            if (code == 0) {
                return object.optString("ShortUrl");
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        String url = "https://www.cnblogs.com/Smileing/p/7207646.html";
        System.out.println(genShortUrl(url));
    }
}
