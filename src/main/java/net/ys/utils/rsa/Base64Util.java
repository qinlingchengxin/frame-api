package net.ys.utils.rsa;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64Util {

    private static BASE64Encoder encoder;
    private static BASE64Decoder decoder;

    static {
        encoder = new BASE64Encoder();
        decoder = new BASE64Decoder();
    }

    /**
     * Base64加密
     */
    public static String encryptBASE64(byte[] key) throws Exception {
        return encoder.encodeBuffer(key);
    }

    /**
     * Base64解密
     */
    public static byte[] decryptBASE64(String key) throws Exception {
        return decoder.decodeBuffer(key);
    }
}