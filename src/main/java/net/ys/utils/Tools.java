package net.ys.utils;

import net.ys.constant.X;

import java.security.MessageDigest;

/**
 * User: LiWenC
 * Date: 16-9-8
 */
public class Tools {

    static MessageDigest md5;
    static final String PREFIX = "88_";

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            LogUtil.error(e);
        }
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
            byte[] bs = md5.digest((PREFIX + str).getBytes(X.ENCODING.U));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bs.length; i++) {
                sb.append(Character.forDigit((bs[i] >>> 4) & 0x0F, 16)).append(Character.forDigit(bs[i] & 0x0F, 16));
            }
            return sb.toString();
        } catch (Exception e) {
        }
        return "";
    }
}
